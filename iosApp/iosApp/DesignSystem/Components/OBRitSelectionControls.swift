import SwiftUI

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
                RoundedRectangle(cornerRadius: 3.2)
                    .fill(checked ? fillColor : Color.clear)
                RoundedRectangle(cornerRadius: 3.2)
                    .stroke(borderColor, lineWidth: checked ? 0 : 1.2)
                if checked {
                    OBRitIcon(kind: .check, color: checkColor)
                }
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
        enabled ? OBRitColors.green300 : OBRitColors.gray600
    }

    private var borderColor: Color {
        enabled ? OBRitColors.gray300 : OBRitColors.gray600
    }

    private var checkColor: Color {
        enabled ? OBRitColors.gray900 : OBRitColors.gray250
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
                if selected {
                    Circle()
                        .fill(ringColor)
                        .padding(6)
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
            return enabled ? OBRitColors.green300 : OBRitColors.gray600
        }
        return enabled ? OBRitColors.gray300 : OBRitColors.gray600
    }
}
