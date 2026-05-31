import Foundation
import Shared

actor SharedItemReadRepository: ItemDetailRepository, ItemDetailEditRepository, ItemRegistrationCatalogRepository {
    private let readService: SharedReadService
    private let writeService: SharedWriteService
    private let calendar: Calendar

    init(
        readService: SharedReadService,
        writeService: SharedWriteService,
        calendar: Calendar = .current
    ) {
        self.readService = readService
        self.writeService = writeService
        self.calendar = calendar
    }

    func catalog() async throws -> ItemRegistrationCatalog {
        let categories = try await readService.getCategories()
        let categoryIcons = try await readService.getCategoryIcons()

        let kinds = categories.map { category in
            ItemKind(
                id: Int(clamping: category.id),
                title: category.name,
                addedCount: Int(category.itemCount),
                imageAssetName: Self.assetName(for: category.name)
            )
        }

        return ItemRegistrationCatalog(
            itemKinds: kinds,
            imageOptions: Self.imageOptions(from: categoryIcons)
        )
    }

    func detail(itemId: Int) async throws -> ItemDetailItem {
        let event = "SharedItemReadRepository.detail"
        let details = "itemId=\(itemId)"
        AppLog.enter(AppLog.swiftRepository, event, details)
        do {
            let items = try await readService.getItems()
            guard let item = items.first(where: { $0.id == Int64(itemId) }) else {
                throw ItemDetailRepositoryError.notFound(itemId: itemId)
            }

            let histories = try await readService.getReplacementHistories(itemId: Int64(itemId), limit: nil)
            let detail = makeDetailItem(from: item, histories: histories)
            AppLog.success(AppLog.swiftRepository, event, "\(details) historyCount=\(histories.count)")
            return detail
        } catch {
            AppLog.failure(AppLog.swiftRepository, event, error, details)
            throw error
        }
    }

    func updateSpareQuantity(
        itemId: Int,
        quantity: Int,
        updatedAt: Date
    ) async throws -> ItemDetailItem {
        let event = "SharedItemReadRepository.updateSpareQuantity"
        let details = "itemId=\(itemId) quantity=\(quantity)"
        AppLog.enter(AppLog.swiftRepository, event, details)
        do {
            let item = try await writeService.patchSpareCount(
                itemId: Int64(itemId),
                count: Int32(quantity)
            )
            let detail = try await makeDetailItem(from: item, updatedAt: updatedAt)
            AppLog.success(AppLog.swiftRepository, event, details)
            return detail
        } catch {
            AppLog.failure(AppLog.swiftRepository, event, error, details)
            throw presentationError(
                from: error,
                fallbackMessage: "여분 수량을 수정하지 못했어요."
            )
        }
    }

    func completeReplacement(
        itemId: Int,
        completedAt: Date
    ) async throws -> ItemDetailItem {
        let event = "SharedItemReadRepository.completeReplacement"
        let details = "itemId=\(itemId)"
        AppLog.enter(AppLog.swiftRepository, event, details)
        do {
            let item = try await writeService.createReplacement(
                itemId: Int64(itemId),
                replacedDate: replacementDateString(from: completedAt)
            )
            let detail = try await makeDetailItem(from: item, updatedAt: completedAt)
            AppLog.success(AppLog.swiftRepository, event, details)
            return detail
        } catch {
            AppLog.failure(AppLog.swiftRepository, event, error, details)
            throw presentationError(
                from: error,
                fallbackMessage: "교체 완료를 기록하지 못했어요."
            )
        }
    }

    func delete(itemId: Int) async throws {
        let event = "SharedItemReadRepository.delete"
        let details = "itemId=\(itemId)"
        AppLog.enter(AppLog.swiftRepository, event, details)
        do {
            try await writeService.deleteItem(itemId: Int64(itemId))
            AppLog.success(AppLog.swiftRepository, event, details)
        } catch {
            AppLog.failure(AppLog.swiftRepository, event, error, details)
            throw presentationError(
                from: error,
                fallbackMessage: "소모품을 삭제하지 못했어요."
            )
        }
    }

    func editSource(itemId: Int) async throws -> ItemDetailEditSource {
        let items = try await readService.getItems()
        guard let item = items.first(where: { $0.id == Int64(itemId) }) else {
            throw ItemDetailRepositoryError.notFound(itemId: itemId)
        }

        let histories = try await readService.getReplacementHistories(itemId: Int64(itemId), limit: nil)
        return ItemDetailEditSource(
            item: makeDetailItem(from: item, histories: histories),
            existingItemNames: items
                .filter { $0.id != Int64(itemId) }
                .map(\.name)
        )
    }

    func updateItem(
        itemId: Int,
        draft: ItemDetailEditDraft,
        original: ItemDetailItem
    ) async throws -> ItemDetailItem {
        do {
            let trimmedName = draft.name.trimmingCharacters(in: .whitespacesAndNewlines)
            let item = try await writeService.patchItem(
                itemId: Int64(itemId),
                name: trimmedName == original.name ? nil : trimmedName,
                count: nil,
                lastReplacedDate: nil,
                replacementIntervalDays: draft.replacementCycleDays == original.replacementCycle.intervalDays
                    ? nil
                    : KotlinInt(int: Int32(draft.replacementCycleDays))
            )
            var updated = try await makeDetailItem(from: item, updatedAt: Date())
            updated.imageAssetName = draft.imageAssetName
            return updated
        } catch {
            throw presentationError(
                from: error,
                fallbackMessage: "소모품 정보를 수정하지 못했어요."
            )
        }
    }

    private func makeDetailItem(
        from item: Shared.Item,
        histories: [Shared.ReplacementHistory]
    ) -> ItemDetailItem {
        makeDetailItem(from: item, histories: histories, updatedAt: Date())
    }

    private func makeDetailItem(
        from item: Shared.Item,
        updatedAt: Date
    ) async throws -> ItemDetailItem {
        let histories = try await readService.getReplacementHistories(itemId: item.id, limit: nil)
        return makeDetailItem(from: item, histories: histories, updatedAt: updatedAt)
    }

    private func makeDetailItem(
        from item: Shared.Item,
        histories: [Shared.ReplacementHistory],
        updatedAt: Date
    ) -> ItemDetailItem {
        let currentCycleStartedAt = date(from: item.lastReplacedDate) ?? updatedAt
        let replacementRecords = makeReplacementRecords(
            from: histories,
            replacementIntervalDays: Int(item.replacementIntervalDays)
        )

        return ItemDetailItem(
            id: Int(clamping: item.id),
            name: item.name,
            kindName: item.categoryName,
            imageAssetName: Self.assetName(for: item.categoryName),
            spareQuantity: Int(item.count),
            replacementCycle: ItemDetailReplacementCycle(intervalDays: Int(item.replacementIntervalDays)),
            currentCycleStartedAt: currentCycleStartedAt,
            notification: ItemDetailNotificationSetting(isEnabled: false),
            replacementRecords: replacementRecords,
            createdAt: currentCycleStartedAt,
            updatedAt: updatedAt
        )
    }

    private func makeReplacementRecords(
        from histories: [Shared.ReplacementHistory],
        replacementIntervalDays: Int
    ) -> [ItemDetailReplacementRecord] {
        histories.compactMap { history in
            guard let replacedAt = date(from: history.replacedDate) else { return nil }
            let previousStartedAt = calendar.date(
                byAdding: .day,
                value: -replacementIntervalDays,
                to: replacedAt
            ) ?? replacedAt

            return ItemDetailReplacementRecord(
                id: Int(clamping: history.id),
                replacedAt: replacedAt,
                previousStartedAt: previousStartedAt,
                memo: nil
            )
        }
        .sorted { $0.replacedAt > $1.replacedAt }
    }

    private func date(from replacementDate: Any?) -> Date? {
        guard let replacementDate else { return nil }

        if let date = replacementDate as? Date {
            return date
        }

        let rawText: String
        if let text = replacementDate as? String {
            rawText = text
        } else if let text = replacementDate as? CustomStringConvertible {
            rawText = text.description
        } else {
            rawText = String(describing: replacementDate)
        }

        guard let range = rawText.range(
            of: #"\d{4}-\d{2}-\d{2}"#,
            options: .regularExpression
        ) else {
            return nil
        }

        let formatter = DateFormatter()
        formatter.calendar = calendar
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.date(from: String(rawText[range]))
    }

    private func replacementDateString(from date: Date) -> String {
        let formatter = DateFormatter()
        formatter.calendar = calendar
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.timeZone = calendar.timeZone
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: date)
    }

    private func presentationError(
        from error: Error,
        fallbackMessage: String
    ) -> ItemDetailRepositoryError {
        if let itemDetailError = error as? ItemDetailRepositoryError {
            return itemDetailError
        }

        return .operationFailed(message: fallbackMessage)
    }

    private static func assetName(for title: String) -> String {
        ItemAssetCatalog.entries.first {
            title.localizedCaseInsensitiveContains($0.title) ||
                $0.title.localizedCaseInsensitiveContains(title)
        }?.assetName ?? ItemRegistrationAsset.fallbackItemImage
    }

    private static func imageOptions(from icons: [Shared.CategoryIcon]) -> [ItemImageOption] {
        icons.map { icon in
            ItemImageOption(
                id: Int(clamping: icon.id),
                assetName: CategoryIconKind.assetName(iconId: icon.id, url: icon.url)
            )
        }
    }
}
