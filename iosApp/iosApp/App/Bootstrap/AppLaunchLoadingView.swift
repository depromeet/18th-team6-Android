import SwiftUI

struct AppLaunchLoadingView: View {
    var body: some View {
        ZStack {
            OBRitColors.backgroundDefaultDefault
                .ignoresSafeArea()

            VStack(spacing: OBRitSpacing.s6) {
                OBRitLogo()
                    .frame(width: 136)

                ProgressView()
                    .tint(OBRitColors.green300)
                    .accessibilityLabel("앱 준비 중")
            }
        }
    }
}

#Preview {
    AppLaunchLoadingView()
}
