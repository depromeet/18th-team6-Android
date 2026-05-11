import SwiftUI
import Shared

public struct OBRitTabItem: Hashable {
    let text: String
    let number: Int?

    public init(
        text: String,
        number: Int? = nil
    ) {
        self.text = text
        self.number = number
    }
}

public struct OBRitTabs: View {
    private let items: [OBRitTabItem]
    private let selectedIndex: Int
    private let onSelect: (Int) -> Void

    public init(
        items: [OBRitTabItem],
        selectedIndex: Int,
        onSelect: @escaping (Int) -> Void
    ) {
        self.items = items
        self.selectedIndex = selectedIndex
        self.onSelect = onSelect
    }

    public var body: some View {
        HStack(spacing: 0) {
            ForEach(Array(items.enumerated()), id: \.offset) { index, item in
                OBRitTab(
                    text: item.text,
                    selected: selectedIndex == index,
                    number: item.number
                ) {
                    onSelect(index)
                }
            }
        }
    }
}

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
            .background(OBRitTabBackdropBlur())
            .overlay(alignment: .bottom) {
                Rectangle()
                    .fill(selected ? OBRitColors.green300 : Color.clear)
                    .frame(height: OBRitSpacing.px)
            }
            .shadow(color: Color.black.opacity(0.16), radius: selected ? 24 : 12, x: 0, y: OBRitSpacing.s1)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

private struct OBRitTabBackdropBlur: UIViewRepresentable {
    func makeUIView(context: Context) -> UIVisualEffectView {
        UIVisualEffectView(effect: UIBlurEffect(style: .systemUltraThinMaterial))
    }

    func updateUIView(_ uiView: UIVisualEffectView, context: Context) {}
}

struct OBRitTabs_Previews: PreviewProvider {
    static var previews: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s5) {
            OBRitTabs(
                items: Array(repeating: OBRitTabItem(text: "{Text}"), count: 8),
                selectedIndex: 0,
                onSelect: { _ in }
            )
            OBRitTabs(
                items: [
                    OBRitTabItem(text: "{Text}", number: 1),
                    OBRitTabItem(text: "{Text}", number: 2),
                    OBRitTabItem(text: "{Text}", number: 3)
                ],
                selectedIndex: 1,
                onSelect: { _ in }
            )
        }
        .padding(OBRitSpacing.s5)
        .background(OBRitColors.gray900)
        .previewLayout(.sizeThatFits)
    }
}
