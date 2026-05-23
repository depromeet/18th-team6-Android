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
        Group {
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
                SearchView(onBack: onBack) { itemId in
                    onNavigate(.detail(itemId: itemId))
                }
            case .filter:
                RoutePlaceholderView(title: "필터", subtitle: "소모품 목록 필터")
            case .sort:
                RoutePlaceholderView(title: "정렬", subtitle: "소모품 목록 정렬")
            case let .detail(itemId):
                ItemDetailView(itemId: itemId, onBack: onBack, onNavigate: onNavigate)
            case let .statusInfo(itemId):
                RoutePlaceholderView(title: "상태 정보", subtitle: "소모품 ID \(itemId)")
            case let .edit(itemId):
                ItemDetailEditRouteView(consumableId: itemId, onBack: onBack)
            case let .notification(itemId):
                RoutePlaceholderView(title: "알림", subtitle: "소모품 ID \(itemId) 알림 설정")
            case let .delete(itemId):
                RoutePlaceholderView(title: "삭제", subtitle: "소모품 ID \(itemId)")
            case let .spareEdit(itemId):
                ItemDetailSpareRouteView(consumableId: itemId, onBack: onBack)
            case let .replacementComplete(itemId):
                ItemDetailReplacementCompleteRouteView(consumableId: itemId, onBack: onBack)
            }
        }
        .itemRouteBackAction(onBack)
    }
}

private struct ItemRouteBackActionModifier: ViewModifier {
    let onBack: () -> Void

    func body(content: Content) -> some View {
        content
            .simultaneousGesture(edgeBackGesture)
            .accessibilityAction(.escape, onBack)
    }

    private var edgeBackGesture: some Gesture {
        DragGesture(minimumDistance: 24, coordinateSpace: .local)
            .onEnded { value in
                guard value.startLocation.x <= ItemRouteBackActionMetrics.edgeActivationWidth,
                      value.translation.width >= ItemRouteBackActionMetrics.minimumHorizontalDistance,
                      abs(value.translation.height) <= ItemRouteBackActionMetrics.maximumVerticalDistance else {
                    return
                }
                onBack()
            }
    }
}

private extension View {
    func itemRouteBackAction(_ onBack: @escaping () -> Void) -> some View {
        modifier(ItemRouteBackActionModifier(onBack: onBack))
    }
}

private enum ItemRouteBackActionMetrics {
    static let edgeActivationWidth: CGFloat = 32
    static let minimumHorizontalDistance: CGFloat = 80
    static let maximumVerticalDistance: CGFloat = 70
}
