import SwiftUI

struct HomeOrbGlassContent: View {
    let normalRatio: Double
    let warningRatio: Double
    let interiorItems: [HomeOrbInteriorItem]
    let sceneCacheKey: HomeOrbInteriorSceneCacheKey
    let drag: HomeOrbDragFrame

    var body: some View {
        ZStack {
            HomeOrbInternalShadow(normalRatio: normalRatio, warningRatio: warningRatio)
                .frame(
                    width: HomeOrbMetrics.internalShadowDiameter,
                    height: HomeOrbMetrics.internalShadowDiameter
                )
                .allowsHitTesting(false)

            HomeOrbGlassTextureOverlay()
                .allowsHitTesting(false)
                .blendMode(.hardLight)

            HomeOrbInteriorItems(
                items: interiorItems,
                normalRatio: normalRatio,
                warningRatio: warningRatio,
                sceneCacheKey: sceneCacheKey,
                drag: drag
            )
            .frame(
                width: HomeOrbMetrics.glassBallDiameter,
                height: HomeOrbMetrics.glassBallDiameter
            )
            .clipShape(Circle())
        }
    }
}

private struct HomeOrbInternalShadow: View {
    let normalRatio: Double
    let warningRatio: Double

    init(normalRatio: Double = 0.62, warningRatio: Double = 0.38) {
        self.normalRatio = normalRatio
        self.warningRatio = warningRatio
    }

    var body: some View {
        Circle()
            .fill(reflectedRingGradient)
            .overlay {
                internalEllipseStack
            }
            .overlay(alignment: .bottom) {
                let diameter = HomeOrbMetrics.internalShadowDiameter

                Ellipse()
                    .fill(
                        RadialGradient(
                            stops: HomeOrbGlassMetrics.bottomShadowOpacityStops.map {
                                .init(color: Color.black.opacity($0.colorOpacity), location: $0.location)
                            },
                            center: HomeOrbGlassMetrics.bottomShadowCenter,
                            startRadius: 0,
                            endRadius: diameter * HomeOrbGlassMetrics.bottomShadowEndRadiusRatio
                        )
                    )
                    .frame(
                        width: diameter * HomeOrbGlassMetrics.bottomShadowWidthRatio,
                        height: diameter * HomeOrbGlassMetrics.bottomShadowHeightRatio
                    )
                    .offset(y: diameter * HomeOrbGlassMetrics.bottomShadowOffsetYRatio)
                    .blur(radius: diameter * HomeOrbGlassMetrics.bottomShadowBlurRatio)
            }
            .overlay(alignment: .trailing) {
                let diameter = HomeOrbMetrics.internalShadowDiameter

                Ellipse()
                    .fill(OBRitColors.common00.opacity(HomeOrbGlassMetrics.trailingHighlightOpacity))
                    .frame(
                        width: diameter * HomeOrbGlassMetrics.trailingHighlightWidthRatio,
                        height: diameter * HomeOrbGlassMetrics.trailingHighlightHeightRatio
                    )
                    .offset(
                        x: diameter * HomeOrbGlassMetrics.trailingHighlightOffsetXRatio,
                        y: diameter * HomeOrbGlassMetrics.trailingHighlightOffsetYRatio
                    )
                    .blur(radius: diameter * HomeOrbGlassMetrics.trailingHighlightBlurRatio)
                    .blendMode(.screen)
            }
            .overlay(alignment: .top) {
                let diameter = HomeOrbMetrics.internalShadowDiameter

                Ellipse()
                    .fill(OBRitColors.common00.opacity(HomeOrbGlassMetrics.topHighlightOpacity))
                    .frame(
                        width: diameter * HomeOrbGlassMetrics.topHighlightWidthRatio,
                        height: diameter * HomeOrbGlassMetrics.topHighlightHeightRatio
                    )
                    .offset(y: diameter * HomeOrbGlassMetrics.topHighlightOffsetYRatio)
                    .blur(radius: diameter * HomeOrbGlassMetrics.topHighlightBlurRatio)
                    .blendMode(.screen)
            }
            .opacity(HomeOrbGlassMetrics.internalShadowOpacity)
            .clipShape(Circle())
    }

    private var internalEllipseStack: some View {
        GeometryReader { geometry in
            let lightDiameter = geometry.size.width * HomeOrbGlassMetrics.innerLightDiameterRatio
            let darkDiameter = geometry.size.width * HomeOrbGlassMetrics.innerDarkDiameterRatio

            ZStack {
                Circle()
                    .fill(HomeOrbGlassMetrics.shadowCoreColor)
                    .frame(width: darkDiameter, height: darkDiameter)
                    .blur(radius: geometry.size.width * HomeOrbGlassMetrics.innerDarkBlurRatio)

                Circle()
                    .fill(
                        LinearGradient(
                            stops: [
                                .init(color: .white, location: 0),
                                .init(color: .white.opacity(0), location: 1)
                            ],
                            startPoint: HomeOrbGlassMetrics.innerLightGradientStart,
                            endPoint: HomeOrbGlassMetrics.innerLightGradientEnd
                        )
                    )
                    .frame(width: lightDiameter, height: lightDiameter)
                    .blur(radius: geometry.size.width * HomeOrbGlassMetrics.innerLightBlurRatio)
            }
            .frame(width: geometry.size.width, height: geometry.size.height)
        }
    }

    private var reflectedRingGradient: LinearGradient {
        LinearGradient(
            stops: HomeOrbStatusGradient.reflectedStops(for: gradientMix),
            startPoint: .leading,
            endPoint: .trailing
        )
    }

    private var gradientMix: HomeOrbGradientMix {
        HomeOrbGradientMix(normalRatio: normalRatio, warningRatio: warningRatio)
    }
}

private struct HomeOrbGlassTextureOverlay: View {
    var body: some View {
        Image("home_orb_glass_texture")
            .resizable()
            .scaledToFill()
            .frame(
                width: HomeOrbMetrics.glassBallDiameter,
                height: HomeOrbMetrics.glassBallDiameter
            )
            .mask {
                textureCoverageMask
            }
            .clipShape(Circle())
    }

    private var textureCoverageMask: some View {
        GeometryReader { geometry in
            let diameter = min(geometry.size.width, geometry.size.height)

            ZStack {
                Circle()
                    .fill(
                        LinearGradient(
                            stops: HomeOrbGlassMetrics.textureMaskStops.map {
                                .init(color: .white.opacity($0.colorOpacity), location: $0.location)
                            },
                            startPoint: .top,
                            endPoint: .bottom
                        )
                    )

                Circle()
                    .strokeBorder(.white, lineWidth: diameter * HomeOrbGlassMetrics.textureRimWidthRatio)
                    .blur(radius: diameter * HomeOrbGlassMetrics.textureRimBlurRatio)
            }
            .frame(width: diameter, height: diameter)
            .position(x: geometry.size.width / 2, y: geometry.size.height / 2)
        }
    }
}
