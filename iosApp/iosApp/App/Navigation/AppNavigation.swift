import SwiftUI

struct AppNavigation {
    @ViewBuilder
    static func destination(
        for route: AppRoute,
        onSetRoot: @escaping (AppRoute) -> Void,
        onNavigateApp: @escaping (AppRoute) -> Void,
        onNavigateConsumable: @escaping (ConsumableRoute) -> Void,
        onNavigateMyPage: @escaping (MyPageRoute) -> Void
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
            RoutePlaceholderView(title: "온보딩", subtitle: "초기 사용자 안내") {
                NavigationActionButton("등록 유도") {
                    onNavigateApp(.registrationPrompt)
                }
            }
        case .registrationPrompt:
            RoutePlaceholderView(title: "등록 유도", subtitle: "첫 소모품 등록 안내") {
                NavigationActionButton("소모품 등록") {
                    onNavigateApp(.initialConsumableRegistration)
                }
                NavigationActionButton("홈 진입") {
                    onSetRoot(.main(.home))
                }
            }
        case .initialConsumableRegistration:
            RoutePlaceholderView(title: "소모품 등록", subtitle: "초기 등록 플로우 진입") {
                NavigationActionButton("등록 방식 선택") {
                    onNavigateConsumable(.registrationMethod)
                }
                NavigationActionButton("홈 진입") {
                    onSetRoot(.main(.home))
                }
            }
        case let .main(selectedTab):
            MainNavigation(
                selectedTab: selectedTab,
                onNavigateConsumable: onNavigateConsumable,
                onNavigateMyPage: onNavigateMyPage
            )
            .navigationBarBackButtonHidden(true)
        }
    }
}
