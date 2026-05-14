import SwiftUI

struct HomeView: View {
    @StateObject private var viewModel: HomeViewModel

    let onNavigateConsumable: (ConsumableRoute) -> Void
    let onNavigateMyPage: (MyPageRoute) -> Void

    init(
        onNavigateConsumable: @escaping (ConsumableRoute) -> Void,
        onNavigateMyPage: @escaping (MyPageRoute) -> Void
    ) {
        _viewModel = StateObject(wrappedValue: HomeViewModel())
        self.onNavigateConsumable = onNavigateConsumable
        self.onNavigateMyPage = onNavigateMyPage
    }

    var body: some View {
        switch viewModel.state {
        case let .success(dashboard):
            HomeContentView(
                dashboard: dashboard,
                selectedStatusFilter: viewModel.selectedStatusFilter,
                statusFilterCounts: viewModel.statusFilterCounts,
                selectedWarningSort: viewModel.selectedWarningSort,
                visibleWarningItems: viewModel.visibleWarningItems,
                action: HomeViewAction(
                    onSearch: { onNavigateConsumable(.search) },
                    onNotification: {},
                    onProfile: { onNavigateMyPage(.myPage) },
                    onRegister: { onNavigateConsumable(.registrationMethod) },
                    onShowList: { onNavigateConsumable(.list) },
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
    let onRegister: () -> Void
    let onShowList: () -> Void
    let onSelectConsumable: (Int) -> Void
    let onSelectStatusFilter: (HomeStatusFilter) -> Void
    let onSelectWarningSort: (HomeWarningSort) -> Void
}
