import SwiftUI

struct HomeListTab: View {
    @StateObject private var viewModel: HomeListTabViewModel

    let onNavigate: (ConsumableRoute) -> Void
    let onBottomSheetVisibleChange: (Bool) -> Void

    init(
        onNavigate: @escaping (ConsumableRoute) -> Void,
        onBottomSheetVisibleChange: @escaping (Bool) -> Void = { _ in }
    ) {
        _viewModel = StateObject(wrappedValue: HomeListTabViewModel())
        self.onNavigate = onNavigate
        self.onBottomSheetVisibleChange = onBottomSheetVisibleChange
    }

    var body: some View {
        HomeListTabContentView(
            state: viewModel.state,
            action: HomeListTabAction(
                onSearch: { onNavigate(.search) },
                onNotification: {},
                onProfile: {},
                onRegisterDirect: { onNavigate(.manualRegistration) },
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
        .onAppear {
            onBottomSheetVisibleChange(viewModel.state.isBottomSheetPresented)
        }
        .onChange(of: viewModel.state.isBottomSheetPresented) { _, isPresented in
            onBottomSheetVisibleChange(isPresented)
        }
        .onDisappear {
            onBottomSheetVisibleChange(false)
        }
    }
}

#Preview {
    HomeListTab(onNavigate: { _ in })
}

private extension HomeListTabState {
    var isBottomSheetPresented: Bool {
        guard case let .success(viewData) = self else { return false }
        return viewData.bottomSheet != nil
    }
}
