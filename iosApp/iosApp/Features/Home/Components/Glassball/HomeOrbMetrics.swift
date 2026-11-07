import SwiftUI

enum HomeOrbMetrics {
    static let outerDiameter = OBRitSpacing.s40 + OBRitSpacing.s16
    static let glassBallDiameter = CGFloat(200)
    static let internalShadowDiameter = CGFloat(198)

    static let ringLineWidth = OBRitSpacing.s1_5
    static let ringDiameter = OBRitSpacing.s40 + OBRitSpacing.s16
}

enum HomeOrbGlassMetrics {
    static let shadowCoreColor = Color(red: 0.11, green: 0.11, blue: 0.13)

    static let bottomShadowOpacityStops = [
        HomeOrbGradientStop(colorOpacity: 0.58, location: 0),
        HomeOrbGradientStop(colorOpacity: 0.32, location: 0.42),
        HomeOrbGradientStop(colorOpacity: 0, location: 1)
    ]
    static let bottomShadowCenter = UnitPoint(x: 0.46, y: 0.42)
    static let bottomShadowEndRadiusRatio: CGFloat = 92 / HomeOrbMetrics.internalShadowDiameter
    static let bottomShadowWidthRatio: CGFloat = 150 / HomeOrbMetrics.internalShadowDiameter
    static let bottomShadowHeightRatio: CGFloat = 86 / HomeOrbMetrics.internalShadowDiameter
    static let bottomShadowOffsetYRatio: CGFloat = -18 / HomeOrbMetrics.internalShadowDiameter
    static let bottomShadowBlurRatio: CGFloat = 14 / HomeOrbMetrics.internalShadowDiameter

    static let trailingHighlightOpacity = 0.30
    static let trailingHighlightWidthRatio: CGFloat = 70 / HomeOrbMetrics.internalShadowDiameter
    static let trailingHighlightHeightRatio: CGFloat = 162 / HomeOrbMetrics.internalShadowDiameter
    static let trailingHighlightOffsetXRatio: CGFloat = 18 / HomeOrbMetrics.internalShadowDiameter
    static let trailingHighlightOffsetYRatio: CGFloat = 8 / HomeOrbMetrics.internalShadowDiameter
    static let trailingHighlightBlurRatio: CGFloat = 18 / HomeOrbMetrics.internalShadowDiameter

    static let topHighlightOpacity = 0.18
    static let topHighlightWidthRatio: CGFloat = 104 / HomeOrbMetrics.internalShadowDiameter
    static let topHighlightHeightRatio: CGFloat = 54 / HomeOrbMetrics.internalShadowDiameter
    static let topHighlightOffsetYRatio: CGFloat = 12 / HomeOrbMetrics.internalShadowDiameter
    static let topHighlightBlurRatio: CGFloat = 16 / HomeOrbMetrics.internalShadowDiameter

    static let internalShadowOpacity = 0.62
    static let innerLightDiameterRatio: CGFloat = 169.24445 / HomeOrbMetrics.internalShadowDiameter
    static let innerDarkDiameterRatio: CGFloat = 159.28889 / HomeOrbMetrics.internalShadowDiameter
    static let innerDarkBlurRatio: CGFloat = 39.82222 / HomeOrbMetrics.internalShadowDiameter
    static let innerLightBlurRatio: CGFloat = 11.94667 / HomeOrbMetrics.internalShadowDiameter
    static let innerLightGradientStart = UnitPoint(x: 1, y: 0.13)
    static let innerLightGradientEnd = UnitPoint(x: 0.22, y: 1)

    static let textureMaskStops = [
        HomeOrbGradientStop(colorOpacity: 1, location: 0),
        HomeOrbGradientStop(colorOpacity: 0.56, location: 0.38),
        HomeOrbGradientStop(colorOpacity: 0.18, location: 0.68),
        HomeOrbGradientStop(colorOpacity: 0.06, location: 1)
    ]
    static let textureRimWidthRatio: CGFloat = 0.11
    static let textureRimBlurRatio: CGFloat = 0.025

    static let groundShadowDiameterRatio: CGFloat = 159.2888946533203 / 224
    static let groundShadowCenterXRatio: CGFloat = (31.859375 + 159.2888946533203 / 2) / 224
    static let groundShadowCenterYRatio: CGFloat = (116.48046875 + 159.2888946533203 / 2) / 224
    static let groundShadowBlurRatio: CGFloat = 39.82222 / 224
}

struct HomeOrbGradientStop {
    let colorOpacity: Double
    let location: CGFloat
}
