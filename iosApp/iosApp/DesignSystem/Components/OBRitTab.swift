import SwiftUI
import Shared

public struct OBRitTab: View {
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
            .obritTextStyle(OBRitTypography.base, weight: AtomFontWeight.shared.Bold, color: OBRitColors.common00)
            .padding(.horizontal, OBRitSpacing.s4)
            .padding(.vertical, OBRitSpacing.s3)
            .overlay(alignment: .bottom) {
                Rectangle()
                    .fill(selected ? OBRitColors.green300 : Color.clear)
                    .frame(height: OBRitSpacing.px)
            }
            .shadow(color: Color.black.opacity(0.16), radius: selected ? 24 : 12, x: 0, y: selected ? 12 : 6)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}
