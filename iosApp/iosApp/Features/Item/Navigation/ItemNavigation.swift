import SwiftUI

struct ItemNavigation {
    @MainActor
    @ViewBuilder
    static func destination(
        for route: ItemRoute,
        onBack: @escaping () -> Void,
        onNavigate: @escaping (ItemRoute) -> Void,
        onSetMainRoot: @escaping (MainTab) -> Void
    ) -> some View {
        switch route {
        case .registrationMethod:
            RegistrationMethodView(
                onNavigate: onNavigate,
                onBack: onBack
            )
        case .itemRegistration:
            ItemRegistrationView(
                onBack: onBack,
                onClose: {
                    onSetMainRoot(.home)
                },
                onComplete: {
                    onSetMainRoot(.home)
                }
            )
        case .itemDetailInput:
            ItemRegistrationView(
                onBack: onBack,
                onClose: {
                    onSetMainRoot(.home)
                },
                onComplete: {
                    onSetMainRoot(.home)
                }
            )
        case .search:
            SearchView { itemId in
                onNavigate(.detail(itemId: itemId))
            }
        case .filter:
            RoutePlaceholderView(title: "필터", subtitle: "소모품 목록 필터")
        case .sort:
            RoutePlaceholderView(title: "정렬", subtitle: "소모품 목록 정렬")
        case let .detail(itemId):
            ItemDetailView(itemId: itemId, onNavigate: onNavigate)
        case let .statusInfo(itemId):
            RoutePlaceholderView(title: "상태 정보", subtitle: "소모품 ID \(itemId)")
        case let .edit(itemId):
            ItemDetailEditRouteView(consumableId: itemId)
        case let .delete(itemId):
            RoutePlaceholderView(title: "삭제", subtitle: "소모품 ID \(itemId)")
        case let .spareEdit(itemId):
            ItemDetailSpareRouteView(consumableId: itemId)
        case let .replacementComplete(itemId):
            ItemDetailReplacementCompleteRouteView(consumableId: itemId)
        }
    }
}
