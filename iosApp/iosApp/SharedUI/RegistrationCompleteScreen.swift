import SwiftUI

struct RegistrationCompleteScreen: View {
    let action: () -> Void

    var body: some View {
        GeometryReader { geometry in
            let horizontalPadding = RegistrationCompleteMetrics.horizontalPadding(for: geometry.size.width)

            VStack(spacing: RegistrationCompleteMetrics.zeroSpacing) {
                Spacer(minLength: RegistrationCompleteMetrics.spacerMinimumLength)

                RegistrationCompleteContent()
                    .padding(.bottom, geometry.size.height * RegistrationCompleteMetrics.contentBottomPaddingRatio)

                Spacer(minLength: RegistrationCompleteMetrics.spacerMinimumLength)

                RegistrationCompleteBottomButton(
                    horizontalPadding: horizontalPadding,
                    action: action
                )
            }
            .frame(width: geometry.size.width, height: geometry.size.height)
            .background(OBRitColors.backgroundDefaultDefault)
        }
    }
}

private struct RegistrationCompleteContent: View {
    var body: some View {
        VStack(spacing: OBRitSpacing.s9) {
            Image(RegistrationCompleteMetrics.badgeAssetName)
                .resizable()
                .scaledToFit()
                .frame(
                    width: RegistrationCompleteMetrics.badgeWidth,
                    height: RegistrationCompleteMetrics.badgeHeight
                )
                .accessibilityHidden(true)

            VStack(spacing: OBRitSpacing.s4) {
                Text("소모품이 등록되었어요!")
                    .lineLimit(RegistrationCompleteMetrics.singleLineLimit)
                    .obritTextStyle(
                        OBRitTypography.s6xl,
                        weight: OBRitFontWeight.bold,
                        color: OBRitColors.common00
                    )

                Text("이제부터 OBRIT이 간편하게\n소모품을 관리해드릴게요")
                    .multilineTextAlignment(.center)
                    .obritTextStyle(
                        OBRitTypography.xl,
                        weight: OBRitFontWeight.medium,
                        color: OBRitColors.textDefaultSecondary
                    )
            }
        }
        .frame(maxWidth: .infinity)
    }
}

private struct RegistrationCompleteBottomButton: View {
    let horizontalPadding: CGFloat
    let action: () -> Void

    var body: some View {
        VStack(spacing: RegistrationCompleteMetrics.zeroSpacing) {
            OBRitFilledTextButton(
                text: "홈 화면으로 돌아가기",
                color: .green,
                enabled: true,
                fillsWidth: true,
                action: action
            )
            .padding(.horizontal, horizontalPadding)
            .padding(.bottom, OBRitSpacing.s10)
        }
        .padding(.top, OBRitSpacing.s5)
        .background(OBRitColors.backgroundDefaultDefault)
    }
}

private enum RegistrationCompleteMetrics {
    static let badgeAssetName = "item_registration_complete_badge"
    static let badgeWidth: CGFloat = 245
    static let badgeHeight: CGFloat = 255
    static let contentBottomPaddingRatio: CGFloat = 0.07
    static let designWidth: CGFloat = 412
    static let zeroSpacing: CGFloat = 0
    static let spacerMinimumLength: CGFloat = 0
    static let singleLineLimit = 1

    static func horizontalPadding(for width: CGFloat) -> CGFloat {
        let horizontalInsetRatio = OBRitSpacing.s5 / designWidth
        return max(OBRitSpacing.s4, min(OBRitSpacing.s7, width * horizontalInsetRatio))
    }
}
