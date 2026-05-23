import SwiftUI
import Shared

public struct OBRitChip: View {
    private let text: String
    private let selected: Bool
    private let number: Int?
    private let onClick: () -> Void

    public init(
        text: String,
        selected: Bool = false,
        number: Int? = nil,
        onClick: @escaping () -> Void
    ) {
        self.text = text
        self.selected = selected
        self.number = number
        self.onClick = onClick
    }

    public var body: some View {
        Button(action: onClick) {
            HStack(spacing: OBRitSpacing.s2) {
                Text(text)
                if let number {
                    Text("\(number)")
                }
            }
            .lineLimit(1)
            .obritTextStyle(OBRitTypography.base, weight: AtomFontWeight.shared.Bold, color: contentColor)
            .padding(.horizontal, OBRitSpacing.s4)
            .padding(.vertical, OBRitSpacing.s2)
            .frame(height: OBRitSpacing.s9 + OBRitSpacing.s0_5)
            .background(containerColor)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
            .contentShape(RoundedRectangle(cornerRadius: OBRitRadius.extraLarge))
        }
        .buttonStyle(.plain)
    }

    private var containerColor: Color {
        selected ? OBRitColors.common00 : OBRitColors.gray800
    }

    private var contentColor: Color {
        selected ? OBRitColors.common100 : OBRitColors.common00
    }
}
