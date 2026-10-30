import Foundation

protocol NotificationInboxRepository: Sendable {
    func load() async throws -> [NotificationInboxCard]
    func markRead(notificationId: Int64) async throws -> String
    func markAllRead() async throws
    func validateItem(id: Int) async throws
}

