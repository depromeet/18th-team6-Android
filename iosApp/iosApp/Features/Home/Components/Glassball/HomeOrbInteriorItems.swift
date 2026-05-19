import SwiftUI

struct HomeOrbInteriorItems: View {
    @StateObject private var sceneStore = HomeOrbInteriorSceneStore()

    let items: [HomeOrbInteriorItem]
    let normalRatio: Double
    let warningRatio: Double
    let drag: HomeOrbDragFrame

    private var visibleItems: [HomeOrbInteriorItem] {
        HomeOrbInteriorInput.visibleItems(from: items)
    }

    private var gradientMix: HomeOrbGradientMix {
        HomeOrbGradientMix(normalRatio: normalRatio, warningRatio: warningRatio)
    }

    var body: some View {
        GeometryReader { geometry in
            HomeOrbSpriteView(scene: sceneStore.scene, drag: drag)
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

private final class HomeOrbInteriorSceneStore: ObservableObject {
    let scene = HomeOrbInteriorPhysicsScene()
}

private enum HomeOrbInteriorInput {
    static func visibleItems(from items: [HomeOrbInteriorItem]) -> [HomeOrbInteriorItem] {
        var seenIds = Set<Int>()
        var visibleItems: [HomeOrbInteriorItem] = []

        for item in items where !item.assetName.isEmpty && seenIds.insert(item.id).inserted {
            visibleItems.append(item)

            if visibleItems.count == HomeOrbVisualConfig.maxVisibleItemCount {
                break
            }
        }

        return visibleItems
    }
}
