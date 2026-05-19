import SwiftUI

struct MainNavigation: View {
    @State private var selectedTab: MainTab
    let onNavigateConsumable: (ConsumableRoute) -> Void
    let onNavigateMyPage: (MyPageRoute) -> Void

    init(
        selectedTab: MainTab,
        onNavigateConsumable: @escaping (ConsumableRoute) -> Void,
        onNavigateMyPage: @escaping (MyPageRoute) -> Void
    ) {
        _selectedTab = State(initialValue: selectedTab)
        self.onNavigateConsumable = onNavigateConsumable
        self.onNavigateMyPage = onNavigateMyPage
    }

    var body: some View {
        ZStack(alignment: .bottom) {
            selectedContent

            OBRitGnb(selected: selectedTab.gnbItem) { item in
                selectedTab = item.mainTab
            }
            .padding(.bottom, OBRitSpacing.s6)
        }
        .background(OBRitColors.backgroundDefaultDefault)
    }

    @ViewBuilder
    private var selectedContent: some View {
        switch selectedTab {
        case .home:
            HomeView(
                onNavigateConsumable: onNavigateConsumable,
                onNavigateMyPage: onNavigateMyPage,
                onShowListTab: {
                    selectedTab = .consumableList
                }
            )
        case .consumableList:
            HomeListTab(
                onNavigate: onNavigateConsumable,
                onNavigateMyPage: onNavigateMyPage
            )
        }
    }
}

private extension MainTab {
    var gnbItem: OBRitGnbItem {
        switch self {
        case .home:
            return .home
        case .consumableList:
            return .list
        }
    }
}

private extension OBRitGnbItem {
    var mainTab: MainTab {
        switch self {
        case .home:
            return .home
        case .list:
            return .consumableList
        }
    }
}
