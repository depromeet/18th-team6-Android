import SwiftUI

public enum OBRitGnbItem: CaseIterable, Hashable {
    case home
    case list
}

public struct OBRitGnb: View {
    private let selected: OBRitGnbItem
    private let onSelect: (OBRitGnbItem) -> Void

    public init(
        selected: OBRitGnbItem = .home,
        onSelect: @escaping (OBRitGnbItem) -> Void
    ) {
        self.selected = selected
        self.onSelect = onSelect
    }

    public var body: some View {
        if #available(iOS 26.0, *) {
            glassBody
        } else {
            fallbackBody
        }
    }

    @available(iOS 26.0, *)
    private var glassBody: some View {
        GlassEffectContainer(spacing: OBRitSpacing.s1) {
            content
                .glassEffect(.regular.tint(OBRitColors.commonWhite00_20).interactive(), in: Capsule())
                .gnbGlassChrome()
        }
        .gnbGlassShadow()
    }

    private var fallbackBody: some View {
        content
            .background {
                Capsule()
                    .fill(.ultraThinMaterial)
                Capsule()
                    .fill(OBRitColors.commonWhite00_20)
            }
            .gnbGlassChrome()
            .gnbGlassShadow()
    }

    private var content: some View {
        HStack(spacing: 0) {
            ForEach(OBRitGnbItem.allCases, id: \.self) { item in
                OBRitGnbButton(
                    item: item,
                    selected: selected == item,
                    onSelect: onSelect
                )
            }
        }
        .padding(.horizontal, OBRitGnbMetrics.horizontalInset)
        .padding(.vertical, OBRitSpacing.s1)
        .frame(width: OBRitGnbMetrics.width, height: OBRitSpacing.s14)
        .contentShape(Capsule())
    }
}

private struct OBRitGnbButton: View {
    let item: OBRitGnbItem
    let selected: Bool
    let onSelect: (OBRitGnbItem) -> Void

    var body: some View {
        Button {
            onSelect(item)
        } label: {
            OBRitGnbIcon(item: item, color: iconColor)
                .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
                .frame(width: OBRitGnbMetrics.itemWidth, height: OBRitSpacing.s12)
                .background {
                    if selected {
                        Capsule()
                            .fill(OBRitColors.common00.opacity(0.78))
                        Capsule()
                            .fill(
                                LinearGradient(
                                    colors: [
                                        OBRitColors.common00.opacity(0.36),
                                        OBRitColors.common00.opacity(0.08),
                                    ],
                                    startPoint: .top,
                                    endPoint: .bottom
                                )
                            )
                        Capsule()
                            .stroke(OBRitColors.common00.opacity(0.32), lineWidth: OBRitGnbMetrics.hairline)
                    }
                }
                .clipShape(Capsule())
                .contentShape(Capsule())
        }
        .buttonStyle(.plain)
        .accessibilityLabel(accessibilityLabel)
        .accessibilityAddTraits(selected ? .isSelected : [])
    }

    private var iconColor: Color {
        selected ? OBRitColors.gray900 : OBRitColors.common00
    }

    private var accessibilityLabel: String {
        switch item {
        case .home:
            return "홈"
        case .list:
            return "목록"
        }
    }
}

private struct OBRitGnbIcon: View {
    let item: OBRitGnbItem
    let color: Color

    var body: some View {
        switch item {
        case .home:
            OBRitGnbHomeShape()
                .fill(color, style: FillStyle(eoFill: true))
        case .list:
            OBRitGnbListIcon(color: color)
        }
    }
}

private struct OBRitGnbHomeShape: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        let w = rect.width
        let h = rect.height

        path.move(to: CGPoint(x: rect.minX + w * 0.16, y: rect.minY + h * 0.44))
        path.addLine(to: CGPoint(x: rect.minX + w * 0.50, y: rect.minY + h * 0.16))
        path.addLine(to: CGPoint(x: rect.minX + w * 0.84, y: rect.minY + h * 0.44))
        path.addLine(to: CGPoint(x: rect.minX + w * 0.78, y: rect.minY + h * 0.84))
        path.addLine(to: CGPoint(x: rect.minX + w * 0.22, y: rect.minY + h * 0.84))
        path.closeSubpath()

        path.addRoundedRect(
            in: CGRect(
                x: rect.minX + w * 0.42,
                y: rect.minY + h * 0.58,
                width: w * 0.16,
                height: h * 0.20
            ),
            cornerSize: CGSize(width: w * 0.04, height: w * 0.04)
        )
        return path
    }
}

private struct OBRitGnbListIcon: View {
    let color: Color

    var body: some View {
        GeometryReader { proxy in
            let size = min(proxy.size.width, proxy.size.height)
            let dot = size * 0.14
            let lineHeight = size * 0.11
            let lineWidth = size * 0.44
            let lineX = size * 0.42
            let rows = [size * 0.24, size * 0.50, size * 0.76]

            ZStack(alignment: .topLeading) {
                ForEach(Array(rows.enumerated()), id: \.offset) { _, centerY in
                    Circle()
                        .fill(color)
                        .frame(width: dot, height: dot)
                        .position(x: size * 0.24, y: centerY)
                    RoundedRectangle(cornerRadius: lineHeight / 2)
                        .fill(color)
                        .frame(width: lineWidth, height: lineHeight)
                        .position(x: lineX + lineWidth / 2, y: centerY)
                }
            }
            .frame(width: size, height: size)
        }
    }
}

private enum OBRitGnbMetrics {
    static let width: CGFloat = 122
    static let itemWidth: CGFloat = 56
    static let horizontalInset: CGFloat = 5
    static let hairline: CGFloat = 1
}

private extension View {
    func gnbGlassChrome() -> some View {
        overlay {
            Capsule()
                .strokeBorder(
                    LinearGradient(
                        colors: [
                            OBRitColors.common00.opacity(0.50),
                            OBRitColors.common00.opacity(0.12),
                            OBRitColors.commonBlack00_20,
                        ],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    ),
                    lineWidth: OBRitGnbMetrics.hairline
                )
        }
        .overlay(alignment: .top) {
            Capsule()
                .fill(OBRitColors.common00.opacity(0.18))
                .frame(height: OBRitSpacing.s1)
                .padding(.horizontal, OBRitSpacing.s5)
                .padding(.top, OBRitSpacing.s1)
                .blur(radius: OBRitSpacing.s0_5)
        }
    }

    func gnbGlassShadow() -> some View {
        shadow(color: Color.black.opacity(0.28), radius: OBRitSpacing.s6, x: 0, y: OBRitSpacing.s4)
            .shadow(color: OBRitColors.common00.opacity(0.10), radius: OBRitSpacing.s2, x: 0, y: 0)
    }
}
