import UIKit
import UserNotifications

@MainActor
protocol OnboardingNotificationSetup {
    func enableAndRequestPermission() async
}

struct LiveOnboardingNotificationSetup: OnboardingNotificationSetup {
    let repository: any NotificationPreferencesRepository

    func enableAndRequestPermission() async {
        var settings = (try? await repository.load()) ?? NotificationSettings()
        settings.replacementUpcoming = true
        settings.replacementOverdue = true
        settings.lowStock = true
        do { _ = try await repository.update(settings) }
        catch { AppLog.failure(AppLog.pushNotifications, "Enable onboarding notifications", error) }

        let center = UNUserNotificationCenter.current()
        let permission = await center.notificationSettings()
        if permission.authorizationStatus == .notDetermined {
            do { _ = try await center.requestAuthorization(options: [.alert, .badge, .sound]) }
            catch { AppLog.failure(AppLog.pushNotifications, "Request onboarding notification permission", error) }
        }
        let updated = await center.notificationSettings()
        if updated.authorizationStatus == .authorized || updated.authorizationStatus == .provisional {
            UIApplication.shared.registerForRemoteNotifications()
        }
    }
}
