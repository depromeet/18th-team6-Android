import CoreGraphics

enum HomeOrbPhysicsConfig {
    static let itemCategory: UInt32 = 0x1 << 0
    static let boundaryCategory: UInt32 = 0x1 << 1

    static let preferredFramesPerSecond = 60
    static let worldSpeed: CGFloat = 0.72
    static let protectedEdgeRatio: CGFloat = 0.05
    static let minimumBoundaryRadiusRatio: CGFloat = 0.24

    static let dragGravityOffsetDivisor: CGFloat = 3.36
    static let dragGravityScale: CGFloat = 1.35
    static let rollGravityScale: CGFloat = 2.1
    static let tiltGravityXScale: CGFloat = 3.2
    static let tiltGravityYScale: CGFloat = 4.8
    static let baselineGravityY: CGFloat = -1.2

    static let massBase: CGFloat = 0.85
    static let massWeightScale: CGFloat = 0.28
    static let massIndexScale: CGFloat = 0.02
    static let itemFriction: CGFloat = 0.82
    static let itemRestitution: CGFloat = 0
    static let itemLinearDamping: CGFloat = 4.8
    static let itemAngularDamping: CGFloat = 9.0
    static let boundaryFriction: CGFloat = 0.72
    static let boundaryRestitution: CGFloat = 0

    static let stablePackingMultiplier: CGFloat = 1.8
    static let physicsRadiusMinimumRatio: CGFloat = 7 / HomeOrbMetrics.glassBallDiameter
    static let physicsRadiusVisualWidthRatio: CGFloat = 0.0735
    static let visualBoundaryInsetRatio: CGFloat = 0.40
}

enum HomeOrbVisualConfig {
    static let minimumRatioTotal: CGFloat = 0.0001
    static let gradientTransitionWidth: CGFloat = 0.12
    static let statusRingTransitionArc: CGFloat = 0.05

    static let maxVisibleItemCount = 8
    static let defaultItemWeight: CGFloat = 1
    static let minimumItemWeight: CGFloat = 0.1
    static let itemWeightBase: CGFloat = 0.92
    static let itemWeightRiskScale: CGFloat = 0.055
    static let riskRankUpperBound = 7
    static let itemTargetWidthBase: CGFloat = 0.56
    static let itemTargetWidthWeightScale: CGFloat = 0.03675
    static let maximumWeightForSizing: CGFloat = 1.45
    static let minimumTextureAspectRatio: CGFloat = 0.2
    static let spriteAnchorPoint = CGPoint(x: 0.5, y: 0.42)

    static let jitterSpeedThresholdRatio: CGFloat = 12 / HomeOrbMetrics.glassBallDiameter
    static let jitterDistanceThresholdRatio: CGFloat = 1.4 / HomeOrbMetrics.glassBallDiameter
    static let visualPositionFollowRate: CGFloat = 0.42
    static let visualRotationFollowRate: CGFloat = 0.38

    static let depthVerticalCenter: CGFloat = 0.5
    static let edgeFalloffStart: CGFloat = 0.62
    static let edgeFalloffRange: CGFloat = 0.38
    static let visualScaleMinimum: CGFloat = 0.72
    static let visualScaleBase: CGFloat = 0.86
    static let visualScaleDepthFactor: CGFloat = 0.16
    static let visualScaleEdgeFactor: CGFloat = 0.12
    static let visualScaleMaximum: CGFloat = 1.06
    static let visualAlphaMinimum: CGFloat = 0.45
    static let visualAlphaBase: CGFloat = 0.58
    static let visualAlphaDepthFactor: CGFloat = 0.34
    static let visualAlphaEdgeFactor: CGFloat = 0.20
    static let visualAlphaMaximum: CGFloat = 1
    static let shadowDepthFactor: CGFloat = 0.26
    static let shadowEdgeFactor: CGFloat = 0.18
    static let shadowMaximum: CGFloat = 0.38
    static let zPositionDepthScale: CGFloat = 100
    static let zPositionVerticalScale: CGFloat = 0.1

    static let spawnColumnSpread: CGFloat = 1.18
    static let spawnOddLayerOffset: CGFloat = 0.08
    static let spawnBaseY: CGFloat = -0.34
    static let spawnLayerY: CGFloat = 0.24
    static let spawnEvenYOffset: CGFloat = 0.04
    static let spawnOddYOffset: CGFloat = -0.03
}

enum HomeOrbInteractionConfig {
    static let maxDragDistanceRatio: CGFloat = 92 / HomeOrbMetrics.glassBallDiameter
}
