import Foundation
import Shared
import UserNotifications

protocol NotificationPreferencesRepository: Sendable {
    func load() async throws -> NotificationSettings
    func update(_ settings: NotificationSettings) async throws -> NotificationSettings
}

actor SharedNotificationPreferencesRepository: NotificationPreferencesRepository {
    private let service: SharedNotificationSettingsService
    private let notificationCenter: UNUserNotificationCenter

    init(service: SharedNotificationSettingsService, notificationCenter: UNUserNotificationCenter = .current()) {
        self.service = service
        self.notificationCenter = notificationCenter
    }

    func load() async throws -> NotificationSettings {
        try settings(from: await service.getSettings())
    }

    func update(_ settings: NotificationSettings) async throws -> NotificationSettings {
        let authorizationStatus = await notificationCenter.notificationSettings().authorizationStatus
        let permissionStatus: NotificationPermissionStatus
        switch authorizationStatus {
        case .notDetermined:
            permissionStatus = .notRequested
        case .denied:
            permissionStatus = .denied
        case .authorized, .provisional, .ephemeral:
            permissionStatus = .granted
        @unknown default:
            throw NotificationPreferencesError.unknownPermissionStatus
        }
        let preferences = NotificationPreferences(
            preReplacementEnabled: settings.replacementUpcoming,
            overdueEnabled: settings.replacementOverdue,
            lowStockEnabled: settings.lowStock,
            dispatchTime: String(format: "%02d:%02d", settings.hour, settings.minute),
            permissionStatus: permissionStatus
        )
        return try self.settings(from: await service.updateSettings(settings: preferences))
    }

    private func settings(from preferences: NotificationPreferences) throws -> NotificationSettings {
        let parts = preferences.dispatchTime.split(separator: ":")
        guard parts.count >= 2,
              let hour = Int(parts[0]), (0 ... 23).contains(hour),
              let minute = Int(parts[1]), minute == 0 || minute == 30
        else { throw NotificationPreferencesError.invalidTime }
        return NotificationSettings(
            replacementUpcoming: preferences.preReplacementEnabled,
            replacementOverdue: preferences.overdueEnabled,
            lowStock: preferences.lowStockEnabled,
            hour: hour,
            minute: minute
        )
    }
}

private enum NotificationPreferencesError: Error {
    case invalidTime
    case unknownPermissionStatus
}

actor PreviewNotificationPreferencesRepository: NotificationPreferencesRepository {
    private var settings = NotificationSettings()

    func load() async throws -> NotificationSettings { settings }

    func update(_ settings: NotificationSettings) async throws -> NotificationSettings {
        self.settings = settings
        return settings
    }
}
