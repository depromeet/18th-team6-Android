import CoreMotion
import SwiftUI

enum HomeOrbMetrics {
    static let outerDiameter = OBRitSpacing.s40 + OBRitSpacing.s16
    static let assetDiameter = OBRitSpacing.s40 + OBRitSpacing.s10
    static let ringDiameter = OBRitSpacing.s40 + OBRitSpacing.s16
    static let surfaceArcDiameter = OBRitSpacing.s40 + OBRitSpacing.s4
    static let planetDiameter = OBRitSpacing.s40 + OBRitSpacing.s8 + OBRitSpacing.s1_5
}

struct HomeGlassBall: View {
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @StateObject private var motionController = HomeOrbMotionController()

    let normalRatio: Double
    let warningRatio: Double

    var body: some View {
        TimelineView(.animation(minimumInterval: 1.0 / 60.0, paused: reduceMotion)) { context in
            let motion = HomeOrbMotionFrame(
                date: context.date,
                tilt: reduceMotion ? .zero : motionController.tilt,
                reduceMotion: reduceMotion
            )

            ZStack {
                HomeOrbAmbientBackground(warningRatio: warningRatio, motion: motion)

                Ellipse()
                    .fill(Color.black.opacity(0.42))
                    .frame(width: OBRitSpacing.s40, height: OBRitSpacing.s14 - OBRitSpacing.s0_5)
                    .blur(radius: OBRitEffects.backgroundBlur16 + OBRitSpacing.s0_5)
                    .scaleEffect(x: motion.shadowScaleX, y: motion.shadowScaleY)
                    .offset(x: motion.shadowOffset.width, y: OBRitSpacing.s24 + OBRitSpacing.s1 + motion.shadowOffset.height)

                ZStack {
                    HomeOrbGlassBody(warningRatio: warningRatio, motion: motion)

                    HomeOrbStatusRing(normalRatio: normalRatio, warningRatio: warningRatio)
                        .frame(width: HomeOrbMetrics.outerDiameter - OBRitSpacing.s3, height: HomeOrbMetrics.outerDiameter - OBRitSpacing.s3)
                        .rotationEffect(.degrees(motion.ringRotationDegrees))
                        .opacity(0.9)

                    HomeOrbBottomBlurLayer()
                        .offset(x: motion.bottomBlurOffset.width, y: motion.bottomBlurOffset.height)

                    HomeOrbPlanetBackdrop(motion: motion)
                        .padding(OBRitSpacing.s3 + OBRitSpacing.px)
                        .opacity(0.58)
                        .clipShape(Circle())

                    HomeOrbAssetStack(motion: motion)
                        .clipShape(Circle())

                    Image("home_orb_glass_texture")
                        .resizable()
                        .scaledToFill()
                        .scaleEffect(1.04)
                        .rotationEffect(.degrees(motion.textureRotationDegrees))
                        .offset(x: motion.textureOffset.width, y: motion.textureOffset.height)
                        .opacity(0.56)
                        .blendMode(.multiply)
                        .clipShape(Circle())

                    HomeOrbSurfaceArc()
                        .stroke(
                            LinearGradient(
                                stops: [
                                    .init(color: .clear, location: 0),
                                    .init(color: OBRitColors.common00.opacity(0.18), location: 0.22),
                                    .init(color: OBRitColors.common00.opacity(0.58), location: 0.48),
                                    .init(color: OBRitColors.common00.opacity(0.16), location: 0.72),
                                    .init(color: .clear, location: 1)
                                ],
                                startPoint: .bottomLeading,
                                endPoint: .topTrailing
                            ),
                            style: StrokeStyle(lineWidth: OBRitSpacing.s1, lineCap: .round)
                        )
                        .blendMode(.screen)
                        .opacity(0.86)
                        .frame(width: HomeOrbMetrics.outerDiameter - OBRitSpacing.s11, height: HomeOrbMetrics.outerDiameter - OBRitSpacing.s11)
                        .rotationEffect(.degrees(motion.highlightRotationDegrees))
                        .offset(x: motion.highlightOffset.width, y: motion.highlightOffset.height)
                }
                .frame(width: HomeOrbMetrics.outerDiameter, height: HomeOrbMetrics.outerDiameter)
                .clipShape(Circle())
                .scaleEffect(motion.orbScale)
                .rotation3DEffect(.degrees(motion.tiltRotationYDegrees), axis: (x: 1, y: 0, z: 0), perspective: 0.55)
                .rotation3DEffect(.degrees(motion.tiltRotationXDegrees), axis: (x: 0, y: 1, z: 0), perspective: 0.55)
                .offset(x: motion.orbOffset.width, y: motion.orbOffset.height)
            }
        }
        .animation(.easeOut(duration: 0.14), value: motionController.tilt)
        .onAppear {
            if !reduceMotion {
                motionController.start()
            }
        }
        .onDisappear {
            motionController.stop()
        }
        .onChange(of: reduceMotion) { newValue in
            if newValue {
                motionController.stop()
            } else {
                motionController.start()
            }
        }
    }
}

private struct HomeOrbGlassBody: View {
    let warningRatio: Double
    let motion: HomeOrbMotionFrame

    var body: some View {
        Circle()
            .fill(Color(red: 7 / 255, green: 12 / 255, blue: 13 / 255))
            .overlay {
                Circle()
                    .stroke(
                        RadialGradient(
                            colors: [
                                .clear,
                                .clear,
                                Color.black.opacity(0.45),
                                Color.black.opacity(0.85)
                            ],
                            center: .center,
                            startRadius: 0,
                            endRadius: HomeOrbMetrics.outerDiameter / 2
                        ),
                        lineWidth: OBRitSpacing.s3 + OBRitSpacing.s0_5
                    )
            }
            .overlay {
                Circle()
                    .fill(
                        RadialGradient(
                            colors: [
                                OBRitColors.textWarningDefault.opacity(0.32 * warningRatio),
                                .clear
                            ],
                            center: UnitPoint(x: 0.78 + motion.tilt.width * 0.04, y: 0.52 + motion.tilt.height * 0.04),
                            startRadius: 0,
                            endRadius: HomeOrbMetrics.outerDiameter * 0.3
                        )
                    )
            }
    }
}

private struct HomeOrbBottomBlurLayer: View {
    var body: some View {
        Circle()
            .fill(OBRitColors.backgroundDefaultDefault)
            .frame(width: OBRitSpacing.s40, height: OBRitSpacing.s40)
            .blur(radius: OBRitSpacing.s20)
            .offset(y: OBRitSpacing.s28)
    }
}

private struct HomeOrbPlanetBackdrop: View {
    let motion: HomeOrbMotionFrame

    var body: some View {
        Circle()
            .fill(
                LinearGradient(
                    stops: [
                        .init(color: OBRitColors.textPositiveDefault, location: 0),
                        .init(color: OBRitColors.textPositiveDefault, location: 0.3),
                        .init(color: OBRitColors.textWarningDefault, location: 0.7),
                        .init(color: OBRitColors.textWarningDefault, location: 1)
                    ],
                    startPoint: .leading,
                    endPoint: .trailing
                )
            )
            .overlay {
                Circle()
                    .stroke(
                        RadialGradient(
                            stops: [
                                .init(color: .clear, location: 0),
                                .init(color: .clear, location: 0.58),
                                .init(color: OBRitColors.common00.opacity(0.08), location: 0.68),
                                .init(color: OBRitColors.common00.opacity(0.24), location: 1)
                            ],
                            center: .center,
                            startRadius: 0,
                            endRadius: HomeOrbMetrics.outerDiameter / 2
                        ),
                        lineWidth: OBRitSpacing.s12
                    )
                    .blendMode(.screen)
            }
            .scaleEffect(1.02)
            .rotationEffect(.degrees(motion.backdropRotationDegrees))
            .offset(x: motion.backdropOffset.width, y: motion.backdropOffset.height)
    }
}

private struct HomeOrbAssetStack: View {
    private struct AssetSpec {
        let name: String
        let width: CGFloat
        let height: CGFloat
        let centerX: CGFloat
        let centerY: CGFloat
        let rotation: CGFloat
        let depth: CGFloat
        let driftSeed: Double
    }

    let motion: HomeOrbMotionFrame

    private let specs = [
        AssetSpec(name: "home_orb_detergent", width: 78.95, height: 93.15, centerX: 55.38, centerY: 70.13, rotation: 22.89, depth: 0.75, driftSeed: 0.4),
        AssetSpec(name: "home_orb_sponge", width: 84.01, height: 72.13, centerX: 98.46, centerY: 41.81, rotation: 22.39, depth: 0.52, driftSeed: 1.1),
        AssetSpec(name: "home_orb_toothbrush", width: 130.83, height: 131.46, centerX: 94.08, centerY: 107.32, rotation: -19.18, depth: 0.92, driftSeed: 2.0),
        AssetSpec(name: "home_orb_diffuser", width: 100.28, height: 110.63, centerX: 138.33, centerY: 61.06, rotation: -30, depth: 0.68, driftSeed: 2.8),
        AssetSpec(name: "home_orb_shower_filter", width: 72.88, height: 83, centerX: 157.5, centerY: 113.5, rotation: 0, depth: 0.84, driftSeed: 3.6),
        AssetSpec(name: "home_orb_razor", width: 126.06, height: 136.84, centerX: 120.35, centerY: 141.01, rotation: 30, depth: 0.98, driftSeed: 4.4),
        AssetSpec(name: "home_orb_towel", width: 123.13, height: 112.9, centerX: 57.53, centerY: 129.9, rotation: 30, depth: 1.08, driftSeed: 5.2)
    ]

    var body: some View {
        GeometryReader { geometry in
            let scale = geometry.size.width / 200
            let enlarge: CGFloat = 1.4
            let spread: CGFloat = 0.9

            ZStack {
                ForEach(specs.indices, id: \.self) { index in
                    let spec = specs[index]
                    let assetOffset = motion.assetOffset(depth: spec.depth, seed: spec.driftSeed)
                    HomeOrbAssetImage(name: spec.name)
                        .frame(width: spec.width * enlarge * scale, height: spec.height * enlarge * scale)
                        .rotationEffect(.degrees(spec.rotation + motion.assetRotationDegrees(depth: spec.depth, seed: spec.driftSeed)))
                        .position(
                            x: stackCenter(spec.centerX, spread: spread) * scale + assetOffset.width,
                            y: stackCenter(spec.centerY, spread: spread) * scale + assetOffset.height
                        )
                }
            }
            .frame(width: geometry.size.width, height: geometry.size.height)
        }
    }

    private func stackCenter(_ originalCenter: CGFloat, spread: CGFloat) -> CGFloat {
        (originalCenter - 100) * spread + 100
    }
}

private struct HomeOrbAssetImage: View {
    let name: String

    var body: some View {
        ZStack {
            baseImage

            tintedImage(Color(red: 223 / 255, green: 1, blue: 247 / 255))
                .opacity(0.2)
                .blendMode(.screen)

            tintedImage(OBRitColors.common00)
                .opacity(0.08)
                .blendMode(.plusLighter)

            tintedImage(Color(red: 1, green: 226 / 255, blue: 234 / 255))
                .opacity(0.04)
                .blendMode(.plusLighter)
        }
    }

    private var baseImage: some View {
        Image(name)
            .resizable()
            .scaledToFit()
    }

    private func tintedImage(_ color: Color) -> some View {
        Image(name)
            .resizable()
            .renderingMode(.template)
            .scaledToFit()
            .foregroundStyle(color)
    }
}

private struct HomeOrbAmbientBackground: View {
    let warningRatio: Double
    let motion: HomeOrbMotionFrame

    var body: some View {
        ZStack {
            Circle()
                .fill(
                    RadialGradient(
                        colors: [
                            OBRitColors.textPositiveDefault.opacity(0.22),
                            OBRitColors.textPositiveDefault.opacity(0.08),
                            Color.clear
                        ],
                        center: .leading,
                        startRadius: OBRitSpacing.s2 + OBRitSpacing.s0_5,
                        endRadius: OBRitSpacing.s36 + OBRitSpacing.s2
                    )
                )
                .frame(width: OBRitSpacing.s40 + OBRitSpacing.s36, height: OBRitSpacing.s40 + OBRitSpacing.s36)
                .offset(x: -OBRitSpacing.s11 + motion.ambientOffset.width, y: motion.ambientOffset.height)

            Circle()
                .fill(
                    RadialGradient(
                        colors: [
                            OBRitColors.textWarningDefault.opacity(0.10 + warningRatio * 0.36),
                            OBRitColors.textWarningDefault.opacity(0.04 + warningRatio * 0.16),
                            Color.clear
                        ],
                        center: .trailing,
                        startRadius: OBRitSpacing.s2 + OBRitSpacing.s0_5,
                        endRadius: OBRitSpacing.s32 + OBRitSpacing.s2 + OBRitSpacing.s0_5
                    )
                )
                .frame(width: OBRitSpacing.s40 + OBRitSpacing.s28 + OBRitSpacing.s1_5, height: OBRitSpacing.s40 + OBRitSpacing.s28 + OBRitSpacing.s1_5)
                .offset(x: OBRitSpacing.s10 + OBRitSpacing.s0_5 - motion.ambientOffset.width, y: OBRitSpacing.s1 - motion.ambientOffset.height)
        }
    }
}

private final class HomeOrbMotionController: ObservableObject {
    @Published var tilt = CGSize.zero

    private let manager = CMMotionManager()
    private let updateInterval = 1.0 / 30.0
    private let tiltLimit = 0.45
    private var isRunning = false

    deinit {
        stop()
    }

    func start() {
        guard !isRunning, manager.isDeviceMotionAvailable else { return }

        isRunning = true
        manager.deviceMotionUpdateInterval = updateInterval
        manager.startDeviceMotionUpdates(to: .main) { [weak self] motion, _ in
            guard let self, let motion else { return }

            let nextTilt = CGSize(
                width: self.normalizedTilt(from: motion.attitude.roll),
                height: self.normalizedTilt(from: -motion.attitude.pitch)
            )

            self.tilt = CGSize(
                width: self.tilt.width * 0.82 + nextTilt.width * 0.18,
                height: self.tilt.height * 0.82 + nextTilt.height * 0.18
            )
        }
    }

    func stop() {
        guard isRunning else { return }

        isRunning = false
        manager.stopDeviceMotionUpdates()
        tilt = .zero
    }

    private func normalizedTilt(from radians: Double) -> CGFloat {
        CGFloat(max(-tiltLimit, min(tiltLimit, radians)) / tiltLimit)
    }
}

private struct HomeOrbMotionFrame: Equatable {
    let phase: Double
    let tilt: CGSize
    let reduceMotion: Bool

    init(date: Date, tilt: CGSize, reduceMotion: Bool) {
        self.phase = reduceMotion ? 0 : date.timeIntervalSinceReferenceDate
        self.tilt = reduceMotion ? .zero : tilt
        self.reduceMotion = reduceMotion
    }

    var idleX: CGFloat {
        reduceMotion ? 0 : CGFloat(sin(phase * 0.72))
    }

    var idleY: CGFloat {
        reduceMotion ? 0 : CGFloat(cos(phase * 0.58))
    }

    var orbOffset: CGSize {
        CGSize(
            width: tilt.width * 3.0 + idleX * 1.3,
            height: tilt.height * 2.6 + idleY * 1.0
        )
    }

    var orbScale: CGFloat {
        1 + abs(idleY) * 0.006
    }

    var tiltRotationXDegrees: CGFloat {
        tilt.width * 4.0 + idleX * 0.6
    }

    var tiltRotationYDegrees: CGFloat {
        -tilt.height * 4.5 + idleY * 0.4
    }

    var shadowOffset: CGSize {
        CGSize(width: tilt.width * 3.0 + idleX * 1.4, height: abs(tilt.height) * 1.0 + idleY * 0.7)
    }

    var shadowScaleX: CGFloat {
        1.0 + abs(tilt.width) * 0.04 + abs(idleX) * 0.01
    }

    var shadowScaleY: CGFloat {
        1.0 - abs(tilt.height) * 0.03
    }

    var ringRotationDegrees: CGFloat {
        tilt.width * 4.2 + idleX * 1.5
    }

    var backdropOffset: CGSize {
        CGSize(width: -tilt.width * 5.5 + idleX * 1.4, height: -tilt.height * 4.0 + idleY * 1.1)
    }

    var backdropRotationDegrees: CGFloat {
        tilt.width * 2.5 + idleX * 0.8
    }

    var textureOffset: CGSize {
        CGSize(width: tilt.width * 4.0 - idleX * 1.6, height: tilt.height * 3.0 - idleY * 1.2)
    }

    var textureRotationDegrees: CGFloat {
        tilt.width * 1.2 + idleX * 0.6
    }

    var highlightOffset: CGSize {
        CGSize(width: tilt.width * 7.0 + idleX * 1.2, height: tilt.height * 5.0 + idleY)
    }

    var highlightRotationDegrees: CGFloat {
        tilt.width * 3.0 + idleX
    }

    var bottomBlurOffset: CGSize {
        CGSize(width: tilt.width * 4.5, height: tilt.height * 2.0)
    }

    var ambientOffset: CGSize {
        CGSize(width: tilt.width * 5.0 + idleX * 1.6, height: tilt.height * 3.0 + idleY)
    }

    func assetOffset(depth: CGFloat, seed: Double) -> CGSize {
        guard !reduceMotion else { return .zero }

        let driftX = CGFloat(sin(phase * (0.64 + Double(depth) * 0.08) + seed)) * (1.8 + depth * 1.9)
        let driftY = CGFloat(cos(phase * (0.52 + Double(depth) * 0.06) + seed * 0.73)) * (1.3 + depth * 1.4)

        return CGSize(
            width: tilt.width * depth * 10.0 + driftX,
            height: tilt.height * depth * 8.0 + driftY
        )
    }

    func assetRotationDegrees(depth: CGFloat, seed: Double) -> CGFloat {
        guard !reduceMotion else { return 0 }

        return tilt.width * depth * 3.2 + CGFloat(sin(phase * 0.48 + seed)) * (0.7 + depth * 0.45)
    }
}

#Preview {
    HomeGlassBall(normalRatio: 0.77, warningRatio: 0.23)
        .frame(width: HomeOrbMetrics.outerDiameter, height: HomeOrbMetrics.outerDiameter)
        .background(OBRitColors.backgroundDefaultDefault)
}
