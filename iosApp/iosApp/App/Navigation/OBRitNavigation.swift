import SwiftUI
import UIKit

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
        .background(OBRitNavigationBackground.swiftUIColor.ignoresSafeArea())
        .background(
            OBRitInteractivePopGestureEnabler(
                canPop: !path.isEmpty,
                backgroundColor: OBRitNavigationBackground.uiColor
            )
        )
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
        withAnimation(OBRitNavigationAnimation.slide) {
            path.append(route)
        }
    }

    private func navigate(to route: ItemRoute) {
        withAnimation(OBRitNavigationAnimation.slide) {
            path.append(route)
        }
    }

    private func popRoute() {
        guard !path.isEmpty else {
            setRoot(.main(.home))
            return
        }
        withAnimation(OBRitNavigationAnimation.slide) {
            path.removeLast()
        }
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

private enum OBRitNavigationAnimation {
    static let slide = Animation.easeInOut(duration: 0.28)
}

private enum OBRitNavigationBackground {
    static let swiftUIColor = OBRitColors.backgroundDefaultDefault

    static var uiColor: UIColor {
        UIColor(swiftUIColor)
    }
}

private struct OBRitInteractivePopGestureEnabler: UIViewControllerRepresentable {
    let canPop: Bool
    let backgroundColor: UIColor

    func makeCoordinator() -> Coordinator {
        Coordinator()
    }

    func makeUIViewController(context: Context) -> UIViewController {
        let viewController = UIViewController()
        viewController.view.backgroundColor = backgroundColor
        return viewController
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        uiViewController.view.backgroundColor = backgroundColor
        context.coordinator.configure(
            canPop: canPop,
            backgroundColor: backgroundColor,
            from: uiViewController
        )
    }

    final class Coordinator: NSObject, UIGestureRecognizerDelegate {
        private weak var navigationController: UINavigationController?
        private var canPop = false

        func configure(canPop: Bool, backgroundColor: UIColor, from viewController: UIViewController) {
            self.canPop = canPop

            DispatchQueue.main.async { [weak self, weak viewController] in
                guard let self,
                      let navigationController = viewController?.nearestNavigationController else {
                    return
                }

                self.navigationController = navigationController
                viewController?.view.window?.backgroundColor = backgroundColor
                navigationController.view.backgroundColor = backgroundColor
                navigationController.interactivePopGestureRecognizer?.isEnabled = true
                navigationController.interactivePopGestureRecognizer?.delegate = self
            }
        }

        func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
            canPop
        }
    }
}

private extension UIViewController {
    var nearestNavigationController: UINavigationController? {
        if let navigationController {
            return navigationController
        }

        if let parentNavigationController = parent?.nearestNavigationController {
            return parentNavigationController
        }

        guard let rootViewController = view.window?.rootViewController,
              rootViewController !== self else {
            return nil
        }

        return rootViewController.firstNavigationControllerInHierarchy
    }

    var firstNavigationControllerInHierarchy: UINavigationController? {
        if let navigationController = self as? UINavigationController {
            return navigationController
        }

        for child in children {
            if let navigationController = child.firstNavigationControllerInHierarchy {
                return navigationController
            }
        }

        return presentedViewController?.firstNavigationControllerInHierarchy
    }
}
