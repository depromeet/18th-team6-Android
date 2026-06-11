import SwiftUI

struct OnboardingView: View {
    @StateObject private var viewModel: OnboardingViewModel

    let onContinue: () -> Void

    init(onContinue: @escaping () -> Void) {
        _viewModel = StateObject(wrappedValue: OnboardingViewModel())
        self.onContinue = onContinue
    }

    init(
        viewModel: OnboardingViewModel,
        onContinue: @escaping () -> Void
    ) {
        _viewModel = StateObject(wrappedValue: viewModel)
        self.onContinue = onContinue
    }

    var body: some View {
        switch viewModel.state {
        case let .success(data):
            GeometryReader { geometry in
                let horizontalPadding = OnboardingLayoutConfig.horizontalPadding(for: geometry.size.width)

                ZStack {
                    OBRitColors.backgroundDefaultDefault
                        .ignoresSafeArea()

                    VStack(alignment: .leading, spacing: 0) {
                        VStack(alignment: .leading, spacing: OnboardingLayoutConfig.titleGap) {
                            Text("관리할 소모품을\n선택해주세요")
                                .fixedSize(horizontal: false, vertical: true)
                                .obritTextStyle(OBRitTypography.s6xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)

                            Text("선택한 항목을 기준으로 교체 주기와 여분 관리를 도와드릴게요")
                                .fixedSize(horizontal: false, vertical: true)
                                .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultSecondary)
                        }
                        .padding(.top, OnboardingLayoutConfig.titleTopPadding)

                        VStack(spacing: OnboardingLayoutConfig.optionGap) {
                            ForEach(data.options) { option in
                                let selected = data.selectedOptionIds.contains(option.id)

                                Button {
                                    viewModel.toggleOption(option)
                                } label: {
                                    HStack(spacing: OnboardingLayoutConfig.optionContentGap) {
                                        OBRitRemoteImage(urlString: option.imageURL)
                                            .frame(width: OnboardingLayoutConfig.optionImageSize, height: OnboardingLayoutConfig.optionImageSize)
                                            .padding(8)
                                            .background(selected ? OBRitColors.common00 : OBRitColors.gray750)
                                            .clipShape(Circle())

                                        Text(option.title)
                                            .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)

                                        Spacer(minLength: 0)

                                        OBRitRadioButton(
                                            selected: selected,
                                            onClick: { viewModel.toggleOption(option) }
                                        )
                                    }
                                    .padding(OnboardingLayoutConfig.optionPadding)
                                    .background(selected ? OBRitColors.gray750 : OBRitColors.backgroundDefaultSecondary)
                                    .clipShape(RoundedRectangle(cornerRadius: OnboardingLayoutConfig.cardRadius))
                                }
                                .buttonStyle(.plain)
                            }
                        }
                        .padding(.top, OnboardingLayoutConfig.optionsTopPadding)

                        Spacer(minLength: 0)

                        OBRitFilledTextButton(
                            text: "다음",
                            enabled: data.canContinue,
                            fillsWidth: true,
                            action: onContinue
                        )
                        .padding(.bottom, OnboardingLayoutConfig.bottomPadding)
                    }
                    .padding(.horizontal, horizontalPadding)
                }
            }
        }
    }
}

private enum OnboardingLayoutConfig {
    static let referenceWidth: CGFloat = 412
    static let horizontalPaddingRatio: CGFloat = 20 / referenceWidth
    static let defaultHorizontalPadding: CGFloat = 20
    static let titleTopPadding: CGFloat = 96
    static let titleGap: CGFloat = 12
    static let optionsTopPadding: CGFloat = 44
    static let optionGap: CGFloat = 10
    static let optionContentGap: CGFloat = 16
    static let optionPadding: CGFloat = 16
    static let optionImageSize: CGFloat = 44
    static let cardRadius: CGFloat = 16
    static let bottomPadding: CGFloat = 36

    static func horizontalPadding(for width: CGFloat) -> CGFloat {
        max(defaultHorizontalPadding, width * horizontalPaddingRatio)
    }
}
