import SwiftUI

struct MyPageNavigation {
    @ViewBuilder
    static func destination(
        for route: MyPageRoute,
        onNavigate: @escaping (MyPageRoute) -> Void
    ) -> some View {
        switch route {
        case .myPage:
            MyPageView(onNavigate: onNavigate)
        case .profile:
            RoutePlaceholderView(title: "프로필", subtitle: "내 정보")
        case .accountInfo:
            RoutePlaceholderView(title: "계정 정보", subtitle: "내 정보")
        case .notificationSettings:
            RoutePlaceholderView(title: "알림 설정", subtitle: "설정") {
                NavigationActionButton("여분 알림") {
                    onNavigate(.spareNotificationSettings)
                }
                NavigationActionButton("교체 알림") {
                    onNavigate(.replacementNotificationSettings)
                }
            }
        case .spareNotificationSettings:
            RoutePlaceholderView(title: "여분 알림", subtitle: "알림 설정")
        case .replacementNotificationSettings:
            RoutePlaceholderView(title: "교체 알림", subtitle: "알림 설정")
        case .appInfo:
            RoutePlaceholderView(title: "앱 정보", subtitle: "설정")
        case .logout:
            RoutePlaceholderView(title: "로그아웃", subtitle: "계정 액션")
        case .withdrawal:
            RoutePlaceholderView(title: "탈퇴", subtitle: "계정 액션")
        }
    }
}
