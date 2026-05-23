import SwiftUI

struct OBRitNavigation: View {
    @State private var rootRoute: AppRoute
    @State private var selectedMainTab: MainTab
    @State private var path: NavigationPath

    init() {
        #if DEBUG
        let debugConfiguration = Self.debugInitialConfiguration
        let initialRootRoute = debugConfiguration.rootRoute
        let initialPath = debugConfiguration.path
        #else
        let initialRootRoute = AppRoute.main(.home)
        let initialPath = NavigationPath()
        #endif
        _rootRoute = State(initialValue: initialRootRoute)
        _selectedMainTab = State(initialValue: initialRootRoute.mainTab ?? .home)
        _path = State(initialValue: initialPath)
    }

    var body: some View {
        NavigationStack(path: $path) {
            AppNavigation.destination(
                for: rootRoute,
                selectedMainTab: selectedMainTab,
                onSetRoot: setRoot,
                onSelectMainTab: selectMainTab,
                onBack: popRoute,
                onNavigateApp: { navigate(to: $0) },
                onNavigateItem: { navigate(to: $0) }
            )
            .navigationBarBackButtonHidden(true)
            .toolbar(.hidden, for: .navigationBar)
            .navigationDestination(for: AppRoute.self) { route in
                AppNavigation.destination(
                    for: route,
                    selectedMainTab: selectedMainTab,
                    onSetRoot: setRoot,
                    onSelectMainTab: selectMainTab,
                    onBack: popRoute,
                    onNavigateApp: { navigate(to: $0) },
                    onNavigateItem: { navigate(to: $0) }
                )
                .navigationBarBackButtonHidden(true)
                .toolbar(.hidden, for: .navigationBar)
            }
            .navigationDestination(for: ItemRoute.self) { route in
                ItemNavigation.destination(
                    for: route,
                    onBack: popRoute,
                    onNavigate: { navigate(to: $0) },
                    onSetMainRoot: { setRoot(.main($0)) }
                )
                .navigationBarBackButtonHidden(true)
                .toolbar(.hidden, for: .navigationBar)
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

    private func navigate(to route: ItemRoute) {
        path.append(route)
    }

    private func popRoute() {
        guard !path.isEmpty else {
            setRoot(.main(.home))
            return
        }
        path.removeLast()
    }

    #if DEBUG
    private static var debugInitialConfiguration: (rootRoute: AppRoute, path: NavigationPath) {
        var initialPath = NavigationPath()
        let route = ProcessInfo.processInfo.environment["OBRIT_INITIAL_ROUTE"]

        switch route {
        case "onboarding":
            return (.onboarding, initialPath)
        case "registrationPrompt":
            return (.registrationPrompt, initialPath)
        case "initialItemRegistration":
            return (.initialItemRegistration, initialPath)
        case "registrationMethod":
            initialPath.append(ItemRoute.registrationMethod)
            return (.main(.home), initialPath)
        case "itemRegistration":
            initialPath.append(ItemRoute.itemRegistration)
            return (.main(.home), initialPath)
        default:
            return (.main(.home), initialPath)
        }
    }
    #endif
}

private extension AppRoute {
    var mainTab: MainTab? {
        guard case let .main(tab) = self else { return nil }
        return tab
    }
}
