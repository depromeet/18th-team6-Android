import Foundation

enum ItemDetailDomainSampleData {
    static let referenceDate = Calendar.current.startOfDay(for: Date())

    static let items: [ItemDetailItem] = HomeListTabSampleData.items.map { makeItem(from: $0) }

    static func item(id: Int) -> ItemDetailItem {
        items.first { $0.id == id } ?? items[0]
    }

    private static func makeItem(from item: HomeListTabItem) -> ItemDetailItem {
        let replacementCycleDays = max(
            ItemDetailConfig.minimumReplacementCycleDays,
            item.daysInUse + item.replacementDday
        )

        return ItemDetailItem(
            id: item.id,
            name: item.title,
            kindName: kindName(for: item),
            imageAssetName: item.assetName,
            spareQuantity: item.stockCount,
            replacementCycle: ItemDetailReplacementCycle(intervalDays: replacementCycleDays),
            currentCycleStartedAt: daysAgo(item.daysInUse),
            notification: ItemDetailNotificationSetting(
                isEnabled: item.replacementDday <= ItemDetailConfig.replacementWarningRemainingDays,
                leadDays: ItemDetailConfig.defaultReminderLeadDays,
                permissionStatus: .authorized
            ),
            replacementRecords: replacementRecords(for: item, replacementCycleDays: replacementCycleDays),
            createdAt: daysAgo(item.daysInUse + replacementCycleDays * 2),
            updatedAt: daysAgo(min(item.id % 14, item.daysInUse))
        )
    }

    private static func replacementRecords(
        for item: HomeListTabItem,
        replacementCycleDays: Int
    ) -> [ItemDetailReplacementRecord] {
        [
            record(
                id: 1,
                replacedDaysAgo: item.daysInUse,
                previousStartedDaysAgo: item.daysInUse + replacementCycleDays,
                memo: nil
            ),
            record(
                id: 2,
                replacedDaysAgo: item.daysInUse + replacementCycleDays,
                previousStartedDaysAgo: item.daysInUse + replacementCycleDays * 2,
                memo: nil
            )
        ]
    }

    private static func kindName(for item: HomeListTabItem) -> String {
        switch item.assetName {
        case "item_toothbrush", "item_razor", "item_shower_filter", "item_towel", "item_body_wash":
            return "욕실용품"
        case "item_detergent":
            return "세탁용품"
        case "item_scrub_sponge", "item_zip_bag", "item_kitchen_towel", "item_dish_soap":
            return "주방용품"
        default:
            return "생활용품"
        }
    }

    private static func daysAgo(_ days: Int) -> Date {
        Calendar.current.date(byAdding: .day, value: -days, to: referenceDate) ?? referenceDate
    }

    private static func record(
        id: Int,
        replacedDaysAgo: Int,
        previousStartedDaysAgo: Int,
        memo: String?
    ) -> ItemDetailReplacementRecord {
        ItemDetailReplacementRecord(
            id: id,
            replacedAt: daysAgo(replacedDaysAgo),
            previousStartedAt: daysAgo(previousStartedDaysAgo),
            memo: memo
        )
    }
}
