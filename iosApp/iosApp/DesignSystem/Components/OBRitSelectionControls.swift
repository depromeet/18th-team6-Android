import SwiftUI

public struct OBRitNumber: View {
    private let text: String
    private let selected: Bool

    public init(
        text: String,
        selected: Bool = false
    ) {
        self.text = text
        self.selected = selected
    }

    public var body: some View {
        Text(text)
            .lineLimit(1)
            .minimumScaleFactor(0.7)
            .obritTextStyle(OBRitTypography.base, weight: OBRitFontWeight.semiBold, color: contentColor)
            .frame(width: OBRitSpacing.s7, height: OBRitSpacing.s7)
            .background(containerColor)
            .clipShape(Circle())
    }

    private var containerColor: Color {
        selected ? OBRitColors.common00 : OBRitColors.gray750
    }

    private var contentColor: Color {
        selected ? OBRitColors.common100 : OBRitColors.common00
    }
}

public struct OBRitIndicatorDot: View {
    private let active: Bool

    public init(active: Bool = false) {
        self.active = active
    }

    public var body: some View {
        Circle()
            .fill(active ? OBRitColors.green300 : OBRitColors.gray700)
            .frame(width: OBRitSpacing.s2, height: OBRitSpacing.s2)
    }
}

public struct OBRitPageIndicator: View {
    private let count: Int
    private let selectedIndex: Int

    public init(
        count: Int,
        selectedIndex: Int
    ) {
        self.count = max(0, count)
        self.selectedIndex = selectedIndex
    }

    public var body: some View {
        HStack(spacing: OBRitSpacing.s2_5) {
            ForEach(0..<count, id: \.self) { index in
                OBRitIndicatorDot(active: index == selectedIndex)
            }
        }
        .padding(.horizontal, OBRitSpacing.s5)
        .padding(.vertical, OBRitSpacing.s3)
        .frame(maxWidth: .infinity)
    }
}

public struct OBRitCheckBox: View {
    private let checked: Bool
    private let enabled: Bool
    private let onCheckedChange: ((Bool) -> Void)?

    public init(
        checked: Bool,
        enabled: Bool = true,
        onCheckedChange: ((Bool) -> Void)? = nil
    ) {
        self.checked = checked
        self.enabled = enabled
        self.onCheckedChange = onCheckedChange
    }

    public var body: some View {
        Button {
            onCheckedChange?(!checked)
        } label: {
            ZStack {
                ZStack {
                    RoundedRectangle(cornerRadius: 3)
                        .fill(checked ? fillColor : Color.clear)
                    if checked {
                        OBRitIcon(kind: .check, color: checkColor)
                    } else {
                        RoundedRectangle(cornerRadius: 3)
                            .stroke(borderColor, lineWidth: 1.2)
                    }
                }
                .frame(width: 18, height: 18)
            }
            .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .disabled(!enabled || onCheckedChange == nil)
        .accessibilityAddTraits(.isButton)
        .accessibilityValue(checked ? "checked" : "unchecked")
    }

    private var fillColor: Color {
        enabled ? OBRitColors.green300 : OBRitColors.green800
    }

    private var borderColor: Color {
        enabled ? OBRitColors.gray400 : OBRitColors.gray700
    }

    private var checkColor: Color {
        OBRitColors.gray900
    }
}

public struct OBRitRadioButton: View {
    private let selected: Bool
    private let enabled: Bool
    private let onClick: (() -> Void)?

    public init(
        selected: Bool,
        enabled: Bool = true,
        onClick: (() -> Void)? = nil
    ) {
        self.selected = selected
        self.enabled = enabled
        self.onClick = onClick
    }

    public var body: some View {
        Button {
            onClick?()
        } label: {
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
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .disabled(!enabled || onClick == nil)
        .accessibilityAddTraits(.isButton)
        .accessibilityValue(selected ? "selected" : "unselected")
    }

    private var ringColor: Color {
        if selected {
            return enabled ? OBRitColors.green300 : OBRitColors.green800
        }
        return enabled ? OBRitColors.gray400 : OBRitColors.gray700
    }
}
