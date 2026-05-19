import SwiftUI

struct HomeOrbDragFrame: Equatable {
    let translation: CGSize
    let settledRollDegrees: CGFloat
    let inertialRollDegrees: CGFloat
    let rollImpulseDegrees: CGFloat
    let screenTilt: CGSize

    var contentOffset: CGSize {
        CGSize(width: translation.width * 0.16, height: translation.height * 0.12)
    }

    var contentRotationDegrees: CGFloat {
        settledRollDegrees + inertialRollDegrees + Self.rollDelta(for: translation)
    }

    var gravityRollDegrees: CGFloat {
        Self.rollDelta(for: translation) + rollImpulseDegrees
    }

    var itemRotationDegrees: CGFloat {
        contentRotationDegrees * 0.64
    }

    var pitchDegrees: CGFloat {
        -translation.height * 0.032 - screenTilt.height * 2.6
    }

    var yawDegrees: CGFloat {
        translation.width * 0.032 + screenTilt.width * 2.6
    }

    static func rollDelta(for translation: CGSize) -> CGFloat {
        translation.width * 1.02 - translation.height * 0.42
    }

    static func inertialRollDelta(from translation: CGSize, predicted: CGSize) -> CGFloat {
        let velocityTranslation = CGSize(
            width: predicted.width - translation.width,
            height: predicted.height - translation.height
        )
        return rollDelta(for: velocityTranslation) * 1.88
    }

    static func clamped(_ translation: CGSize) -> CGSize {
        let maxDistance: CGFloat = 92
        let distance = hypot(translation.width, translation.height)
        guard distance > maxDistance else { return translation }

        let scale = maxDistance / max(distance, 0.0001)
        return CGSize(width: translation.width * scale, height: translation.height * scale)
    }
}
