import SwiftUI

struct HomeListTab: View {
    @StateObject private var viewModel: HomeListTabViewModel

    let onNavigate: (ConsumableRoute) -> Void
    let onNavigateMyPage: (MyPageRoute) -> Void

    init(
        onNavigate: @escaping (ConsumableRoute) -> Void,
        onNavigateMyPage: @escaping (MyPageRoute) -> Void = { _ in }
    ) {
        _viewModel = StateObject(wrappedValue: HomeListTabViewModel())
        self.onNavigate = onNavigate
        self.onNavigateMyPage = onNavigateMyPage
    }

    var body: some View {
        HomeListTabContentView(
            state: viewModel.state,
            action: HomeListTabAction(
                onSearch: { onNavigate(.search) },
                onNotification: {},
                onProfile: { onNavigateMyPage(.myPage) },
                onRegister: { onNavigate(.registrationMethod) },
                onSelectConsumable: { onNavigate(.detail(consumableId: $0)) },
                onOpenFilterSheet: viewModel.openFilterSheet,
                onOpenSortSheet: viewModel.openSortSheet,
                onDismissBottomSheet: viewModel.dismissBottomSheet,
                onUpdateDraftReplacementDday: viewModel.updateDraftReplacementDday,
                onUpdateDraftStockCount: viewModel.updateDraftStockCount,
                onApplyFilters: viewModel.applyFilters,
                onResetDraftFilters: viewModel.resetDraftFilters,
                onClearReplacementDdayFilter: viewModel.clearReplacementDdayFilter,
                onClearStockCountFilter: viewModel.clearStockCountFilter,
                onSelectSortOption: viewModel.selectSortOption,
                onLoadNextPageIfNeeded: viewModel.loadNextPageIfNeeded,
                onFilterBarVisibleChange: viewModel.setFilterBarVisible
            )
        )
    }
}

#Preview {
    HomeListTab(onNavigate: { _ in })
}
