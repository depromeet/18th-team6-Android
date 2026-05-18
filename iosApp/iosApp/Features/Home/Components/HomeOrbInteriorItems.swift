import SpriteKit
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

struct HomeOrbInteriorItems: View {
    @StateObject private var sceneStore = HomeOrbInteriorSceneStore()

    let items: [HomeOrbInteriorItem]
    let drag: HomeOrbDragFrame

    private var visibleItems: [HomeOrbInteriorItem] {
        Array(items.prefix(maxVisibleItemCount))
    }

    var body: some View {
        GeometryReader { geometry in
            SpriteView(
                scene: sceneStore.scene,
                preferredFramesPerSecond: 60,
                options: [.allowsTransparency]
            )
            .frame(width: geometry.size.width, height: geometry.size.height)
            .onAppear {
                sceneStore.scene.configure(
                    items: visibleItems,
                    size: geometry.size,
                    drag: drag
                )
            }
            .onChange(of: visibleItems) { nextItems in
                sceneStore.scene.configure(
                    items: nextItems,
                    size: geometry.size,
                    drag: drag
                )
            }
            .onChange(of: geometry.size) { nextSize in
                sceneStore.scene.configure(
                    items: visibleItems,
                    size: nextSize,
                    drag: drag
                )
            }
            .onChange(of: drag) { nextDrag in
                sceneStore.scene.updateGravity(for: nextDrag)
            }
        }
    }

    private var maxVisibleItemCount: Int { 8 }
}

private final class HomeOrbInteriorSceneStore: ObservableObject {
    let scene = HomeOrbInteriorPhysicsScene()
}

private final class HomeOrbInteriorNodePair {
    let physicsNode: SKNode
    let spriteNode: SKSpriteNode
    var physicsRadius: CGFloat = 0
    var visualPosition: CGPoint
    var visualRotation: CGFloat

    init(physicsNode: SKNode, spriteNode: SKSpriteNode) {
        self.physicsNode = physicsNode
        self.spriteNode = spriteNode
        self.visualPosition = physicsNode.position
        self.visualRotation = physicsNode.zRotation
    }
}

private final class HomeOrbInteriorPhysicsScene: SKScene {
    private var itemNodes: [Int: HomeOrbInteriorNodePair] = [:]
    private var configuredSize = CGSize.zero
    private let itemCategory: UInt32 = 0x1 << 0
    private let boundaryCategory: UInt32 = 0x1 << 1
    private static let depthShaderSource = """
    uniform float u_frontDepth;
    uniform float u_edgeFalloff;

    void main() {
        vec4 color = SKDefaultShading();
        vec2 centered = v_tex_coord - vec2(0.5, 0.5);
        float spriteEdge = smoothstep(0.30, 0.78, length(centered));
        float depthLight = mix(0.68, 1.08, u_frontDepth);
        float rimShade = 1.0 - spriteEdge * mix(0.16, 0.34, u_edgeFalloff);
        color.rgb *= depthLight * rimShade;
        color.a *= mix(0.72, 1.0, u_frontDepth) * (1.0 - spriteEdge * 0.16);
        gl_FragColor = color;
    }
    """

    override init() {
        super.init(size: CGSize(width: HomeOrbMetrics.glassBallDiameter, height: HomeOrbMetrics.glassBallDiameter))
        scaleMode = .resizeFill
        backgroundColor = .clear
        physicsWorld.speed = 0.72
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func didMove(to view: SKView) {
        view.allowsTransparency = true
        view.backgroundColor = .clear
        view.clipsToBounds = true
    }

    override func didSimulatePhysics() {
        for pair in itemNodes.values {
            applySphericalDepth(to: pair)
        }
    }

    func configure(items: [HomeOrbInteriorItem], size: CGSize, drag: HomeOrbDragFrame) {
        guard size.width > 0, size.height > 0 else { return }

        if configuredSize != size {
            configuredSize = size
            self.size = size
            updateBoundary(for: size)
        }

        let visibleIds = Set(items.map(\.id))
        let staleIds = itemNodes.keys.filter { !visibleIds.contains($0) }
        for id in staleIds {
            itemNodes[id]?.physicsNode.removeFromParent()
            itemNodes[id]?.spriteNode.removeFromParent()
            itemNodes[id] = nil
        }

        for (index, item) in items.enumerated() {
            let pair = itemNodes[item.id] ?? makeNodePair(
                for: item,
                itemCount: items.count,
                index: index,
                size: size
            )
            configure(pair: pair, for: item, itemCount: items.count, index: index, in: size)

            if pair.physicsNode.parent == nil {
                addChild(pair.physicsNode)
            }

            if pair.spriteNode.parent == nil {
                addChild(pair.spriteNode)
            }

            itemNodes[item.id] = pair
        }

        updateGravity(for: drag)
    }

    func updateGravity(for drag: HomeOrbDragFrame) {
        let rollRadians = drag.contentRotationDegrees * .pi / 180
        let offsetGravityX = drag.contentOffset.width / 3.36 * 1.35
        let rollGravityX = sin(rollRadians) * 2.1
        physicsWorld.gravity = CGVector(dx: offsetGravityX + rollGravityX, dy: -2.6)
    }

    private func makeNodePair(
        for item: HomeOrbInteriorItem,
        itemCount: Int,
        index: Int,
        size: CGSize
    ) -> HomeOrbInteriorNodePair {
        let initialPosition = initialPosition(for: index, itemCount: itemCount, in: size)
        let initialRotation = initialRotation(for: item, index: index)
        let physicsNode = SKNode()
        physicsNode.position = initialPosition
        physicsNode.zRotation = initialRotation

        let texture = SKTexture(imageNamed: item.assetName)
        let spriteNode = SKSpriteNode(texture: texture)
        spriteNode.name = item.assetName
        spriteNode.position = initialPosition
        spriteNode.zRotation = initialRotation
        spriteNode.blendMode = .alpha
        spriteNode.shader = Self.makeDepthShader()
        spriteNode.alpha = 1

        return HomeOrbInteriorNodePair(
            physicsNode: physicsNode,
            spriteNode: spriteNode
        )
    }

    private func configure(
        pair: HomeOrbInteriorNodePair,
        for item: HomeOrbInteriorItem,
        itemCount: Int,
        index: Int,
        in size: CGSize
    ) {
        let diameter = min(size.width, size.height)
        let targetWidth = diameter * (0.8 + min(item.weight, 1.45) * 0.0525)
        let textureSize = pair.spriteNode.texture?.size() ?? CGSize(width: 1, height: 1)
        let aspectRatio = max(textureSize.height / max(textureSize.width, 0.0001), 0.2)
        let targetSize = CGSize(width: targetWidth, height: targetWidth * aspectRatio)
        let targetPhysicsRadius = physicsRadius(for: targetWidth, itemCount: itemCount, in: size)

        guard
            pair.spriteNode.size != targetSize ||
                abs(pair.physicsRadius - targetPhysicsRadius) > 0.5 ||
                pair.physicsNode.physicsBody == nil
        else {
            return
        }

        pair.spriteNode.size = targetSize
        pair.spriteNode.anchorPoint = CGPoint(x: 0.5, y: 0.42)
        pair.physicsRadius = targetPhysicsRadius
        pair.physicsNode.physicsBody = SKPhysicsBody(circleOfRadius: targetPhysicsRadius)
        pair.physicsNode.physicsBody?.allowsRotation = true
        pair.physicsNode.physicsBody?.affectedByGravity = true
        pair.physicsNode.physicsBody?.categoryBitMask = itemCategory
        pair.physicsNode.physicsBody?.collisionBitMask = itemCategory | boundaryCategory
        pair.physicsNode.physicsBody?.contactTestBitMask = 0
        pair.physicsNode.physicsBody?.mass = 0.85 + item.weight * 0.28 + CGFloat(index) * 0.02
        pair.physicsNode.physicsBody?.friction = 0.82
        pair.physicsNode.physicsBody?.restitution = 0
        pair.physicsNode.physicsBody?.linearDamping = 4.8
        pair.physicsNode.physicsBody?.angularDamping = 9.0
    }

    private static func makeDepthShader() -> SKShader {
        let shader = SKShader(source: depthShaderSource)
        shader.uniforms = [
            SKUniform(name: "u_frontDepth", float: 1),
            SKUniform(name: "u_edgeFalloff", float: 0)
        ]
        return shader
    }

    private func updateBoundary(for size: CGSize) {
        let radius = boundaryRadius(for: size)
        let rect = CGRect(
            x: size.width / 2 - radius,
            y: size.height / 2 - radius,
            width: radius * 2,
            height: radius * 2
        )
        let path = CGMutablePath()
        path.addEllipse(in: rect)

        physicsBody = SKPhysicsBody(edgeLoopFrom: path)
        physicsBody?.categoryBitMask = boundaryCategory
        physicsBody?.collisionBitMask = itemCategory
        physicsBody?.contactTestBitMask = 0
        physicsBody?.friction = 0.72
        physicsBody?.restitution = 0
    }

    private func boundaryRadius(for size: CGSize) -> CGFloat {
        let diameter = min(size.width, size.height)
        return diameter / 2 - diameter * 0.10
    }

    private func physicsRadius(for visualWidth: CGFloat, itemCount: Int, in size: CGSize) -> CGFloat {
        let stablePackingRadius = boundaryRadius(for: size) / sqrt(CGFloat(max(itemCount, 1)) * 1.8)
        return max(10, min(visualWidth * 0.105, stablePackingRadius))
    }

    private func applySphericalDepth(to pair: HomeOrbInteriorNodePair) {
        let physicsNode = pair.physicsNode
        let spriteNode = pair.spriteNode
        let diameter = min(size.width, size.height)
        let orbCenter = CGPoint(x: size.width / 2, y: size.height / 2)
        let orbRadius = boundaryRadius(for: size)
        let bodyVelocity = physicsNode.physicsBody?.velocity ?? .zero
        let bodySpeed = hypot(bodyVelocity.dx, bodyVelocity.dy)
        let distanceFromVisual = hypot(
            physicsNode.position.x - pair.visualPosition.x,
            physicsNode.position.y - pair.visualPosition.y
        )
        let shouldAbsorbMicroJitter = bodySpeed < 12 && distanceFromVisual < 1.4

        if !shouldAbsorbMicroJitter {
            pair.visualPosition = CGPoint(
                x: pair.visualPosition.x + (physicsNode.position.x - pair.visualPosition.x) * 0.42,
                y: pair.visualPosition.y + (physicsNode.position.y - pair.visualPosition.y) * 0.42
            )
            pair.visualRotation += (physicsNode.zRotation - pair.visualRotation) * 0.38
        }

        let delta = CGVector(dx: pair.visualPosition.x - orbCenter.x, dy: pair.visualPosition.y - orbCenter.y)
        let distanceRatio = min(sqrt(delta.dx * delta.dx + delta.dy * delta.dy) / max(orbRadius, 0.0001), 1)
        let verticalRatio = max(-1, min((pair.visualPosition.y - orbCenter.y) / max(orbRadius, 0.0001), 1))
        let frontDepth = max(0, min(0.5 - verticalRatio * 0.5, 1))
        let edgeFalloff = max(0, min((distanceRatio - 0.62) / 0.38, 1))
        let visualScale = max(0.72, min(0.86 + frontDepth * 0.16 - edgeFalloff * 0.12, 1.06))
        let visualAlpha = max(0.45, min(0.58 + frontDepth * 0.34 - edgeFalloff * 0.20, 1))
        let darkness = max(0, min((1 - frontDepth) * 0.26 + edgeFalloff * 0.18, 0.38))

        spriteNode.position = pair.visualPosition
        spriteNode.zRotation = pair.visualRotation
        spriteNode.xScale = visualScale
        spriteNode.yScale = visualScale
        spriteNode.alpha = visualAlpha
        spriteNode.color = .black
        spriteNode.colorBlendFactor = darkness
        spriteNode.shader?.uniformNamed("u_frontDepth")?.floatValue = Float(frontDepth)
        spriteNode.shader?.uniformNamed("u_edgeFalloff")?.floatValue = Float(edgeFalloff)
        spriteNode.zPosition = frontDepth * 100 + max(0, size.height - pair.visualPosition.y) * 0.1
    }

    private func initialPosition(for index: Int, itemCount: Int, in size: CGSize) -> CGPoint {
        let diameter = min(size.width, size.height)
        let center = CGPoint(x: size.width / 2, y: size.height / 2)
        let layer = CGFloat(index / 4)
        let column = CGFloat(index % 4)
        let denominator = CGFloat(max(min(itemCount, 4) - 1, 1))
        let x = center.x
            + (column / denominator - 0.5) * diameter * 0.50
            + (layer.truncatingRemainder(dividingBy: 2) == 0 ? 0 : diameter * 0.05)
        let y = center.y
            - diameter * 0.22
            + layer * diameter * 0.12
            + (index.isMultiple(of: 2) ? diameter * 0.018 : -diameter * 0.012)
        return CGPoint(x: x, y: y)
    }

    private func initialRotation(for item: HomeOrbInteriorItem, index: Int) -> CGFloat {
        let seed = CGFloat((item.id % 9) - 4)
        return (seed * 8 + CGFloat(index) * 4) * .pi / 180
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
