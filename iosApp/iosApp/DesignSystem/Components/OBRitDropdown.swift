import SwiftUI
import Shared

public enum OBRitDropdownInputState {
    case `default`
    case error
}

public enum OBRitDropdownMenuItemSize {
    case small
    case large
}

public enum OBRitDropdownVariant {
    case input
    case chip
}

public struct OBRitDropdown: View {
    private let value: String
    private let placeholder: String
    private let inputState: OBRitDropdownInputState
    private let supportingText: String
    private let enabled: Bool
    private let expanded: Bool
    private let variant: OBRitDropdownVariant
    private let containerColor: Color
    private let onClick: () -> Void

    public init(
        value: String,
        placeholder: String = "",
        inputState: OBRitDropdownInputState = .default,
        supportingText: String = "",
        enabled: Bool = true,
        expanded: Bool = false,
        variant: OBRitDropdownVariant = .input,
        containerColor: Color = OBRitColors.gray800,
        onClick: @escaping () -> Void
    ) {
        self.value = value
        self.placeholder = placeholder
        self.inputState = inputState
        self.supportingText = supportingText
        self.enabled = enabled
        self.expanded = expanded
        self.variant = variant
        self.containerColor = containerColor
        self.onClick = onClick
    }

    public var body: some View {
        switch variant {
        case .input:
            inputBody
        case .chip:
            chipBody
        }
    }

    private var inputBody: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s2) {
            Button(action: onClick) {
                HStack(spacing: OBRitSpacing.s2) {
                    Text(displayedText)
                        .lineLimit(1)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .obritTextStyle(OBRitTypography.xl, weight: AtomFontWeight.shared.Medium, color: displayedTextColor)

                    OBRitIcon(kind: .chevronDown, color: OBRitColors.common00)
                        .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
                }
                .frame(minHeight: OBRitSpacing.s14)
                .padding(.leading, OBRitSpacing.s5)
                .padding(.trailing, OBRitSpacing.s5)
                .padding(.vertical, OBRitSpacing.s4)
                .background(containerColor)
                .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.middle))
                .overlay(borderOverlay)
            }
            .buttonStyle(.plain)
            .disabled(!enabled)

            if inputState == .error && !supportingText.isEmpty {
                HStack(spacing: OBRitSpacing.s1_5) {
                    OBRitIcon(kind: .exclamation, color: OBRitColors.red300)
                        .frame(width: OBRitSpacing.s4, height: OBRitSpacing.s4)
                    Text(supportingText)
                        .obritTextStyle(OBRitTypography.base, weight: AtomFontWeight.shared.SemiBold, color: OBRitColors.red300)
                }
            }
        }
    }

    private var chipBody: some View {
        Button(action: onClick) {
            HStack(spacing: OBRitSpacing.s0_5) {
                Text(displayedText)
                    .lineLimit(1)
                    .obritTextStyle(
                        OBRitTypography.base,
                        weight: AtomFontWeight.shared.SemiBold,
                        color: chipTextColor
                    )

                OBRitIcon(kind: .chevronDown, color: chipTextColor)
                    .frame(width: OBRitSpacing.s4, height: OBRitSpacing.s4)
            }
            .padding(.horizontal, OBRitSpacing.s3)
            .padding(.vertical, OBRitSpacing.s2)
            .overlay {
                RoundedRectangle(cornerRadius: OBRitRadius.small)
                    .stroke(chipBorderColor, lineWidth: OBRitSpacing.px)
            }
            .contentShape(RoundedRectangle(cornerRadius: OBRitRadius.small))
        }
        .buttonStyle(.plain)
        .disabled(!enabled)
    }

    private var displayedText: String {
        value.isEmpty ? placeholder : value
    }

    private var displayedTextColor: Color {
        value.isEmpty || !enabled ? OBRitColors.gray700 : OBRitColors.common00
    }

    @ViewBuilder
    private var borderOverlay: some View {
        if let borderColor {
            RoundedRectangle(cornerRadius: OBRitRadius.middle)
                .stroke(borderColor, lineWidth: OBRitSpacing.px)
        }
    }

    private var borderColor: Color? {
        if !enabled {
            return OBRitColors.gray700
        }
        if inputState == .error {
            return OBRitColors.red300
        }
        if expanded {
            return OBRitColors.gray700
        }
        return nil
    }

    private var chipTextColor: Color {
        enabled ? OBRitColors.textDefaultDefault : OBRitColors.textDisabledDefault
    }

    private var chipBorderColor: Color {
        enabled ? OBRitColors.borderDefaultSecondary : OBRitColors.borderDisabledDefault
    }
}

public struct OBRitDropdownMenuItem: View {
    private let text: String
    private let size: OBRitDropdownMenuItemSize
    private let selected: Bool
    private let enabled: Bool
    private let onClick: (() -> Void)?

    public init(
        text: String,
        size: OBRitDropdownMenuItemSize = .large,
        selected: Bool = false,
        enabled: Bool = true,
        onClick: (() -> Void)? = nil
    ) {
        self.text = text
        self.size = size
        self.selected = selected
        self.enabled = enabled
        self.onClick = onClick
    }

    public var body: some View {
        Group {
            if let onClick {
                Button(action: onClick) {
                    content
                }
                .buttonStyle(.plain)
                .disabled(!enabled)
            } else {
                content
            }
        }
    }

    private var content: some View {
        Text(text)
            .lineLimit(1)
            .frame(maxWidth: .infinity, alignment: textAlignment)
            .padding(.horizontal, OBRitSpacing.s5)
            .padding(.vertical, verticalPadding)
            .frame(width: fixedWidth)
            .background(selected ? OBRitColors.gray750 : OBRitColors.gray800)
            .obritTextStyle(textStyle, weight: AtomFontWeight.shared.Medium, color: OBRitColors.common00)
            .contentShape(Rectangle())
    }

    private var textStyle: OBRitTypography.TextToken {
        switch size {
        case .small:
            return OBRitTypography.base
        case .large:
            return OBRitTypography.xl
        }
    }

    private var verticalPadding: CGFloat {
        switch size {
        case .small:
            return OBRitSpacing.s2_5
        case .large:
            return OBRitSpacing.s4
        }
    }

    private var fixedWidth: CGFloat? {
        switch size {
        case .small:
            return 82
        case .large:
            return nil
        }
    }

    private var textAlignment: Alignment {
        switch size {
        case .small:
            return .center
        case .large:
            return .leading
        }
    }
}

public struct OBRitDropdownMenu: View {
    private let items: [String]
    private let selectedIndex: Int?
    private let itemSize: OBRitDropdownMenuItemSize
    private let enabled: Bool
    private let onItemClick: (Int) -> Void

    public init(
        items: [String],
        selectedIndex: Int? = nil,
        itemSize: OBRitDropdownMenuItemSize = .large,
        enabled: Bool = true,
        onItemClick: @escaping (Int) -> Void
    ) {
        self.items = items
        self.selectedIndex = selectedIndex
        self.itemSize = itemSize
        self.enabled = enabled
        self.onItemClick = onItemClick
    }

    public var body: some View {
        VStack(spacing: 0) {
            ForEach(Array(items.enumerated()), id: \.offset) { index, item in
                OBRitDropdownMenuItem(
                    text: item,
                    size: itemSize,
                    selected: selectedIndex == index,
                    enabled: enabled,
                    onClick: { onItemClick(index) }
                )
            }
        }
        .fixedSize(horizontal: true, vertical: false)
        .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.small))
        .overlay(
            RoundedRectangle(cornerRadius: OBRitRadius.small)
                .stroke(OBRitColors.gray700, lineWidth: OBRitSpacing.px)
        )
    }
}
