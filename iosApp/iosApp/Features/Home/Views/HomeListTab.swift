import SwiftUI

struct HomeListTab: View {
    @StateObject private var viewModel: HomeListTabViewModel

    let onNavigate: (ItemRoute) -> Void
    let onBottomSheetVisibleChange: (Bool) -> Void

    @MainActor
    init(
        viewModelFactory: @MainActor @escaping () -> HomeListTabViewModel,
        onNavigate: @escaping (ItemRoute) -> Void,
        onBottomSheetVisibleChange: @escaping (Bool) -> Void = { _ in }
    ) {
        _viewModel = StateObject(wrappedValue: viewModelFactory())
        self.onNavigate = onNavigate
        self.onBottomSheetVisibleChange = onBottomSheetVisibleChange
    }

    @MainActor
    init(
        onNavigate: @escaping (ItemRoute) -> Void,
        onBottomSheetVisibleChange: @escaping (Bool) -> Void = { _ in }
    ) {
        self.init(
            viewModelFactory: AppDependencies.preview.makeHomeListTabViewModel,
            onNavigate: onNavigate,
            onBottomSheetVisibleChange: onBottomSheetVisibleChange
        )
    }

    var body: some View {
        HomeListTabContentView(
            state: viewModel.state,
            action: HomeListTabAction(
                onSearch: { onNavigate(.search) },
                onNotification: {},
                onProfile: {},
                onRegisterDirect: { onNavigate(.itemRegistration) },
                onSelectItem: { onNavigate(.detail(itemId: $0)) },
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
