import SwiftUI

struct OnboardingCompleteView: View {
    @Environment(\.scenePhase) private var scenePhase
    @State private var hasCompleted = false

    let action: OnboardingViewAction

    var body: some View {
        VStack(spacing: 0) {
            Image("item_registration_complete_badge")
                .accessibilityHidden(true)
                .frame(
                    width: OnboardingCompletionMetrics.illustrationSize,
                    height: OnboardingCompletionMetrics.illustrationSize
                )

            VStack(spacing: OBRitSpacing.s4) {
                Text("Obrit과 함께\n소모품 관리 시작해요!")
                    .obritTextStyle(
                        OBRitTypography.s6xl,
                        weight: OBRitFontWeight.bold,
                        color: OBRitColors.common00
                    )
                Text("이제부터 Obrit이 간편하게\n소모품을 관리해드릴게요")
                    .obritTextStyle(
                        OBRitTypography.xl,
                        weight: OBRitFontWeight.medium,
                        color: OBRitColors.textDefaultSecondary
                    )
            }
            .multilineTextAlignment(.center)
            .fixedSize(horizontal: false, vertical: true)
            .padding(.horizontal, OBRitSpacing.s5)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding(.bottom, OBRitSpacing.s10)
        .background(OBRitColors.backgroundDefaultDefault.ignoresSafeArea())
        .task(id: scenePhase) {
            await finishAfterDisplay()
        }
    }

    @MainActor
    private func finishAfterDisplay() async {
        guard scenePhase == .active, !hasCompleted else { return }
        do {
            try await Task.sleep(for: .seconds(2))
        } catch {
            return
        }
        guard !Task.isCancelled, !hasCompleted else { return }
        hasCompleted = true
        action.onComplete()
    }
}

private enum OnboardingCompletionMetrics {
    static let illustrationSize: CGFloat = 260
}
