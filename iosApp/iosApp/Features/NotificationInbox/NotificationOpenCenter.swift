import Combine
import Foundation

struct NotificationOpenRequest {
    let notificationId: Int64?
    let deepLink: String?
}

@MainActor
final class NotificationOpenCenter: ObservableObject {
    static let shared = NotificationOpenCenter()
    @Published private(set) var pending: [NotificationOpenRequest] = []

    func enqueue(userInfo: [AnyHashable: Any]) {
        let id: Int64?
        if let value = userInfo["notificationId"] as? String { id = Int64(value) }
        else if let value = userInfo["notificationId"] as? NSNumber { id = value.int64Value }
        else { id = nil }
        pending.append(NotificationOpenRequest(notificationId: id, deepLink: userInfo["deepLink"] as? String))
    }

    func takeFirst() -> NotificationOpenRequest? {
        guard !pending.isEmpty else { return nil }
        return pending.removeFirst()
    }
}

extension Notification.Name {
    static let notificationInboxDidReceive = Notification.Name("obrit.notificationInboxDidReceive")
}
