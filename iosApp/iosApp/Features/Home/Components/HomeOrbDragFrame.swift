import SwiftUI

struct HomeOrbDragFrame: Equatable {
    let translation: CGSize
    let settledRollDegrees: CGFloat
    let inertialRollDegrees: CGFloat

    var contentOffset: CGSize {
        CGSize(width: translation.width * 0.06, height: translation.height * 0.05)
    }

    var contentRotationDegrees: CGFloat {
        settledRollDegrees + inertialRollDegrees + Self.rollDelta(for: translation)
    }

    var itemRotationDegrees: CGFloat {
        contentRotationDegrees * 0.64
    }

    var pitchDegrees: CGFloat {
        -translation.height * 0.045
    }

    var yawDegrees: CGFloat {
        translation.width * 0.045
    }

    static func rollDelta(for translation: CGSize) -> CGFloat {
        translation.width * 0.85 - translation.height * 0.35
    }

    static func inertialRollDelta(from translation: CGSize, predicted: CGSize) -> CGFloat {
        let velocityTranslation = CGSize(
            width: predicted.width - translation.width,
            height: predicted.height - translation.height
        )
        return rollDelta(for: velocityTranslation) * 1.65
    }

    static func clamped(_ translation: CGSize) -> CGSize {
        let maxDistance: CGFloat = 56
        let distance = hypot(translation.width, translation.height)
        guard distance > maxDistance else { return translation }

        let scale = maxDistance / max(distance, 0.0001)
        return CGSize(width: translation.width * scale, height: translation.height * scale)
    }
}
