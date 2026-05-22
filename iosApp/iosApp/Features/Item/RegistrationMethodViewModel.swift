import Foundation

@MainActor
final class RegistrationMethodViewModel: ObservableObject {
    @Published private(set) var state: RegistrationMethodViewState

    init(options: [RegistrationMethodOption] = RegistrationMethodSampleData.options) {
        self.state = .success(options)
    }
}
