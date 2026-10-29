import Foundation
import Shared

protocol NotificationPreferencesRepository: Sendable {
    func load() async throws -> NotificationSettings
    func update(_ settings: NotificationSettings) async throws -> NotificationSettings
}

actor SharedNotificationPreferencesRepository: NotificationPreferencesRepository {
    private let service: SharedNotificationSettingsService

    init(service: SharedNotificationSettingsService) {
        self.service = service
    }

    func load() async throws -> NotificationSettings {
        try settings(from: await service.getSettings())
    }

    func update(_ settings: NotificationSettings) async throws -> NotificationSettings {
        let preferences = Shared.NotificationSettings(
            isReplacementUpcomingEnabled: settings.replacementUpcoming,
            isReplacementOverdueEnabled: settings.replacementOverdue,
            isSpareShortageEnabled: settings.lowStock,
            sendTime: String(format: "%02d:%02d", settings.hour, settings.minute)
        )
        return try self.settings(from: await service.updateSettings(settings: preferences))
    }

    private func settings(from preferences: Shared.NotificationSettings) throws -> NotificationSettings {
        let parts = preferences.sendTime.split(separator: ":")
        guard parts.count >= 2,
              let hour = Int(parts[0]), (0 ... 23).contains(hour),
              let minute = Int(parts[1]), minute == 0 || minute == 30
        else { throw NotificationPreferencesError.invalidTime }
        return NotificationSettings(
            replacementUpcoming: preferences.isReplacementUpcomingEnabled,
            replacementOverdue: preferences.isReplacementOverdueEnabled,
            lowStock: preferences.isSpareShortageEnabled,
            hour: hour,
            minute: minute
        )
    }
}

private enum NotificationPreferencesError: Error {
    case invalidTime
}

actor PreviewNotificationPreferencesRepository: NotificationPreferencesRepository {
    private var settings = NotificationSettings()

    func load() async throws -> NotificationSettings { settings }

    func update(_ settings: NotificationSettings) async throws -> NotificationSettings {
        self.settings = settings
        return settings
    }
}
