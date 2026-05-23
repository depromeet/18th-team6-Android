import SwiftUI

struct ConsumableNavigation {
    @MainActor
    @ViewBuilder
    static func destination(
        for route: ConsumableRoute,
        onNavigate: @escaping (ConsumableRoute) -> Void,
        onSetMainRoot: @escaping (MainTab) -> Void
    ) -> some View {
        switch route {
        case .registrationMethod:
            RegistrationMethodView(onNavigate: onNavigate)
        case .manualRegistration:
            RoutePlaceholderView(title: "직접 등록", subtitle: "직접 등록 시작") {
                NavigationActionButton("상세 정보 입력") {
                    onNavigate(.manualDetailInput)
                }
            }
        case .manualDetailInput:
            RoutePlaceholderView(title: "상세 정보 입력", subtitle: "직접 등록 상세 정보") {
                NavigationActionButton("등록 완료") {
                    onNavigate(.registrationComplete)
                }
            }
        case .receiptCaptureOrUpload:
            RoutePlaceholderView(title: "촬영/업로드", subtitle: "영수증 이미지 입력") {
                NavigationActionButton("AI 인식·확인") {
                    onNavigate(.receiptRecognitionReview)
                }
            }
        case .receiptRecognitionReview:
            RoutePlaceholderView(title: "AI 인식·확인", subtitle: "인식 결과 검토") {
                NavigationActionButton("상세 정보 입력") {
                    onNavigate(.receiptDetailInput)
                }
            }
        case .receiptDetailInput:
            RoutePlaceholderView(title: "상세 정보 입력", subtitle: "영수증 등록 상세 정보") {
                NavigationActionButton("등록 완료") {
                    onNavigate(.registrationComplete)
                }
            }
        case .registrationComplete:
            RoutePlaceholderView(title: "등록 완료", subtitle: "소모품 등록 완료") {
                NavigationActionButton("홈 진입") {
                    onSetMainRoot(.home)
                }
                NavigationActionButton("전체 소모품 목록") {
                    onSetMainRoot(.homeListTab)
                }
            }
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
            ItemDetailEditRouteView(consumableId: consumableId)
        case let .delete(consumableId):
            RoutePlaceholderView(title: "삭제", subtitle: "소모품 ID \(consumableId)")
        case let .spareEdit(consumableId):
            ItemDetailSpareRouteView(consumableId: consumableId)
        case let .replacementComplete(consumableId):
            ItemDetailReplacementCompleteRouteView(consumableId: consumableId)
        }
    }
}
