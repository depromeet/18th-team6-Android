import SwiftUI

struct HomeOrbStatusRing: View {
    let normalRatio: Double
    let warningRatio: Double

    var body: some View {
        Circle()
            .stroke(
                statusGradient,
                style: StrokeStyle(lineWidth: ringLineWidth, lineCap: .butt)
            )
    }

    private var ringLineWidth: CGFloat { HomeOrbMetrics.ringLineWidth }

    private var statusGradient: AngularGradient {
        AngularGradient(
            stops: gradientStops,
            center: .center,
            startAngle: .degrees(0),
            endAngle: .degrees(360)
        )
    }

    private var gradientStops: [Gradient.Stop] {
        if warningShare <= 0 {
            return [
                .init(color: OBRitColors.backgroundPositiveDefault, location: 0),
                .init(color: OBRitColors.backgroundPositiveDefault, location: 1)
            ]
        }

        if positiveShare <= 0 {
            return [
                .init(color: OBRitColors.backgroundWarningDefault, location: 0),
                .init(color: OBRitColors.backgroundWarningDefault, location: 1)
            ]
        }

        return [
            .init(color: OBRitColors.backgroundWarningDefault, location: 0),
            .init(color: OBRitColors.backgroundWarningDefault, location: warningArcHalf - transitionArc),
            .init(color: OBRitColors.backgroundPositiveDefault, location: warningArcHalf + transitionArc),
            .init(color: OBRitColors.backgroundPositiveDefault, location: 1 - warningArcHalf - transitionArc),
            .init(color: OBRitColors.backgroundWarningDefault, location: 1 - warningArcHalf + transitionArc),
            .init(color: OBRitColors.backgroundWarningDefault, location: 1)
        ]
    }

    private var positiveShare: CGFloat {
        let total = max(normalRatio + warningRatio, 0.0001)
        return CGFloat(max(0, min(1, normalRatio / total)))
    }

    private var warningShare: CGFloat {
        let total = max(normalRatio + warningRatio, 0.0001)
        return CGFloat(max(0, min(1, warningRatio / total)))
    }

    private var warningArcHalf: CGFloat {
        warningShare / 2
    }

    private var transitionArc: CGFloat {
        guard positiveShare > 0, warningShare > 0 else { return 0 }

        return min(0.05, warningArcHalf, positiveShare / 2)
    }
}

struct HomeOrbGroundShadow: View {
    var body: some View {
        GeometryReader { geometry in
            let scale = geometry.size.width / designCanvasSize
            let shadowDiameter = designShadowDiameter * scale

            Circle()
                .fill(Color(red: 0.11, green: 0.11, blue: 0.13))
                .frame(width: shadowDiameter, height: shadowDiameter)
                .position(
                    x: (designShadowX + designShadowDiameter / 2) * scale,
                    y: (designShadowY + designShadowDiameter / 2) * scale
                )
                .blur(radius: designBlurRadius * scale)
        }
        .allowsHitTesting(false)
    }

    private var designCanvasSize: CGFloat { 224 }
    private var designShadowX: CGFloat { 31.859375 }
    private var designShadowY: CGFloat { 116.48046875 }
    private var designShadowDiameter: CGFloat { 159.2888946533203 }
    private var designBlurRadius: CGFloat { 39.82222 }
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
