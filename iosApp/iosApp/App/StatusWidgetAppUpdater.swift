import UIKit
import WidgetKit

@MainActor
final class StatusWidgetAppUpdater {
    static let shared = StatusWidgetAppUpdater()
    private var refreshTask: Task<Void, Never>?
    private var backgroundTask = UIBackgroundTaskIdentifier.invalid

    func refreshInstalledWidgets() {
        guard refreshTask == nil else { return }
        backgroundTask = UIApplication.shared.beginBackgroundTask(withName: "Refresh status widget") { [weak self] in
            Task { @MainActor in
                self?.refreshTask?.cancel()
                self?.endBackgroundTask()
            }
        }
        refreshTask = Task {
            defer {
                endBackgroundTask()
                refreshTask = nil
            }
            guard let configurations = await currentConfigurations(),
                  configurations.contains(where: { $0.kind == "OBRitStatusWidget" }), !Task.isCancelled else { return }
            _ = await StatusWidgetDataSource.shared.refresh(force: true)
            if !Task.isCancelled { WidgetCenter.shared.reloadTimelines(ofKind: "OBRitStatusWidget") }
        }
    }

    private func endBackgroundTask() {
        guard backgroundTask != .invalid else { return }
        UIApplication.shared.endBackgroundTask(backgroundTask)
        backgroundTask = .invalid
    }

    private func currentConfigurations() async -> [WidgetInfo]? {
        await withCheckedContinuation { continuation in
            WidgetCenter.shared.getCurrentConfigurations { result in
                continuation.resume(returning: try? result.get())
            }
        }
    }
}
