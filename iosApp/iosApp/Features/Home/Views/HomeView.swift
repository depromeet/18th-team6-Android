import SwiftUI

struct HomeView: View {
    @StateObject private var viewModel: HomeViewModel

    let onNavigateItem: (ItemRoute) -> Void
    let onShowListTab: () -> Void
    let onBottomSheetVisibleChange: (Bool) -> Void

    @MainActor
    init(
        viewModelFactory: @MainActor @escaping () -> HomeViewModel,
        onNavigateItem: @escaping (ItemRoute) -> Void,
        onShowListTab: @escaping () -> Void,
        onBottomSheetVisibleChange: @escaping (Bool) -> Void = { _ in }
    ) {
        _viewModel = StateObject(wrappedValue: viewModelFactory())
        self.onNavigateItem = onNavigateItem
        self.onShowListTab = onShowListTab
        self.onBottomSheetVisibleChange = onBottomSheetVisibleChange
    }

    var body: some View {
        switch viewModel.state {
        case let .success(dashboard):
            HomeContentView(
                dashboard: dashboard,
                selectedStatusFilter: viewModel.selectedStatusFilter,
                statusFilterCounts: viewModel.statusFilterCounts,
                selectedWarningSort: viewModel.selectedWarningSort,
                visibleQuickItems: viewModel.visibleQuickItems,
                visibleWarningItems: viewModel.visibleWarningItems,
                onBottomSheetVisibleChange: onBottomSheetVisibleChange,
                action: HomeViewAction(
                    onSearch: { onNavigateItem(.search) },
                    onNotification: {},
                    onProfile: {},
                    onRegisterDirect: { onNavigateItem(.itemRegistration) },
                    onShowList: onShowListTab,
                    onSelectItem: { onNavigateItem(.detail(itemId: $0)) },
                    onSelectStatusFilter: viewModel.selectStatusFilter,
                    onSelectWarningSort: viewModel.selectWarningSort
                )
            )
        }
    }
}

struct HomeViewAction {
    let onSearch: () -> Void
    let onNotification: () -> Void
    let onProfile: () -> Void
    let onRegisterDirect: () -> Void
    let onShowList: () -> Void
    let onSelectItem: (Int) -> Void
    let onSelectStatusFilter: (HomeStatusFilter) -> Void
    let onSelectWarningSort: (HomeWarningSort) -> Void
}
