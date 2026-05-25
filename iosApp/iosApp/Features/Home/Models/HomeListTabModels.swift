import Foundation

struct HomeListTabItem: Identifiable, Equatable {
    let id: Int
    let title: String
    let daysInUse: Int
    let stockCount: Int
    let replacementDday: Int
    let lastReplacementOrder: Int
    let cardLevel: OBRitCardLevel
    let assetName: String

    var daysInUseLabel: String {
        "\(daysInUse)일"
    }

    var replaceLabel: String {
        "교체 \(replacementDday.ddayText)"
    }

    var sparesLabel: String {
        "여분 \(stockCount)개"
    }
}

struct HomeListTabFilters: Equatable {
    var maxReplacementDday: Int?
    var maxStockCount: Int?

    static let empty = HomeListTabFilters(maxReplacementDday: nil, maxStockCount: nil)

    var isEmpty: Bool {
        maxReplacementDday == nil && maxStockCount == nil
    }
}

struct HomeListTabFilterBounds: Equatable {
    let minReplacementDday: Int
    let maxReplacementDday: Int
    let minStockCount: Int
    let maxStockCount: Int
}

enum HomeListTabSortOption: CaseIterable, Equatable {
    case replacementDueSoon
    case lowStock
    case oldestReplacement
    case alphabetical

    var title: String {
        switch self {
        case .replacementDueSoon:
            return "교체 임박 순"
        case .lowStock:
            return "여분 적은 순"
        case .oldestReplacement:
            return "교체 오래된 순"
        case .alphabetical:
            return "가나다 순"
        }
    }
}

enum HomeListTabBottomSheet: Equatable {
    case filter
    case sort
}

enum HomeListTabState: Equatable {
    case loading
    case loadFailed
    case success(HomeListTabViewData)
}

struct HomeListTabViewData: Equatable {
    let items: [HomeListTabItem]
    let totalItemCount: Int
    let filters: HomeListTabFilters
    let draftFilters: HomeListTabFilters
    let filterBounds: HomeListTabFilterBounds
    let sortOption: HomeListTabSortOption
    let bottomSheet: HomeListTabBottomSheet?
    let hasMore: Bool
    let isLoadingMore: Bool
    let isFilterBarVisible: Bool
}

struct HomeListTabPageRequest: Equatable {
    let filters: HomeListTabFilters
    let sortOption: HomeListTabSortOption
    let cursor: Int64?
    let size: Int
}

struct HomeListTabPage: Equatable {
    let items: [HomeListTabItem]
    let totalItemCount: Int
    let nextCursor: Int64?
    let hasNext: Bool
    let allItemsForBounds: [HomeListTabItem]
}

enum HomeListTabSampleData {
    static let items: [HomeListTabItem] = {
        let seedItems = [
            SeedItem("칫솔", 30, 0, -1, .l1, "item_toothbrush"),
            SeedItem("면도기 날", 18, 0, 2, .l2, "item_razor"),
            SeedItem("수세미", 30, 0, 0, .l3, "item_scrub_sponge"),
            SeedItem("샤워기 필터", 82, 1, 8, .l4, "item_shower_filter"),
            SeedItem("세탁 세제", 20, 1, 10, .l4, "item_detergent"),
            SeedItem("수건", 26, 4, 14, .l5, "item_towel"),
            SeedItem("디퓨저", 82, 2, 18, .l5, "item_diffuser"),
            SeedItem("지퍼백", 15, 5, 24, .l6, "item_zip_bag"),
            SeedItem("키친타월", 8, 2, 28, .l6, "item_kitchen_towel"),
            SeedItem("바디워시", 12, 1, 32, .l6, "item_body_wash"),
            SeedItem("주방 세제", 22, 0, -2, .l1, "item_dish_soap"),
            SeedItem("쓰레기 봉투", 3, 6, 27, .l6, "item_trash_bag")
        ]

        return (0 ..< 50).map { index in
            let seed = seedItems[index % seedItems.count]
            let batch = index / seedItems.count
            return HomeListTabItem(
                id: index + 1,
                title: seed.title,
                daysInUse: seed.daysInUse + batch * 2,
                stockCount: seed.stockCount,
                replacementDday: seed.replacementDday + batch,
                lastReplacementOrder: index,
                cardLevel: seed.cardLevel,
                assetName: seed.assetName
            )
        }
    }()
}

private struct SeedItem {
    let title: String
    let daysInUse: Int
    let stockCount: Int
    let replacementDday: Int
    let cardLevel: OBRitCardLevel
    let assetName: String

    init(
        _ title: String,
        _ daysInUse: Int,
        _ stockCount: Int,
        _ replacementDday: Int,
        _ cardLevel: OBRitCardLevel,
        _ assetName: String
    ) {
        self.title = title
        self.daysInUse = daysInUse
        self.stockCount = stockCount
        self.replacementDday = replacementDday
        self.cardLevel = cardLevel
        self.assetName = assetName
    }
}

extension Int {
    var ddayText: String {
        if self < 0 {
            return "D+\(abs(self))"
        }

        if self == 0 {
            return "D-day"
        }

        return "D-\(self)"
    }
}

extension HomeListTabSortOption {
    func sortsInAscendingOrder(_ lhs: HomeListTabItem, _ rhs: HomeListTabItem) -> Bool {
        switch self {
        case .replacementDueSoon:
            return lhs.replacementDday < rhs.replacementDday
        case .lowStock:
            if lhs.stockCount == rhs.stockCount {
                return lhs.replacementDday < rhs.replacementDday
            }
            return lhs.stockCount < rhs.stockCount
        case .oldestReplacement:
            return lhs.lastReplacementOrder > rhs.lastReplacementOrder
        case .alphabetical:
            return lhs.title.localizedStandardCompare(rhs.title) == .orderedAscending
        }
    }
}
