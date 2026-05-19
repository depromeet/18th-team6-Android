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
            stops: HomeOrbStatusGradient.ringStops(for: gradientMix),
            center: .center,
            startAngle: .degrees(0),
            endAngle: .degrees(360)
        )
    }

    private var gradientMix: HomeOrbGradientMix {
        HomeOrbGradientMix(normalRatio: normalRatio, warningRatio: warningRatio)
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

enum HomeOrbStatusGradient {
    static func ringStops(for mix: HomeOrbGradientMix) -> [Gradient.Stop] {
        if mix.warningShareCGFloat <= 0 {
            return solidStops(color: OBRitColors.backgroundPositiveDefault)
        }

        if mix.positiveShareCGFloat <= 0 {
            return solidStops(color: OBRitColors.backgroundWarningDefault)
        }

        let warningArcHalf = mix.warningShareCGFloat / 2
        let transitionArc = min(
            HomeOrbVisualConfig.statusRingTransitionArc,
            warningArcHalf,
            mix.positiveShareCGFloat / 2
        )

        return [
            .init(color: OBRitColors.backgroundWarningDefault, location: 0),
            .init(color: OBRitColors.backgroundWarningDefault, location: warningArcHalf - transitionArc),
            .init(color: OBRitColors.backgroundPositiveDefault, location: warningArcHalf + transitionArc),
            .init(color: OBRitColors.backgroundPositiveDefault, location: 1 - warningArcHalf - transitionArc),
            .init(color: OBRitColors.backgroundWarningDefault, location: 1 - warningArcHalf + transitionArc),
            .init(color: OBRitColors.backgroundWarningDefault, location: 1)
        ]
    }

    static func reflectedStops(for mix: HomeOrbGradientMix) -> [Gradient.Stop] {
        if mix.warningShareCGFloat <= 0 {
            return solidStops(color: OBRitColors.backgroundPositiveDefault)
        }

        if mix.positiveShareCGFloat <= 0 {
            return solidStops(color: OBRitColors.backgroundWarningDefault)
        }

        let transition = min(
            HomeOrbVisualConfig.gradientTransitionWidth,
            mix.positiveShareCGFloat / 2,
            mix.warningShareCGFloat / 2
        )

        return [
            .init(color: OBRitColors.backgroundPositiveDefault, location: 0),
            .init(color: OBRitColors.backgroundPositiveDefault, location: max(0, mix.positiveShareCGFloat - transition)),
            .init(color: OBRitColors.backgroundWarningDefault, location: min(1, mix.positiveShareCGFloat + transition)),
            .init(color: OBRitColors.backgroundWarningDefault, location: 1)
        ]
    }

    private static func solidStops(color: Color) -> [Gradient.Stop] {
        [
            .init(color: color, location: 0),
            .init(color: color, location: 1)
        ]
    }
}

#Preview {
    HomeOrbStatusRing(normalRatio: 0.77, warningRatio: 0.23)
        .frame(width: HomeOrbMetrics.ringDiameter, height: HomeOrbMetrics.ringDiameter)
        .background(OBRitColors.backgroundDefaultDefault)
}
