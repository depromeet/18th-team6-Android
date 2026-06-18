import SwiftUI

struct HomeRegistrationFloatingActionMenu: View {
    @State private var isPresented = false

    let onRegisterImage: () -> Void
    let onRegisterDirect: () -> Void

    var body: some View {
        OBRitFloatingActionMenu(
            isPresented: $isPresented,
            items: [
                OBRitFloatingActionMenuItem(
                    id: "image",
                    title: "이미지 등록",
                    action: onRegisterImage
                ),
                OBRitFloatingActionMenuItem(
                    id: "direct",
                    title: "직접 등록",
                    action: onRegisterDirect
                )
            ],
            accessibilityLabel: "소모품 등록 옵션"
        )
    }
}
