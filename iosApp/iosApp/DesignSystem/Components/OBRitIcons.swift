import SwiftUI

enum OBRitIconKind {
    case check
    case chevronDown
    case exclamation
    case info
    case question
    case success
}

struct OBRitIcon: View {
    let kind: OBRitIconKind
    let color: Color

    var body: some View {
        ZStack {
            switch kind {
            case .check:
                CheckmarkShape()
                    .stroke(color, style: StrokeStyle(lineWidth: 2.1, lineCap: .round, lineJoin: .round))
                    .padding(5)
            case .chevronDown:
                ChevronDownShape()
                    .stroke(color, style: StrokeStyle(lineWidth: 2, lineCap: .round, lineJoin: .round))
                    .padding(.horizontal, 2)
                    .padding(.vertical, 4)
            case .exclamation:
                Circle()
                    .fill(color)
                ExclamationShape()
                    .fill(OBRitColors.common00)
                    .padding(5)
            case .info:
                Circle()
                    .fill(color)
                InfoShape()
                    .fill(OBRitColors.gray900)
                    .padding(5)
            case .question:
                Circle()
                    .fill(color)
                QuestionShape()
                    .fill(OBRitColors.gray900)
                    .padding(4)
            case .success:
                Circle()
                    .fill(color)
                CheckmarkShape()
                    .stroke(OBRitColors.gray900, style: StrokeStyle(lineWidth: 2, lineCap: .round, lineJoin: .round))
                    .padding(5)
            }
        }
        .aspectRatio(1, contentMode: .fit)
    }
}

struct CheckmarkShape: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        path.move(to: CGPoint(x: rect.minX + rect.width * 0.18, y: rect.minY + rect.height * 0.55))
        path.addLine(to: CGPoint(x: rect.minX + rect.width * 0.42, y: rect.minY + rect.height * 0.78))
        path.addLine(to: CGPoint(x: rect.minX + rect.width * 0.84, y: rect.minY + rect.height * 0.24))
        return path
    }
}

struct ChevronDownShape: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        path.move(to: CGPoint(x: rect.minX, y: rect.minY + rect.height * 0.28))
        path.addLine(to: CGPoint(x: rect.midX, y: rect.minY + rect.height * 0.72))
        path.addLine(to: CGPoint(x: rect.maxX, y: rect.minY + rect.height * 0.28))
        return path
    }
}

struct ExclamationShape: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        let midX = rect.midX
        path.addRoundedRect(
            in: CGRect(x: midX - rect.width * 0.08, y: rect.minY + rect.height * 0.15, width: rect.width * 0.16, height: rect.height * 0.48),
            cornerSize: CGSize(width: rect.width * 0.08, height: rect.width * 0.08)
        )
        path.addEllipse(in: CGRect(x: midX - rect.width * 0.09, y: rect.minY + rect.height * 0.74, width: rect.width * 0.18, height: rect.width * 0.18))
        return path
    }
}

struct InfoShape: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        let midX = rect.midX
        path.addEllipse(
            in: CGRect(
                x: midX - rect.width * 0.09,
                y: rect.minY + rect.height * 0.15,
                width: rect.width * 0.18,
                height: rect.width * 0.18
            )
        )
        path.addRoundedRect(
            in: CGRect(
                x: midX - rect.width * 0.08,
                y: rect.minY + rect.height * 0.42,
                width: rect.width * 0.16,
                height: rect.height * 0.43
            ),
            cornerSize: CGSize(width: rect.width * 0.08, height: rect.width * 0.08)
        )
        return path
    }
}

struct QuestionShape: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        path.move(to: CGPoint(x: rect.minX + rect.width * 0.27, y: rect.minY + rect.height * 0.34))
        path.addCurve(
            to: CGPoint(x: rect.minX + rect.width * 0.62, y: rect.minY + rect.height * 0.33),
            control1: CGPoint(x: rect.minX + rect.width * 0.32, y: rect.minY + rect.height * 0.12),
            control2: CGPoint(x: rect.minX + rect.width * 0.58, y: rect.minY + rect.height * 0.13)
        )
        path.addCurve(
            to: CGPoint(x: rect.minX + rect.width * 0.48, y: rect.minY + rect.height * 0.58),
            control1: CGPoint(x: rect.minX + rect.width * 0.67, y: rect.minY + rect.height * 0.52),
            control2: CGPoint(x: rect.minX + rect.width * 0.48, y: rect.minY + rect.height * 0.48)
        )
        path.addRoundedRect(
            in: CGRect(x: rect.minX + rect.width * 0.45, y: rect.minY + rect.height * 0.58, width: rect.width * 0.12, height: rect.height * 0.08),
            cornerSize: CGSize(width: rect.width * 0.04, height: rect.width * 0.04)
        )
        path.addEllipse(in: CGRect(x: rect.minX + rect.width * 0.43, y: rect.minY + rect.height * 0.74, width: rect.width * 0.16, height: rect.width * 0.16))
        return path
    }
}
