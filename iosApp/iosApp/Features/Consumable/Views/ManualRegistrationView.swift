import SwiftUI
import Shared
import UIKit

struct ManualRegistrationView: View {
    @StateObject private var viewModel: ManualRegistrationViewModel

    let onBack: () -> Void
    let onClose: () -> Void
    let onComplete: () -> Void

    init(
        onBack: @escaping () -> Void,
        onClose: @escaping () -> Void,
        onComplete: @escaping () -> Void
    ) {
        _viewModel = StateObject(wrappedValue: ManualRegistrationViewModel())
        self.onBack = onBack
        self.onClose = onClose
        self.onComplete = onComplete
    }

    init(
        viewModel: ManualRegistrationViewModel,
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
            ManualRegistrationContentView(
                data: data,
                action: ManualRegistrationAction(
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

struct ManualRegistrationAction {
    let onBack: () -> Void
    let onClose: () -> Void
    let onUpdateItemName: (String) -> Void
    let onOpenKindSheet: () -> Void
    let onDismissBottomSheet: () -> Void
    let onUpdateKindSearchQuery: (String) -> Void
    let onSelectKind: (ManualConsumableKind) -> Void
    let onSelectKindCandidate: (ManualConsumableKind) -> Void
    let onConfirmKindSelection: () -> Void
    let onSelectReplacementDate: (ManualReplacementDateOption) -> Void
    let onIncrementQuantity: () -> Void
    let onDecrementQuantity: () -> Void
    let onShowDirectKindRegistration: () -> Void
    let onUpdateDirectKindName: (String) -> Void
    let onSelectImageOption: (ManualConsumableImageOption) -> Void
    let onSubmitDirectKind: () -> Void
    let onSubmitForm: () -> Void
    let onComplete: () -> Void
}

private struct ManualRegistrationContentView: View {
    let data: ManualRegistrationViewData
    let action: ManualRegistrationAction

    var body: some View {
        GeometryReader { geometry in
            ZStack {
                OBRitColors.backgroundDefaultDefault
                    .ignoresSafeArea()

                switch data.mode {
                case .form:
                    ManualRegistrationFormView(data: data, action: action)
                case .directKind:
                    ManualDirectKindRegistrationView(data: data, action: action)
                case .complete:
                    ManualRegistrationCompleteView(action: action)
                }

                if let bottomSheet = data.bottomSheet {
                    Color.black.opacity(ManualRegistrationLayoutConfig.dimOpacity)
                        .ignoresSafeArea()
                        .onTapGesture(perform: action.onDismissBottomSheet)
                        .transition(.opacity)

                    VStack {
                        Spacer(minLength: 0)
                        bottomSheetView(bottomSheet)
                            .transition(.move(edge: .bottom).combined(with: .opacity))
                    }
                    .ignoresSafeArea(edges: .bottom)
                }
            }
            .frame(width: geometry.size.width, height: geometry.size.height)
            .animation(.easeOut(duration: ManualRegistrationLayoutConfig.bottomSheetAnimationDuration), value: data.bottomSheet)
        }
    }

    @ViewBuilder
    private func bottomSheetView(_ bottomSheet: ManualRegistrationBottomSheet) -> some View {
        switch bottomSheet {
        case .kind:
            ManualKindSelectionBottomSheet(data: data, action: action)
        }
    }
}

private struct ManualRegistrationFormView: View {
    @State private var isReplacementDateDropdownExpanded = false

    let data: ManualRegistrationViewData
    let action: ManualRegistrationAction

    var body: some View {
        GeometryReader { geometry in
            let horizontalPadding = ManualRegistrationLayoutConfig.horizontalPadding(for: geometry.size.width)

            VStack(spacing: 0) {
                OBRitDepthTopBar(
                    title: "소모품 등록",
                    backgroundColor: false,
                    onBackClick: action.onBack,
                    onMoreClick: action.onClose
                )

                ScrollView(showsIndicators: false) {
                    VStack(alignment: .leading, spacing: ManualRegistrationLayoutConfig.contentSectionGap) {
                        ManualRegistrationTitle(
                            title: "소모품의 상세 정보를\n입력해주세요",
                            subtitle: "원활한 관리를 위해 구체적인 정보를 입력해주세요"
                        )

                        VStack(alignment: .leading, spacing: ManualRegistrationLayoutConfig.fieldGroupGap) {
                            ManualRequiredField(title: "소모품 종류") {
                                ManualKindPickerField(
                                    value: data.draft.selectedKind?.title ?? "",
                                    placeholder: "소모품 종류를 선택해주세요",
                                    onClick: action.onOpenKindSheet
                                )
                            }

                            ManualRequiredField(title: "소모품 명") {
                                ManualTextInputField(
                                    text: Binding(
                                        get: { data.draft.itemName },
                                        set: action.onUpdateItemName
                                    ),
                                    placeholder: "구분을 위한 이름을 입력해주세요",
                                    maxLength: ManualRegistrationConfig.itemNameMaxLength,
                                    singleLine: true
                                )
                            }

                            ManualRequiredField(title: "마지막 교체 일자") {
                                ManualReplacementDateDropdown(
                                    selectedOption: data.draft.lastReplacementDateOption,
                                    isExpanded: $isReplacementDateDropdownExpanded,
                                    onSelect: { option in
                                        action.onSelectReplacementDate(option)
                                        isReplacementDateDropdownExpanded = false
                                    }
                                )
                            }

                            ManualRequiredField(title: "등록할 수량") {
                                ManualQuantityCard(
                                    kind: data.draft.selectedKind,
                                    quantity: data.draft.quantity,
                                    quantityLabelPrefix: "전체",
                                    helperText: "소모품의 전체 수량은 추후 수정할 수 있어요.",
                                    action: action
                                )
                            }
                        }

                        ManualScrollButton(
                            text: "소모품 등록하기",
                            enabled: data.canSubmitForm,
                            action: action.onSubmitForm
                        )
                    }
                    .padding(.horizontal, horizontalPadding)
                    .padding(.top, ManualRegistrationLayoutConfig.topContentInset)
                    .padding(.bottom, ManualRegistrationLayoutConfig.scrollBottomPadding)
                }
                .scrollDismissesKeyboard(.interactively)
                .background(OBRitColors.backgroundDefaultDefault.onTapGesture(perform: dismissKeyboard))
            }
            .frame(width: geometry.size.width, height: geometry.size.height)
        }
    }
}

private struct ManualDirectKindRegistrationView: View {
    let data: ManualRegistrationViewData
    let action: ManualRegistrationAction

    var body: some View {
        GeometryReader { geometry in
            let horizontalPadding = ManualRegistrationLayoutConfig.horizontalPadding(for: geometry.size.width)

            VStack(spacing: 0) {
                OBRitDepthTopBar(
                    title: "소모품 직접 등록",
                    backgroundColor: false,
                    onBackClick: action.onBack,
                    onMoreClick: action.onClose
                )

                ScrollView(showsIndicators: false) {
                    VStack(alignment: .leading, spacing: ManualRegistrationLayoutConfig.contentSectionGap) {
                        ManualRegistrationTitle(
                            title: "소모품 종류 직접 등록하기",
                            subtitle: "원하는 소모품 종류가 없다면 직접 추가할 수 있어요."
                        )

                        VStack(alignment: .leading, spacing: ManualRegistrationLayoutConfig.fieldGroupGap) {
                            ManualRequiredField(title: "소모품 종류 이름") {
                                ManualTextInputField(
                                    text: Binding(
                                        get: { data.draft.directKindName },
                                        set: action.onUpdateDirectKindName
                                    ),
                                    placeholder: "소모품의 종류 이름을 입력해주세요",
                                    maxLength: ManualRegistrationConfig.kindNameMaxLength,
                                    singleLine: true
                                )
                            }

                            ManualRequiredField(title: "대표 이미지") {
                                LazyVGrid(
                                    columns: ManualRegistrationLayoutConfig.imageGridColumns,
                                    alignment: .leading,
                                    spacing: ManualRegistrationLayoutConfig.imageGridRowGap
                                ) {
                                    ForEach(data.imageOptions) { option in
                                        ManualImageOptionButton(
                                            option: option,
                                            selected: option == data.draft.selectedImageOption,
                                            onSelect: { action.onSelectImageOption(option) }
                                        )
                                    }
                                }
                            }
                        }

                        ManualScrollButton(
                            text: "소모품 종류 등록하기",
                            enabled: data.canSubmitDirectKind,
                            action: action.onSubmitDirectKind
                        )
                    }
                    .padding(.horizontal, horizontalPadding)
                    .padding(.top, ManualRegistrationLayoutConfig.topContentInset)
                    .padding(.bottom, ManualRegistrationLayoutConfig.scrollBottomPadding)
                }
                .scrollDismissesKeyboard(.interactively)
                .background(OBRitColors.backgroundDefaultDefault.onTapGesture(perform: dismissKeyboard))
            }
            .frame(width: geometry.size.width, height: geometry.size.height)
        }
    }
}

private struct ManualRegistrationCompleteView: View {
    let action: ManualRegistrationAction

    var body: some View {
        GeometryReader { geometry in
            let horizontalPadding = ManualRegistrationLayoutConfig.horizontalPadding(for: geometry.size.width)

            VStack(spacing: 0) {
                Spacer(minLength: 0)

                VStack(spacing: ManualRegistrationLayoutConfig.completeContentGap) {
                    Image("manual_registration_complete_badge")
                        .resizable()
                        .scaledToFit()
                        .frame(
                            width: ManualRegistrationLayoutConfig.completeBadgeWidth,
                            height: ManualRegistrationLayoutConfig.completeBadgeHeight
                        )

                    VStack(spacing: OBRitSpacing.s4) {
                        Text("소모품이 등록되었어요!")
                            .lineLimit(1)
                            .obritTextStyle(
                                OBRitTypography.s6xl,
                                weight: AtomFontWeight.shared.Bold,
                                color: OBRitColors.common00
                            )

                        Text("이제부터 OBRIT이 간편하게\n소모품을 관리해드릴게요")
                            .multilineTextAlignment(.center)
                            .obritTextStyle(
                                OBRitTypography.xl,
                                weight: AtomFontWeight.shared.Medium,
                                color: OBRitColors.textDefaultSecondary
                            )
                    }
                }
                .frame(maxWidth: .infinity)
                .padding(.bottom, geometry.size.height * ManualRegistrationLayoutConfig.completeCenterYOffsetRatio)

                Spacer(minLength: 0)

                ManualBottomButton(
                    text: "홈 화면으로 돌아가기",
                    enabled: true,
                    horizontalPadding: horizontalPadding,
                    action: action.onComplete
                )
            }
        }
    }
}

private struct ManualRegistrationTitle: View {
    let title: String
    let subtitle: String

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
            Text(title)
                .fixedSize(horizontal: false, vertical: true)
                .obritTextStyle(
                    OBRitTypography.s6xl,
                    weight: AtomFontWeight.shared.Bold,
                    color: OBRitColors.textDefaultDefault
                )

            Text(subtitle)
                .fixedSize(horizontal: false, vertical: true)
                .obritTextStyle(
                    OBRitTypography.xl,
                    weight: AtomFontWeight.shared.Medium,
                    color: OBRitColors.textDefaultSecondary
                )
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.vertical, OBRitSpacing.s4)
    }
}

private struct ManualRequiredField<Content: View>: View {
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
                        weight: AtomFontWeight.shared.SemiBold,
                        color: OBRitColors.common00
                    )
                Text("*")
                    .obritTextStyle(
                        OBRitTypography.base,
                        weight: AtomFontWeight.shared.Bold,
                        color: OBRitColors.red300
                    )
                    .padding(.top, 1)
            }

            content
        }
    }
}

private struct ManualKindPickerField: View {
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
                        weight: AtomFontWeight.shared.Medium,
                        color: value.isEmpty ? OBRitColors.gray700 : OBRitColors.common00
                    )

                Image(systemName: "magnifyingglass")
                    .font(.system(size: OBRitSpacing.s5, weight: .regular))
                    .foregroundStyle(OBRitColors.common00)
                    .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
            }
            .frame(height: ManualRegistrationLayoutConfig.fieldHeight)
            .padding(.horizontal, OBRitSpacing.s5)
            .background(OBRitColors.gray800)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
            .contentShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
        }
        .buttonStyle(.plain)
    }
}

private struct ManualDropdownPickerField: View {
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
                        weight: AtomFontWeight.shared.Medium,
                        color: value.isEmpty ? OBRitColors.gray700 : OBRitColors.common00
                    )

                OBRitIcon(kind: .chevronDown, color: OBRitColors.common00)
                    .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
            }
            .frame(height: ManualRegistrationLayoutConfig.fieldHeight)
            .padding(.horizontal, OBRitSpacing.s5)
            .background(OBRitColors.gray800)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
            .contentShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
        }
        .buttonStyle(.plain)
    }
}

private struct ManualReplacementDateDropdown: View {
    let selectedOption: ManualReplacementDateOption?
    @Binding var isExpanded: Bool
    let onSelect: (ManualReplacementDateOption) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s2) {
            ManualDropdownPickerField(
                value: selectedOption?.title ?? "",
                placeholder: "마지막 교체 일자를 등록해주세요",
                onClick: {
                    dismissKeyboard()
                    withAnimation(.easeOut(duration: ManualRegistrationLayoutConfig.dropdownAnimationDuration)) {
                        isExpanded.toggle()
                    }
                }
            )

            if isExpanded {
                OBRitDropdownMenu(
                    items: ManualReplacementDateOption.allCases.map(\.title),
                    selectedIndex: selectedOption?.rawValue,
                    onItemClick: { index in
                        guard let option = ManualReplacementDateOption(rawValue: index) else { return }
                        onSelect(option)
                    }
                )
                .transition(.opacity.combined(with: .move(edge: .top)))
            }
        }
        .animation(.easeOut(duration: ManualRegistrationLayoutConfig.dropdownAnimationDuration), value: isExpanded)
    }
}

private struct ManualTextInputField: View {
    @Binding var text: String
    let placeholder: String
    let maxLength: Int
    let singleLine: Bool

    var body: some View {
        HStack(spacing: OBRitSpacing.s2) {
            ZStack(alignment: .leading) {
                if text.isEmpty {
                    Text(placeholder)
                        .lineLimit(1)
                        .obritTextStyle(
                            OBRitTypography.xl,
                            weight: AtomFontWeight.shared.Medium,
                            color: OBRitColors.gray700
                        )
                }

                TextField("", text: $text, axis: singleLine ? .horizontal : .vertical)
                    .lineLimit(singleLine ? 1 : nil)
                    .textInputAutocapitalization(.never)
                    .autocorrectionDisabled()
                    .tint(OBRitColors.common00)
                    .obritTextStyle(
                        OBRitTypography.xl,
                        weight: AtomFontWeight.shared.Medium,
                        color: OBRitColors.common00
                    )
            }
            .frame(maxWidth: .infinity, alignment: .leading)

            Text("\(min(text.count, maxLength))/\(maxLength)")
                .lineLimit(1)
                .obritTextStyle(
                    OBRitTypography.small,
                    weight: AtomFontWeight.shared.Medium,
                    color: OBRitColors.common00
                )
        }
        .frame(height: ManualRegistrationLayoutConfig.fieldHeight)
        .padding(.horizontal, OBRitSpacing.s5)
        .background(OBRitColors.gray800)
        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
    }
}

private struct ManualSearchInputField: View {
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
                            weight: AtomFontWeight.shared.Medium,
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
                        weight: AtomFontWeight.shared.Medium,
                        color: OBRitColors.common00
                    )
            }
            .frame(maxWidth: .infinity, alignment: .leading)

            Image(systemName: "magnifyingglass")
                .font(.system(size: OBRitSpacing.s6, weight: .regular))
                .foregroundStyle(OBRitColors.common00)
                .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
        }
        .frame(height: ManualRegistrationLayoutConfig.fieldHeight)
        .padding(.horizontal, OBRitSpacing.s5)
        .overlay {
            RoundedRectangle(cornerRadius: OBRitRadius.middle)
                .stroke(OBRitColors.gray300, lineWidth: 1.4)
        }
        .onAppear {
            isFocused = true
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

private struct ManualQuantityCard: View {
    let kind: ManualConsumableKind?
    let quantity: Int
    let quantityLabelPrefix: String
    let helperText: String
    let action: ManualRegistrationAction

    var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s2) {
            HStack(spacing: OBRitSpacing.s4) {
                ManualConsumableImage(
                    assetName: kind?.imageAssetName ?? "manual_consumable_razor",
                    size: ManualRegistrationLayoutConfig.quantityImageSize
                )

                VStack(alignment: .leading, spacing: OBRitSpacing.s1) {
                    Text(kind?.title ?? "{title}")
                        .lineLimit(1)
                        .obritTextStyle(
                            OBRitTypography.xl,
                            weight: AtomFontWeight.shared.Bold,
                            color: OBRitColors.common00
                        )
                    Text("\(quantityLabelPrefix) \(quantity)개")
                        .lineLimit(1)
                        .obritTextStyle(
                            OBRitTypography.small,
                            weight: AtomFontWeight.shared.Medium,
                            color: OBRitColors.common00.opacity(0.64)
                        )
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                OBRitStepper(
                    value: quantity,
                    size: .small,
                    minimumValue: ManualRegistrationConfig.quantityMinimum,
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
                        weight: AtomFontWeight.shared.SemiBold,
                        color: OBRitColors.textDefaultTertiary
                    )
            }
        }
    }
}

private struct ManualKindSelectionBottomSheet: View {
    let data: ManualRegistrationViewData
    let action: ManualRegistrationAction

    var body: some View {
        OBRitBottomSheet(contentHeight: ManualRegistrationLayoutConfig.kindSheetContentHeight) {
            VStack(alignment: .leading, spacing: OBRitSpacing.s8) {
                ManualSearchInputField(
                    text: data.kindSearchQuery,
                    placeholder: "원하시는 소모품을 검색해보세요",
                    onTextChange: action.onUpdateKindSearchQuery
                )

                VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
                    ManualSheetCountText(
                        prefix: data.kindSearchQuery.isEmpty ? "전체 소모품" : "검색 결과",
                        count: data.kindSearchQuery.isEmpty ? data.consumableKinds.count : data.filteredKinds.count
                    )

                    if data.filteredKinds.isEmpty {
                        ManualKindNoResultView(action: action)
                    } else {
                        ScrollView(showsIndicators: false) {
                            LazyVStack(spacing: OBRitSpacing.s2) {
                                ForEach(data.filteredKinds) { kind in
                                    ManualKindSelectionRow(
                                        kind: kind,
                                        selected: kind == data.kindCandidateForDisplay,
                                        action: action
                                    )
                                }
                            }
                            .padding(.bottom, ManualRegistrationLayoutConfig.sheetButtonHeight + OBRitSpacing.s6)
                        }
                        .overlay(alignment: .bottom) {
                            ManualSheetGradientButton(
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

private struct ManualSheetCountText: View {
    let prefix: String
    let count: Int

    var body: some View {
        (
            Text(prefix + " ")
                .foregroundColor(OBRitColors.common00) +
                Text("\(count)개")
                .foregroundColor(OBRitColors.green300)
        )
        .font(OBRitTypography.font(OBRitTypography.s3xl, weight: AtomFontWeight.shared.Bold))
        .tracking(OBRitTypography.letterSpacing(for: OBRitTypography.s3xl))
    }
}

private struct ManualKindSelectionRow: View {
    let kind: ManualConsumableKind
    let selected: Bool
    let action: ManualRegistrationAction

    var body: some View {
        Button {
            action.onSelectKindCandidate(kind)
        } label: {
            HStack(spacing: OBRitSpacing.s4) {
                ManualConsumableImage(
                    assetName: kind.imageAssetName,
                    size: ManualRegistrationLayoutConfig.sheetRowImageSize
                )

                VStack(alignment: .leading, spacing: OBRitSpacing.s1) {
                    Text(kind.title)
                        .lineLimit(1)
                        .obritTextStyle(
                            OBRitTypography.xl,
                            weight: AtomFontWeight.shared.Bold,
                            color: OBRitColors.common00
                        )
                    Text("추가된 소모품 \(kind.addedCount)개")
                        .lineLimit(1)
                        .obritTextStyle(
                            OBRitTypography.small,
                            weight: AtomFontWeight.shared.Medium,
                            color: OBRitColors.common00.opacity(0.64)
                        )
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                OBRitRadioButton(selected: selected, onClick: { action.onSelectKindCandidate(kind) })
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

private struct ManualKindNoResultView: View {
    let action: ManualRegistrationAction

    var body: some View {
        VStack(spacing: OBRitSpacing.s5) {
            VStack(spacing: OBRitSpacing.s2) {
                Text("소모품 검색 결과가 없어요!")
                    .obritTextStyle(
                        OBRitTypography.s3xl,
                        weight: AtomFontWeight.shared.Bold,
                        color: OBRitColors.common00
                    )
                Text("직접 소모품 종류를 등록할 수 있어요.")
                    .obritTextStyle(
                        OBRitTypography.base,
                        weight: AtomFontWeight.shared.Medium,
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
        .frame(height: ManualRegistrationLayoutConfig.noResultHeight)

        ManualSheetGradientButton(text: "소모품 종류 선택하기", enabled: false, action: {})
    }
}

private struct ManualSheetGradientButton: View {
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

private struct ManualImageOptionButton: View {
    let option: ManualConsumableImageOption
    let selected: Bool
    let onSelect: () -> Void

    var body: some View {
        Button(action: onSelect) {
            Image(option.assetName)
                .resizable()
                .scaledToFill()
                .frame(
                    width: ManualRegistrationLayoutConfig.imageOptionSize,
                    height: ManualRegistrationLayoutConfig.imageOptionSize
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

private struct ManualConsumableImage: View {
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

private struct ManualBottomButton: View {
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

private struct ManualScrollButton: View {
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

private enum ManualRegistrationLayoutConfig {
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
    static let dropdownAnimationDuration: Double = 0.18

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

#Preview("Manual Registration") {
    ManualRegistrationView(
        onBack: {},
        onClose: {},
        onComplete: {}
    )
}
