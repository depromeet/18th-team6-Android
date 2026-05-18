import SwiftUI

struct HomeGlassBall: View {
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @State private var dragTranslation = CGSize.zero
    @State private var settledRollDegrees: CGFloat = 0
    @State private var inertialRollDegrees: CGFloat = 0

    let normalRatio: Double
    let warningRatio: Double
    let interiorItems: [HomeOrbInteriorItem]

    init(
        normalRatio: Double,
        warningRatio: Double,
        interiorItems: [HomeOrbInteriorItem] = HomeOrbInteriorItem.previewItems
    ) {
        self.normalRatio = normalRatio
        self.warningRatio = warningRatio
        self.interiorItems = interiorItems
    }

    var body: some View {
        let drag = HomeOrbDragFrame(
            translation: reduceMotion ? .zero : dragTranslation,
            settledRollDegrees: reduceMotion ? 0 : settledRollDegrees,
            inertialRollDegrees: reduceMotion ? 0 : inertialRollDegrees
        )

        ZStack {
            // 상태 링
            HomeOrbStatusRing(normalRatio: normalRatio, warningRatio: warningRatio)
                .frame(
                    width: HomeOrbMetrics.ringDiameter,
                    height: HomeOrbMetrics.ringDiameter
                )

            // 링 위에만 얹히는 하단 그림자
            HomeOrbGroundShadow()
                .frame(
                    width: HomeOrbMetrics.ringDiameter,
                    height: HomeOrbMetrics.ringDiameter
                )
                .blendMode(.multiply)
                .allowsHitTesting(false)

            // 유리볼
            ZStack {
                // (Planet Background) Elipse 기반 Shadow
                HomeOrbInternalShadow()
                    .frame(
                        width: HomeOrbMetrics.internalShadowDiameter,
                        height: HomeOrbMetrics.internalShadowDiameter
                    )
                    .allowsHitTesting(false)

                // 내부 텍스쳐
                HomeOrbGlassTextureOverlay()
                    .allowsHitTesting(false)
                    .blendMode(.hardLight)

                // 내부에 들어가는 아이템들
                HomeOrbInteriorItems(items: interiorItems, drag: drag)
                    .frame(
                        width: HomeOrbMetrics.glassBallDiameter,
                        height: HomeOrbMetrics.glassBallDiameter
                    )
                    .clipShape(Circle())
                    .blendMode(.hardLight)
            }
            .frame(
                width: HomeOrbMetrics.glassBallDiameter,
                height: HomeOrbMetrics.glassBallDiameter
            )
            .clipShape(Circle())
            .compositingGroup()
            .rotation3DEffect(
                .degrees(drag.pitchDegrees),
                axis: (x: 1, y: 0, z: 0),
                perspective: 0.55
            )
            .rotation3DEffect(
                .degrees(drag.yawDegrees),
                axis: (x: 0, y: 1, z: 0),
                perspective: 0.55
            )
            .contentShape(Circle())
            .gesture(orbDragGesture, including: reduceMotion ? .none : .all)
        }
        .frame(
            width: HomeOrbMetrics.outerDiameter,
            height: HomeOrbMetrics.outerDiameter
        )
    }

    private var orbDragGesture: some Gesture {
        DragGesture(minimumDistance: 3)
            .onChanged { value in
                dragTranslation = HomeOrbDragFrame.clamped(value.translation)
            }
            .onEnded { value in
                let currentTranslation = HomeOrbDragFrame.clamped(value.translation)
                let projectedTranslation = HomeOrbDragFrame.clamped(value.predictedEndTranslation)
                let releaseRollDegrees = HomeOrbDragFrame.rollDelta(for: currentTranslation)
                let inertialDeltaDegrees = HomeOrbDragFrame.inertialRollDelta(
                    from: currentTranslation,
                    predicted: projectedTranslation
                )

                settledRollDegrees += inertialRollDegrees + releaseRollDegrees
                inertialRollDegrees = 0

                withAnimation(.timingCurve(0.16, 1, 0.3, 1, duration: 0.82)) {
                    inertialRollDegrees = inertialDeltaDegrees
                    dragTranslation = .zero
                }
            }
    }
}

#Preview {
    HomeGlassBall(normalRatio: 0.62, warningRatio: 0.38)
        .frame(width: HomeOrbMetrics.outerDiameter, height: HomeOrbMetrics.outerDiameter)
        .background(OBRitColors.backgroundDefaultDefault)
}
