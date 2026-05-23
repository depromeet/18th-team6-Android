import SwiftUI

struct RegistrationMethodView: View {
    @StateObject private var viewModel: RegistrationMethodViewModel

    let onNavigate: (ItemRoute) -> Void
    let onBack: () -> Void

    init(
        onNavigate: @escaping (ItemRoute) -> Void,
        onBack: @escaping () -> Void
    ) {
        _viewModel = StateObject(wrappedValue: RegistrationMethodViewModel())
        self.onNavigate = onNavigate
        self.onBack = onBack
    }

    init(
        viewModel: RegistrationMethodViewModel,
        onNavigate: @escaping (ItemRoute) -> Void,
        onBack: @escaping () -> Void
    ) {
        _viewModel = StateObject(wrappedValue: viewModel)
        self.onNavigate = onNavigate
        self.onBack = onBack
    }

    var body: some View {
        switch viewModel.state {
        case let .success(options):
            GeometryReader { geometry in
                let horizontalPadding = RegistrationMethodLayoutConfig.horizontalPadding(for: geometry.size.width)

                ZStack {
                    OBRitColors.backgroundDefaultDefault
                        .ignoresSafeArea()

                    VStack(spacing: 0) {
                        OBRitDepthTopBar(
                            title: "소모품 등록",
                            backgroundColor: false,
                            showRightButton: false,
                            onBackClick: onBack
                        )

                        VStack(alignment: .leading, spacing: RegistrationMethodLayoutConfig.contentGap) {
                            VStack(alignment: .leading, spacing: RegistrationMethodLayoutConfig.titleGap) {
                                Text("소모품을 어떻게\n등록할까요?")
                                    .fixedSize(horizontal: false, vertical: true)
                                    .obritTextStyle(
                                        OBRitTypography.s6xl,
                                        weight: OBRitFontWeight.bold,
                                        color: OBRitColors.textDefaultDefault
                                    )

                                Text("편한 방식으로 소모품을 등록하고 관리를 시작해보세요")
                                    .fixedSize(horizontal: false, vertical: true)
                                    .obritTextStyle(
                                        OBRitTypography.xl,
                                        weight: OBRitFontWeight.medium,
                                        color: OBRitColors.textDefaultSecondary
                                    )
                            }
                            .padding(.vertical, OBRitSpacing.s4)

                            VStack(spacing: RegistrationMethodLayoutConfig.optionGap) {
                                ForEach(options) { option in
                                    RegistrationMethodOptionCard(option: option) {
                                        switch option.id {
                                        case .direct:
                                            onNavigate(.itemRegistration)
                                        }
                                    }
                                }
                            }
                        }
                        .padding(.horizontal, horizontalPadding)
                        .padding(.top, RegistrationMethodLayoutConfig.topContentInset)

                        Spacer(minLength: 0)
                    }
                }
            }
        }
    }
}

private struct RegistrationMethodOptionCard: View {
    let option: RegistrationMethodOption
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: RegistrationMethodLayoutConfig.iconGap) {
                ZStack {
                    Circle()
                        .fill(OBRitColors.gray750)
                    Image(systemName: option.symbolName)
                        .font(.system(size: RegistrationMethodLayoutConfig.iconSize, weight: .semibold))
                        .foregroundStyle(option.accentColor)
                }
                .frame(width: RegistrationMethodLayoutConfig.iconContainerSize, height: RegistrationMethodLayoutConfig.iconContainerSize)

                VStack(alignment: .leading, spacing: 4) {
                    Text(option.title)
                        .obritTextStyle(OBRitTypography.xl, weight: OBRitFontWeight.bold, color: OBRitColors.common00)
                    Text(option.subtitle)
                        .fixedSize(horizontal: false, vertical: true)
                        .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.medium, color: OBRitColors.textDefaultSecondary)
                }

                Spacer(minLength: 0)

                Image(systemName: "chevron.right")
                    .font(.system(size: 16, weight: .bold))
                    .foregroundStyle(OBRitColors.common00)
            }
            .padding(RegistrationMethodLayoutConfig.cardPadding)
            .background(OBRitColors.backgroundDefaultSecondary)
            .clipShape(RoundedRectangle(cornerRadius: RegistrationMethodLayoutConfig.cardRadius))
        }
        .buttonStyle(.plain)
    }
}

private enum RegistrationMethodLayoutConfig {
    static let referenceWidth: CGFloat = 412
    static let horizontalPaddingRatio: CGFloat = 20 / referenceWidth
    static let defaultHorizontalPadding: CGFloat = 20
    static let topContentInset: CGFloat = 20
    static let contentGap: CGFloat = 28
    static let titleGap: CGFloat = 12
    static let optionGap: CGFloat = 12
    static let cardPadding: CGFloat = 20
    static let cardRadius: CGFloat = 16
    static let iconGap: CGFloat = 16
    static let iconSize: CGFloat = 24
    static let iconContainerSize: CGFloat = 56

    static func horizontalPadding(for width: CGFloat) -> CGFloat {
        max(defaultHorizontalPadding, width * horizontalPaddingRatio)
    }
}
