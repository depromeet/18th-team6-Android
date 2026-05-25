import SwiftUI

struct ContentView: View {
    let dependencies: AppDependencies

    init(dependencies: AppDependencies = .preview) {
        self.dependencies = dependencies
    }

    var body: some View {
        OBRitNavigation(dependencies: dependencies)
            .dismissKeyboardOnBackgroundTap()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
