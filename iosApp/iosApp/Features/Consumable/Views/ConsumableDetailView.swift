import SwiftUI

struct ConsumableDetailView: View {
    let consumableId: Int
    let onNavigate: (ConsumableRoute) -> Void

    var body: some View {
        RoutePlaceholderView(title: "소모품 상세", subtitle: "소모품 ID \(consumableId)") {
            NavigationActionButton("상태 정보") {
                onNavigate(.statusInfo(consumableId: consumableId))
            }
            NavigationActionButton("편집") {
                onNavigate(.edit(consumableId: consumableId))
            }
            NavigationActionButton("삭제") {
                onNavigate(.delete(consumableId: consumableId))
            }
            NavigationActionButton("여분 수정") {
                onNavigate(.spareEdit(consumableId: consumableId))
            }
            NavigationActionButton("교체 완료") {
                onNavigate(.replacementComplete(consumableId: consumableId))
            }
        }
    }
}
