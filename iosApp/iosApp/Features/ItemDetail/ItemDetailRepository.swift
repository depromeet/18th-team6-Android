import Foundation

protocol ItemDetailRepository {
    func detail(itemId: Int) async throws -> ItemDetailItem
    func updateSpareQuantity(itemId: Int, quantity: Int, updatedAt: Date) async throws -> ItemDetailItem
    func completeReplacement(itemId: Int, completedAt: Date) async throws -> ItemDetailItem
    func delete(itemId: Int) async throws
}

struct ItemDetailEditSource: Equatable {
    let item: ItemDetailItem
    let existingItemNames: [String]
}

protocol ItemDetailEditRepository {
    func editSource(itemId: Int) async throws -> ItemDetailEditSource
    func updateItem(itemId: Int, draft: ItemDetailEditDraft, original: ItemDetailItem) async throws -> ItemDetailItem
}

enum ItemDetailRepositoryError: LocalizedError, Equatable {
    case notFound(itemId: Int)
    case unsupportedMutation
    case operationFailed(message: String)

    var errorDescription: String? {
        switch self {
        case let .notFound(itemId):
            return "소모품 ID \(itemId)를 찾을 수 없어요."
        case .unsupportedMutation:
            return "이 기능은 아직 준비 중이에요."
        case let .operationFailed(message):
            return message
        }
    }
}

actor ItemDetailSampleRepository: ItemDetailRepository {
    private var itemsByID: [Int: ItemDetailItem]

    init(items: [ItemDetailItem] = []) {
        self.itemsByID = Dictionary(uniqueKeysWithValues: items.map { ($0.id, $0) })
    }

    func detail(itemId: Int) async throws -> ItemDetailItem {
        guard let item = itemsByID[itemId] else {
            throw ItemDetailRepositoryError.notFound(itemId: itemId)
        }

        return item
    }

    func updateSpareQuantity(
        itemId: Int,
        quantity: Int,
        updatedAt: Date
    ) async throws -> ItemDetailItem {
        let item = try await detail(itemId: itemId)
        let updated = item.updatingSpareQuantity(quantity, at: updatedAt)
        itemsByID[itemId] = updated
        return updated
    }

    func completeReplacement(
        itemId: Int,
        completedAt: Date
    ) async throws -> ItemDetailItem {
        let item = try await detail(itemId: itemId)
        let updated = item.completingReplacement(at: completedAt)
        itemsByID[itemId] = updated
        return updated
    }

    func delete(itemId: Int) async throws {
        guard itemsByID.removeValue(forKey: itemId) != nil else {
            throw ItemDetailRepositoryError.notFound(itemId: itemId)
        }
    }
}

actor ItemDetailSampleEditRepository: ItemDetailEditRepository {
    private var itemsByID: [Int: ItemDetailItem]

    init(items: [ItemDetailItem] = []) {
        self.itemsByID = Dictionary(uniqueKeysWithValues: items.map { ($0.id, $0) })
    }

    func editSource(itemId: Int) async throws -> ItemDetailEditSource {
        guard let item = itemsByID[itemId] else {
            throw ItemDetailRepositoryError.notFound(itemId: itemId)
        }

        return ItemDetailEditSource(
            item: item,
            existingItemNames: itemsByID.values
                .filter { $0.id != itemId }
                .map(\.name)
        )
    }

    func updateItem(
        itemId: Int,
        draft: ItemDetailEditDraft,
        original: ItemDetailItem
    ) async throws -> ItemDetailItem {
        guard itemsByID[itemId] != nil else {
            throw ItemDetailRepositoryError.notFound(itemId: itemId)
        }

        var updated = original
        updated.name = draft.name.trimmingCharacters(in: .whitespacesAndNewlines)
        updated.replacementCycle = ItemDetailReplacementCycle(intervalDays: draft.replacementCycleDays)
        updated.imageURL = draft.imageURL
        updated.updatedAt = Date()
        itemsByID[itemId] = updated
        return updated
    }
}
