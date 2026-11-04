import Foundation
import Shared

actor SharedNotificationInboxRepository: NotificationInboxRepository {
    private let service: SharedNotificationInboxService
    private let readService: SharedReadService

    init(service: SharedNotificationInboxService, readService: SharedReadService) {
        self.service = service
        self.readService = readService
    }

    func load() async throws -> [NotificationInboxCard] {
        try await service.getNotifications().map {
            NotificationInboxCard(
                notificationId: $0.notificationId,
                entryId: $0.entryId?.int64Value,
                title: $0.heading ?? $0.title,
                content: $0.content,
                itemName: $0.itemName,
                label: $0.label,
                nextReplacementDate: $0.nextReplacementDate,
                hasLowStock: $0.hasLowStock,
                deepLink: $0.deepLink,
                createdAt: $0.createdAt,
                isRead: $0.isRead
            )
        }
    }

    func markRead(notificationId: Int64) async throws -> String {
        try await service.markAsRead(notificationId: notificationId)
    }

    func markAllRead() async throws {
        try await service.markAllAsRead()
    }

    func validateItem(id: Int) async throws {
        _ = try await readService.getItem(itemId: Int64(id))
    }
}

actor PreviewNotificationInboxRepository: NotificationInboxRepository {
    func load() async throws -> [NotificationInboxCard] { [] }
    func markRead(notificationId: Int64) async throws -> String { "obrit://home" }
    func markAllRead() async throws {}
    func validateItem(id: Int) async throws {}
}
