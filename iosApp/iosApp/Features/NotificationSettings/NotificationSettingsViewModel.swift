import Combine
import Foundation
import UserNotifications

struct NotificationSettings: Equatable {
    var replacementUpcoming = false
    var replacementOverdue = false
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

enum NotificationSettingsViewState {
    case loading
    case loadFailed
    case success(NotificationSettings)
}

@MainActor
final class NotificationSettingsViewModel: ObservableObject {
    @Published private(set) var state: NotificationSettingsViewState = .loading
    @Published var saveError: String?
    @Published private(set) var authorizationStatus: UNAuthorizationStatus?
    @Published var permissionError: String?
    @Published private(set) var isRequestingPermission = false

    private let repository: any NotificationPreferencesRepository
    private var savedSettings: NotificationSettings?
    private var pendingSettings: NotificationSettings?
    private var saveTask: Task<Void, Never>?
    private let notificationCenter: UNUserNotificationCenter

    init(
        repository: any NotificationPreferencesRepository = PreviewNotificationPreferencesRepository(),
        notificationCenter: UNUserNotificationCenter = .current()
    ) {
        self.repository = repository
        self.notificationCenter = notificationCenter
    }

    func loadIfNeeded() async {
        guard case .loading = state else { return }
        do {
            let settings = try await repository.load()
            savedSettings = settings
            state = .success(settings)
        } catch {
            if Task.isCancelled { return }
            AppLog.failure(AppLog.notificationSettingsViewModel, "Load notification settings", error)
            state = .loadFailed
        }
    }

    func retry() {
        state = .loading
        Task { await loadIfNeeded() }
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
        settings.minute = (components.minute ?? 0) < 30 ? 0 : 30
        save(settings)
    }

    private func save(_ settings: NotificationSettings) {
        state = .success(settings)
        pendingSettings = settings
        guard saveTask == nil else { return }
        saveTask = Task {
            defer { saveTask = nil }
            while let next = pendingSettings {
                pendingSettings = nil
                do {
                    let saved = try await repository.update(next)
                    savedSettings = saved
                    if pendingSettings == nil { state = .success(saved) }
                } catch {
                    AppLog.failure(AppLog.notificationSettingsViewModel, "Save notification settings", error)
                    if pendingSettings == nil {
                        if let savedSettings { state = .success(savedSettings) }
                        saveError = "알림 설정을 저장하지 못했어요. 다시 시도해 주세요."
                    }
                }
            }
        }
    }
}
