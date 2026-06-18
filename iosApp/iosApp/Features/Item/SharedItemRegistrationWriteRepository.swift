import Foundation
import Shared

actor SharedItemRegistrationWriteRepository: ItemRegistrationWriteRepository {
    private let readService: SharedReadService
    private let writeService: SharedWriteService

    init(
        readService: SharedReadService,
        writeService: SharedWriteService
    ) {
        self.readService = readService
        self.writeService = writeService
    }

    func createKind(name: String, imageOption: ItemImageOption) async throws -> ItemKind {
        let event = "SharedItemRegistrationWriteRepository.createKind"
        let details = "iconId=\(imageOption.id) nameLength=\(name.count)"
        AppLog.enter(AppLog.swiftRepository, event, details)
        do {
            let category = try await writeService.createCategory(
                name: name,
                iconId: Int64(imageOption.id)
            )

            let kind = ItemKind(
                id: Int(clamping: category.id),
                title: category.name,
                addedCount: ItemRegistrationConfig.newKindInitialAddedCount,
                imageURL: category.iconUrl
            )
            AppLog.success(AppLog.swiftRepository, event, "kindId=\(kind.id)")
            return kind
        } catch {
            AppLog.failure(AppLog.swiftRepository, event, error, details)
            throw error
        }
    }

    func createItem(request: ItemRegistrationCreateItemRequest) async throws {
        guard request.quantity >= ItemRegistrationConfig.quantityMinimum else {
            throw SharedItemRegistrationWriteRepositoryError.invalidQuantity
        }

        let event = "SharedItemRegistrationWriteRepository.createItem"
        let details = "categoryId=\(request.categoryId) quantity=\(request.quantity) lastReplacementPeriod=\(request.lastReplacementPeriod?.rawValue ?? "nil")"
        AppLog.enter(AppLog.swiftRepository, event, details)
        do {
            if let existingItem = try await existingItem(for: request) {
                _ = try await addQuantity(request.quantity, to: existingItem)
            } else {
                _ = try await writeService.createItem(
                    categoryId: Int64(request.categoryId),
                    name: request.name,
                    count: KotlinInt(int: Int32(request.quantity)),
                    lastReplacementPeriod: request.lastReplacementPeriod?.rawValue,
                    replacementIntervalDays: nil
                )
            }
            AppLog.success(AppLog.swiftRepository, event, details)
        } catch {
            AppLog.failure(AppLog.swiftRepository, event, error, details)
            throw error
        }
    }

    func createItems(request: ItemRegistrationCreateItemsRequest) async throws {
        guard !request.items.isEmpty else {
            throw SharedItemRegistrationWriteRepositoryError.emptyItems
        }
        guard request.items.allSatisfy({ $0.quantity >= ItemRegistrationConfig.quantityMinimum }) else {
            throw SharedItemRegistrationWriteRepositoryError.invalidQuantity
        }

        let event = "SharedItemRegistrationWriteRepository.createItems"
        let details = "count=\(request.items.count) receiptImageURL=\(request.receiptImageURL != nil)"
        AppLog.enter(AppLog.swiftRepository, event, details)
        do {
            try await createNewItemsOrAddQuantities(for: request)
            AppLog.success(AppLog.swiftRepository, event, details)
        } catch {
            AppLog.failure(AppLog.swiftRepository, event, error, details)
            throw error
        }
    }

    private func existingItem(for request: ItemRegistrationCreateItemRequest) async throws -> Shared.Item? {
        let items = try await readService.getItems()
        return items.first { item in
            item.matches(request: request)
        }
    }

    private func createNewItemsOrAddQuantities(for request: ItemRegistrationCreateItemsRequest) async throws {
        let existingItems = try await readService.getItems()
        let requestedItems = request.items.mergedByItemIdentity()
        let patchTargets = patchTargets(for: requestedItems, existingItems: existingItems)
        let createItems = requestedItems.filter { item in
            !existingItems.contains { existingItem in
                existingItem.matches(request: item)
            }
        }

        for target in patchTargets {
            _ = try await addQuantity(target.addedQuantity, to: target.item)
        }

        guard !createItems.isEmpty else { return }
        _ = try await writeService.createItems(
            params: createItems.map(\.sharedCreateItemParams),
            receiptImageUrl: request.receiptImageURL?.nilIfBlank
        )
    }

    private func patchTargets(
        for requestedItems: [ItemRegistrationCreateItemsRequestItem],
        existingItems: [Shared.Item]
    ) -> [ExistingItemPatchTarget] {
        var targets: [Int64: ExistingItemPatchTarget] = [:]

        for requestedItem in requestedItems {
            guard let existingItem = existingItems.first(where: { $0.matches(request: requestedItem) }) else {
                continue
            }

            var target = targets[existingItem.id] ?? ExistingItemPatchTarget(item: existingItem)
            target.addedQuantity += requestedItem.quantity
            targets[existingItem.id] = target
        }

        return Array(targets.values)
    }

    private func addQuantity(
        _ quantity: Int,
        to item: Shared.Item
    ) async throws -> Shared.Item {
        try await writeService.patchSpareCount(
            itemId: item.id,
            count: Int32(Int(item.count) + quantity)
        )
    }
}

private struct ExistingItemPatchTarget {
    let item: Shared.Item
    var addedQuantity: Int = 0
}

private enum SharedItemRegistrationWriteRepositoryError: LocalizedError {
    case emptyItems
    case invalidQuantity

    var errorDescription: String? {
        switch self {
        case .emptyItems:
            "등록할 소모품이 없어요."
        case .invalidQuantity:
            "등록할 수량은 \(ItemRegistrationConfig.quantityMinimum)개 이상이어야 해요."
        }
    }
}

private extension ItemRegistrationCreateItemsRequestItem {
    var sharedCreateItemParams: CreateItemParams {
        CreateItemParams(
            categoryId: categoryId.map { KotlinLong(longLong: Int64($0)) },
            name: name,
            spareQuantity: KotlinInt(int: Int32(quantity)),
            lastReplacementPeriod: lastReplacementPeriod?.sharedReplacementPeriod,
            replacementIntervalDays: nil,
            newCategoryName: newCategoryName?.nilIfBlank,
            newCategoryDefaultReplacementIntervalDays: newCategoryDefaultReplacementIntervalDays.map { KotlinInt(int: Int32($0)) }
        )
    }

    func hasSameIdentity(as other: ItemRegistrationCreateItemsRequestItem) -> Bool {
        categoryId == other.categoryId &&
            name.hasSameIdentity(as: other.name) &&
            normalizedNewCategoryName == other.normalizedNewCategoryName
    }

    func addingQuantity(_ addedQuantity: Int) -> ItemRegistrationCreateItemsRequestItem {
        ItemRegistrationCreateItemsRequestItem(
            categoryId: categoryId,
            name: name,
            quantity: quantity + addedQuantity,
            lastReplacementPeriod: lastReplacementPeriod,
            newCategoryName: newCategoryName,
            newCategoryDefaultReplacementIntervalDays: newCategoryDefaultReplacementIntervalDays
        )
    }

    private var normalizedNewCategoryName: String? {
        guard categoryId == nil else { return nil }
        return newCategoryName?.normalizedIdentity
    }
}

private extension Array where Element == ItemRegistrationCreateItemsRequestItem {
    func mergedByItemIdentity() -> [ItemRegistrationCreateItemsRequestItem] {
        var mergedItems: [ItemRegistrationCreateItemsRequestItem] = []

        for item in self {
            guard let index = mergedItems.firstIndex(where: { $0.hasSameIdentity(as: item) }) else {
                mergedItems.append(item)
                continue
            }

            mergedItems[index] = mergedItems[index].addingQuantity(item.quantity)
        }

        return mergedItems
    }
}

private extension Shared.Item {
    func matches(request: ItemRegistrationCreateItemRequest) -> Bool {
        categoryId == Int64(request.categoryId) &&
            name.hasSameIdentity(as: request.name)
    }

    func matches(request: ItemRegistrationCreateItemsRequestItem) -> Bool {
        guard name.hasSameIdentity(as: request.name) else { return false }

        if let categoryId = request.categoryId {
            return self.categoryId == Int64(categoryId)
        }

        if let newCategoryName = request.newCategoryName?.nilIfBlank {
            return categoryName.hasSameIdentity(as: newCategoryName)
        }

        return true
    }
}

private extension ItemRegistrationLastReplacementPeriod {
    var sharedReplacementPeriod: ReplacementPeriod {
        switch self {
        case .withinWeek:
            .withinWeek
        case .withinMonth:
            .withinMonth
        case .withinThreeMonths:
            .withinThreeMonths
        case .overThreeMonths:
            .overThreeMonths
        }
    }
}

private extension String {
    var nilIfBlank: String? {
        let trimmedValue = trimmingCharacters(in: .whitespacesAndNewlines)
        return trimmedValue.isEmpty ? nil : trimmedValue
    }

    var normalizedIdentity: String {
        trimmingCharacters(in: .whitespacesAndNewlines).lowercased()
    }

    func hasSameIdentity(as other: String) -> Bool {
        normalizedIdentity == other.normalizedIdentity
    }
}
