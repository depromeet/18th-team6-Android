import SwiftUI
import Shared

public enum OBRitDropdownInputState {
    case `default`
    case error
}

public struct OBRitDropdown: View {
    private let value: String
    private let placeholder: String
    private let inputState: OBRitDropdownInputState
    private let supportingText: String
    private let enabled: Bool
    private let expanded: Bool
    private let containerColor: Color
    private let onClick: () -> Void

    public init(
        value: String,
        placeholder: String = "",
        inputState: OBRitDropdownInputState = .default,
        supportingText: String = "",
        enabled: Bool = true,
        expanded: Bool = false,
        containerColor: Color = OBRitColors.gray800,
        onClick: @escaping () -> Void
    ) {
        self.value = value
        self.placeholder = placeholder
        self.inputState = inputState
        self.supportingText = supportingText
        self.enabled = enabled
        self.expanded = expanded
        self.containerColor = containerColor
        self.onClick = onClick
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s3) {
            Button(action: onClick) {
                HStack(spacing: OBRitSpacing.s2) {
                    Text(displayedText)
                        .lineLimit(1)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .obritTextStyle(OBRitTypography.xl, weight: AtomFontWeight.shared.Medium, color: displayedTextColor)

                    OBRitIcon(kind: .chevronDown, color: OBRitColors.common00)
                        .frame(width: OBRitSpacing.s4, height: OBRitSpacing.s4)
                }
                .frame(minHeight: OBRitSpacing.s14)
                .padding(.leading, OBRitSpacing.s5)
                .padding(.trailing, OBRitSpacing.s6)
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
        if inputState == .error {
            return OBRitColors.red300
        }
        if expanded {
            return OBRitColors.gray700
        }
        return nil
    }
}

public struct OBRitDropdownMenu: View {
    private let items: [String]
    private let selectedIndex: Int?
    private let enabled: Bool
    private let onItemClick: (Int) -> Void

    public init(
        items: [String],
        selectedIndex: Int? = nil,
        enabled: Bool = true,
        onItemClick: @escaping (Int) -> Void
    ) {
        self.items = items
        self.selectedIndex = selectedIndex
        self.enabled = enabled
        self.onItemClick = onItemClick
    }

    public var body: some View {
        VStack(spacing: 0) {
            ForEach(Array(items.enumerated()), id: \.offset) { index, item in
                Button {
                    onItemClick(index)
                } label: {
                    Text(item)
                        .lineLimit(1)
                        .frame(maxWidth: .infinity, minHeight: OBRitSpacing.s14, alignment: .leading)
                        .padding(.horizontal, OBRitSpacing.s5)
                        .background(selectedIndex == index ? OBRitColors.gray750 : OBRitColors.gray800)
                        .obritTextStyle(OBRitTypography.xl, weight: AtomFontWeight.shared.Medium, color: OBRitColors.common00)
                }
                .buttonStyle(.plain)
                .disabled(!enabled)
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
