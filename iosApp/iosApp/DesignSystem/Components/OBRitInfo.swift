import SwiftUI
import Shared

public enum OBRitInfoSize {
    case large
    case small
}

public enum OBRitInfoState {
    case info
    case success
    case warning
}

public struct OBRitEssential: View {
    public init() {}

    public var body: some View {
        ZStack {
            AsteriskShape()
                .stroke(
                    OBRitColors.red300,
                    style: StrokeStyle(lineWidth: 1.35, lineCap: .round, lineJoin: .round)
                )
                .frame(width: 6, height: 6.725)
        }
        .frame(width: OBRitSpacing.s6, height: OBRitSpacing.s6)
    }
}

public struct OBRitBullet: View {
    private let color: Color

    public init(color: Color = OBRitColors.common00) {
        self.color = color
    }

    public var body: some View {
        ZStack {
            Circle()
                .fill(color)
                .frame(width: OBRitSpacing.s1, height: OBRitSpacing.s1)
        }
        .frame(width: OBRitSpacing.s5, height: OBRitSpacing.s5)
    }
}

public struct OBRitInfo: View {
    private let text: String
    private let size: OBRitInfoSize
    private let state: OBRitInfoState

    public init(
        text: String,
        size: OBRitInfoSize = .large,
        state: OBRitInfoState = .info
    ) {
        self.text = text
        self.size = size
        self.state = state
    }

    public var body: some View {
        HStack(spacing: gap) {
            OBRitIcon(kind: iconKind, color: color)
                .frame(width: OBRitSpacing.s4, height: OBRitSpacing.s4)

            Text(text)
                .lineLimit(1)
                .obritTextStyle(textToken, weight: AtomFontWeight.shared.SemiBold, color: color)
        }
    }

    private var gap: CGFloat {
        switch size {
        case .large:
            return OBRitSpacing.s1_5
        case .small:
            return OBRitSpacing.s1
        }
    }

    private var textToken: OBRitTypography.TextToken {
        switch size {
        case .large:
            return OBRitTypography.base
        case .small:
            return OBRitTypography.small
        }
    }

    private var iconKind: OBRitIconKind {
        switch state {
        case .info:
            return .info
        case .success:
            return .success
        case .warning:
            return .exclamation
        }
    }

    private var color: Color {
        switch state {
        case .info:
            return OBRitColors.gray500
        case .success:
            return OBRitColors.green300
        case .warning:
            return OBRitColors.red300
        }
    }
}

private struct AsteriskShape: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        path.move(to: CGPoint(x: rect.midX, y: rect.minY))
        path.addLine(to: CGPoint(x: rect.midX, y: rect.maxY))
        path.move(to: CGPoint(x: rect.minX, y: rect.minY + rect.height * 0.24))
        path.addLine(to: CGPoint(x: rect.maxX, y: rect.minY + rect.height * 0.76))
        path.move(to: CGPoint(x: rect.maxX, y: rect.minY + rect.height * 0.24))
        path.addLine(to: CGPoint(x: rect.minX, y: rect.minY + rect.height * 0.76))
        return path
    }
}

struct OBRitInfo_Previews: PreviewProvider {
    static var previews: some View {
        VStack(alignment: .leading, spacing: OBRitSpacing.s4) {
            HStack(spacing: OBRitSpacing.s4) {
                OBRitEssential()
                OBRitBullet()
            }
            OBRitInfo(text: "TEXT", state: .info)
            OBRitInfo(text: "TEXT", state: .success)
            OBRitInfo(text: "TEXT", state: .warning)
            OBRitInfo(text: "TEXT", size: .small, state: .info)
            OBRitInfo(text: "TEXT", size: .small, state: .success)
            OBRitInfo(text: "TEXT", size: .small, state: .warning)
        }
        .padding(OBRitSpacing.s5)
        .background(OBRitColors.gray900)
        .previewLayout(.sizeThatFits)
    }
}
