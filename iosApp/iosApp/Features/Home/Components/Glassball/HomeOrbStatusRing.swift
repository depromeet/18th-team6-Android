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
            let diameter = min(geometry.size.width, geometry.size.height)
            let shadowDiameter = diameter * HomeOrbGlassMetrics.groundShadowDiameterRatio

            Circle()
                .fill(HomeOrbGlassMetrics.shadowCoreColor)
                .frame(width: shadowDiameter, height: shadowDiameter)
                .position(
                    x: diameter * HomeOrbGlassMetrics.groundShadowCenterXRatio,
                    y: diameter * HomeOrbGlassMetrics.groundShadowCenterYRatio
                )
                .blur(radius: diameter * HomeOrbGlassMetrics.groundShadowBlurRatio)
        }
        .allowsHitTesting(false)
    }
}

enum HomeOrbStatusGradient {
    static func ringStops(for mix: HomeOrbGradientMix) -> [Gradient.Stop] {
        if mix.warningShareCGFloat <= 0 {
            return solidStops(color: OBRitColors.backgroundPositiveDefault)
        }

        if mix.positiveShareCGFloat <= 0 {
            return solidStops(color: OBRitColors.backgroundWarningDefault)
        }

        let positiveArcHalf = mix.positiveShareCGFloat / 2
        let transitionArc = min(
            HomeOrbVisualConfig.statusRingTransitionArc,
            positiveArcHalf,
            mix.warningShareCGFloat / 2
        )

        return [
            .init(color: OBRitColors.backgroundPositiveDefault, location: 0),
            .init(color: OBRitColors.backgroundPositiveDefault, location: positiveArcHalf - transitionArc),
            .init(color: OBRitColors.backgroundWarningDefault, location: positiveArcHalf + transitionArc),
            .init(color: OBRitColors.backgroundWarningDefault, location: 1 - positiveArcHalf - transitionArc),
            .init(color: OBRitColors.backgroundPositiveDefault, location: 1 - positiveArcHalf + transitionArc),
            .init(color: OBRitColors.backgroundPositiveDefault, location: 1)
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
            .init(color: OBRitColors.backgroundWarningDefault, location: 0),
            .init(color: OBRitColors.backgroundWarningDefault, location: max(0, mix.warningShareCGFloat - transition)),
            .init(color: OBRitColors.backgroundPositiveDefault, location: min(1, mix.warningShareCGFloat + transition)),
            .init(color: OBRitColors.backgroundPositiveDefault, location: 1)
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
