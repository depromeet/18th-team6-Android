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
            OBRitGnbHomeIconShape()
                .fill(color, style: FillStyle(eoFill: true))
                .frame(
                    width: OBRitGnbMetrics.homeIconWidth,
                    height: OBRitGnbMetrics.homeIconHeight
                )
        case .list:
            OBRitGnbListIconShape()
                .fill(color)
        }
    }
}

private struct OBRitGnbHomeIconShape: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()

        path.move(to: CGPoint(x: 10.7087, y: 0.292786))
        path.addLine(to: CGPoint(x: 19.7087, y: 9.29279))
        path.addCurve(
            to: CGPoint(x: 19.0017, y: 10.9998),
            control1: CGPoint(x: 20.3387, y: 9.92279),
            control2: CGPoint(x: 19.8927, y: 10.9998)
        )
        path.addLine(to: CGPoint(x: 18.0017, y: 10.9998))
        path.addLine(to: CGPoint(x: 18.0017, y: 16.9998))
        path.addCurve(
            to: CGPoint(x: 17.1231, y: 19.1211),
            control1: CGPoint(x: 18.0017, y: 17.7954),
            control2: CGPoint(x: 17.6857, y: 18.5585)
        )
        path.addCurve(
            to: CGPoint(x: 15.0017, y: 19.9998),
            control1: CGPoint(x: 16.5605, y: 19.6837),
            control2: CGPoint(x: 15.7974, y: 19.9998)
        )
        path.addLine(to: CGPoint(x: 5.00175, y: 19.9998))
        path.addCurve(
            to: CGPoint(x: 2.88043, y: 19.1211),
            control1: CGPoint(x: 4.2061, y: 19.9998),
            control2: CGPoint(x: 3.44304, y: 19.6837)
        )
        path.addCurve(
            to: CGPoint(x: 2.00175, y: 16.9998),
            control1: CGPoint(x: 2.31782, y: 18.5585),
            control2: CGPoint(x: 2.00175, y: 17.7954)
        )
        path.addLine(to: CGPoint(x: 2.00175, y: 10.9998))
        path.addLine(to: CGPoint(x: 1.00175, y: 10.9998))
        path.addCurve(
            to: CGPoint(x: 0.294748, y: 9.29279),
            control1: CGPoint(x: 0.111748, y: 10.9998),
            control2: CGPoint(x: -0.335252, y: 9.92279)
        )
        path.addLine(to: CGPoint(x: 9.29475, y: 0.292786))
        path.addCurve(
            to: CGPoint(x: 10.0017, y: 0),
            control1: CGPoint(x: 9.48228, y: 0.105315),
            control2: CGPoint(x: 9.73658, y: 0)
        )
        path.addCurve(
            to: CGPoint(x: 10.7087, y: 0.292786),
            control1: CGPoint(x: 10.2669, y: 0),
            control2: CGPoint(x: 10.5212, y: 0.105315)
        )
        path.closeSubpath()

        path.move(to: CGPoint(x: 11.5017, y: 8.99979))
        path.addLine(to: CGPoint(x: 8.50175, y: 8.99979))
        path.addCurve(
            to: CGPoint(x: 7.44109, y: 9.43913),
            control1: CGPoint(x: 8.10392, y: 8.99979),
            control2: CGPoint(x: 7.72239, y: 9.15782)
        )
        path.addCurve(
            to: CGPoint(x: 7.00175, y: 10.4998),
            control1: CGPoint(x: 7.15978, y: 9.72043),
            control2: CGPoint(x: 7.00175, y: 10.102)
        )
        path.addLine(to: CGPoint(x: 7.00175, y: 13.4998))
        path.addCurve(
            to: CGPoint(x: 7.44109, y: 14.5604),
            control1: CGPoint(x: 7.00175, y: 13.8976),
            control2: CGPoint(x: 7.15978, y: 14.2791)
        )
        path.addCurve(
            to: CGPoint(x: 8.50175, y: 14.9998),
            control1: CGPoint(x: 7.72239, y: 14.8418),
            control2: CGPoint(x: 8.10392, y: 14.9998)
        )
        path.addLine(to: CGPoint(x: 11.5017, y: 14.9998))
        path.addCurve(
            to: CGPoint(x: 12.5624, y: 14.5604),
            control1: CGPoint(x: 11.8996, y: 14.9998),
            control2: CGPoint(x: 12.2811, y: 14.8418)
        )
        path.addCurve(
            to: CGPoint(x: 13.0017, y: 13.4998),
            control1: CGPoint(x: 12.8437, y: 14.2791),
            control2: CGPoint(x: 13.0017, y: 13.8976)
        )
        path.addLine(to: CGPoint(x: 13.0017, y: 10.4998))
        path.addCurve(
            to: CGPoint(x: 12.5624, y: 9.43913),
            control1: CGPoint(x: 13.0017, y: 10.102),
            control2: CGPoint(x: 12.8437, y: 9.72043)
        )
        path.addCurve(
            to: CGPoint(x: 11.5017, y: 8.99979),
            control1: CGPoint(x: 12.2811, y: 9.15782),
            control2: CGPoint(x: 11.8996, y: 8.99979)
        )

        return path.applying(
            CGAffineTransform(
                a: rect.width / OBRitGnbMetrics.homeIconVectorWidth,
                b: 0,
                c: 0,
                d: rect.height / OBRitGnbMetrics.homeIconVectorHeight,
                tx: rect.minX,
                ty: rect.minY
            )
        )
    }
}

private struct OBRitGnbListIconShape: Shape {
    func path(in rect: CGRect) -> Path {
        let scaleX = rect.width / OBRitGnbMetrics.listIconVectorSize
        let scaleY = rect.height / OBRitGnbMetrics.listIconVectorSize

        func scaledRect(x: CGFloat, y: CGFloat, width: CGFloat, height: CGFloat) -> CGRect {
            CGRect(
                x: rect.minX + x * scaleX,
                y: rect.minY + y * scaleY,
                width: width * scaleX,
                height: height * scaleY
            )
        }

        var path = Path()
        let lineYPositions: [CGFloat] = [5, 11, 17]
        let dotYPositions: [CGFloat] = [4.005, 10.005, 16.005]

        lineYPositions.forEach { y in
            path.addRoundedRect(
                in: scaledRect(x: 9, y: y, width: 12, height: 2),
                cornerSize: CGSize(width: scaleX, height: scaleY)
            )
        }
        dotYPositions.forEach { y in
            path.addEllipse(in: scaledRect(x: 3, y: y, width: 4, height: 4))
        }
        return path
    }
}

private enum OBRitGnbMetrics {
    static let width: CGFloat = 122
    static let itemWidth: CGFloat = 56
    static let horizontalInset: CGFloat = 5
    static let hairline: CGFloat = 1
    static let homeIconWidth: CGFloat = 20.0037
    static let homeIconHeight: CGFloat = 19.9998
    static let homeIconVectorWidth: CGFloat = 20.0037
    static let homeIconVectorHeight: CGFloat = 19.9998
    static let listIconVectorSize: CGFloat = 24
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
