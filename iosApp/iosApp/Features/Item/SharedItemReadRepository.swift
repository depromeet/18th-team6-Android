import Foundation
import Shared

actor SharedItemReadRepository: ItemDetailRepository, ItemRegistrationCatalogRepository {
    private let readService: SharedReadService
    private let calendar: Calendar

    init(
        readService: SharedReadService,
        calendar: Calendar = .current
    ) {
        self.readService = readService
        self.calendar = calendar
    }

    func catalog() async throws -> ItemRegistrationCatalog {
        let categories = try await readService.getCategories()
        let categoryIcons = try await readService.getCategoryIcons()

        let kinds = categories.map { category in
            ItemKind(
                id: Int(clamping: category.id),
                title: category.name,
                addedCount: ItemRegistrationConfig.newKindInitialAddedCount,
                imageAssetName: Self.assetName(for: category.name)
            )
        }

        return ItemRegistrationCatalog(
            itemKinds: kinds,
            imageOptions: Self.imageOptions(from: categoryIcons)
        )
    }

    func detail(itemId: Int) async throws -> ItemDetailItem {
        let items = try await readService.getItems()
        guard let item = items.first(where: { $0.id == Int64(itemId) }) else {
            throw ItemDetailRepositoryError.notFound(itemId: itemId)
        }

        let histories = try await readService.getReplacementHistories(itemId: Int64(itemId), limit: nil)
        return makeDetailItem(from: item, histories: histories)
    }

    func updateSpareQuantity(
        itemId: Int,
        quantity: Int,
        updatedAt: Date
    ) async throws -> ItemDetailItem {
        throw ItemDetailRepositoryError.unsupportedMutation
    }

    func completeReplacement(
        itemId: Int,
        completedAt: Date
    ) async throws -> ItemDetailItem {
        throw ItemDetailRepositoryError.unsupportedMutation
    }

    func delete(itemId: Int) async throws {
        throw ItemDetailRepositoryError.unsupportedMutation
    }

    private func makeDetailItem(
        from item: Shared.Item,
        histories: [Shared.ReplacementHistory]
    ) -> ItemDetailItem {
        let now = Date()
        let currentCycleStartedAt = date(from: item.lastReplacedDate) ?? now
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
            updatedAt: now
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

    private static func assetName(for title: String) -> String {
        ItemAssetCatalog.entries.first {
            title.localizedCaseInsensitiveContains($0.title) ||
                $0.title.localizedCaseInsensitiveContains(title)
        }?.assetName ?? ItemRegistrationAsset.fallbackItemImage
    }

    private static func imageOptions(from icons: [Shared.CategoryIcon]) -> [ItemImageOption] {
        guard !icons.isEmpty else {
            return ItemRegistrationSampleData.imageOptions
        }

        return icons.enumerated().map { index, icon in
            ItemImageOption(
                id: Int(clamping: icon.id),
                assetName: ItemAssetCatalog.assetNames[index % ItemAssetCatalog.assetNames.count]
            )
        }
    }
}
