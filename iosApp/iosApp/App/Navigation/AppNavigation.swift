import SwiftUI

struct AppNavigation {
    @ViewBuilder
    static func destination(
        for route: AppRoute,
        selectedMainTab: MainTab,
        onSetRoot: @escaping (AppRoute) -> Void,
        onSelectMainTab: @escaping (MainTab) -> Void,
        onBack: @escaping () -> Void,
        onNavigateApp: @escaping (AppRoute) -> Void,
        onNavigateConsumable: @escaping (ConsumableRoute) -> Void
    ) -> some View {
        switch route {
        case .splash:
            RoutePlaceholderView(title: "스플래시", subtitle: "앱 시작 진입점") {
                NavigationActionButton("앱 소개") {
                    onNavigateApp(.appIntro)
                }
            }
        case .appIntro:
            RoutePlaceholderView(title: "앱 소개", subtitle: "서비스 소개 플로우") {
                NavigationActionButton("온보딩") {
                    onNavigateApp(.onboarding)
                }
            }
        case .onboarding:
            OnboardingView {
                onNavigateApp(.registrationPrompt)
            }
        case .registrationPrompt:
            RegistrationPromptView(
                onRegister: {
                    onNavigateApp(.initialConsumableRegistration)
                },
                onSkip: {
                    onSetRoot(.main(.home))
                }
            )
        case .initialConsumableRegistration:
            RegistrationMethodView(
                onNavigate: onNavigateConsumable,
                onBack: onBack
            )
        case .main:
            MainNavigation(
                selectedTab: selectedMainTab,
                onNavigateConsumable: onNavigateConsumable,
                onSelectMainTab: onSelectMainTab
            )
            .navigationBarBackButtonHidden(true)
        }
    }
}
