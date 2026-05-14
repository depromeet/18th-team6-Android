import SwiftUI

struct HomeOrbStatusRing: View {
    let normalRatio: Double
    let warningRatio: Double

    var body: some View {
        ZStack {
            Circle()
                .trim(from: warningArcHalf, to: 1 - warningArcHalf)
                .stroke(
                    OBRitColors.textPositiveDefault,
                    style: StrokeStyle(lineWidth: ringLineWidth, lineCap: .butt)
                )

            Circle()
                .trim(from: 0, to: warningArcHalf)
                .stroke(
                    OBRitColors.textWarningDefault,
                    style: StrokeStyle(lineWidth: ringLineWidth, lineCap: .butt)
                )

            Circle()
                .trim(from: 1 - warningArcHalf, to: 1)
                .stroke(
                    OBRitColors.textWarningDefault,
                    style: StrokeStyle(lineWidth: ringLineWidth, lineCap: .butt)
                )

            Circle()
                .trim(from: warningArcHalf - transitionArc, to: warningArcHalf + transitionArc)
                .stroke(
                    LinearGradient(
                        colors: [OBRitColors.textWarningDefault, OBRitColors.textPositiveDefault],
                        startPoint: .topTrailing,
                        endPoint: .topLeading
                    ),
                    style: StrokeStyle(lineWidth: ringLineWidth, lineCap: .butt)
                )

            Circle()
                .trim(from: 1 - warningArcHalf - transitionArc, to: 1 - warningArcHalf + transitionArc)
                .stroke(
                    LinearGradient(
                        colors: [OBRitColors.textPositiveDefault, OBRitColors.textWarningDefault],
                        startPoint: .bottomLeading,
                        endPoint: .bottomTrailing
                    ),
                    style: StrokeStyle(lineWidth: ringLineWidth, lineCap: .butt)
                )
        }
    }

    private var warningArcHalf: CGFloat {
        CGFloat(max(0.04, min(0.46, warningRatio / max(normalRatio + warningRatio, 0.0001) / 2)))
    }

    private var transitionArc: CGFloat { 0.03 }

    private var ringLineWidth: CGFloat { OBRitSpacing.s1_5 }
}

struct HomeOrbSurfaceArc: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        path.addArc(
            center: CGPoint(x: rect.midX, y: rect.midY),
            radius: min(rect.width, rect.height) / 2,
            startAngle: .degrees(205),
            endAngle: .degrees(277),
            clockwise: false
        )
        return path
    }
}

#Preview {
    HomeOrbStatusRing(normalRatio: 0.77, warningRatio: 0.23)
        .frame(width: HomeOrbMetrics.ringDiameter, height: HomeOrbMetrics.ringDiameter)
        .background(OBRitColors.backgroundDefaultDefault)
}
