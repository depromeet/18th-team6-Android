import SwiftUI

struct OBRitNavigation: View {
    @State private var rootRoute: AppRoute
    @State private var path = NavigationPath()

    init() {
        _rootRoute = State(initialValue: Self.initialRootRoute)
    }

    var body: some View {
        NavigationStack(path: $path) {
            AppNavigation.destination(
                for: rootRoute,
                onSetRoot: setRoot,
                onNavigateApp: { navigate(to: $0) },
                onNavigateConsumable: { navigate(to: $0) },
                onNavigateMyPage: { navigate(to: $0) }
            )
            .navigationDestination(for: AppRoute.self) { route in
                AppNavigation.destination(
                    for: route,
                    onSetRoot: setRoot,
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
        rootRoute = route
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
