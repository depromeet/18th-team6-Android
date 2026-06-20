import SwiftUI

struct OnboardingScaffold<Content: View, BottomBar: View>: View {
    let content: () -> Content
    let bottomBar: () -> BottomBar

    init(
        @ViewBuilder content: @escaping () -> Content,
        @ViewBuilder bottomBar: @escaping () -> BottomBar
    ) {
        self.content = content
        self.bottomBar = bottomBar
    }

    var body: some View {
        GeometryReader { geometry in
            let horizontalPadding = OnboardingLayoutMetrics.horizontalPadding(for: geometry.size.width)

            content()
                .padding(.horizontal, horizontalPadding)
                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
                .background(OBRitColors.backgroundDefaultDefault.ignoresSafeArea())
                .safeAreaInset(edge: .bottom, spacing: 0) {
                    OnboardingBottomBarContainer(horizontalPadding: horizontalPadding) {
                        bottomBar()
                    }
                }
        }
    }
}

struct OnboardingStepScaffold<Content: View, BottomBar: View>: View {
    let currentStep: Int
    let action: OnboardingViewAction
    let content: () -> Content
    let bottomBar: () -> BottomBar

    init(
        currentStep: Int,
        action: OnboardingViewAction,
        @ViewBuilder content: @escaping () -> Content,
        @ViewBuilder bottomBar: @escaping () -> BottomBar
    ) {
        self.currentStep = currentStep
        self.action = action
        self.content = content
        self.bottomBar = bottomBar
    }

    var body: some View {
        GeometryReader { geometry in
            let horizontalPadding = OnboardingLayoutMetrics.horizontalPadding(for: geometry.size.width)

            VStack(spacing: 0) {
                OBRitDepthTopBar(
                    title: "소모품 등록",
                    backgroundColor: false,
                    showRightButton: false,
                    onBackClick: action.onBack
                )

                ScrollView(showsIndicators: false) {
                    VStack(alignment: .leading, spacing: OBRitSpacing.s5) {
                        OnboardingProgressView(currentStep: currentStep)
                            .padding(.top, OBRitSpacing.s4)

                        content()
                    }
                    .padding(.horizontal, horizontalPadding)
                    .padding(.bottom, OnboardingLayoutMetrics.scrollBottomPadding)
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
            .background(OBRitColors.backgroundDefaultDefault.ignoresSafeArea())
            .safeAreaInset(edge: .bottom, spacing: 0) {
                OnboardingBottomBarContainer(horizontalPadding: horizontalPadding) {
                    bottomBar()
                }
            }
        }
    }
}

struct OnboardingTitleBlock: View {
    let title: String
    let subtitle: String

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
            Text(title)
                .fixedSize(horizontal: false, vertical: true)
                .obritTextStyle(
                    OBRitTypography.s6xl,
                    weight: OBRitFontWeight.bold,
                    color: OBRitColors.textDefaultDefault
                )

            Text(subtitle)
                .fixedSize(horizontal: false, vertical: true)
                .obritTextStyle(
                    OBRitTypography.xl,
                    weight: OBRitFontWeight.medium,
                    color: OBRitColors.textDefaultSecondary
                )
        }
        .padding(.vertical, OBRitSpacing.s4)
    }
}

struct OnboardingProgressView: View {
    let currentStep: Int

    var body: some View {
        HStack(spacing: OBRitSpacing.s1) {
            OnboardingProgressNumber(text: "1", state: state(for: 1))
            Rectangle()
                .fill(OBRitColors.gray750)
                .frame(width: OBRitSpacing.s7, height: 1)
            OnboardingProgressNumber(text: "2", state: state(for: 2))
        }
    }

    private func state(for step: Int) -> OnboardingProgressNumberState {
        if step == currentStep {
            return .current
        }
        if step < currentStep {
            return .completed
        }
        return .pending
    }
}

enum OnboardingProgressNumberState {
    case current
    case completed
    case pending
}

struct OnboardingProgressNumber: View {
    let text: String
    let state: OnboardingProgressNumberState

    var body: some View {
        Text(text)
            .lineLimit(1)
            .minimumScaleFactor(0.7)
            .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.semiBold, color: contentColor)
            .frame(width: OBRitSpacing.s7, height: OBRitSpacing.s7)
            .background(containerColor)
            .clipShape(Circle())
    }

    private var containerColor: Color {
        switch state {
        case .current:
            return OBRitColors.common00
        case .completed, .pending:
            return OBRitColors.gray750
        }
    }

    private var contentColor: Color {
        switch state {
        case .current:
            return OBRitColors.common1000
        case .completed:
            return OBRitColors.gray450
        case .pending:
            return OBRitColors.common00
        }
    }
}

struct OnboardingSelectedCountText: View {
    let count: Int

    var body: some View {
        (
            Text("선택한 소모품 ")
                .foregroundColor(OBRitColors.common00) +
                Text("\(count)개")
                .foregroundColor(OBRitColors.textPositiveDefault)
        )
        .font(OBRitTypography.font(OBRitTypography.s3xl, weight: OBRitFontWeight.bold))
        .tracking(OBRitTypography.letterSpacing(for: OBRitTypography.s3xl))
        .lineSpacing(max(0, OBRitTypography.s3xl.lineHeight - OBRitTypography.s3xl.size))
    }
}

struct OnboardingCategoryCard: View {
    let option: OnboardingItemOption
    let selected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: OBRitSpacing.s4) {
                OnboardingItemImage(
                    urlString: option.imageURL,
                    size: OnboardingLayoutMetrics.categoryImageSize
                )

                VStack(alignment: .leading, spacing: OBRitSpacing.s1) {
                    Text(option.title)
                        .lineLimit(1)
                        .obritTextStyle(
                            OBRitTypography.xl,
                            weight: OBRitFontWeight.bold,
                            color: OBRitColors.common00
                        )

                    HStack(spacing: 2) {
                        Text("추가한 소모품")
                            .obritTextStyle(
                                OBRitTypography.small,
                                weight: OBRitFontWeight.medium,
                                color: OBRitColors.commonWhite00_60
                            )
                        Text("\(option.addedCount)개")
                            .obritTextStyle(
                                OBRitTypography.small,
                                weight: OBRitFontWeight.semiBold,
                                color: OBRitColors.common00
                            )
                    }
                }

                Spacer(minLength: 0)

                OnboardingCheckBox(checked: selected)
                    .frame(width: OBRitSpacing.s10, height: OBRitSpacing.s10)
            }
            .padding(.horizontal, OBRitSpacing.s5)
            .padding(.vertical, OBRitSpacing.s4)
            .background(OBRitColors.backgroundDefaultSecondary)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
            .contentShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
        }
        .buttonStyle(.plain)
        .accessibilityLabel("\(option.title), 추가한 소모품 \(option.addedCount)개")
        .accessibilityValue(selected ? "선택됨" : "선택 안 됨")
    }
}

struct OnboardingReplacementPeriodCard: View {
    let option: OnboardingItemOption
    let period: OnboardingReplacementPeriod?
    let onOpenPicker: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s2) {
            HStack(spacing: OBRitSpacing.s2) {
                OnboardingItemImage(
                    urlString: option.imageURL,
                    size: OnboardingLayoutMetrics.periodImageSize
                )

                Text(option.title)
                    .lineLimit(1)
                    .obritTextStyle(
                        OBRitTypography.s2xl,
                        weight: OBRitFontWeight.semiBold,
                        color: OBRitColors.common00
                    )

                Text("*")
                    .obritTextStyle(
                        OBRitTypography.xl,
                        weight: OBRitFontWeight.semiBold,
                        color: OBRitColors.red300
                    )
            }

            OBRitDropdown(
                value: period?.title ?? "",
                placeholder: "마지막 교체일 선택",
                onClick: onOpenPicker
            )
        }
        .padding(.horizontal, OBRitSpacing.s5)
        .padding(.vertical, OBRitSpacing.s5)
        .background(OBRitColors.backgroundDefaultSecondary)
        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
    }
}

struct OnboardingItemDetailCard: View {
    let option: OnboardingItemOption
    let name: String
    let period: OnboardingReplacementPeriod?
    let quantity: Int
    let isPeriodDropdownExpanded: Bool
    let action: OnboardingViewAction
    let onTogglePeriodDropdown: () -> Void
    let onSelectPeriod: (OnboardingReplacementPeriod) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s5) {
            OnboardingRequiredField(title: "소모품 명") {
                OnboardingItemNameInputField(
                    text: name,
                    placeholder: "소모품명을 입력해주세요",
                    maxLength: OnboardingRegistrationConfig.itemNameMaxLength,
                    onTextChange: { input in
                        action.onUpdateItemName(option, input)
                    }
                )
            }

            OnboardingRequiredField(title: "마지막 교체 일자") {
                OnboardingReplacementPeriodDropdown(
                    selectedPeriod: period,
                    isExpanded: isPeriodDropdownExpanded,
                    onToggle: onTogglePeriodDropdown,
                    onSelect: onSelectPeriod
                )
            }
            .zIndex(isPeriodDropdownExpanded ? OnboardingLayoutMetrics.expandedControlZIndex : OnboardingLayoutMetrics.defaultZIndex)

            VStack(alignment: .leading, spacing: OBRitSpacing.s2) {
                Text("등록할 수량")
                    .obritTextStyle(
                        OBRitTypography.s2xl,
                        weight: OBRitFontWeight.semiBold,
                        color: OBRitColors.common00
                    )

                OBRitStepper(
                    value: quantity,
                    size: .small,
                    minimumValue: OnboardingRegistrationConfig.quantityMinimum,
                    maximumValue: OnboardingRegistrationConfig.quantityMaximum,
                    onDecrement: {
                        action.onDecrementQuantity(option)
                    },
                    onIncrement: {
                        action.onIncrementQuantity(option)
                    },
                    onValueChange: { value in
                        action.onUpdateQuantity(option, value)
                    }
                )

                HStack(alignment: .center, spacing: OBRitSpacing.s1) {
                    OBRitIcon(kind: .info, color: OBRitColors.textDefaultTertiary)
                        .frame(width: OBRitSpacing.s4, height: OBRitSpacing.s4)
                    Text("소모품의 전체 수량은 추후에도 등록할 수 있어요.")
                        .fixedSize(horizontal: false, vertical: true)
                        .obritTextStyle(
                            OBRitTypography.s,
                            weight: OBRitFontWeight.semiBold,
                            color: OBRitColors.textDefaultTertiary
                        )
                }
            }
        }
        .padding(OBRitSpacing.s5)
        .frame(maxWidth: OnboardingLayoutMetrics.detailCardMaxWidth, alignment: .leading)
        .background {
            RoundedRectangle(cornerRadius: OBRitRadius.extraLarge)
                .fill(OBRitColors.backgroundDefaultSecondary)
        }
    }
}

struct OnboardingReplacementPeriodDropdown: View {
    let selectedPeriod: OnboardingReplacementPeriod?
    let isExpanded: Bool
    let onToggle: () -> Void
    let onSelect: (OnboardingReplacementPeriod) -> Void

    var body: some View {
        ZStack(alignment: .topLeading) {
            OBRitDropdown(
                value: selectedPeriod?.title ?? "",
                placeholder: "마지막 교체 일자를 등록해주세요",
                expanded: isExpanded,
                onClick: {
                    dismissKeyboard()
                    onToggle()
                }
            )
            .frame(maxWidth: .infinity)

            if isExpanded {
                OBRitDropdownMenu(
                    items: OnboardingReplacementPeriod.allCases.map(\.title),
                    selectedIndex: selectedIndex,
                    fillsWidth: true,
                    onItemClick: select
                )
                .frame(maxWidth: .infinity)
                .offset(y: OBRitSpacing.s14 + OBRitSpacing.s2)
                .zIndex(OnboardingLayoutMetrics.dropdownMenuZIndex)
            }
        }
        .frame(maxWidth: .infinity)
        .frame(height: OBRitSpacing.s14, alignment: .top)
        .zIndex(isExpanded ? OnboardingLayoutMetrics.expandedControlZIndex : OnboardingLayoutMetrics.defaultZIndex)
    }

    private var selectedIndex: Int? {
        guard let selectedPeriod else { return nil }
        return OnboardingReplacementPeriod.allCases.firstIndex(of: selectedPeriod)
    }

    private func select(_ index: Int) {
        guard OnboardingReplacementPeriod.allCases.indices.contains(index) else { return }
        onSelect(OnboardingReplacementPeriod.allCases[index])
    }
}

struct OnboardingRequiredField<Content: View>: View {
    let title: String
    let content: Content

    init(
        title: String,
        @ViewBuilder content: () -> Content
    ) {
        self.title = title
        self.content = content()
    }

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s2) {
            HStack(alignment: .top, spacing: OBRitSpacing.s0_5) {
                Text(title)
                    .obritTextStyle(
                        OBRitTypography.s2xl,
                        weight: OBRitFontWeight.semiBold,
                        color: OBRitColors.common00
                    )
                Text("*")
                    .obritTextStyle(
                        OBRitTypography.base,
                        weight: OBRitFontWeight.bold,
                        color: OBRitColors.textWarningDefault
                    )
                    .padding(.top, OnboardingLayoutMetrics.requiredMarkerTopPadding)
            }

            content
        }
    }
}

struct OnboardingItemNameInputField: View {
    @State private var localText: String
    @FocusState private var isFocused: Bool

    let text: String
    let placeholder: String
    let maxLength: Int
    let onTextChange: (String) -> Void

    init(
        text: String,
        placeholder: String,
        maxLength: Int,
        onTextChange: @escaping (String) -> Void
    ) {
        _localText = State(initialValue: String(text.prefix(maxLength)))
        self.text = text
        self.placeholder = placeholder
        self.maxLength = maxLength
        self.onTextChange = onTextChange
    }

    var body: some View {
        HStack(spacing: OBRitSpacing.s2) {
            ZStack(alignment: .leading) {
                if localText.isEmpty {
                    Text(placeholder)
                        .lineLimit(1)
                        .obritTextStyle(
                            OBRitTypography.xl,
                            weight: OBRitFontWeight.medium,
                            color: OBRitColors.gray700
                        )
                }

                TextField("", text: lengthLimitedText)
                    .lineLimit(1)
                    .submitLabel(.done)
                    .textInputAutocapitalization(.never)
                    .autocorrectionDisabled()
                    .focused($isFocused)
                    .tint(OBRitColors.common00)
                    .obritTextStyle(
                        OBRitTypography.xl,
                        weight: OBRitFontWeight.medium,
                        color: OBRitColors.common00
                    )
            }
            .frame(maxWidth: .infinity, alignment: .leading)

            Text("\(min(localText.count, maxLength))/\(maxLength)")
                .lineLimit(1)
                .obritTextStyle(
                    OBRitTypography.s,
                    weight: OBRitFontWeight.medium,
                    color: OBRitColors.common00
                )
        }
        .frame(height: OBRitSpacing.s14)
        .padding(.horizontal, OBRitSpacing.s5)
        .background(OBRitColors.gray800)
        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
        .contentShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
        .onTapGesture {
            isFocused = true
        }
        .onChange(of: text) { _, newValue in
            let clippedText = String(newValue.prefix(maxLength))
            guard clippedText != localText else { return }
            localText = clippedText
        }
    }

    private var lengthLimitedText: Binding<String> {
        Binding {
            localText
        } set: { newValue in
            let clippedText = String(newValue.prefix(maxLength))
            guard clippedText != localText else { return }
            localText = clippedText
            onTextChange(clippedText)
        }
    }
}

struct OnboardingItemImage: View {
    let urlString: String
    let size: CGFloat

    var body: some View {
        ZStack {
            Circle()
                .fill(OBRitColors.gray750)
            OBRitRemoteImage(urlString: urlString) {
                Image(systemName: "drop.fill")
                    .font(.system(size: size * 0.44, weight: .semibold))
                    .foregroundStyle(OBRitColors.textPositiveDefault)
            }
            .frame(width: size, height: size)
            .clipShape(Circle())
        }
        .frame(width: size, height: size)
    }
}

struct OnboardingCheckBox: View {
    let checked: Bool

    var body: some View {
        ZStack {
            if checked {
                RoundedRectangle(cornerRadius: 3)
                    .fill(OBRitColors.green300)
                OBRitIcon(kind: .check, color: OBRitColors.gray900)
                    .padding(3)
            } else {
                RoundedRectangle(cornerRadius: 3)
                    .stroke(OBRitColors.gray400, lineWidth: 1.4)
            }
        }
        .frame(width: 21, height: 21)
    }
}

struct OnboardingBottomCTA: View {
    let text: String
    let enabled: Bool
    var isProcessing: Bool = false
    let action: () -> Void

    var body: some View {
        OBRitFilledTextButton(
            text: isProcessing ? "등록 중..." : text,
            enabled: enabled && !isProcessing,
            fillsWidth: true,
            action: action
        )
    }
}

struct OnboardingBottomBarContainer<Content: View>: View {
    let horizontalPadding: CGFloat
    let content: () -> Content

    init(
        horizontalPadding: CGFloat,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.horizontalPadding = horizontalPadding
        self.content = content
    }

    var body: some View {
        VStack(spacing: 0) {
            content()
                .padding(.horizontal, horizontalPadding)
                .padding(.top, OnboardingLayoutMetrics.bottomBarTopPadding)
                .padding(.bottom, OnboardingLayoutMetrics.bottomButtonPadding)
        }
        .frame(maxWidth: .infinity)
        .background(OBRitColors.backgroundDefaultDefault.ignoresSafeArea(edges: .bottom))
    }
}

struct OnboardingMessageView: View {
    let title: String

    var body: some View {
        ZStack {
            OBRitColors.backgroundDefaultDefault
                .ignoresSafeArea()
            Text(title)
                .multilineTextAlignment(.center)
                .obritTextStyle(
                    OBRitTypography.xl,
                    weight: OBRitFontWeight.semiBold,
                    color: OBRitColors.textDefaultDefault
                )
                .padding(.horizontal, OBRitSpacing.s5)
        }
    }
}

struct OnboardingFailureView: View {
    let message: String
    let action: OnboardingViewAction

    var body: some View {
        OnboardingScaffold {
            VStack(alignment: .center, spacing: OBRitSpacing.s4) {
                Spacer(minLength: 0)
                Text(message)
                    .multilineTextAlignment(.center)
                    .obritTextStyle(
                        OBRitTypography.xl,
                        weight: OBRitFontWeight.semiBold,
                        color: OBRitColors.textDefaultDefault
                    )
                Spacer(minLength: 0)
            }
            .frame(maxWidth: .infinity)
        } bottomBar: {
            OnboardingBottomCTA(
                text: "다시 시도",
                enabled: true,
                action: action.onRetry
            )
        }
    }
}

enum OnboardingLayoutMetrics {
    static let referenceWidth: CGFloat = 412
    static let horizontalPaddingRatio: CGFloat = 20 / referenceWidth
    static let defaultHorizontalPadding: CGFloat = 20
    static let scrollBottomPadding: CGFloat = 24
    static let bottomBarTopPadding: CGFloat = 16
    static let bottomButtonPadding: CGFloat = 40
    static let categoryImageSize: CGFloat = 52
    static let periodImageSize: CGFloat = 28
    static let detailCardMaxWidth: CGFloat = 340
    static let requiredMarkerTopPadding: CGFloat = 1
    static let dropdownMenuZIndex: Double = 1
    static let expandedControlZIndex: Double = 10
    static let defaultZIndex: Double = 0

    static func horizontalPadding(for width: CGFloat) -> CGFloat {
        max(defaultHorizontalPadding, width * horizontalPaddingRatio)
    }
}
