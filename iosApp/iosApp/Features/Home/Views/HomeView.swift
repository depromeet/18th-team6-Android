import SwiftUI

struct HomeView: View {
    @StateObject private var viewModel: HomeViewModel

    let onNavigateConsumable: (ConsumableRoute) -> Void
    let onShowListTab: () -> Void
    let onBottomSheetVisibleChange: (Bool) -> Void

    init(
        onNavigateConsumable: @escaping (ConsumableRoute) -> Void,
        onShowListTab: @escaping () -> Void,
        onBottomSheetVisibleChange: @escaping (Bool) -> Void = { _ in }
    ) {
        _viewModel = StateObject(wrappedValue: HomeViewModel())
        self.onNavigateConsumable = onNavigateConsumable
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
                    onSearch: { onNavigateConsumable(.search) },
                    onNotification: {},
                    onProfile: {},
                    onRegisterDirect: { onNavigateConsumable(.manualRegistration) },
                    onShowList: onShowListTab,
                    onSelectConsumable: { onNavigateConsumable(.detail(consumableId: $0)) },
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
    let onSelectConsumable: (Int) -> Void
    let onSelectStatusFilter: (HomeStatusFilter) -> Void
    let onSelectWarningSort: (HomeWarningSort) -> Void
}
