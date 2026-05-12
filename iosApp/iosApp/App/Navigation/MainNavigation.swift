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
        TabView(selection: $selectedTab) {
            HomeView(
                onNavigateConsumable: onNavigateConsumable,
                onNavigateMyPage: onNavigateMyPage
            )
            .tabItem {
                Label("홈", systemImage: "house")
            }
            .tag(MainTab.home)

            ConsumableListTabView(onNavigate: onNavigateConsumable)
                .tabItem {
                    Label("리스트", systemImage: "list.bullet")
                }
                .tag(MainTab.consumableList)
        }
    }
}
