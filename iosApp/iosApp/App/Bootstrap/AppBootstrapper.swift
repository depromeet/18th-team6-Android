import Foundation
import Shared

@MainActor
final class AppBootstrapper: ObservableObject {
    @Published private(set) var dependencies: AppDependencies?

    private var didStartBootstrap = false

    func bootstrap() async {
        guard !didStartBootstrap else { return }
        didStartBootstrap = true

        let startedAt = Date()
        AppLog.enter(AppLog.appBootstrap, "AppBootstrapper.bootstrap")

        await Task.yield()

        SharedKoinKt.startSharedKoin()
        dependencies = .live

        let elapsedMilliseconds = Int(Date().timeIntervalSince(startedAt) * 1_000)
        AppLog.success(AppLog.appBootstrap, "AppBootstrapper.bootstrap", "elapsedMs=\(elapsedMilliseconds)")
    }
}
