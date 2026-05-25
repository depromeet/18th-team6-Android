import Foundation

final class AppRefreshCenter: ObservableObject {
    @Published private(set) var itemRefreshToken = UUID()

    func refreshItems() {
        itemRefreshToken = UUID()
    }
}
