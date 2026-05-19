import SpriteKit
import SwiftUI

struct HomeOrbSpriteView: UIViewRepresentable {
    let scene: SKScene

    func makeUIView(context: Context) -> HomeOrbSKView {
        let view = HomeOrbSKView()
        configure(view)
        view.presentScene(scene)
        return view
    }

    func updateUIView(_ view: HomeOrbSKView, context: Context) {
        configure(view)

        if view.scene !== scene {
            view.presentScene(scene)
        }
    }

    private func configure(_ view: SKView) {
        view.allowsTransparency = true
        view.backgroundColor = .clear
        view.clipsToBounds = true
        view.ignoresSiblingOrder = false
        view.isOpaque = false
        view.preferredFramesPerSecond = HomeOrbPhysicsConfig.preferredFramesPerSecond
    }
}

final class HomeOrbSKView: SKView {
    override var canBecomeFocused: Bool {
        false
    }
}
