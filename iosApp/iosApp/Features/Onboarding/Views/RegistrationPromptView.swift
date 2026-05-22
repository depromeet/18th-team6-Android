import SwiftUI
import Shared

struct RegistrationPromptView: View {
    let onRegister: () -> Void
    let onSkip: () -> Void

    var body: some View {
        GeometryReader { geometry in
            let horizontalPadding = RegistrationPromptLayoutConfig.horizontalPadding(for: geometry.size.width)

            ZStack {
                OBRitColors.backgroundDefaultDefault
                    .ignoresSafeArea()

                VStack(spacing: 0) {
                    Spacer(minLength: 0)

                    VStack(spacing: RegistrationPromptLayoutConfig.contentGap) {
                        Image("manual_registration_complete_badge")
                            .resizable()
                            .scaledToFit()
                            .frame(width: RegistrationPromptLayoutConfig.badgeSize, height: RegistrationPromptLayoutConfig.badgeSize)

                        VStack(spacing: RegistrationPromptLayoutConfig.titleGap) {
                            Text("첫 소모품을\n등록해볼까요?")
                                .multilineTextAlignment(.center)
                                .obritTextStyle(OBRitTypography.s6xl, weight: AtomFontWeight.shared.Bold, color: OBRitColors.common00)

                            Text("등록한 소모품부터 교체 시기와 여분 상태를 확인할 수 있어요")
                                .multilineTextAlignment(.center)
                                .obritTextStyle(OBRitTypography.xl, weight: AtomFontWeight.shared.Medium, color: OBRitColors.textDefaultSecondary)
                        }
                    }

                    Spacer(minLength: 0)

                    VStack(spacing: RegistrationPromptLayoutConfig.buttonGap) {
                        OBRitFilledTextButton(text: "소모품 등록하기", fillsWidth: true, action: onRegister)
                        OBRitFilledTextButton(text: "나중에 할게요", color: .gray, fillsWidth: true, action: onSkip)
                    }
                    .padding(.bottom, RegistrationPromptLayoutConfig.bottomPadding)
                }
                .padding(.horizontal, horizontalPadding)
            }
        }
    }
}

private enum RegistrationPromptLayoutConfig {
    static let referenceWidth: CGFloat = 412
    static let horizontalPaddingRatio: CGFloat = 20 / referenceWidth
    static let defaultHorizontalPadding: CGFloat = 20
    static let badgeSize: CGFloat = 152
    static let contentGap: CGFloat = 36
    static let titleGap: CGFloat = 16
    static let buttonGap: CGFloat = 12
    static let bottomPadding: CGFloat = 36

    static func horizontalPadding(for width: CGFloat) -> CGFloat {
        max(defaultHorizontalPadding, width * horizontalPaddingRatio)
    }
}
