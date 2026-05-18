import SwiftUI

struct HomeOrbInternalShadow: View {
    var body: some View {
        Circle()
            .fill(reflectedRingGradient)
            .overlay {
                internalEllipseStack
            }
            .overlay(alignment: .bottom) {
                Ellipse()
                    .fill(
                        RadialGradient(
                            stops: [
                                .init(color: Color.black.opacity(0.58), location: 0),
                                .init(color: Color.black.opacity(0.32), location: 0.42),
                                .init(color: Color.black.opacity(0), location: 1)
                            ],
                            center: UnitPoint(x: 0.46, y: 0.42),
                            startRadius: 0,
                            endRadius: 92
                        )
                    )
                    .frame(width: 150, height: 86)
                    .offset(y: -18)
                    .blur(radius: 14)
            }
            .overlay(alignment: .trailing) {
                Ellipse()
                    .fill(OBRitColors.common00.opacity(0.30))
                    .frame(width: 70, height: 162)
                    .offset(x: 18, y: 8)
                    .blur(radius: 18)
                    .blendMode(.screen)
            }
            .overlay(alignment: .top) {
                Ellipse()
                    .fill(OBRitColors.common00.opacity(0.18))
                    .frame(width: 104, height: 54)
                    .offset(y: 12)
                    .blur(radius: 16)
                    .blendMode(.screen)
            }
            .opacity(0.62)
            .clipShape(Circle())
    }

    private var internalEllipseStack: some View {
        GeometryReader { geometry in
            let scale = geometry.size.width / HomeOrbMetrics.internalShadowDiameter
            let lightDiameter = 169.24445 * scale
            let darkDiameter = 159.28889 * scale

            ZStack {
                Circle()
                    .fill(Color(red: 0.11, green: 0.11, blue: 0.13))
                    .frame(width: darkDiameter, height: darkDiameter)
                    .blur(radius: 39.82222 * scale)

                Circle()
                    .fill(
                        LinearGradient(
                            stops: [
                                .init(color: .white, location: 0),
                                .init(color: .white.opacity(0), location: 1)
                            ],
                            startPoint: UnitPoint(x: 1, y: 0.13),
                            endPoint: UnitPoint(x: 0.22, y: 1)
                        )
                    )
                    .frame(width: lightDiameter, height: lightDiameter)
                    .blur(radius: 11.94667 * scale)
            }
            .frame(width: geometry.size.width, height: geometry.size.height)
        }
    }

    private var reflectedRingGradient: LinearGradient {
        LinearGradient(
            stops: [
                .init(color: OBRitColors.backgroundPositiveDefault, location: 0),
                .init(color: OBRitColors.backgroundPositiveDefault, location: 0.2),
                .init(color: OBRitColors.backgroundWarningDefault, location: 0.6),
                .init(color: OBRitColors.backgroundWarningDefault, location: 1)
            ],
            startPoint: .leading,
            endPoint: .trailing
        )
    }
}

struct HomeOrbGlassTextureOverlay: View {
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
                            stops: [
                                .init(color: .white, location: 0),
                                .init(color: .white.opacity(0.56), location: 0.38),
                                .init(color: .white.opacity(0.18), location: 0.68),
                                .init(color: .white.opacity(0.06), location: 1)
                            ],
                            startPoint: .top,
                            endPoint: .bottom
                        )
                    )

                Circle()
                    .strokeBorder(.white, lineWidth: diameter * 0.11)
                    .blur(radius: diameter * 0.025)
            }
            .frame(width: diameter, height: diameter)
            .position(x: geometry.size.width / 2, y: geometry.size.height / 2)
        }
    }
}
