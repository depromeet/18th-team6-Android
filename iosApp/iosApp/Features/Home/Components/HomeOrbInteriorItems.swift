import SwiftUI

struct HomeOrbInteriorItem: Identifiable, Hashable {
    let id: Int
    let assetName: String
    let weight: CGFloat

    init(id: Int, assetName: String, weight: CGFloat = 1) {
        self.id = id
        self.assetName = assetName
        self.weight = weight
    }
}

extension HomeOrbInteriorItem {
    static let previewItems = [
        HomeOrbInteriorItem(id: 1, assetName: "home_orb_detergent", weight: 1.1),
        HomeOrbInteriorItem(id: 2, assetName: "home_orb_sponge", weight: 1.0),
        HomeOrbInteriorItem(id: 3, assetName: "home_orb_toothbrush", weight: 0.95),
        HomeOrbInteriorItem(id: 4, assetName: "home_orb_diffuser", weight: 1.15)
    ]
}

struct HomeOrbInteriorItems: View {
    @StateObject private var sceneStore = HomeOrbInteriorSceneStore()

    let items: [HomeOrbInteriorItem]
    let normalRatio: Double
    let warningRatio: Double
    let drag: HomeOrbDragFrame

    private var visibleItems: [HomeOrbInteriorItem] {
        Array(items.prefix(HomeOrbInteriorMetrics.maxVisibleItemCount))
    }

    private var gradientMix: HomeOrbGradientMix {
        HomeOrbGradientMix(normalRatio: normalRatio, warningRatio: warningRatio)
    }

    var body: some View {
        GeometryReader { geometry in
            HomeOrbSpriteView(scene: sceneStore.scene)
                .frame(width: geometry.size.width, height: geometry.size.height)
                .onAppear {
                    configureScene(size: geometry.size)
                }
                .onChange(of: visibleItems) { _, _ in
                    configureScene(size: geometry.size)
                }
                .onChange(of: gradientMix) { _, _ in
                    configureScene(size: geometry.size)
                }
                .onChange(of: geometry.size) { _, nextSize in
                    configureScene(size: nextSize)
                }
                .onChange(of: drag) { _, nextDrag in
                    sceneStore.scene.updateGravity(for: nextDrag)
                }
        }
    }

    private func configureScene(size: CGSize) {
        sceneStore.scene.configure(
            items: visibleItems,
            gradientMix: gradientMix,
            size: size,
            drag: drag
        )
    }
}

private enum HomeOrbInteriorMetrics {
    static let maxVisibleItemCount = 8
}

private final class HomeOrbInteriorSceneStore: ObservableObject {
    let scene = HomeOrbInteriorPhysicsScene()
}
