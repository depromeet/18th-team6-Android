import SwiftUI

struct AppBootstrapView: View {
    @StateObject private var bootstrapper = AppBootstrapper()

    var body: some View {
        Group {
            if let dependencies = bootstrapper.dependencies {
                ContentView(dependencies: dependencies)
            } else {
                AppLaunchLoadingView()
            }
        }
        .task {
            await bootstrapper.bootstrap()
        }
    }
}
