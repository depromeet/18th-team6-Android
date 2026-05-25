import Shared
import SwiftUI

@main
struct iOSApp: App {
    private let dependencies: AppDependencies

    init() {
        SharedKoinKt.startSharedKoin()
        dependencies = .live
    }

    var body: some Scene {
        WindowGroup {
            ContentView(dependencies: dependencies)
        }
    }
}
