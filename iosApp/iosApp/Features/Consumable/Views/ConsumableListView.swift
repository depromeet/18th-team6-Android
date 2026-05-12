import SwiftUI

struct ConsumableListTabView: View {
    let onNavigate: (ConsumableRoute) -> Void

    var body: some View {
        ConsumableListContentView(title: "리스트탭", subtitle: "메인 리스트 탭", onNavigate: onNavigate)
    }
}

struct ConsumableListView: View {
    let onNavigate: (ConsumableRoute) -> Void

    var body: some View {
        ConsumableListContentView(title: "전체 소모품 목록", subtitle: "전체 목록 화면", onNavigate: onNavigate)
    }
}

private struct ConsumableListContentView: View {
    let title: String
    let subtitle: String
    let onNavigate: (ConsumableRoute) -> Void

    var body: some View {
        RoutePlaceholderView(title: title, subtitle: subtitle) {
            NavigationActionButton("검색") {
                onNavigate(.search)
            }
            NavigationActionButton("필터") {
                onNavigate(.filter)
            }
            NavigationActionButton("정렬") {
                onNavigate(.sort)
            }
            NavigationActionButton("소모품 상세") {
                onNavigate(.detail(consumableId: 1))
            }
        }
    }
}
