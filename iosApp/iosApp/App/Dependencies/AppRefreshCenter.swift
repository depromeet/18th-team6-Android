import Foundation

final class AppRefreshCenter: ObservableObject {
    @Published private(set) var itemRefreshToken = UUID()

    @Published private(set) var registrationSuccess: RegistrationSuccessMessage?

    func showRegistrationSuccess(_ message: String) {
        registrationSuccess = RegistrationSuccessMessage(message: message)
    }

    func dismissRegistrationSuccess(id: UUID) {
        if registrationSuccess?.id == id { registrationSuccess = nil }
    }

    func refreshItems() {
        itemRefreshToken = UUID()
    }
}

struct RegistrationSuccessMessage: Identifiable {
    let id = UUID()
    let message: String
}
