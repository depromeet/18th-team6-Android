import SwiftUI

struct RegistrationMethodView: View {
    let onNavigate: (ConsumableRoute) -> Void

    var body: some View {
        RoutePlaceholderView(title: "등록 방식 선택", subtitle: "소모품 등록 방식 분기") {
            NavigationActionButton("직접 등록") {
                onNavigate(.manualRegistration)
            }
            NavigationActionButton("영수증 등록") {
                onNavigate(.receiptCaptureOrUpload)
            }
        }
    }
}
