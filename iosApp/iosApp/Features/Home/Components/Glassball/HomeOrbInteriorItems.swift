import SwiftUI

enum HomeOrbInteriorSceneCacheKey: Hashable {
    case homeInventory
}

struct HomeOrbInteriorItems: View {
    @StateObject private var sceneStore: HomeOrbInteriorSceneStore

    let items: [HomeOrbInteriorItem]
    let normalRatio: Double
    let warningRatio: Double
    let drag: HomeOrbDragFrame

    init(
        items: [HomeOrbInteriorItem],
        normalRatio: Double,
        warningRatio: Double,
        sceneCacheKey: HomeOrbInteriorSceneCacheKey,
        drag: HomeOrbDragFrame
    ) {
        _sceneStore = StateObject(wrappedValue: HomeOrbInteriorSceneStore(cacheKey: sceneCacheKey))
        self.items = items
        self.normalRatio = normalRatio
        self.warningRatio = warningRatio
        self.drag = drag
    }

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

@MainActor
private final class HomeOrbInteriorSceneStore: ObservableObject {
    let scene: HomeOrbInteriorPhysicsScene

    init(cacheKey: HomeOrbInteriorSceneCacheKey) {
        self.scene = HomeOrbInteriorSceneCache.shared.scene(for: cacheKey)
    }
}

@MainActor
private final class HomeOrbInteriorSceneCache {
    static let shared = HomeOrbInteriorSceneCache()

    private var scenes: [HomeOrbInteriorSceneCacheKey: HomeOrbInteriorPhysicsScene] = [:]

    private init() {}

    func scene(for key: HomeOrbInteriorSceneCacheKey) -> HomeOrbInteriorPhysicsScene {
        if let cachedScene = scenes[key] {
            return cachedScene
        }

        let scene = HomeOrbInteriorPhysicsScene()
        scenes[key] = scene
        return scene
    }
}

private enum HomeOrbInteriorInput {
    static func visibleItems(from items: [HomeOrbInteriorItem]) -> [HomeOrbInteriorItem] {
        var seenIds = Set<Int>()
        var visibleItems: [HomeOrbInteriorItem] = []

        for item in items where !item.imageURL.isEmpty && seenIds.insert(item.id).inserted {
            visibleItems.append(item)

            if visibleItems.count == HomeOrbVisualConfig.maxVisibleItemCount {
                break
            }
        }

        return visibleItems
    }
}
