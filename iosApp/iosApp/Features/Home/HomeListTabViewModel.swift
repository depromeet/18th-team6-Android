import Foundation

@MainActor
final class HomeListTabViewModel: ObservableObject {
    @Published private(set) var state: HomeListTabState = .loading

    private let sourceItems: [HomeListTabItem]
    private var filters: HomeListTabFilters = .empty
    private var draftFilters: HomeListTabFilters = .empty
    private var sortOption: HomeListTabSortOption = .replacementDueSoon
    private var bottomSheet: HomeListTabBottomSheet?
    private var page = 1
    private var isFilterBarVisible = true

    private let pageSize = 20

    init(sourceItems: [HomeListTabItem] = HomeListTabSampleData.items) {
        self.sourceItems = sourceItems
        reload()
    }

    func openFilterSheet() {
        draftFilters = filters.withDefaults(bounds: filterBounds)
        bottomSheet = .filter
        publish()
    }

    func openSortSheet() {
        bottomSheet = .sort
        publish()
    }

    func dismissBottomSheet() {
        bottomSheet = nil
        publish()
    }

    func updateDraftReplacementDday(_ value: Double) {
        draftFilters.maxReplacementDday = Int(value.rounded())
        publish()
    }

    func updateDraftStockCount(_ value: Double) {
        draftFilters.maxStockCount = Int(value.rounded())
        publish()
    }

    func applyFilters() {
        filters = draftFilters.removingDefaults(bounds: filterBounds)
        bottomSheet = nil
        page = 1
        publish()
    }

    func resetDraftFilters() {
        draftFilters = HomeListTabFilters.empty.withDefaults(bounds: filterBounds)
        publish()
    }

    func clearReplacementDdayFilter() {
        filters.maxReplacementDday = nil
        page = 1
        publish()
    }

    func clearStockCountFilter() {
        filters.maxStockCount = nil
        page = 1
        publish()
    }

    func selectSortOption(_ option: HomeListTabSortOption) {
        sortOption = option
        bottomSheet = nil
        page = 1
        publish()
    }

    func loadNextPageIfNeeded(currentItemID: Int?) {
        guard let currentItemID else { return }
        let allItems = filteredAndSortedItems
        let visibleItems = Array(allItems.prefix(page * pageSize))
        guard visibleItems.last?.id == currentItemID, visibleItems.count < allItems.count else { return }
        page += 1
        publish()
    }

    func setFilterBarVisible(_ visible: Bool) {
        guard isFilterBarVisible != visible else { return }
        isFilterBarVisible = visible
        publish()
    }

    private func reload() {
        state = .success(viewData)
    }

    private func publish() {
        state = .success(viewData)
    }

    private var viewData: HomeListTabViewData {
        let allItems = filteredAndSortedItems
        let visibleItems = Array(allItems.prefix(page * pageSize))

        return HomeListTabViewData(
            items: visibleItems,
            totalItemCount: allItems.count,
            filters: filters,
            draftFilters: draftFilters,
            filterBounds: filterBounds,
            sortOption: sortOption,
            bottomSheet: bottomSheet,
            hasMore: visibleItems.count < allItems.count,
            isLoadingMore: false,
            isFilterBarVisible: isFilterBarVisible
        )
    }

    private var filteredAndSortedItems: [HomeListTabItem] {
        sourceItems
            .filter { item in
                let matchesDday = filters.maxReplacementDday.map { item.replacementDday <= $0 } ?? true
                let matchesStock = filters.maxStockCount.map { item.stockCount <= $0 } ?? true
                return matchesDday && matchesStock
            }
            .sorted(by: sortComparator)
    }

    private var filterBounds: HomeListTabFilterBounds {
        HomeListTabFilterBounds(
            minReplacementDday: sourceItems.map(\.replacementDday).min() ?? 0,
            maxReplacementDday: sourceItems.map(\.replacementDday).max() ?? 0,
            minStockCount: sourceItems.map(\.stockCount).min() ?? 0,
            maxStockCount: sourceItems.map(\.stockCount).max() ?? 0
        )
    }

    private func sortComparator(_ lhs: HomeListTabItem, _ rhs: HomeListTabItem) -> Bool {
        switch sortOption {
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

private extension HomeListTabFilters {
    func withDefaults(bounds: HomeListTabFilterBounds) -> HomeListTabFilters {
        HomeListTabFilters(
            maxReplacementDday: maxReplacementDday ?? bounds.maxReplacementDday,
            maxStockCount: maxStockCount ?? bounds.maxStockCount
        )
    }

    func removingDefaults(bounds: HomeListTabFilterBounds) -> HomeListTabFilters {
        HomeListTabFilters(
            maxReplacementDday: maxReplacementDday == bounds.maxReplacementDday ? nil : maxReplacementDday,
            maxStockCount: maxStockCount == bounds.maxStockCount ? nil : maxStockCount
        )
    }
}
