import SwiftUI

enum HomeSampleData {
    static let dashboard = HomeDashboard(
        summary: summary,
        warningItems: warningItems,
        usageItems: summaryItems
    )

    static var orbFallbackInteriorItems: [HomeOrbInteriorItem] {
        Array(summaryItems.prefix(HomeOrbVisualConfig.maxVisibleItemCount)).map(\.orbInteriorItem)
    }

    private static let summary = HomeSummary(
        items: summaryItems,
        averageStatusHistory: averageStatusHistory
    )

    private static let averageStatusHistory = [
        0.22, 0.34, 0.28, 0.46, 0.36, 0.58, 0.42, 0.62,
        0.54, 0.70, 0.50, 0.78, 0.60, 0.72, 0.48, 0.64,
        0.56, 0.68, 0.52, 0.74, 0.66, 0.86, 0.72, 0.92,
        0.80, 0.96, 0.76, 0.88, 0.82, 0.98, 0.84, 0.90
    ]

    private static let warningItems: [HomeItemItem] = [
        HomeItemItem(
            id: 1,
            title: "칫솔",
            daysInUse: 30,
            stockCount: 0,
            dDayLabel: "D-day",
            replaceLabel: "교체 D+0",
            sparesLabel: "여분 0개",
            cardLevel: .l1,
            imageColor: Color(red: 1.0, green: 0.37, blue: 0.16),
            orbAssetName: "item_toothbrush"
        ),
        HomeItemItem(
            id: 2,
            title: "면도기 날",
            daysInUse: 44,
            stockCount: 0,
            dDayLabel: "D+14",
            replaceLabel: "교체 D+14",
            sparesLabel: "여분 0개",
            cardLevel: .l2,
            imageColor: Color(red: 0.18, green: 0.20, blue: 0.24),
            orbAssetName: "item_razor"
        ),
        HomeItemItem(
            id: 3,
            title: "샤워기 필터",
            daysInUse: 82,
            stockCount: 1,
            dDayLabel: "D-3",
            replaceLabel: "교체 D-3",
            sparesLabel: "여분 1개",
            cardLevel: .l3,
            imageColor: Color(red: 0.30, green: 0.46, blue: 0.58),
            orbAssetName: "item_shower_filter"
        ),
        HomeItemItem(
            id: 4,
            title: "주방 세제",
            daysInUse: 22,
            stockCount: 0,
            dDayLabel: "D+2",
            replaceLabel: "교체 D+2",
            sparesLabel: "여분 0개",
            cardLevel: .l4,
            imageColor: Color(red: 0.93, green: 0.79, blue: 0.49),
            orbAssetName: "item_dish_soap"
        ),
        HomeItemItem(
            id: 5,
            title: "디퓨저",
            daysInUse: 82,
            stockCount: 2,
            dDayLabel: "D-8",
            replaceLabel: "교체 D-8",
            sparesLabel: "여분 2개",
            cardLevel: .l4,
            imageColor: Color(red: 0.80, green: 0.70, blue: 1.0),
            orbAssetName: "item_diffuser"
        ),
        HomeItemItem(
            id: 6,
            title: "세탁 세제",
            daysInUse: 20,
            stockCount: 1,
            dDayLabel: "D-10",
            replaceLabel: "교체 D-10",
            sparesLabel: "여분 1개",
            cardLevel: .l4,
            imageColor: Color(red: 0.56, green: 0.77, blue: 1.0),
            orbAssetName: "item_detergent"
        )
    ]

    private static let usageItems: [HomeItemItem] = [
        HomeItemItem(id: 5, title: "디퓨저", daysInUse: 82, stockCount: 2, dDayLabel: "D-8", replaceLabel: "교체 D-8", sparesLabel: "여분 2개", cardLevel: .l4, imageColor: Color(red: 0.80, green: 0.70, blue: 1.0), orbAssetName: "item_diffuser"),
        HomeItemItem(id: 6, title: "샤워기 필터", daysInUse: 82, stockCount: 1, dDayLabel: "D-8", replaceLabel: "교체 D-8", sparesLabel: "여분 1개", cardLevel: .l4, imageColor: Color(red: 0.30, green: 0.46, blue: 0.58), orbAssetName: "item_shower_filter"),
        HomeItemItem(id: 7, title: "칫솔", daysInUse: 30, stockCount: 0, dDayLabel: "D-day", replaceLabel: "교체 D+0", sparesLabel: "여분 0개", cardLevel: .l1, imageColor: Color(red: 1.0, green: 0.37, blue: 0.16), orbAssetName: "item_toothbrush"),
        HomeItemItem(id: 8, title: "수세미", daysInUse: 30, stockCount: 3, dDayLabel: "D-day", replaceLabel: "교체 D+0", sparesLabel: "여분 3개", cardLevel: .l4, imageColor: Color(red: 0.62, green: 0.84, blue: 0.72), orbAssetName: "item_scrub_sponge"),
        HomeItemItem(id: 9, title: "수건", daysInUse: 26, stockCount: 4, dDayLabel: "D-4", replaceLabel: "교체 D-4", sparesLabel: "여분 4개", cardLevel: .l5, imageColor: Color(red: 0.72, green: 0.86, blue: 1.0), orbAssetName: "item_towel"),
        HomeItemItem(id: 10, title: "주방 세제", daysInUse: 22, stockCount: 0, dDayLabel: "D+2", replaceLabel: "교체 D+2", sparesLabel: "여분 0개", cardLevel: .l1, imageColor: Color(red: 0.93, green: 0.79, blue: 0.49), orbAssetName: "item_dish_soap"),
        HomeItemItem(id: 11, title: "세탁 세제", daysInUse: 20, stockCount: 1, dDayLabel: "D-10", replaceLabel: "교체 D-10", sparesLabel: "여분 1개", cardLevel: .l4, imageColor: Color(red: 0.56, green: 0.77, blue: 1.0), orbAssetName: "item_detergent"),
        HomeItemItem(id: 12, title: "면도기 날", daysInUse: 18, stockCount: 0, dDayLabel: "D+14", replaceLabel: "교체 D+14", sparesLabel: "여분 0개", cardLevel: .l2, imageColor: Color(red: 0.18, green: 0.20, blue: 0.24), orbAssetName: "item_razor"),
        HomeItemItem(id: 13, title: "지퍼백", daysInUse: 15, stockCount: 5, dDayLabel: "D-20", replaceLabel: "교체 D-20", sparesLabel: "여분 5개", cardLevel: .l6, imageColor: Color(red: 0.74, green: 0.82, blue: 0.96), orbAssetName: "item_zip_bag"),
        HomeItemItem(id: 14, title: "바디워시", daysInUse: 12, stockCount: 1, dDayLabel: "D-18", replaceLabel: "교체 D-18", sparesLabel: "여분 1개", cardLevel: .l4, imageColor: Color(red: 0.81, green: 0.66, blue: 0.94), orbAssetName: "item_body_wash"),
        HomeItemItem(id: 15, title: "키친타월", daysInUse: 8, stockCount: 2, dDayLabel: "D-22", replaceLabel: "교체 D-22", sparesLabel: "여분 2개", cardLevel: .l5, imageColor: Color(red: 0.88, green: 0.88, blue: 0.80), orbAssetName: "item_kitchen_towel"),
        HomeItemItem(id: 16, title: "쓰레기 봉투", daysInUse: 3, stockCount: 6, dDayLabel: "D-27", replaceLabel: "교체 D-27", sparesLabel: "여분 6개", cardLevel: .l6, imageColor: Color(red: 0.55, green: 0.59, blue: 0.66), orbAssetName: "item_trash_bag")
    ]

    private static var summaryItems: [HomeItemItem] {
        Array(warningItems.prefix(4)) + usageItems
    }
}
