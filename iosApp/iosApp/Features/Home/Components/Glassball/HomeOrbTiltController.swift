import CoreMotion
import SwiftUI

final class HomeOrbTiltController: ObservableObject {
    @Published private(set) var tilt = CGSize.zero

    private let manager = CMMotionManager()
    private let updateInterval = 1.0 / 30.0
    private let tiltLimit = 0.42
    private var isRunning = false

    deinit {
        stop()
    }

    func start() {
        guard !isRunning, manager.isDeviceMotionAvailable else { return }

        isRunning = true
        manager.deviceMotionUpdateInterval = updateInterval
        manager.startDeviceMotionUpdates(to: .main) { [weak self] motion, _ in
            guard let self, let motion else { return }

            let nextTilt = CGSize(
                width: self.normalizedTilt(from: motion.gravity.x),
                height: self.normalizedTilt(from: motion.gravity.y)
            )

            self.tilt = CGSize(
                width: self.tilt.width * 0.84 + nextTilt.width * 0.16,
                height: self.tilt.height * 0.84 + nextTilt.height * 0.16
            )
        }
    }

    func stop() {
        guard isRunning else { return }

        isRunning = false
        manager.stopDeviceMotionUpdates()
        tilt = .zero
    }

    private func normalizedTilt(from value: Double) -> CGFloat {
        CGFloat(max(-tiltLimit, min(tiltLimit, value)) / tiltLimit)
    }
}
