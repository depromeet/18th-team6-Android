import SwiftUI

@main
struct iOSApp: App {
    @Environment(\.scenePhase) private var scenePhase
    @UIApplicationDelegateAdaptor(AppDelegate.self) private var appDelegate

    var body: some Scene {
        WindowGroup {
            AppBootstrapView()
                .onChange(of: scenePhase) { _, phase in
                    if phase == .background { StatusWidgetAppUpdater.shared.refreshInstalledWidgets() }
                }
        }
    }
}
