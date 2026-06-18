import SwiftUI

struct OnboardingStartView: View {
    let action: OnboardingViewAction

    var body: some View {
        OnboardingScaffold {
            VStack(alignment: .leading, spacing: 0) {
                Spacer()
                    .frame(height: OnboardingStartMetrics.titleTopPadding)

                VStack(alignment: .leading, spacing: OBRitSpacing.s1) {
                    Text("소모품 등록")
                        .obritTextStyle(
                            OBRitTypography.xl,
                            weight: OBRitFontWeight.medium,
                            color: OBRitColors.textPositiveDefault
                        )

                    VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
                        Text("환영합니다!\nOBRIT과 함께 소모품 관리해요")
                            .fixedSize(horizontal: false, vertical: true)
                            .obritTextStyle(
                                OBRitTypography.s6xl,
                                weight: OBRitFontWeight.bold,
                                color: OBRitColors.textDefaultDefault
                            )

                        Text("소모품 관리, 온보딩으로 간편하게 시작해요!")
                            .fixedSize(horizontal: false, vertical: true)
                            .obritTextStyle(
                                OBRitTypography.xl,
                                weight: OBRitFontWeight.medium,
                                color: OBRitColors.textDefaultSecondary
                            )
                    }
                }

                Spacer(minLength: 0)
            }
        } bottomBar: {
            OnboardingBottomCTA(
                text: "온보딩 시작하기",
                enabled: true,
                action: action.onStart
            )
        }
    }
}

private enum OnboardingStartMetrics {
    static let titleTopPadding: CGFloat = 100
}
