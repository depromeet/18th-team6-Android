import Foundation
import SpriteKit
import UIKit

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
            // SpriteKit 물리 위치를 그대로 쓰면 미세 떨림이 노출되므로, 표시용 sprite에 구면 깊이와 damping을 따로 적용한다.
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

        // SwiftUI layout 크기가 바뀔 때만 Scene size와 원형 boundary를 다시 만든다.
        if configuredSize != size {
            configuredSize = size
            self.size = size
            updateBoundary(for: size)
        }

        let visibleIds = Set(items.map(\.id))
        let staleIds = itemNodes.keys.filter { !visibleIds.contains($0) }
        for id in staleIds {
            // 입력에서 사라진 item은 physics node와 sprite node를 함께 제거해야 z-order와 충돌체가 남지 않는다.
            itemNodes[id]?.remoteImageTask?.cancel()
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
        // 드래그 오프셋, release roll, 기기 기울기를 하나의 SpriteKit gravity vector로 합성한다.
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
        // 최초 배치는 겹침을 줄이기 위해 원 내부에 column/layer 기반으로 흩뿌린다.
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
            boundaryRadius: boundaryRadius(for: size),
            in: size
        )
        pair.targetMaxSide = targetWidth
        HomeOrbInteriorNodeFactory.configureTexture(for: pair, imageURL: item.imageURL)

        // 크기와 충돌 반경이 그대로면 body를 다시 만들지 않아 현재 속도와 회전을 보존한다.
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
        // 상태 비율은 shader uniform으로만 갱신해서 texture/node 재생성을 피한다.
        pair.spriteNode.shader?.uniformNamed(HomeOrbShaderUniform.positiveShare)?.floatValue = gradientMix.positiveShare
        pair.spriteNode.shader?.uniformNamed(HomeOrbShaderUniform.transitionWidth)?.floatValue = gradientMix.transitionWidth
    }

    private func updateBoundary(for size: CGSize) {
        // 유리구 가장자리와 item 반경을 보호하기 위해 실제 충돌 boundary는 보이는 원보다 살짝 안쪽에 둔다.
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
        // item이 edge에 붙어 잘리지 않도록 최대 예상 표시 여백만큼 boundary를 안으로 당긴다.
        let diameter = min(size.width, size.height)
        return max(
            diameter * HomeOrbPhysicsConfig.minimumBoundaryRadiusRatio,
            protectedContentRadius(for: size) - HomeOrbInteriorSizing.maximumVisualBoundaryInsetEstimate(in: size)
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
    var imageURL: String
    var loadedImageURL: String?
    var remoteImageTask: Task<Void, Never>?
    var physicsRadius: CGFloat = 0
    var targetMaxSide: CGFloat = 0
    var visualPosition: CGPoint
    var visualRotation: CGFloat

    init(physicsNode: SKNode, spriteNode: SKSpriteNode, imageURL: String) {
        // 물리 node는 충돌만 담당하고, sprite node는 shader/depth/damping 표시만 담당한다.
        self.physicsNode = physicsNode
        self.spriteNode = spriteNode
        self.imageURL = imageURL
        self.visualPosition = physicsNode.position
        self.visualRotation = physicsNode.zRotation
    }

    deinit {
        remoteImageTask?.cancel()
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

        let spriteNode = SKSpriteNode(texture: placeholderTexture())
        spriteNode.name = item.imageURL
        spriteNode.position = position
        spriteNode.zRotation = rotation
        spriteNode.blendMode = .alpha
        spriteNode.shader = HomeOrbShaderSource.makeDepthShader()
        spriteNode.color = .white
        spriteNode.colorBlendFactor = 0
        spriteNode.alpha = 1

        return HomeOrbInteriorNodePair(
            physicsNode: physicsNode,
            spriteNode: spriteNode,
            imageURL: item.imageURL
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

    static func configureTexture(for pair: HomeOrbInteriorNodePair, imageURL: String) {
        guard pair.loadedImageURL != imageURL else { return }

        pair.imageURL = imageURL
        pair.loadedImageURL = nil
        pair.spriteNode.name = imageURL
        pair.spriteNode.texture = placeholderTexture()
        pair.remoteImageTask?.cancel()
        guard !imageURL.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else { return }

        pair.remoteImageTask = Task { [weak pair] in
            guard let data = await HomeOrbRemoteImageDataCache.shared.data(for: imageURL) else { return }

            await MainActor.run {
                guard
                    let pair,
                    pair.imageURL == imageURL,
                    let image = UIImage(data: data)
                else {
                    return
                }

                pair.spriteNode.texture = SKTexture(image: image)
                pair.spriteNode.size = HomeOrbInteriorSizing.targetSize(
                    imageSize: image.size,
                    targetWidth: pair.targetMaxSide
                )
                pair.loadedImageURL = imageURL
            }
        }
    }

    private static func placeholderTexture() -> SKTexture {
        SKTexture(image: UIImage())
    }
}

private actor HomeOrbRemoteImageDataCache {
    static let shared = HomeOrbRemoteImageDataCache()

    private var cache: [String: Data] = [:]

    func data(for urlString: String) async -> Data? {
        let trimmedURLString = urlString.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmedURLString.isEmpty else { return nil }

        if let cachedData = cache[trimmedURLString] {
            return cachedData
        }

        guard let url = URL(string: trimmedURLString) else { return nil }

        do {
            let (data, _) = try await URLSession.shared.data(from: url)
            cache[trimmedURLString] = data
            return data
        } catch {
            return nil
        }
    }
}

private enum HomeOrbInteriorSizing {
    static func targetWidth(for item: HomeOrbInteriorItem, in size: CGSize) -> CGFloat {
        // item weight는 asset의 실제 픽셀 크기가 아니라 유리구 안에서의 시각적 비중을 의미한다.
        let diameter = min(size.width, size.height)
        return diameter * (
            HomeOrbVisualConfig.itemTargetWidthBase +
                min(item.weight, HomeOrbVisualConfig.maximumWeightForSizing) * HomeOrbVisualConfig.itemTargetWidthWeightScale
        )
    }

    static func targetSize(for pair: HomeOrbInteriorNodePair, targetWidth: CGFloat) -> CGSize {
        let textureSize = pair.spriteNode.texture?.size() ?? CGSize(width: 1, height: 1)
        return targetSize(imageSize: textureSize, targetWidth: targetWidth)
    }

    static func targetSize(imageSize: CGSize, targetWidth: CGFloat) -> CGSize {
        let aspectRatio = max(
            imageSize.height / max(imageSize.width, HomeOrbVisualConfig.minimumRatioTotal),
            HomeOrbVisualConfig.minimumTextureAspectRatio
        )

        if aspectRatio > 1 {
            return CGSize(width: targetWidth / aspectRatio, height: targetWidth)
        }

        return CGSize(width: targetWidth, height: targetWidth * aspectRatio)
    }

    static func maximumPhysicsRadiusEstimate(in size: CGSize) -> CGFloat {
        maximumVisualWidth(in: size) * HomeOrbPhysicsConfig.physicsRadiusVisualWidthRatio
    }

    static func maximumVisualBoundaryInsetEstimate(in size: CGSize) -> CGFloat {
        maximumVisualWidth(in: size) *
            HomeOrbVisualConfig.visualScaleMaximum *
            HomeOrbPhysicsConfig.visualBoundaryInsetRatio
    }

    static func physicsRadius(
        for visualWidth: CGFloat,
        itemCount: Int,
        boundaryRadius: CGFloat,
        in size: CGSize
    ) -> CGFloat {
        // 물리 반경은 시각 크기보다 작게 잡고, item 수가 많을수록 안정적으로 쌓이도록 상한을 낮춘다.
        let diameter = min(size.width, size.height)
        let minimumPhysicsRadius = diameter * HomeOrbPhysicsConfig.physicsRadiusMinimumRatio
        let stablePackingRadius = boundaryRadius / sqrt(
            CGFloat(max(itemCount, 1)) * HomeOrbPhysicsConfig.stablePackingMultiplier
        )
        return max(
            minimumPhysicsRadius,
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
        // 초기 위치는 중앙 하단부에 가까운 격자형 seed를 써서 동일 데이터가 매번 같은 배치를 갖게 한다.
        let center = CGPoint(x: size.width / 2, y: size.height / 2)
        let spawnRadius = max(0, boundaryRadius - maximumPhysicsRadiusEstimate)
        let columnCount = max(1, Int(ceil(sqrt(Double(max(itemCount, 1))))))
        let layer = CGFloat(index / columnCount)
        let column = CGFloat(index % columnCount)
        let denominator = CGFloat(max(columnCount - 1, 1))
        let normalizedColumn = columnCount == 1 ? 0.5 : column / denominator
        let x = center.x
            + (normalizedColumn - 0.5) * spawnRadius * HomeOrbVisualConfig.spawnColumnSpread
            + (layer.truncatingRemainder(dividingBy: 2) == 0 ? 0 : spawnRadius * HomeOrbVisualConfig.spawnOddLayerOffset)
        let y = center.y
            + HomeOrbVisualConfig.spawnBaseY * spawnRadius
            + layer * spawnRadius * HomeOrbVisualConfig.spawnLayerY
            + (index.isMultiple(of: 2) ? spawnRadius * HomeOrbVisualConfig.spawnEvenYOffset : spawnRadius * HomeOrbVisualConfig.spawnOddYOffset)
        return CGPoint(x: x, y: y)
    }

    static func initialRotation(for item: HomeOrbInteriorItem, index: Int) -> CGFloat {
        // id와 index 기반의 deterministic seed로 랜덤 없이 자연스러운 초기 각도를 만든다.
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
        let sceneDiameter = min(sceneSize.width, sceneSize.height)
        let shouldAbsorbMicroJitter =
            bodySpeed < sceneDiameter * HomeOrbVisualConfig.jitterSpeedThresholdRatio &&
            distanceFromVisual < sceneDiameter * HomeOrbVisualConfig.jitterDistanceThresholdRatio

        // 거의 멈춘 상태의 작은 물리 흔들림은 시각 위치에 반영하지 않아 정지 상태를 안정적으로 보이게 한다.
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
        // 유리구의 앞쪽/가장자리 위치를 추정해 scale, alpha, shader shadow, zPosition을 함께 조정한다.
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
