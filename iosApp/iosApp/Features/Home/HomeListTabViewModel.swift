import Foundation

@MainActor
final class HomeListTabViewModel: ObservableObject {
    @Published private(set) var state: HomeListTabState = .loading

    private let repository: HomeListTabRepository
    private var filters: HomeListTabFilters = .empty
    private var draftFilters: HomeListTabFilters = .empty
    private var sortOption: HomeListTabSortOption = .replacementDueSoon
    private var bottomSheet: HomeListTabBottomSheet?
    private var isFilterBarVisible = true
    private var items: [HomeListTabItem] = []
    private var itemsForBounds: [HomeListTabItem] = []
    private var totalItemCount = 0
    private var nextCursor: Int64?
    private var hasNext = false
    private var isLoadingMore = false

    private let pageSize = 20

    init(
        repository: HomeListTabRepository = HomeListTabSampleRepository(),
        automaticallyLoads: Bool = true
    ) {
        self.repository = repository

        if automaticallyLoads {
            reload()
        }
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
        reload()
    }

    func resetDraftFilters() {
        draftFilters = HomeListTabFilters.empty.withDefaults(bounds: filterBounds)
        publish()
    }

    func clearReplacementDdayFilter() {
        filters.maxReplacementDday = nil
        reload()
    }

    func clearStockCountFilter() {
        filters.maxStockCount = nil
        reload()
    }

    func selectSortOption(_ option: HomeListTabSortOption) {
        sortOption = option
        bottomSheet = nil
        reload()
    }

    func loadNextPageIfNeeded(currentItemID: Int?) {
        guard let currentItemID else { return }
        guard items.last?.id == currentItemID, hasNext, !isLoadingMore else { return }

        isLoadingMore = true
        publish()

        Task {
            await loadNextPage()
        }
    }

    func setFilterBarVisible(_ visible: Bool) {
        guard isFilterBarVisible != visible else { return }
        isFilterBarVisible = visible
        publish()
    }

    func reload() {
        state = .loading

        Task {
            await loadFirstPage()
        }
    }

    private func publish() {
        state = .success(viewData)
    }

    private var viewData: HomeListTabViewData {
        return HomeListTabViewData(
            items: items,
            totalItemCount: totalItemCount,
            filters: filters,
            draftFilters: draftFilters,
            filterBounds: filterBounds,
            sortOption: sortOption,
            bottomSheet: bottomSheet,
            hasMore: hasNext,
            isLoadingMore: isLoadingMore,
            isFilterBarVisible: isFilterBarVisible
        )
    }

    private var filterBounds: HomeListTabFilterBounds {
        let sourceItems = itemsForBounds.isEmpty ? items : itemsForBounds

        return HomeListTabFilterBounds(
            minReplacementDday: sourceItems.map(\.replacementDday).min() ?? 0,
            maxReplacementDday: sourceItems.map(\.replacementDday).max() ?? 0,
            minStockCount: sourceItems.map(\.stockCount).min() ?? 0,
            maxStockCount: sourceItems.map(\.stockCount).max() ?? 0
        )
    }

    private func loadFirstPage() async {
        do {
            let page = try await repository.items(
                request: HomeListTabPageRequest(
                    filters: filters,
                    sortOption: sortOption,
                    cursor: nil,
                    size: pageSize
                )
            )
            items = page.items
            itemsForBounds = page.allItemsForBounds
            totalItemCount = page.totalItemCount
            nextCursor = page.nextCursor
            hasNext = page.hasNext
            isLoadingMore = false
            publish()
        } catch {
            items = []
            itemsForBounds = []
            totalItemCount = 0
            nextCursor = nil
            hasNext = false
            isLoadingMore = false
            state = .loadFailed
        }
    }

    private func loadNextPage() async {
        do {
            let page = try await repository.items(
                request: HomeListTabPageRequest(
                    filters: filters,
                    sortOption: sortOption,
                    cursor: nextCursor,
                    size: pageSize
                )
            )
            items.append(contentsOf: page.items)
            itemsForBounds = page.allItemsForBounds.isEmpty ? itemsForBounds : page.allItemsForBounds
            totalItemCount = page.totalItemCount
            nextCursor = page.nextCursor
            hasNext = page.hasNext
            isLoadingMore = false
            publish()
        } catch {
            isLoadingMore = false
            publish()
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
