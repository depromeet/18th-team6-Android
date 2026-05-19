import SpriteKit

final class HomeOrbInteriorPhysicsScene: SKScene {
    private var itemNodes: [Int: HomeOrbInteriorNodePair] = [:]
    private var configuredSize = CGSize.zero

    override init() {
        super.init(size: CGSize(width: HomeOrbMetrics.glassBallDiameter, height: HomeOrbMetrics.glassBallDiameter))
        scaleMode = .resizeFill
        backgroundColor = .clear
        physicsWorld.speed = HomeOrbPhysicsConfig.worldSpeed
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func didMove(to view: SKView) {
        view.allowsTransparency = true
        view.isOpaque = false
        view.backgroundColor = .clear
        view.clipsToBounds = true
    }

    override func didSimulatePhysics() {
        let radius = boundaryRadius(for: size)
        for pair in itemNodes.values {
            HomeOrbInteriorVisualProjector.applySphericalDepth(
                to: pair,
                sceneSize: size,
                orbRadius: radius
            )
        }
    }

    func configure(
        items: [HomeOrbInteriorItem],
        gradientMix: HomeOrbGradientMix,
        size: CGSize,
        drag: HomeOrbDragFrame
    ) {
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
            updateGradientUniforms(for: pair, gradientMix: gradientMix)

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
        let rollRadians = drag.gravityRollDegrees * .pi / 180
        let offsetGravityX = drag.contentOffset.width / HomeOrbPhysicsConfig.dragGravityOffsetDivisor * HomeOrbPhysicsConfig.dragGravityScale
        let rollGravityX = sin(rollRadians) * HomeOrbPhysicsConfig.rollGravityScale
        let tiltGravityX = drag.screenTilt.width * HomeOrbPhysicsConfig.tiltGravityXScale
        let tiltGravityY = drag.screenTilt.height * HomeOrbPhysicsConfig.tiltGravityYScale
        physicsWorld.gravity = CGVector(
            dx: offsetGravityX + rollGravityX + tiltGravityX,
            dy: HomeOrbPhysicsConfig.baselineGravityY + tiltGravityY
        )
    }

    private func makeNodePair(
        for item: HomeOrbInteriorItem,
        itemCount: Int,
        index: Int,
        size: CGSize
    ) -> HomeOrbInteriorNodePair {
        let initialPosition = HomeOrbInteriorSpawner.initialPosition(
            for: index,
            itemCount: itemCount,
            in: size,
            boundaryRadius: boundaryRadius(for: size),
            maximumPhysicsRadiusEstimate: HomeOrbInteriorSizing.maximumPhysicsRadiusEstimate(in: size)
        )
        let initialRotation = HomeOrbInteriorSpawner.initialRotation(for: item, index: index)
        return HomeOrbInteriorNodeFactory.makePair(
            for: item,
            position: initialPosition,
            rotation: initialRotation
        )
    }

    private func configure(
        pair: HomeOrbInteriorNodePair,
        for item: HomeOrbInteriorItem,
        itemCount: Int,
        index: Int,
        in size: CGSize
    ) {
        let targetWidth = HomeOrbInteriorSizing.targetWidth(for: item, in: size)
        let targetSize = HomeOrbInteriorSizing.targetSize(for: pair, targetWidth: targetWidth)
        let targetPhysicsRadius = HomeOrbInteriorSizing.physicsRadius(
            for: targetWidth,
            itemCount: itemCount,
            boundaryRadius: boundaryRadius(for: size)
        )

        guard
            pair.spriteNode.size != targetSize ||
                abs(pair.physicsRadius - targetPhysicsRadius) > 0.5 ||
                pair.physicsNode.physicsBody == nil
        else {
            return
        }

        HomeOrbInteriorNodeFactory.configureBody(
            for: pair,
            item: item,
            index: index,
            targetSize: targetSize,
            targetPhysicsRadius: targetPhysicsRadius
        )
    }

    private func updateGradientUniforms(for pair: HomeOrbInteriorNodePair, gradientMix: HomeOrbGradientMix) {
        pair.spriteNode.shader?.uniformNamed(HomeOrbShaderUniform.positiveShare)?.floatValue = gradientMix.positiveShare
        pair.spriteNode.shader?.uniformNamed(HomeOrbShaderUniform.transitionWidth)?.floatValue = gradientMix.transitionWidth
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
        physicsBody?.categoryBitMask = HomeOrbPhysicsConfig.boundaryCategory
        physicsBody?.collisionBitMask = HomeOrbPhysicsConfig.itemCategory
        physicsBody?.contactTestBitMask = 0
        physicsBody?.friction = HomeOrbPhysicsConfig.boundaryFriction
        physicsBody?.restitution = HomeOrbPhysicsConfig.boundaryRestitution
    }

    private func boundaryRadius(for size: CGSize) -> CGFloat {
        let diameter = min(size.width, size.height)
        return max(
            diameter * HomeOrbPhysicsConfig.minimumBoundaryRadiusRatio,
            protectedContentRadius(for: size) - HomeOrbInteriorSizing.maximumPhysicsRadiusEstimate(in: size)
        )
    }

    private func protectedContentRadius(for size: CGSize) -> CGFloat {
        let diameter = min(size.width, size.height)
        return diameter / 2 - diameter * HomeOrbPhysicsConfig.protectedEdgeRatio
    }
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

private enum HomeOrbInteriorNodeFactory {
    static func makePair(
        for item: HomeOrbInteriorItem,
        position: CGPoint,
        rotation: CGFloat
    ) -> HomeOrbInteriorNodePair {
        let physicsNode = SKNode()
        physicsNode.position = position
        physicsNode.zRotation = rotation

        let texture = SKTexture(imageNamed: item.assetName)
        let spriteNode = SKSpriteNode(texture: texture)
        spriteNode.name = item.assetName
        spriteNode.position = position
        spriteNode.zRotation = rotation
        spriteNode.blendMode = .alpha
        spriteNode.shader = HomeOrbShaderSource.makeDepthShader()
        spriteNode.color = .white
        spriteNode.colorBlendFactor = 0
        spriteNode.alpha = 1

        return HomeOrbInteriorNodePair(
            physicsNode: physicsNode,
            spriteNode: spriteNode
        )
    }

    static func configureBody(
        for pair: HomeOrbInteriorNodePair,
        item: HomeOrbInteriorItem,
        index: Int,
        targetSize: CGSize,
        targetPhysicsRadius: CGFloat
    ) {
        pair.spriteNode.size = targetSize
        pair.spriteNode.anchorPoint = HomeOrbVisualConfig.spriteAnchorPoint
        pair.physicsRadius = targetPhysicsRadius
        pair.physicsNode.physicsBody = SKPhysicsBody(circleOfRadius: targetPhysicsRadius)
        pair.physicsNode.physicsBody?.allowsRotation = true
        pair.physicsNode.physicsBody?.affectedByGravity = true
        pair.physicsNode.physicsBody?.categoryBitMask = HomeOrbPhysicsConfig.itemCategory
        pair.physicsNode.physicsBody?.collisionBitMask = HomeOrbPhysicsConfig.itemCategory | HomeOrbPhysicsConfig.boundaryCategory
        pair.physicsNode.physicsBody?.contactTestBitMask = 0
        pair.physicsNode.physicsBody?.mass =
            HomeOrbPhysicsConfig.massBase +
            item.weight * HomeOrbPhysicsConfig.massWeightScale +
            CGFloat(index) * HomeOrbPhysicsConfig.massIndexScale
        pair.physicsNode.physicsBody?.friction = HomeOrbPhysicsConfig.itemFriction
        pair.physicsNode.physicsBody?.restitution = HomeOrbPhysicsConfig.itemRestitution
        pair.physicsNode.physicsBody?.linearDamping = HomeOrbPhysicsConfig.itemLinearDamping
        pair.physicsNode.physicsBody?.angularDamping = HomeOrbPhysicsConfig.itemAngularDamping
    }
}

private enum HomeOrbInteriorSizing {
    static func targetWidth(for item: HomeOrbInteriorItem, in size: CGSize) -> CGFloat {
        let diameter = min(size.width, size.height)
        return diameter * (
            HomeOrbVisualConfig.itemTargetWidthBase +
                min(item.weight, HomeOrbVisualConfig.maximumWeightForSizing) * HomeOrbVisualConfig.itemTargetWidthWeightScale
        )
    }

    static func targetSize(for pair: HomeOrbInteriorNodePair, targetWidth: CGFloat) -> CGSize {
        let textureSize = pair.spriteNode.texture?.size() ?? CGSize(width: 1, height: 1)
        let aspectRatio = max(
            textureSize.height / max(textureSize.width, HomeOrbVisualConfig.minimumRatioTotal),
            HomeOrbVisualConfig.minimumTextureAspectRatio
        )
        return CGSize(width: targetWidth, height: targetWidth * aspectRatio)
    }

    static func maximumPhysicsRadiusEstimate(in size: CGSize) -> CGFloat {
        maximumVisualWidth(in: size) * HomeOrbPhysicsConfig.physicsRadiusVisualWidthRatio
    }

    static func physicsRadius(
        for visualWidth: CGFloat,
        itemCount: Int,
        boundaryRadius: CGFloat
    ) -> CGFloat {
        let stablePackingRadius = boundaryRadius / sqrt(
            CGFloat(max(itemCount, 1)) * HomeOrbPhysicsConfig.stablePackingMultiplier
        )
        return max(
            HomeOrbPhysicsConfig.physicsRadiusMinimum,
            min(visualWidth * HomeOrbPhysicsConfig.physicsRadiusVisualWidthRatio, stablePackingRadius)
        )
    }

    private static func maximumVisualWidth(in size: CGSize) -> CGFloat {
        let diameter = min(size.width, size.height)
        return diameter * (
            HomeOrbVisualConfig.itemTargetWidthBase +
                HomeOrbVisualConfig.maximumWeightForSizing * HomeOrbVisualConfig.itemTargetWidthWeightScale
        )
    }
}

private enum HomeOrbInteriorSpawner {
    static func initialPosition(
        for index: Int,
        itemCount: Int,
        in size: CGSize,
        boundaryRadius: CGFloat,
        maximumPhysicsRadiusEstimate: CGFloat
    ) -> CGPoint {
        let center = CGPoint(x: size.width / 2, y: size.height / 2)
        let spawnRadius = max(0, boundaryRadius - maximumPhysicsRadiusEstimate)
        let layer = CGFloat(index / 4)
        let column = CGFloat(index % 4)
        let denominator = CGFloat(max(min(itemCount, 4) - 1, 1))
        let x = center.x
            + (column / denominator - 0.5) * spawnRadius * HomeOrbVisualConfig.spawnColumnSpread
            + (layer.truncatingRemainder(dividingBy: 2) == 0 ? 0 : spawnRadius * HomeOrbVisualConfig.spawnOddLayerOffset)
        let y = center.y
            + HomeOrbVisualConfig.spawnBaseY * spawnRadius
            + layer * spawnRadius * HomeOrbVisualConfig.spawnLayerY
            + (index.isMultiple(of: 2) ? spawnRadius * HomeOrbVisualConfig.spawnEvenYOffset : spawnRadius * HomeOrbVisualConfig.spawnOddYOffset)
        return CGPoint(x: x, y: y)
    }

    static func initialRotation(for item: HomeOrbInteriorItem, index: Int) -> CGFloat {
        let seed = CGFloat((item.id % 9) - 4)
        return (seed * 8 + CGFloat(index) * 4) * .pi / 180
    }
}

private enum HomeOrbInteriorVisualProjector {
    static func applySphericalDepth(
        to pair: HomeOrbInteriorNodePair,
        sceneSize: CGSize,
        orbRadius: CGFloat
    ) {
        let physicsNode = pair.physicsNode
        let spriteNode = pair.spriteNode
        let orbCenter = CGPoint(x: sceneSize.width / 2, y: sceneSize.height / 2)
        let bodyVelocity = physicsNode.physicsBody?.velocity ?? .zero
        let bodySpeed = hypot(bodyVelocity.dx, bodyVelocity.dy)
        let distanceFromVisual = hypot(
            physicsNode.position.x - pair.visualPosition.x,
            physicsNode.position.y - pair.visualPosition.y
        )
        let shouldAbsorbMicroJitter =
            bodySpeed < HomeOrbVisualConfig.jitterSpeedThreshold &&
            distanceFromVisual < HomeOrbVisualConfig.jitterDistanceThreshold

        if !shouldAbsorbMicroJitter {
            pair.visualPosition = CGPoint(
                x: pair.visualPosition.x + (physicsNode.position.x - pair.visualPosition.x) * HomeOrbVisualConfig.visualPositionFollowRate,
                y: pair.visualPosition.y + (physicsNode.position.y - pair.visualPosition.y) * HomeOrbVisualConfig.visualPositionFollowRate
            )
            pair.visualRotation += (physicsNode.zRotation - pair.visualRotation) * HomeOrbVisualConfig.visualRotationFollowRate
        }

        let delta = CGVector(dx: pair.visualPosition.x - orbCenter.x, dy: pair.visualPosition.y - orbCenter.y)
        let safeOrbRadius = max(orbRadius, HomeOrbVisualConfig.minimumRatioTotal)
        let distanceRatio = min(sqrt(delta.dx * delta.dx + delta.dy * delta.dy) / safeOrbRadius, 1)
        let verticalRatio = max(-1, min((pair.visualPosition.y - orbCenter.y) / safeOrbRadius, 1))
        let frontDepth = max(0, min(HomeOrbVisualConfig.depthVerticalCenter - verticalRatio * HomeOrbVisualConfig.depthVerticalCenter, 1))
        let edgeFalloff = max(
            0,
            min((distanceRatio - HomeOrbVisualConfig.edgeFalloffStart) / HomeOrbVisualConfig.edgeFalloffRange, 1)
        )
        let visualScale = max(
            HomeOrbVisualConfig.visualScaleMinimum,
            min(
                HomeOrbVisualConfig.visualScaleBase +
                    frontDepth * HomeOrbVisualConfig.visualScaleDepthFactor -
                    edgeFalloff * HomeOrbVisualConfig.visualScaleEdgeFactor,
                HomeOrbVisualConfig.visualScaleMaximum
            )
        )
        let visualAlpha = max(
            HomeOrbVisualConfig.visualAlphaMinimum,
            min(
                HomeOrbVisualConfig.visualAlphaBase +
                    frontDepth * HomeOrbVisualConfig.visualAlphaDepthFactor -
                    edgeFalloff * HomeOrbVisualConfig.visualAlphaEdgeFactor,
                HomeOrbVisualConfig.visualAlphaMaximum
            )
        )
        let darkness = max(
            0,
            min(
                (1 - frontDepth) * HomeOrbVisualConfig.shadowDepthFactor +
                    edgeFalloff * HomeOrbVisualConfig.shadowEdgeFactor,
                HomeOrbVisualConfig.shadowMaximum
            )
        )

        spriteNode.position = pair.visualPosition
        spriteNode.zRotation = pair.visualRotation
        spriteNode.xScale = visualScale
        spriteNode.yScale = visualScale
        spriteNode.alpha = visualAlpha
        spriteNode.color = .white
        spriteNode.colorBlendFactor = 0
        spriteNode.shader?.uniformNamed(HomeOrbShaderUniform.frontDepth)?.floatValue = Float(frontDepth)
        spriteNode.shader?.uniformNamed(HomeOrbShaderUniform.edgeFalloff)?.floatValue = Float(edgeFalloff)
        spriteNode.shader?.uniformNamed(HomeOrbShaderUniform.orbProgressX)?.floatValue = Float(
            max(0, min(pair.visualPosition.x / max(sceneSize.width, HomeOrbVisualConfig.minimumRatioTotal), 1))
        )
        spriteNode.shader?.uniformNamed(HomeOrbShaderUniform.shadowAmount)?.floatValue = Float(darkness)
        spriteNode.zPosition =
            frontDepth * HomeOrbVisualConfig.zPositionDepthScale +
            max(0, sceneSize.height - pair.visualPosition.y) * HomeOrbVisualConfig.zPositionVerticalScale
    }
}
