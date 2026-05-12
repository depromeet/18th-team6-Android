import SwiftUI

struct HomeView: View {
    let onNavigateConsumable: (ConsumableRoute) -> Void
    let onNavigateMyPage: (MyPageRoute) -> Void

    var body: some View {
        RoutePlaceholderView(title: "홈탭", subtitle: "홈 진입 이후 기본 탭") {
            NavigationActionButton("마이페이지") {
                onNavigateMyPage(.myPage)
            }
            NavigationActionButton("+ 등록") {
                onNavigateConsumable(.registrationMethod)
            }
            NavigationActionButton("전체 소모품 목록") {
                onNavigateConsumable(.list)
            }
            NavigationActionButton("소모품 상세") {
                onNavigateConsumable(.detail(consumableId: 1))
            }
        }
    }
}
