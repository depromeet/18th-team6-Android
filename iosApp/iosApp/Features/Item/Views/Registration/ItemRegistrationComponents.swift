import SwiftUI

struct ItemRegistrationTitle: View {
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

struct ItemRequiredField<Content: View>: View {
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
                    .padding(.top, ItemRegistrationLayout.requiredMarkerTopPadding)
            }

            content
        }
    }
}

struct ItemKindPickerField: View {
    let value: String
    let placeholder: String
    let onClick: () -> Void

    var body: some View {
        Button {
            dismissKeyboard()
            onClick()
        } label: {
            HStack(spacing: OBRitSpacing.s2) {
                Text(value.isEmpty ? placeholder : value)
                    .lineLimit(ItemRegistrationLayout.singleLineLimit)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .obritTextStyle(
                        OBRitTypography.xl,
                        weight: OBRitFontWeight.medium,
                        color: value.isEmpty ? OBRitColors.gray700 : OBRitColors.common00
                    )

                Image(systemName: ItemRegistrationAsset.searchSymbol)
                    .font(.system(size: OBRitSpacing.s5, weight: .regular))
                    .foregroundStyle(OBRitColors.common00)
                    .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
            }
            .frame(height: ItemRegistrationLayout.fieldHeight)
            .padding(.horizontal, OBRitSpacing.s5)
            .background(OBRitColors.gray800)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
            .contentShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
        }
        .buttonStyle(.plain)
    }
}

struct ItemDropdownPickerField: View {
    let value: String
    let placeholder: String
    let onClick: () -> Void

    var body: some View {
        Button(action: onClick) {
            HStack(spacing: OBRitSpacing.s2) {
                Text(value.isEmpty ? placeholder : value)
                    .lineLimit(ItemRegistrationLayout.singleLineLimit)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .obritTextStyle(
                        OBRitTypography.xl,
                        weight: OBRitFontWeight.medium,
                        color: value.isEmpty ? OBRitColors.gray700 : OBRitColors.common00
                    )

                OBRitIcon(kind: .chevronDown, color: OBRitColors.common00)
                    .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
            }
            .frame(height: ItemRegistrationLayout.fieldHeight)
            .padding(.horizontal, OBRitSpacing.s5)
            .background(OBRitColors.gray800)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
            .contentShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
        }
        .buttonStyle(.plain)
    }
}

struct ItemReplacementDateDropdown: View {
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
                .offset(y: ItemRegistrationLayout.fieldHeight + OBRitSpacing.s2)
                .zIndex(ItemRegistrationLayout.dropdownMenuZIndex)
            }
        }
        .frame(maxWidth: .infinity)
        .frame(height: ItemRegistrationLayout.fieldHeight, alignment: .top)
        .zIndex(isExpanded ? ItemRegistrationLayout.expandedControlZIndex : ItemRegistrationLayout.defaultZIndex)
    }
}

struct ItemTextInputField: View {
    @State private var localText: String
    @FocusState private var isFocused: Bool

    let text: String
    let placeholder: String
    let maxLength: Int
    let helperText: String
    let showsMaxLengthWarning: Bool
    let singleLine: Bool
    let onTextChange: (String) -> Void

    init(
        text: String,
        placeholder: String,
        maxLength: Int,
        helperText: String,
        showsMaxLengthWarning: Bool = false,
        singleLine: Bool,
        onTextChange: @escaping (String) -> Void
    ) {
        _localText = State(initialValue: String(text.prefix(maxLength)))
        self.text = text
        self.placeholder = placeholder
        self.maxLength = maxLength
        self.helperText = helperText
        self.showsMaxLengthWarning = showsMaxLengthWarning
        self.singleLine = singleLine
        self.onTextChange = onTextChange
    }

    var body: some View {
        let isMaxLengthWarning = showsMaxLengthWarning && localText.count >= maxLength
        let helperTextColor = isMaxLengthWarning ? OBRitColors.textWarningDefault : OBRitColors.gray300
        let helperIconColor = isMaxLengthWarning ? OBRitColors.iconWarningDefault : OBRitColors.gray300

        VStack(alignment: .leading, spacing: OBRitSpacing.s2_5) {
            HStack(spacing: OBRitSpacing.s2) {
                ZStack(alignment: .leading) {
                    if localText.isEmpty {
                        Text(placeholder)
                            .lineLimit(ItemRegistrationLayout.singleLineLimit)
                            .obritTextStyle(
                                OBRitTypography.xl,
                                weight: OBRitFontWeight.medium,
                                color: OBRitColors.gray700
                            )
                    }

                    TextField("", text: lengthLimitedText, axis: singleLine ? .horizontal : .vertical)
                        .lineLimit(singleLine ? ItemRegistrationLayout.singleLineLimit : nil)
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

                Text(ItemRegistrationText.characterCount(current: localText.count, maximum: maxLength))
                    .lineLimit(ItemRegistrationLayout.singleLineLimit)
                    .obritTextStyle(
                        OBRitTypography.small,
                        weight: OBRitFontWeight.medium,
                        color: isMaxLengthWarning ? OBRitColors.textWarningDefault : OBRitColors.common00
                    )
            }
            .frame(height: ItemRegistrationLayout.fieldHeight)
            .padding(.horizontal, OBRitSpacing.s5)
            .background(OBRitColors.gray800)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
            .overlay {
                if isMaxLengthWarning {
                    RoundedRectangle(cornerRadius: OBRitRadius.middle)
                        .stroke(OBRitColors.borderWarningDefault, lineWidth: OBRitSpacing.px)
                }
            }
            .contentShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
            .onTapGesture {
                isFocused = true
            }

            HStack(alignment: .center, spacing: OBRitSpacing.s1) {
                OBRitIcon(kind: isMaxLengthWarning ? .exclamation : .success, color: helperIconColor)
                    .frame(width: OBRitSpacing.s4, height: OBRitSpacing.s4)
                Text(helperText)
                    .fixedSize(horizontal: false, vertical: true)
                    .obritTextStyle(
                        OBRitTypography.s,
                        weight: OBRitFontWeight.medium,
                        color: helperTextColor
                    )
            }
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

struct ItemSearchInputField: View {
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
                        .lineLimit(ItemRegistrationLayout.singleLineLimit)
                        .obritTextStyle(
                            OBRitTypography.xl,
                            weight: OBRitFontWeight.medium,
                            color: OBRitColors.gray700
                        )
                }

                TextField("", text: $localText)
                    .lineLimit(ItemRegistrationLayout.singleLineLimit)
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

            Image(systemName: ItemRegistrationAsset.searchSymbol)
                .font(.system(size: OBRitSpacing.s6, weight: .regular))
                .foregroundStyle(OBRitColors.common00)
                .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
        }
        .frame(height: ItemRegistrationLayout.fieldHeight)
        .padding(.horizontal, OBRitSpacing.s5)
        .overlay {
            RoundedRectangle(cornerRadius: OBRitRadius.middle)
                .stroke(OBRitColors.gray300, lineWidth: ItemRegistrationLayout.searchFieldBorderWidth)
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

struct ItemQuantityCard: View {
    let kind: ItemKind?
    let quantity: Int
    let quantityLabelPrefix: String
    let helperText: String
    let action: ItemRegistrationAction

    var body: some View {
        let isKindSelected = kind != nil
        let displayQuantity = isKindSelected ? quantity : ItemRegistrationConfig.defaultQuantity

        VStack(alignment: .leading, spacing: OBRitSpacing.s2) {
            HStack(spacing: OBRitSpacing.s4) {
                if let kind {
                    ItemImage(
                        imageURL: kind.imageURL,
                        size: ItemRegistrationLayout.itemThumbnailSize
                    )
                } else {
                    Circle()
                        .fill(OBRitColors.gray750)
                        .frame(
                            width: ItemRegistrationLayout.itemThumbnailSize,
                            height: ItemRegistrationLayout.itemThumbnailSize
                        )
                }

                VStack(alignment: .leading, spacing: OBRitSpacing.s1) {
                    Text(kind?.title ?? "-")
                        .lineLimit(ItemRegistrationLayout.singleLineLimit)
                        .obritTextStyle(
                            OBRitTypography.xl,
                            weight: OBRitFontWeight.bold,
                            color: OBRitColors.common00
                        )
                    Text(
                        ItemRegistrationText.quantityText(
                            prefix: quantityLabelPrefix,
                            quantity: displayQuantity
                        )
                    )
                        .lineLimit(ItemRegistrationLayout.singleLineLimit)
                        .obritTextStyle(
                            OBRitTypography.small,
                            weight: OBRitFontWeight.medium,
                            color: OBRitColors.common00.opacity(ItemRegistrationLayout.secondaryTextOpacity)
                        )
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                OBRitStepper(
                    value: displayQuantity,
                    size: .small,
                    minimumValue: ItemRegistrationConfig.quantityMinimum,
                    maximumValue: ItemRegistrationConfig.quantityMaximum,
                    isEnabled: isKindSelected,
                    onDecrement: action.onDecrementQuantity,
                    onIncrement: action.onIncrementQuantity,
                    onValueChange: action.onUpdateQuantity
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

struct ItemImageOptionButton: View {
    let option: ItemImageOption
    let selected: Bool
    let onSelect: () -> Void

    var body: some View {
        Button(action: onSelect) {
            OBRitRemoteImage(urlString: option.imageURL, contentMode: .fill)
                .frame(
                    width: ItemRegistrationLayout.imageOptionSize,
                    height: ItemRegistrationLayout.imageOptionSize
                )
                .clipShape(Circle())
                .overlay {
                    if selected {
                        Circle()
                            .stroke(OBRitColors.green300, lineWidth: ItemRegistrationLayout.selectedImageBorderWidth)
                    }
                }
                .contentShape(Circle())
        }
        .buttonStyle(.plain)
    }
}

struct ItemImage: View {
    let imageURL: String
    let size: CGFloat

    var body: some View {
        OBRitRemoteImage(urlString: imageURL, contentMode: .fill)
            .frame(width: size, height: size)
            .clipShape(Circle())
    }
}

struct ItemBottomButton: View {
    let text: String
    let enabled: Bool
    let horizontalPadding: CGFloat
    let action: () -> Void

    var body: some View {
        VStack(spacing: ItemRegistrationLayout.zeroSpacing) {
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

struct ItemScrollButton: View {
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
