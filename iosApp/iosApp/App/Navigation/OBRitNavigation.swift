import SwiftUI

struct OBRitNavigation: View {
    @State private var rootRoute: AppRoute
    @State private var selectedMainTab: MainTab
    @State private var path = NavigationPath()

    init() {
        let initialRootRoute = Self.initialRootRoute
        _rootRoute = State(initialValue: initialRootRoute)
        _selectedMainTab = State(initialValue: initialRootRoute.mainTab ?? .home)
    }

    var body: some View {
        NavigationStack(path: $path) {
            AppNavigation.destination(
                for: rootRoute,
                selectedMainTab: selectedMainTab,
                onSetRoot: setRoot,
                onSelectMainTab: selectMainTab,
                onNavigateApp: { navigate(to: $0) },
                onNavigateConsumable: { navigate(to: $0) },
                onNavigateMyPage: { navigate(to: $0) }
            )
            .navigationDestination(for: AppRoute.self) { route in
                AppNavigation.destination(
                    for: route,
                    selectedMainTab: selectedMainTab,
                    onSetRoot: setRoot,
                    onSelectMainTab: selectMainTab,
                    onNavigateApp: { navigate(to: $0) },
                    onNavigateConsumable: { navigate(to: $0) },
                    onNavigateMyPage: { navigate(to: $0) }
                )
            }
            .navigationDestination(for: ConsumableRoute.self) { route in
                ConsumableNavigation.destination(
                    for: route,
                    onNavigate: { navigate(to: $0) },
                    onSetMainRoot: { setRoot(.main($0)) }
                )
            }
            .navigationDestination(for: MyPageRoute.self) { route in
                MyPageNavigation.destination(
                    for: route,
                    onNavigate: { navigate(to: $0) }
                )
            }
        }
    }

    private func setRoot(_ route: AppRoute) {
        path = NavigationPath()
        if let mainTab = route.mainTab {
            selectedMainTab = mainTab
        }
        rootRoute = route
    }

    private func selectMainTab(_ tab: MainTab) {
        path = NavigationPath()
        selectedMainTab = tab
    }

    private func navigate(to route: AppRoute) {
        path.append(route)
    }

    private func navigate(to route: ConsumableRoute) {
        path.append(route)
    }

    private func navigate(to route: MyPageRoute) {
        path.append(route)
    }

    private static var initialRootRoute: AppRoute {
        .main(.home)
    }
}

private extension AppRoute {
    var mainTab: MainTab? {
        guard case let .main(tab) = self else { return nil }
        return tab
    }
}
