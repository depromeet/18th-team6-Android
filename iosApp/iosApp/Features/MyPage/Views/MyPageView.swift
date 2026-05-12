import SwiftUI

struct MyPageView: View {
    let onNavigate: (MyPageRoute) -> Void

    var body: some View {
        RoutePlaceholderView(title: "마이페이지", subtitle: "내 정보 및 설정") {
            NavigationActionButton("프로필") {
                onNavigate(.profile)
            }
            NavigationActionButton("계정 정보") {
                onNavigate(.accountInfo)
            }
            NavigationActionButton("알림 설정") {
                onNavigate(.notificationSettings)
            }
            NavigationActionButton("앱 정보") {
                onNavigate(.appInfo)
            }
            NavigationActionButton("로그아웃") {
                onNavigate(.logout)
            }
            NavigationActionButton("탈퇴") {
                onNavigate(.withdrawal)
            }
        }
    }
}
