import SwiftUI

struct ItemDetailView: View {
    let itemId: Int
    let onNavigate: (ItemRoute) -> Void

    var body: some View {
        RoutePlaceholderView(title: "소모품 상세", subtitle: "소모품 ID \(itemId)") {
            NavigationActionButton("상태 정보") {
                onNavigate(.statusInfo(itemId: itemId))
            }
            NavigationActionButton("편집") {
                onNavigate(.edit(itemId: itemId))
            }
            NavigationActionButton("삭제") {
                onNavigate(.delete(itemId: itemId))
            }
            NavigationActionButton("여분 수정") {
                onNavigate(.spareEdit(itemId: itemId))
            }
            NavigationActionButton("교체 완료") {
                onNavigate(.replacementComplete(itemId: itemId))
            }
        }
    }
}
