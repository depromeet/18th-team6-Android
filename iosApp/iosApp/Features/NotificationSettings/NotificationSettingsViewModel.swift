import Combine
import Foundation
import UserNotifications

struct NotificationSettings: Codable, Equatable {
    var replacementUpcoming = true
    var replacementOverdue = true
    var lowStock = false
    var hour = 9
    var minute = 0

    var timeLabel: String {
        let period = hour < 12 ? "오전" : "오후"
        let displayHour = hour % 12 == 0 ? 12 : hour % 12
        return String(format: "%@ %02d : %02d", period, displayHour, minute)
    }

    var date: Date {
        Calendar.current.date(from: DateComponents(hour: hour, minute: minute)) ?? Date()
    }
}

struct NotificationSettingsStore {
    let defaults: UserDefaults
    private let key = "notificationSettings.preferences"

    func load() -> NotificationSettings {
        guard let data = defaults.data(forKey: key),
              let settings = try? JSONDecoder().decode(NotificationSettings.self, from: data),
              (0 ... 23).contains(settings.hour),
              (0 ... 59).contains(settings.minute)
        else { return NotificationSettings() }
        return settings
    }

    func save(_ settings: NotificationSettings) {
        guard let data = try? JSONEncoder().encode(settings) else { return }
        defaults.set(data, forKey: key)
    }
}

enum NotificationSettingsViewState {
    case success(NotificationSettings)
}

@MainActor
final class NotificationSettingsViewModel: ObservableObject {
    @Published private(set) var state: NotificationSettingsViewState
    @Published private(set) var authorizationStatus: UNAuthorizationStatus?
    @Published var permissionError: String?
    @Published private(set) var isRequestingPermission = false

    private let store: NotificationSettingsStore
    private let notificationCenter: UNUserNotificationCenter

    init(
        store: NotificationSettingsStore,
        notificationCenter: UNUserNotificationCenter = .current()
    ) {
        self.store = store
        self.notificationCenter = notificationCenter
        state = .success(store.load())
    }

    func refreshPermission() async {
        authorizationStatus = await notificationCenter.notificationSettings().authorizationStatus
    }

    func requestPermission() async {
        guard !isRequestingPermission else { return }
        isRequestingPermission = true
        defer { isRequestingPermission = false }
        do {
            _ = try await notificationCenter.requestAuthorization(options: [.alert, .sound, .badge])
        } catch {
            permissionError = "알림 권한을 요청하지 못했어요. 잠시 후 다시 시도해 주세요."
        }
        await refreshPermission()
    }

    func setToggle(_ keyPath: WritableKeyPath<NotificationSettings, Bool>, enabled: Bool) {
        guard case var .success(settings) = state else { return }
        settings[keyPath: keyPath] = enabled
        save(settings)
    }

    func setTime(_ date: Date) {
        guard case var .success(settings) = state else { return }
        let components = Calendar.current.dateComponents([.hour, .minute], from: date)
        settings.hour = components.hour ?? 9
        settings.minute = components.minute ?? 0
        save(settings)
    }

    private func save(_ settings: NotificationSettings) {
        store.save(settings)
        state = .success(settings)
    }
}
