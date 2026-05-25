import SwiftUI

public enum OBRitTooltipDirection: Sendable {
    case top
    case bottom
    case left
    case right
}

public enum OBRitTooltipAlignment: Sendable {
    case start
    case center
    case end
}

public struct OBRitTooltip: View {
    private let text: String
    private let direction: OBRitTooltipDirection
    private let alignment: OBRitTooltipAlignment

    public init(
        text: String,
        direction: OBRitTooltipDirection = .top,
        alignment: OBRitTooltipAlignment = .center
    ) {
        self.text = text
        self.direction = direction
        self.alignment = alignment
    }

    public var body: some View {
        switch direction {
        case .top:
            VStack(alignment: horizontalStackAlignment, spacing: OBRitSpacing.s0) {
                popup
                horizontalArrow(direction: .top)
            }
        case .bottom:
            VStack(alignment: horizontalStackAlignment, spacing: OBRitSpacing.s0) {
                horizontalArrow(direction: .bottom)
                popup
            }
        case .left:
            HStack(alignment: verticalStackAlignment, spacing: OBRitSpacing.s0) {
                popup
                verticalArrow(direction: .left)
            }
        case .right:
            HStack(alignment: verticalStackAlignment, spacing: OBRitSpacing.s0) {
                verticalArrow(direction: .right)
                popup
            }
        }
    }

    private var popup: some View {
        Text(text)
            .lineLimit(1)
            .obritTextStyle(OBRitTypography.small, weight: OBRitFontWeight.medium, color: OBRitColors.common100)
            .padding(.horizontal, 14)
            .padding(.vertical, OBRitSpacing.s1_5)
            .background(OBRitColors.common00)
            .clipShape(RoundedRectangle(cornerRadius: OBRitRadius.small))
    }

    private func horizontalArrow(direction: OBRitTooltipDirection) -> some View {
        HStack(spacing: OBRitSpacing.s0) {
            if alignment == .end {
                Spacer(minLength: OBRitSpacing.s0)
            }

            TooltipArrowShape(direction: direction)
                .fill(OBRitColors.common00)
                .frame(width: 10, height: OBRitSpacing.s1_5)

            if alignment == .start {
                Spacer(minLength: OBRitSpacing.s0)
            }
        }
        .padding(.horizontal, alignment == .center ? OBRitSpacing.s0 : OBRitSpacing.s2)
        .frame(maxWidth: .infinity)
        .frame(height: OBRitSpacing.s1_5)
    }

    private func verticalArrow(direction: OBRitTooltipDirection) -> some View {
        VStack(spacing: OBRitSpacing.s0) {
            if alignment == .end {
                Spacer(minLength: OBRitSpacing.s0)
            }

            TooltipArrowShape(direction: direction)
                .fill(OBRitColors.common00)
                .frame(width: OBRitSpacing.s1_5, height: 10)

            if alignment == .start {
                Spacer(minLength: OBRitSpacing.s0)
            }
        }
        .frame(width: OBRitSpacing.s1_5)
        .frame(height: 30)
    }

    private var horizontalStackAlignment: HorizontalAlignment {
        switch alignment {
        case .start:
            return .leading
        case .center:
            return .center
        case .end:
            return .trailing
        }
    }

    private var verticalStackAlignment: VerticalAlignment {
        switch alignment {
        case .start:
            return .top
        case .center:
            return .center
        case .end:
            return .bottom
        }
    }
}

private struct TooltipArrowShape: Shape {
    let direction: OBRitTooltipDirection

    func path(in rect: CGRect) -> Path {
        var path = Path()

        switch direction {
        case .top:
            path.move(to: CGPoint(x: rect.minX, y: rect.minY))
            path.addLine(to: CGPoint(x: rect.maxX, y: rect.minY))
            path.addLine(to: CGPoint(x: rect.midX, y: rect.maxY))
        case .bottom:
            path.move(to: CGPoint(x: rect.midX, y: rect.minY))
            path.addLine(to: CGPoint(x: rect.maxX, y: rect.maxY))
            path.addLine(to: CGPoint(x: rect.minX, y: rect.maxY))
        case .left:
            path.move(to: CGPoint(x: rect.minX, y: rect.midY))
            path.addLine(to: CGPoint(x: rect.maxX, y: rect.minY))
            path.addLine(to: CGPoint(x: rect.maxX, y: rect.maxY))
        case .right:
            path.move(to: CGPoint(x: rect.minX, y: rect.minY))
            path.addLine(to: CGPoint(x: rect.maxX, y: rect.midY))
            path.addLine(to: CGPoint(x: rect.minX, y: rect.maxY))
        }

        path.closeSubpath()
        return path
    }
}

struct OBRitTooltip_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: OBRitSpacing.s8) {
            HStack(spacing: OBRitSpacing.s8) {
                OBRitTooltip(text: "Place your text here", direction: .top, alignment: .start)
                OBRitTooltip(text: "Place your text here", direction: .top)
                OBRitTooltip(text: "Place your text here", direction: .top, alignment: .end)
            }
            HStack(spacing: OBRitSpacing.s8) {
                OBRitTooltip(text: "Place your text here", direction: .bottom, alignment: .start)
                OBRitTooltip(text: "Place your text here", direction: .bottom)
                OBRitTooltip(text: "Place your text here", direction: .bottom, alignment: .end)
            }
            HStack(spacing: OBRitSpacing.s8) {
                OBRitTooltip(text: "Place your text here", direction: .right, alignment: .start)
                OBRitTooltip(text: "Place your text here", direction: .right)
                OBRitTooltip(text: "Place your text here", direction: .right, alignment: .end)
            }
            HStack(spacing: OBRitSpacing.s8) {
                OBRitTooltip(text: "Place your text here", direction: .left, alignment: .start)
                OBRitTooltip(text: "Place your text here", direction: .left)
                OBRitTooltip(text: "Place your text here", direction: .left, alignment: .end)
            }
        }
        .padding(OBRitSpacing.s5)
        .background(OBRitColors.gray900)
        .previewLayout(.sizeThatFits)
    }
}
