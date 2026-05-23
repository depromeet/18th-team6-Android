import Foundation

enum ItemDetailDomainSampleData {
    static let referenceDate = Calendar.current.startOfDay(for: Date())

    static let consumables: [ItemDetailConsumable] = [
        ItemDetailConsumable(
            id: 1,
            name: "칫솔",
            kindName: "욕실용품",
            imageAssetName: "item_detail_toothbrush",
            spareQuantity: 3,
            replacementCycle: ItemDetailReplacementCycle(intervalDays: 30),
            currentCycleStartedAt: daysAgo(23),
            notification: ItemDetailNotificationSetting(
                isEnabled: true,
                leadDays: 1,
                permissionStatus: .authorized
            ),
            replacementRecords: [
                record(id: 1, replacedDaysAgo: 23, previousStartedDaysAgo: 61, memo: "월초 교체"),
                record(id: 2, replacedDaysAgo: 61, previousStartedDaysAgo: 93, memo: nil),
                record(id: 3, replacedDaysAgo: 93, previousStartedDaysAgo: 124, memo: nil),
                record(id: 4, replacedDaysAgo: 124, previousStartedDaysAgo: 153, memo: nil)
            ],
            createdAt: daysAgo(123),
            updatedAt: daysAgo(2)
        ),
        ItemDetailConsumable(
            id: 2,
            name: "면도기 날",
            kindName: "욕실용품",
            imageAssetName: "home_orb_razor",
            spareQuantity: 1,
            replacementCycle: ItemDetailReplacementCycle(intervalDays: 21),
            currentCycleStartedAt: daysAgo(18),
            notification: ItemDetailNotificationSetting(
                isEnabled: true,
                leadDays: 2,
                time: DateComponents(hour: 20, minute: 0),
                permissionStatus: .authorized
            ),
            replacementRecords: [
                record(id: 1, replacedDaysAgo: 18, previousStartedDaysAgo: 40, memo: nil),
                record(id: 2, replacedDaysAgo: 40, previousStartedDaysAgo: 62, memo: nil)
            ],
            createdAt: daysAgo(62),
            updatedAt: daysAgo(1)
        ),
        ItemDetailConsumable(
            id: 3,
            name: "샤워기 필터",
            kindName: "필터",
            imageAssetName: "home_orb_shower_filter",
            spareQuantity: 2,
            replacementCycle: ItemDetailReplacementCycle(intervalDays: 90),
            currentCycleStartedAt: daysAgo(62),
            notification: ItemDetailNotificationSetting(
                isEnabled: false,
                permissionStatus: .notDetermined
            ),
            replacementRecords: [
                record(id: 1, replacedDaysAgo: 62, previousStartedDaysAgo: 153, memo: "수압 저하로 교체")
            ],
            createdAt: daysAgo(153),
            updatedAt: daysAgo(10)
        )
    ]

    static func consumable(id: Int) -> ItemDetailConsumable {
        consumables.first { $0.id == id } ?? consumables[0]
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
