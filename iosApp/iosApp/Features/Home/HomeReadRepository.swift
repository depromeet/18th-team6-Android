import Foundation
import Shared
import SwiftUI

protocol HomeDashboardRepository {
    func dashboard() async throws -> HomeDashboard
}

protocol HomeListTabRepository {
    func items(request: HomeListTabPageRequest) async throws -> HomeListTabPage
}

enum HomeReadRepositoryError: LocalizedError, Equatable {
    case loadFailed

    var errorDescription: String? {
        switch self {
        case .loadFailed:
            return "홈 정보를 불러오지 못했어요."
        }
    }
}

actor HomeSampleDashboardRepository: HomeDashboardRepository {
    private let dashboardValue: HomeDashboard

    init(dashboard: HomeDashboard = HomeSampleData.dashboard) {
        self.dashboardValue = dashboard
    }

    func dashboard() async throws -> HomeDashboard {
        dashboardValue
    }
}

actor SharedHomeDashboardRepository: HomeDashboardRepository {
    private let readService: SharedReadService
    private let pageSize: Int32

    init(readService: SharedReadService, pageSize: Int32 = 20) {
        self.readService = readService
        self.pageSize = pageSize
    }

    func dashboard() async throws -> HomeDashboard {
        let event = "SharedHomeDashboardRepository.dashboard"
        AppLog.enter(AppLog.swiftRepository, event, "pageSize=\(pageSize)")
        do {
            let mySummary = try await readService.getMyStatusSummary()
            if mySummary.totalCount == 0 {
                let dashboard = HomeDashboard(
                    summary: SharedHomeReadMapper.emptySummary(summary: mySummary),
                    warningItems: [],
                    usageItems: []
                )
                AppLog.success(AppLog.swiftRepository, event, "warningCount=0 totalCount=0 skipped=empty")
                return dashboard
            }

            async let overallStatus = readService.getOverallStatus()
            async let itemsSlice = readService.getHomeItems(
                params: HomeItemsParams(
                    order: HomeItemOrder.replacementUrgent,
                    dDay: nil,
                    spareQuantity: nil,
                    cursor: nil,
                    size: KotlinInt(int: pageSize)
                )
            )

            let (status, slice) = try await (overallStatus, itemsSlice)
            let warningItems = slice.content.map(SharedHomeReadMapper.homeItem)

            let dashboard = HomeDashboard(
                summary: SharedHomeReadMapper.summary(status: status, summary: mySummary),
                warningItems: warningItems,
                usageItems: warningItems.sorted { $0.daysInUse > $1.daysInUse }
            )
            AppLog.success(AppLog.swiftRepository, event, "warningCount=\(warningItems.count)")
            return dashboard
        } catch {
            AppLog.failure(AppLog.swiftRepository, event, error)
            throw error
        }
    }
}

actor HomeListTabSampleRepository: HomeListTabRepository {
    private let sourceItems: [HomeListTabItem]

    init(items: [HomeListTabItem] = HomeListTabSampleData.items) {
        self.sourceItems = items
    }

    func items(request: HomeListTabPageRequest) async throws -> HomeListTabPage {
        let filteredItems = sourceItems
            .filter { item in
                let matchesDday = request.filters.maxReplacementDday.map { item.replacementDday <= $0 } ?? true
                let matchesStock = request.filters.maxStockCount.map { item.stockCount <= $0 } ?? true
                return matchesDday && matchesStock
            }
            .sorted { lhs, rhs in
                request.sortOption.sortsInAscendingOrder(lhs, rhs)
            }

        let startIndex = Int(request.cursor ?? 0)
        guard startIndex < filteredItems.count else {
            return HomeListTabPage(
                items: [],
                totalItemCount: filteredItems.count,
                nextCursor: nil,
                hasNext: false,
                allItemsForBounds: filteredItems
            )
        }

        let endIndex = min(startIndex + request.size, filteredItems.count)
        let pageItems = Array(filteredItems[startIndex ..< endIndex])
        let nextCursor = endIndex < filteredItems.count ? Int64(endIndex) : nil

        return HomeListTabPage(
            items: pageItems,
            totalItemCount: filteredItems.count,
            nextCursor: nextCursor,
            hasNext: nextCursor != nil,
            allItemsForBounds: filteredItems
        )
    }
}

actor SharedHomeListTabRepository: HomeListTabRepository {
    private let readService: SharedReadService
    private let metadataPageSize: Int32
    private var cachedMetadata: HomeListTabMetadata?

    init(readService: SharedReadService, metadataPageSize: Int32 = 100) {
        self.readService = readService
        self.metadataPageSize = metadataPageSize
    }

    func items(request: HomeListTabPageRequest) async throws -> HomeListTabPage {
        let event = "SharedHomeListTabRepository.items"
        let details = "cursor=\(String(describing: request.cursor)) size=\(request.size) sort=\(request.sortOption)"
        AppLog.enter(AppLog.swiftRepository, event, details)
        do {
            let slice = try await readService.getHomeItems(
                params: HomeItemsParams(
                    order: request.sortOption.homeItemOrder,
                    dDay: request.filters.maxReplacementDday.kotlinInt,
                    spareQuantity: request.filters.maxStockCount.kotlinInt,
                    cursor: request.cursor.kotlinLong,
                    size: KotlinInt(int: Int32(request.size))
                )
            )

            let items = slice.content
                .map(SharedHomeReadMapper.homeListItem)
                .sorted { lhs, rhs in
                    request.sortOption.sortsInAscendingOrder(lhs, rhs)
                }
            let allItemsForBounds = try await metadataItems(for: request, firstSlice: request.cursor == nil ? slice : nil)

            let page = HomeListTabPage(
                items: items,
                totalItemCount: allItemsForBounds.count,
                nextCursor: slice.nextCursor?.int64Value,
                hasNext: slice.hasNext,
                allItemsForBounds: allItemsForBounds
            )
            AppLog.success(
                AppLog.swiftRepository,
                event,
                "\(details) items=\(items.count) total=\(page.totalItemCount) nextCursor=\(String(describing: page.nextCursor)) hasNext=\(page.hasNext)"
            )
            return page
        } catch {
            AppLog.failure(AppLog.swiftRepository, event, error, details)
            throw error
        }
    }

    private func metadataItems(
        for request: HomeListTabPageRequest,
        firstSlice: HomeItemCursorSlice?
    ) async throws -> [HomeListTabItem] {
        if let cachedMetadata,
           request.cursor != nil,
           cachedMetadata.matches(filters: request.filters, sortOption: request.sortOption) {
            return cachedMetadata.items
        }

        let items = try await loadMetadataItems(for: request, firstSlice: firstSlice)
        cachedMetadata = HomeListTabMetadata(
            filters: request.filters,
            sortOption: request.sortOption,
            items: items
        )
        return items
    }

    private func loadMetadataItems(
        for request: HomeListTabPageRequest,
        firstSlice: HomeItemCursorSlice?
    ) async throws -> [HomeListTabItem] {
        var items = firstSlice?.content.map(SharedHomeReadMapper.homeListItem) ?? []
        var cursor = firstSlice?.nextCursor?.int64Value
        var hasNext = firstSlice?.hasNext ?? true
        var previousCursor: Int64?

        while hasNext {
            if cursor == nil, !items.isEmpty || firstSlice != nil {
                AppLog.failure(
                    AppLog.swiftRepository,
                    "SharedHomeListTabRepository.loadMetadataItems",
                    HomeListTabMetadataError.missingCursor
                )
                break
            }

            if let cursor, cursor == previousCursor {
                AppLog.failure(
                    AppLog.swiftRepository,
                    "SharedHomeListTabRepository.loadMetadataItems",
                    HomeListTabMetadataError.repeatedCursor,
                    "cursor=\(cursor)"
                )
                break
            }

            previousCursor = cursor
            let slice = try await readService.getHomeItems(
                params: HomeItemsParams(
                    order: request.sortOption.homeItemOrder,
                    dDay: request.filters.maxReplacementDday.kotlinInt,
                    spareQuantity: request.filters.maxStockCount.kotlinInt,
                    cursor: cursor.map { KotlinLong(longLong: $0) },
                    size: KotlinInt(int: metadataPageSize)
                )
            )

            items.append(contentsOf: slice.content.map(SharedHomeReadMapper.homeListItem))
            cursor = slice.nextCursor?.int64Value
            hasNext = slice.hasNext
        }

        return items.sorted { lhs, rhs in
            request.sortOption.sortsInAscendingOrder(lhs, rhs)
        }
    }
}

private struct HomeListTabMetadata {
    let filters: HomeListTabFilters
    let sortOption: HomeListTabSortOption
    let items: [HomeListTabItem]

    func matches(filters: HomeListTabFilters, sortOption: HomeListTabSortOption) -> Bool {
        self.filters == filters && self.sortOption == sortOption
    }
}

private enum HomeListTabMetadataError: LocalizedError {
    case missingCursor
    case repeatedCursor

    var errorDescription: String? {
        switch self {
        case .missingCursor:
            return "홈 목록 다음 페이지 커서가 비어 있어 전체 범위 계산을 멈췄어요."
        case .repeatedCursor:
            return "홈 목록 전체 범위를 계산하는 중 같은 커서가 반복됐어요."
        }
    }
}

private enum SharedHomeReadMapper {
    static func emptySummary(summary: MyStatusSummary) -> HomeSummary {
        let ownPercent = normalizedPercent(summary.score)
        let averagePercent = normalizedPercent(summary.averageScore)
        let positiveRatio = Int((ownPercent * 100).rounded())

        return HomeSummary(
            status: "",
            replacementStatus: "",
            stockStatus: "",
            positiveRatio: positiveRatio,
            warningRatio: 100 - positiveRatio,
            totalCount: Int(summary.totalCount),
            warningCount: Int(summary.needReplaceCount),
            history: [averagePercent],
            ownStatusPercent: ownPercent,
            averageStatusPercent: averagePercent
        )
    }

    static func summary(status: HomeOverallStatus, summary: MyStatusSummary) -> HomeSummary {
        let ownPercent = normalizedPercent(summary.score)
        let averagePercent = normalizedPercent(summary.averageScore)
        let positiveRatio = Int((ownPercent * 100).rounded())

        return HomeSummary(
            status: status.overall.localizedTitle,
            replacementStatus: status.replacement.localizedTitle,
            stockStatus: status.spare.localizedTitle,
            positiveRatio: positiveRatio,
            warningRatio: 100 - positiveRatio,
            totalCount: Int(summary.totalCount),
            warningCount: Int(summary.needReplaceCount),
            history: [averagePercent],
            ownStatusPercent: ownPercent,
            averageStatusPercent: averagePercent
        )
    }

    static func homeItem(_ item: HomeItemCard) -> HomeItemItem {
        return HomeItemItem(
            id: Int(clamping: item.itemId),
            title: item.name,
            daysInUse: Int(item.daysInUse),
            stockCount: Int(item.spareQuantity),
            dDayLabel: item.replacementDday,
            replaceLabel: "교체 \(item.replacementDday)",
            sparesLabel: "여분 \(item.spareQuantity)개",
            cardLevel: cardLevel(itemBucket: item.itemBucket),
            imageColor: Color.clear,
            orbAssetName: OBRitSharedAssetMapper.homeOrbAssetName(for: item.name)
        )
    }

    static func homeListItem(_ item: HomeItemCard) -> HomeListTabItem {
        let replacementDday = ddayValue(from: item.replacementDday)

        return HomeListTabItem(
            id: Int(clamping: item.itemId),
            title: item.name,
            daysInUse: Int(item.daysInUse),
            stockCount: Int(item.spareQuantity),
            replacementDday: replacementDday,
            lastReplacementOrder: Int(item.daysInUse),
            cardLevel: cardLevel(itemBucket: item.itemBucket),
            assetName: OBRitSharedAssetMapper.itemAssetName(for: item.name)
        )
    }

    private static func ddayValue(from label: String) -> Int {
        if label.localizedCaseInsensitiveContains("day") {
            return 0
        }

        if label.hasPrefix("D+"), let days = Int(label.dropFirst(2)) {
            return -days
        }

        if label.hasPrefix("D-"), let days = Int(label.dropFirst(2)) {
            return days
        }

        return Int(label.filter { $0.isNumber || $0 == "-" }) ?? 0
    }

    private static func cardLevel(itemBucket: ItemBucket) -> OBRitCardLevel {
        if itemBucket == .noneOverdue {
            return .l1
        }

        if itemBucket == .noneWarn {
            return .l2
        }

        if itemBucket == .hasOverdue {
            return .l3
        }

        if itemBucket == .hasWarn {
            return .l4
        }

        if itemBucket == .noneSafe {
            return .l5
        }

        return .l6
    }

    private static func normalizedPercent(_ value: Double) -> Double {
        let percent = value > 1 ? value / 100 : value
        return min(max(percent, 0), 1)
    }
}

private extension HomeOverallLevel {
    var localizedTitle: String {
        switch name {
        case "PERFECT":
            return "완벽"
        case "GOOD":
            return "양호"
        case "WARNING":
            return "경고"
        case "DANGER":
            return "위험"
        default:
            return "알 수 없음"
        }
    }
}

private extension HomeStatusLevel {
    var localizedTitle: String {
        switch name {
        case "GOOD":
            return "양호"
        case "WARNING":
            return "경고"
        case "DANGER":
            return "위험"
        default:
            return "알 수 없음"
        }
    }
}

private extension HomeListTabSortOption {
    var homeItemOrder: HomeItemOrder? {
        switch self {
        case .replacementDueSoon:
            return .replacementUrgent
        case .lowStock:
            return .spareLow
        case .oldestReplacement:
            return .usedOld
        case .alphabetical:
            return nil
        }
    }
}

private extension Optional where Wrapped == Int {
    var kotlinInt: KotlinInt? {
        map { KotlinInt(int: Int32($0)) }
    }
}

private extension Optional where Wrapped == Int64 {
    var kotlinLong: KotlinLong? {
        map { KotlinLong(longLong: $0) }
    }
}
