import CoreGraphics

struct HomeOrbGradientMix: Equatable {
    let normalRatio: Double
    let warningRatio: Double

    var positiveShare: Float {
        Float(normalizedPositiveShare)
    }

    var positiveShareCGFloat: CGFloat {
        CGFloat(normalizedPositiveShare)
    }

    var warningShareCGFloat: CGFloat {
        CGFloat(1 - normalizedPositiveShare)
    }

    var transitionWidth: Float {
        let positive = normalizedPositiveShare
        let warning = 1 - positive
        return Float(min(Double(HomeOrbVisualConfig.gradientTransitionWidth), positive / 2, warning / 2))
    }

    private var normalizedPositiveShare: Double {
        let total = max(normalRatio + warningRatio, Double(HomeOrbVisualConfig.minimumRatioTotal))
        return max(0, min(1, normalRatio / total))
    }
}
