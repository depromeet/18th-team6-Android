import SwiftUI

struct ConsumableNavigation {
    @MainActor
    @ViewBuilder
    static func destination(
        for route: ConsumableRoute,
        onBack: @escaping () -> Void,
        onNavigate: @escaping (ConsumableRoute) -> Void,
        onSetMainRoot: @escaping (MainTab) -> Void
    ) -> some View {
        switch route {
        case .registrationMethod:
            RegistrationMethodView(
                onNavigate: onNavigate,
                onBack: onBack
            )
        case .manualRegistration:
            ManualRegistrationView(
                onBack: onBack,
                onClose: {
                    onSetMainRoot(.home)
                },
                onComplete: {
                    onSetMainRoot(.home)
                }
            )
        case .manualDetailInput:
            ManualRegistrationView(
                onBack: onBack,
                onClose: {
                    onSetMainRoot(.home)
                },
                onComplete: {
                    onSetMainRoot(.home)
                }
            )
        case .search:
            SearchView { consumableId in
                onNavigate(.detail(consumableId: consumableId))
            }
        case .filter:
            RoutePlaceholderView(title: "필터", subtitle: "소모품 목록 필터")
        case .sort:
            RoutePlaceholderView(title: "정렬", subtitle: "소모품 목록 정렬")
        case let .detail(consumableId):
            ConsumableDetailView(consumableId: consumableId, onNavigate: onNavigate)
        case let .statusInfo(consumableId):
            RoutePlaceholderView(title: "상태 정보", subtitle: "소모품 ID \(consumableId)")
        case let .edit(consumableId):
            RoutePlaceholderView(title: "편집", subtitle: "소모품 ID \(consumableId)")
        case let .delete(consumableId):
            RoutePlaceholderView(title: "삭제", subtitle: "소모품 ID \(consumableId)")
        case let .spareEdit(consumableId):
            RoutePlaceholderView(title: "여분 수정", subtitle: "소모품 ID \(consumableId)")
        case let .replacementComplete(consumableId):
            RoutePlaceholderView(title: "교체 완료", subtitle: "소모품 ID \(consumableId)")
        }
    }
}
