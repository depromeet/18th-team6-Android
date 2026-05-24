import SwiftUI
import UIKit

struct ItemRegistrationView: View {
    @StateObject private var viewModel: ItemRegistrationViewModel

    let onBack: () -> Void
    let onClose: () -> Void
    let onComplete: () -> Void

    init(
        onBack: @escaping () -> Void,
        onClose: @escaping () -> Void,
        onComplete: @escaping () -> Void
    ) {
        _viewModel = StateObject(wrappedValue: ItemRegistrationViewModel())
        self.onBack = onBack
        self.onClose = onClose
        self.onComplete = onComplete
    }

    init(
        viewModel: ItemRegistrationViewModel,
        onBack: @escaping () -> Void,
        onClose: @escaping () -> Void,
        onComplete: @escaping () -> Void
    ) {
        _viewModel = StateObject(wrappedValue: viewModel)
        self.onBack = onBack
        self.onClose = onClose
        self.onComplete = onComplete
    }

    var body: some View {
        switch viewModel.state {
        case let .success(data):
            ItemRegistrationContentView(
                data: data,
                action: ItemRegistrationAction(
                    onBack: {
                        if data.mode == .directKind {
                            viewModel.resetToForm()
                        } else {
                            onBack()
                        }
                    },
                    onClose: onClose,
                    onUpdateItemName: viewModel.updateItemName,
                    onOpenKindSheet: viewModel.openKindSheet,
                    onDismissBottomSheet: viewModel.dismissBottomSheet,
                    onUpdateKindSearchQuery: viewModel.updateKindSearchQuery,
                    onSelectKind: viewModel.selectKind,
                    onSelectKindCandidate: viewModel.selectKindCandidate,
                    onConfirmKindSelection: viewModel.confirmKindSelection,
                    onSelectReplacementDate: viewModel.selectReplacementDate,
                    onIncrementQuantity: viewModel.incrementQuantity,
                    onDecrementQuantity: viewModel.decrementQuantity,
                    onShowDirectKindRegistration: viewModel.showDirectKindRegistration,
                    onUpdateDirectKindName: viewModel.updateDirectKindName,
                    onSelectImageOption: viewModel.selectImageOption,
                    onSubmitDirectKind: viewModel.submitDirectKind,
                    onSubmitForm: viewModel.submitForm,
                    onComplete: onComplete
                )
            )
        }
    }
}

struct ItemRegistrationAction {
    let onBack: () -> Void
    let onClose: () -> Void
    let onUpdateItemName: (String) -> Void
    let onOpenKindSheet: () -> Void
    let onDismissBottomSheet: () -> Void
    let onUpdateKindSearchQuery: (String) -> Void
    let onSelectKind: (ItemKind) -> Void
    let onSelectKindCandidate: (ItemKind) -> Void
    let onConfirmKindSelection: () -> Void
    let onSelectReplacementDate: (ItemReplacementDateOption) -> Void
    let onIncrementQuantity: () -> Void
    let onDecrementQuantity: () -> Void
    let onShowDirectKindRegistration: () -> Void
    let onUpdateDirectKindName: (String) -> Void
    let onSelectImageOption: (ItemImageOption) -> Void
    let onSubmitDirectKind: () -> Void
    let onSubmitForm: () -> Void
    let onComplete: () -> Void
}

private struct ItemRegistrationContentView: View {
    let data: ItemRegistrationViewData
    let action: ItemRegistrationAction

    var body: some View {
        GeometryReader { geometry in
            ZStack {
                OBRitColors.backgroundDefaultDefault
                    .ignoresSafeArea()

                Group {
                    switch data.mode {
                    case .form:
                        ItemRegistrationFormView(data: data, action: action)
                    case .directKind:
                        ItemDirectKindRegistrationView(data: data, action: action)
                    case .complete:
                        ItemRegistrationCompleteView(action: action)
                    }
                }
                .ignoresSafeArea(.keyboard)

                if let bottomSheet = data.bottomSheet {
                    let bottomPadding = bottomSheetBottomPadding(in: geometry)

                    Color.black.opacity(ItemRegistrationLayoutConfig.dimOpacity)
                        .ignoresSafeArea()
                        .onTapGesture(perform: action.onDismissBottomSheet)
                        .transition(.opacity)

                    VStack {
                        Spacer(minLength: 0)
                        bottomSheetView(
                            bottomSheet,
                            contentHeight: bottomSheetContentHeight(
                                in: geometry,
                                bottomPadding: bottomPadding
                            ),
                            bottomPadding: bottomPadding
                        )
                            .transition(.move(edge: .bottom).combined(with: .opacity))
                    }
                    .ignoresSafeArea(.container, edges: .bottom)
                }
            }
            .frame(width: geometry.size.width, height: geometry.size.height)
            .animation(.easeOut(duration: ItemRegistrationLayoutConfig.bottomSheetAnimationDuration), value: data.bottomSheet)
        }
    }

    @ViewBuilder
    private func bottomSheetView(
        _ bottomSheet: ItemRegistrationBottomSheet,
        contentHeight: CGFloat,
        bottomPadding: CGFloat
    ) -> some View {
        switch bottomSheet {
        case .kind:
            ItemKindSelectionBottomSheet(
                data: data,
                action: action,
                contentHeight: contentHeight,
                bottomPadding: bottomPadding
            )
        }
    }

    private func bottomSheetContentHeight(
        in geometry: GeometryProxy,
        bottomPadding: CGFloat
    ) -> CGFloat {
        let availableHeight = geometry.size.height
            - geometry.safeAreaInsets.top
            - ItemRegistrationLayoutConfig.bottomSheetTopMargin
            - ItemRegistrationLayoutConfig.bottomSheetHeaderHeight
            - bottomPadding
        return min(ItemRegistrationLayoutConfig.kindSheetContentHeight, max(0, availableHeight))
    }

    private func bottomSheetBottomPadding(in geometry: GeometryProxy) -> CGFloat {
        max(
            ItemRegistrationLayoutConfig.bottomSheetBottomPadding,
            min(geometry.safeAreaInsets.bottom, ItemRegistrationLayoutConfig.bottomSheetMaximumSafeAreaPadding)
        )
    }
}

private struct ItemRegistrationFormView: View {
    @State private var isReplacementDateDropdownExpanded = false

    let data: ItemRegistrationViewData
    let action: ItemRegistrationAction

    var body: some View {
        GeometryReader { geometry in
            let horizontalPadding = ItemRegistrationLayoutConfig.horizontalPadding(for: geometry.size.width)

            VStack(spacing: 0) {
                OBRitDepthTopBar(
                    title: "소모품 등록",
                    backgroundColor: false,
                    onBackClick: action.onBack,
                    onMoreClick: action.onClose
                )

                ScrollView(showsIndicators: false) {
                    VStack(alignment: .leading, spacing: ItemRegistrationLayoutConfig.contentSectionGap) {
                        ItemRegistrationTitle(
                            title: "소모품의 상세 정보를\n입력해주세요",
                            subtitle: "원활한 관리를 위해 구체적인 정보를 입력해주세요"
                        )

                        VStack(alignment: .leading, spacing: ItemRegistrationLayoutConfig.fieldGroupGap) {
                            ItemRequiredField(title: "소모품 종류") {
                                ItemKindPickerField(
                                    value: data.draft.selectedKind?.title ?? "",
                                    placeholder: "소모품 종류를 선택해주세요",
                                    onClick: action.onOpenKindSheet
                                )
                            }

                            ItemRequiredField(title: "소모품명") {
                                ItemTextInputField(
                                    text: data.draft.itemName,
                                    placeholder: "소모품명을 입력해주세요",
                                    maxLength: ItemRegistrationConfig.itemNameMaxLength,
                                    helperText: "\(ItemRegistrationConfig.itemNameMaxLength)자 이내로 입력해주세요",
                                    singleLine: true,
                                    onTextChange: action.onUpdateItemName
                                )
                            }

                            ItemRequiredField(title: "마지막 교체 일자") {
                                ItemReplacementDateDropdown(
                                    selectedOption: data.draft.lastReplacementDateOption,
                                    isExpanded: $isReplacementDateDropdownExpanded,
                                    onSelect: { option in
                                        action.onSelectReplacementDate(option)
                                        isReplacementDateDropdownExpanded = false
                                    }
                                )
                            }
                            .zIndex(isReplacementDateDropdownExpanded ? 10 : 0)

                            ItemRequiredField(title: "등록할 수량") {
                                ItemQuantityCard(
                                    kind: data.draft.selectedKind,
                                    quantity: data.draft.quantity,
                                    quantityLabelPrefix: "전체",
                                    helperText: "소모품의 전체 수량은 추후 수정할 수 있어요.",
                                    action: action
                                )
                            }
                        }

                        ItemScrollButton(
                            text: "소모품 등록하기",
                            enabled: data.canSubmitForm,
                            action: action.onSubmitForm
                        )
                    }
                    .padding(.horizontal, horizontalPadding)
                    .padding(.top, ItemRegistrationLayoutConfig.topContentInset)
                    .padding(.bottom, ItemRegistrationLayoutConfig.scrollBottomPadding)
                }
                .scrollDismissesKeyboard(.interactively)
                .background(
                    OBRitColors.backgroundDefaultDefault
                        .contentShape(Rectangle())
                        .onTapGesture(perform: dismissKeyboard)
                )
            }
            .frame(width: geometry.size.width, height: geometry.size.height)
            .background(OBRitColors.backgroundDefaultDefault)
        }
    }
}

private struct ItemDirectKindRegistrationView: View {
    let data: ItemRegistrationViewData
    let action: ItemRegistrationAction

    var body: some View {
        GeometryReader { geometry in
            let horizontalPadding = ItemRegistrationLayoutConfig.horizontalPadding(for: geometry.size.width)

            VStack(spacing: 0) {
                OBRitDepthTopBar(
                    title: "소모품 직접 등록",
                    backgroundColor: false,
                    onBackClick: action.onBack,
                    onMoreClick: action.onClose
                )

                ScrollView(showsIndicators: false) {
                    VStack(alignment: .leading, spacing: ItemRegistrationLayoutConfig.contentSectionGap) {
                        ItemRegistrationTitle(
                            title: "소모품 종류 직접 등록하기",
                            subtitle: "원하는 소모품 종류가 없다면 직접 추가할 수 있어요."
                        )

                        VStack(alignment: .leading, spacing: ItemRegistrationLayoutConfig.fieldGroupGap) {
                            ItemRequiredField(title: "소모품 종류명") {
                                ItemTextInputField(
                                    text: data.draft.directKindName,
                                    placeholder: "소모품 종류명을 입력해주세요",
                                    maxLength: ItemRegistrationConfig.kindNameMaxLength,
                                    helperText: "\(ItemRegistrationConfig.kindNameMaxLength)자 이내로 입력해주세요",
                                    singleLine: true,
                                    onTextChange: action.onUpdateDirectKindName
                                )
                            }

                            ItemRequiredField(title: "대표 이미지") {
                                LazyVGrid(
                                    columns: ItemRegistrationLayoutConfig.imageGridColumns,
                                    alignment: .leading,
                                    spacing: ItemRegistrationLayoutConfig.imageGridRowGap
                                ) {
                                    ForEach(data.imageOptions) { option in
                                        ItemImageOptionButton(
                                            option: option,
                                            selected: option == data.draft.selectedImageOption,
                                            onSelect: { action.onSelectImageOption(option) }
                                        )
                                    }
                                }
                            }
                        }

                        ItemScrollButton(
                            text: "소모품 종류 등록하기",
                            enabled: data.canSubmitDirectKind,
                            action: action.onSubmitDirectKind
                        )
                    }
                    .padding(.horizontal, horizontalPadding)
                    .padding(.top, ItemRegistrationLayoutConfig.topContentInset)
                    .padding(.bottom, ItemRegistrationLayoutConfig.scrollBottomPadding)
                }
                .scrollDismissesKeyboard(.interactively)
                .background(
                    OBRitColors.backgroundDefaultDefault
                        .contentShape(Rectangle())
                        .onTapGesture(perform: dismissKeyboard)
                )
            }
            .frame(width: geometry.size.width, height: geometry.size.height)
            .background(OBRitColors.backgroundDefaultDefault)
        }
    }
}

private struct ItemRegistrationCompleteView: View {
    let action: ItemRegistrationAction

    var body: some View {
        GeometryReader { geometry in
            let horizontalPadding = ItemRegistrationLayoutConfig.horizontalPadding(for: geometry.size.width)

            VStack(spacing: 0) {
                Spacer(minLength: 0)

                VStack(spacing: ItemRegistrationLayoutConfig.completeContentGap) {
                    Image("item_registration_complete_badge")
                        .resizable()
                        .scaledToFit()
                        .frame(
                            width: ItemRegistrationLayoutConfig.completeBadgeWidth,
                            height: ItemRegistrationLayoutConfig.completeBadgeHeight
                        )

                    VStack(spacing: OBRitSpacing.s4) {
                        Text("소모품이 등록되었어요!")
                            .lineLimit(1)
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
                .padding(.bottom, geometry.size.height * ItemRegistrationLayoutConfig.completeCenterYOffsetRatio)

                Spacer(minLength: 0)

                ItemBottomButton(
                    text: "홈 화면으로 돌아가기",
                    enabled: true,
                    horizontalPadding: horizontalPadding,
                    action: action.onComplete
                )
            }
        }
    }
}

private struct ItemRegistrationTitle: View {
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
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.vertical, OBRitSpacing.s4)
    }
}

private struct ItemRequiredField<Content: View>: View {
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
                        color: OBRitColors.red300
                    )
                    .padding(.top, 1)
            }

            content
        }
    }
}

private struct ItemKindPickerField: View {
    let value: String
    let placeholder: String
    let onClick: () -> Void

    var body: some View {
        Button(action: onClick) {
            HStack(spacing: OBRitSpacing.s2) {
                Text(value.isEmpty ? placeholder : value)
                    .lineLimit(1)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .obritTextStyle(
                        OBRitTypography.xl,
                        weight: OBRitFontWeight.medium,
                        color: value.isEmpty ? OBRitColors.gray700 : OBRitColors.common00
                    )

                Image(systemName: "magnifyingglass")
                    .font(.system(size: OBRitSpacing.s5, weight: .regular))
                    .foregroundStyle(OBRitColors.common00)
                    .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
            }
            .frame(height: ItemRegistrationLayoutConfig.fieldHeight)
            .padding(.horizontal, OBRitSpacing.s5)
            .background(OBRitColors.gray800)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
            .contentShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
        }
        .buttonStyle(.plain)
    }
}

private struct ItemDropdownPickerField: View {
    let value: String
    let placeholder: String
    let onClick: () -> Void

    var body: some View {
        Button(action: onClick) {
            HStack(spacing: OBRitSpacing.s2) {
                Text(value.isEmpty ? placeholder : value)
                    .lineLimit(1)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .obritTextStyle(
                        OBRitTypography.xl,
                        weight: OBRitFontWeight.medium,
                        color: value.isEmpty ? OBRitColors.gray700 : OBRitColors.common00
                    )

                OBRitIcon(kind: .chevronDown, color: OBRitColors.common00)
                    .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
            }
            .frame(height: ItemRegistrationLayoutConfig.fieldHeight)
            .padding(.horizontal, OBRitSpacing.s5)
            .background(OBRitColors.gray800)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
            .contentShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
        }
        .buttonStyle(.plain)
    }
}

private struct ItemReplacementDateDropdown: View {
    let selectedOption: ItemReplacementDateOption?
    @Binding var isExpanded: Bool
    let onSelect: (ItemReplacementDateOption) -> Void

    var body: some View {
        ZStack(alignment: .topLeading) {
            ItemDropdownPickerField(
                value: selectedOption?.title ?? "",
                placeholder: "마지막 교체 일자를 등록해주세요",
                onClick: {
                    dismissKeyboard()
                    isExpanded.toggle()
                }
            )
            .frame(maxWidth: .infinity)

            if isExpanded {
                OBRitDropdownMenu(
                    items: ItemReplacementDateOption.allCases.map(\.title),
                    selectedIndex: selectedOption?.rawValue,
                    fillsWidth: true,
                    onItemClick: { index in
                        guard let option = ItemReplacementDateOption(rawValue: index) else { return }
                        onSelect(option)
                    }
                )
                .frame(maxWidth: .infinity)
                .offset(y: ItemRegistrationLayoutConfig.fieldHeight + OBRitSpacing.s2)
                .zIndex(1)
            }
        }
        .frame(maxWidth: .infinity)
        .frame(height: ItemRegistrationLayoutConfig.fieldHeight, alignment: .top)
        .zIndex(isExpanded ? 10 : 0)
    }
}

private struct ItemTextInputField: View {
    @State private var localText: String
    @FocusState private var isFocused: Bool

    let text: String
    let placeholder: String
    let maxLength: Int
    let helperText: String
    let singleLine: Bool
    let onTextChange: (String) -> Void

    init(
        text: String,
        placeholder: String,
        maxLength: Int,
        helperText: String,
        singleLine: Bool,
        onTextChange: @escaping (String) -> Void
    ) {
        _localText = State(initialValue: String(text.prefix(maxLength)))
        self.text = text
        self.placeholder = placeholder
        self.maxLength = maxLength
        self.helperText = helperText
        self.singleLine = singleLine
        self.onTextChange = onTextChange
    }

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s2_5) {
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

                    TextField("", text: $localText, axis: singleLine ? .horizontal : .vertical)
                        .lineLimit(singleLine ? 1 : nil)
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
                        OBRitTypography.small,
                        weight: OBRitFontWeight.medium,
                        color: OBRitColors.common00
                    )
            }
            .frame(height: ItemRegistrationLayoutConfig.fieldHeight)
            .padding(.horizontal, OBRitSpacing.s5)
            .background(OBRitColors.gray800)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
            .contentShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
            .onTapGesture {
                isFocused = true
            }

            HStack(alignment: .center, spacing: OBRitSpacing.s1) {
                OBRitIcon(kind: .success, color: OBRitColors.gray300)
                    .frame(width: OBRitSpacing.s4, height: OBRitSpacing.s4)
                Text(helperText)
                    .fixedSize(horizontal: false, vertical: true)
                    .obritTextStyle(
                        OBRitTypography.s,
                        weight: OBRitFontWeight.medium,
                        color: OBRitColors.gray300
                    )
            }
        }
        .onChange(of: localText) { _, newValue in
            let clippedText = String(newValue.prefix(maxLength))
            if clippedText != newValue {
                localText = clippedText
            }
            onTextChange(clippedText)
        }
        .onChange(of: text) { _, newValue in
            let clippedText = String(newValue.prefix(maxLength))
            guard clippedText != localText else { return }
            localText = clippedText
        }
    }
}

private struct ItemSearchInputField: View {
    @State private var localText: String
    @FocusState private var isFocused: Bool

    let text: String
    let placeholder: String
    let onTextChange: (String) -> Void

    init(
        text: String,
        placeholder: String,
        onTextChange: @escaping (String) -> Void
    ) {
        _localText = State(initialValue: text)
        self.text = text
        self.placeholder = placeholder
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

                TextField("", text: $localText)
                    .lineLimit(1)
                    .submitLabel(.search)
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

            Image(systemName: "magnifyingglass")
                .font(.system(size: OBRitSpacing.s6, weight: .regular))
                .foregroundStyle(OBRitColors.common00)
                .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
        }
        .frame(height: ItemRegistrationLayoutConfig.fieldHeight)
        .padding(.horizontal, OBRitSpacing.s5)
        .overlay {
            RoundedRectangle(cornerRadius: OBRitRadius.middle)
                .stroke(OBRitColors.gray300, lineWidth: 1.4)
        }
        .onChange(of: localText) { _, newValue in
            onTextChange(newValue)
        }
        .onChange(of: text) { _, newValue in
            guard newValue != localText else { return }
            localText = newValue
        }
    }
}

private struct ItemQuantityCard: View {
    let kind: ItemKind?
    let quantity: Int
    let quantityLabelPrefix: String
    let helperText: String
    let action: ItemRegistrationAction

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s2) {
            HStack(spacing: OBRitSpacing.s4) {
                ItemImage(
                    assetName: kind?.imageAssetName ?? "item_razor",
                    size: ItemRegistrationLayoutConfig.quantityImageSize
                )

                VStack(alignment: .leading, spacing: OBRitSpacing.s1) {
                    Text(kind?.title ?? "{title}")
                        .lineLimit(1)
                        .obritTextStyle(
                            OBRitTypography.xl,
                            weight: OBRitFontWeight.bold,
                            color: OBRitColors.common00
                        )
                    Text("\(quantityLabelPrefix) \(quantity)개")
                        .lineLimit(1)
                        .obritTextStyle(
                            OBRitTypography.small,
                            weight: OBRitFontWeight.medium,
                            color: OBRitColors.common00.opacity(0.64)
                        )
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                OBRitStepper(
                    value: quantity,
                    size: .small,
                    minimumValue: ItemRegistrationConfig.quantityMinimum,
                    onDecrement: action.onDecrementQuantity,
                    onIncrement: action.onIncrementQuantity
                )
            }
            .padding(OBRitSpacing.s5)
            .background(OBRitColors.backgroundDefaultSecondary)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))

            HStack(spacing: OBRitSpacing.s1_5) {
                OBRitIcon(kind: .info, color: OBRitColors.textDefaultTertiary)
                    .frame(width: OBRitSpacing.s4, height: OBRitSpacing.s4)
                Text(helperText)
                    .fixedSize(horizontal: false, vertical: true)
                    .obritTextStyle(
                        OBRitTypography.base,
                        weight: OBRitFontWeight.semiBold,
                        color: OBRitColors.textDefaultTertiary
                    )
            }
        }
    }
}

private struct ItemKindSelectionBottomSheet: View {
    let data: ItemRegistrationViewData
    let action: ItemRegistrationAction
    let contentHeight: CGFloat
    let bottomPadding: CGFloat

    var body: some View {
        let filteredKinds = data.filteredKinds

        OBRitBottomSheet(
            contentHeight: contentHeight,
            bottomPadding: bottomPadding,
            onDismiss: action.onDismissBottomSheet
        ) {
            VStack(alignment: .leading, spacing: OBRitSpacing.s8) {
                ItemSearchInputField(
                    text: data.kindSearchQuery,
                    placeholder: "원하시는 소모품을 검색해보세요",
                    onTextChange: action.onUpdateKindSearchQuery
                )

                VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
                    ItemSheetCountText(
                        prefix: data.kindSearchQuery.isEmpty ? "전체 소모품" : "검색 결과",
                        count: data.kindSearchQuery.isEmpty ? data.itemKinds.count : filteredKinds.count
                    )

                    if filteredKinds.isEmpty {
                        ItemKindNoResultView(action: action)
                    } else {
                        ScrollView(showsIndicators: false) {
                            LazyVStack(spacing: OBRitSpacing.s2) {
                                ForEach(filteredKinds) { kind in
                                    ItemKindSelectionRow(
                                        kind: kind,
                                        selected: kind == data.kindCandidateForDisplay,
                                        action: action
                                    )
                                }
                            }
                            .padding(.bottom, ItemRegistrationLayoutConfig.sheetButtonHeight + OBRitSpacing.s6)
                        }
                        .overlay(alignment: .bottom) {
                            ItemSheetGradientButton(
                                text: "소모품 종류 선택하기",
                                enabled: data.selectedKindCandidate != nil,
                                action: action.onConfirmKindSelection
                            )
                        }
                    }
                }
            }
        }
    }
}

private struct ItemSheetCountText: View {
    let prefix: String
    let count: Int

    var body: some View {
        (
            Text(prefix + " ")
                .foregroundColor(OBRitColors.common00) +
                Text("\(count)개")
                .foregroundColor(OBRitColors.green300)
        )
        .font(OBRitTypography.font(OBRitTypography.s3xl, weight: OBRitFontWeight.bold))
        .tracking(OBRitTypography.letterSpacing(for: OBRitTypography.s3xl))
    }
}

private struct ItemKindSelectionRow: View {
    let kind: ItemKind
    let selected: Bool
    let action: ItemRegistrationAction

    var body: some View {
        Button {
            action.onSelectKindCandidate(kind)
        } label: {
            HStack(spacing: OBRitSpacing.s4) {
                ItemImage(
                    assetName: kind.imageAssetName,
                    size: ItemRegistrationLayoutConfig.sheetRowImageSize
                )

                VStack(alignment: .leading, spacing: OBRitSpacing.s1) {
                    Text(kind.title)
                        .lineLimit(1)
                        .obritTextStyle(
                            OBRitTypography.xl,
                            weight: OBRitFontWeight.bold,
                            color: OBRitColors.common00
                        )
                    Text("추가된 소모품 \(kind.addedCount)개")
                        .lineLimit(1)
                        .obritTextStyle(
                            OBRitTypography.small,
                            weight: OBRitFontWeight.medium,
                            color: OBRitColors.common00.opacity(0.64)
                        )
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                ItemKindSelectionRadioIndicator(selected: selected)
                    .frame(width: OBRitSpacing.s10, height: OBRitSpacing.s10)
            }
            .padding(.horizontal, OBRitSpacing.s5)
            .padding(.vertical, OBRitSpacing.s4)
            .background(OBRitColors.backgroundDefaultSecondary)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
        }
        .buttonStyle(.plain)
    }
}

private struct ItemKindSelectionRadioIndicator: View {
    let selected: Bool

    var body: some View {
        ZStack {
            Circle()
                .stroke(ringColor, lineWidth: 1.5)
                .frame(width: 21, height: 21)
            if selected {
                Circle()
                    .fill(ringColor)
                    .frame(width: OBRitSpacing.s3, height: OBRitSpacing.s3)
            }
        }
        .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
        .accessibilityHidden(true)
    }

    private var ringColor: Color {
        selected ? OBRitColors.green300 : OBRitColors.gray400
    }
}

private struct ItemKindNoResultView: View {
    let action: ItemRegistrationAction

    var body: some View {
        VStack(spacing: OBRitSpacing.s5) {
            VStack(spacing: OBRitSpacing.s2) {
                Text("소모품 검색 결과가 없어요!")
                    .obritTextStyle(
                        OBRitTypography.s3xl,
                        weight: OBRitFontWeight.bold,
                        color: OBRitColors.common00
                    )
                Text("직접 소모품 종류를 등록할 수 있어요.")
                    .obritTextStyle(
                        OBRitTypography.base,
                        weight: OBRitFontWeight.medium,
                        color: OBRitColors.textDefaultSecondary
                    )
            }

            OBRitFilledTextButton(
                text: "직접 등록",
                size: .small,
                color: .white,
                action: action.onShowDirectKindRegistration
            )
        }
        .frame(maxWidth: .infinity)
        .frame(height: ItemRegistrationLayoutConfig.noResultHeight)

        ItemSheetGradientButton(text: "소모품 종류 선택하기", enabled: false, action: {})
    }
}

private struct ItemSheetGradientButton: View {
    let text: String
    let enabled: Bool
    let action: () -> Void

    init(text: String, enabled: Bool = true, action: @escaping () -> Void) {
        self.text = text
        self.enabled = enabled
        self.action = action
    }

    var body: some View {
        VStack(spacing: 0) {
            LinearGradient(
                colors: [OBRitColors.backgroundDefaultDefault.opacity(0), OBRitColors.backgroundDefaultDefault],
                startPoint: .top,
                endPoint: .bottom
            )
            .frame(height: OBRitSpacing.s5)

            OBRitFilledTextButton(
                text: text,
                color: .green,
                enabled: enabled,
                fillsWidth: true,
                action: action
            )
        }
        .background(OBRitColors.backgroundDefaultDefault)
        .disabled(!enabled)
    }
}

private struct ItemImageOptionButton: View {
    let option: ItemImageOption
    let selected: Bool
    let onSelect: () -> Void

    var body: some View {
        Button(action: onSelect) {
            Image(option.assetName)
                .resizable()
                .scaledToFill()
                .frame(
                    width: ItemRegistrationLayoutConfig.imageOptionSize,
                    height: ItemRegistrationLayoutConfig.imageOptionSize
                )
                .clipShape(Circle())
                .overlay {
                    if selected {
                        Circle()
                            .stroke(OBRitColors.green300, lineWidth: 2)
                    }
                }
                .contentShape(Circle())
        }
        .buttonStyle(.plain)
    }
}

private struct ItemImage: View {
    let assetName: String
    let size: CGFloat

    var body: some View {
        Image(assetName)
            .resizable()
            .scaledToFill()
            .frame(width: size, height: size)
            .clipShape(Circle())
    }
}

private struct ItemBottomButton: View {
    let text: String
    let enabled: Bool
    let horizontalPadding: CGFloat
    let action: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            OBRitFilledTextButton(
                text: text,
                color: .green,
                enabled: enabled,
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

private struct ItemScrollButton: View {
    let text: String
    let enabled: Bool
    let action: () -> Void

    var body: some View {
        OBRitFilledTextButton(
            text: text,
            color: .green,
            enabled: enabled,
            fillsWidth: true,
            action: action
        )
        .padding(.top, OBRitSpacing.s5)
    }
}

private enum ItemRegistrationLayoutConfig {
    static let designWidth: CGFloat = 412
    static let horizontalInsetRatio: CGFloat = 20 / designWidth
    static let topContentInset: CGFloat = OBRitSpacing.s5
    static let contentSectionGap: CGFloat = OBRitSpacing.s7
    static let fieldGroupGap: CGFloat = OBRitSpacing.s10
    static let fieldHeight: CGFloat = 56
    static let scrollBottomPadding: CGFloat = OBRitSpacing.s10
    static let quantityImageSize: CGFloat = 52
    static let sheetRowImageSize: CGFloat = 52
    static let imageOptionSize: CGFloat = 60
    static let imageGridRowGap: CGFloat = OBRitSpacing.s3
    static let kindSheetContentHeight: CGFloat = 766
    static let noResultHeight: CGFloat = 278
    static let sheetButtonHeight: CGFloat = 76
    static let dimOpacity: CGFloat = 0.8
    static let completeBadgeWidth: CGFloat = 245
    static let completeBadgeHeight: CGFloat = 255
    static let completeContentGap: CGFloat = OBRitSpacing.s9
    static let completeCenterYOffsetRatio: CGFloat = 0.07
    static let bottomSheetAnimationDuration: Double = 0.24
    static let bottomSheetTopMargin: CGFloat = OBRitSpacing.s5
    static let bottomSheetHeaderHeight: CGFloat = OBRitSpacing.s1 + OBRitSpacing.s8 + OBRitSpacing.s2_5
    static let bottomSheetBottomPadding: CGFloat = OBRitSpacing.s5
    static let bottomSheetMaximumSafeAreaPadding: CGFloat = 48

    static let imageGridColumns: [GridItem] = Array(
        repeating: GridItem(.fixed(imageOptionSize), spacing: 18, alignment: .leading),
        count: 5
    )

    static func horizontalPadding(for width: CGFloat) -> CGFloat {
        max(OBRitSpacing.s4, min(OBRitSpacing.s7, width * horizontalInsetRatio))
    }
}

private func dismissKeyboard() {
    UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
}

#Preview("Item Registration") {
    ItemRegistrationView(
        onBack: {},
        onClose: {},
        onComplete: {}
    )
}
