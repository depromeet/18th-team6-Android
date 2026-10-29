import FirebaseCore
import FirebaseMessaging
import Shared
import UIKit
import UserNotifications

final class AppDelegate: NSObject, UIApplicationDelegate {
    private lazy var writeService = SharedWriteService(repositoryProvider: SharedRepositoryProvider())

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        FirebaseApp.configure()
        UNUserNotificationCenter.current().delegate = self
        Messaging.messaging().delegate = self
        application.registerForRemoteNotifications()
        return true
    }

    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
        Messaging.messaging().apnsToken = deviceToken
        AppLog.success(AppLog.pushNotifications, "APNs registration")
    }

    func application(
        _ application: UIApplication,
        didFailToRegisterForRemoteNotificationsWithError error: Error
    ) {
        AppLog.failure(AppLog.pushNotifications, "APNs registration", error)
    }
}

extension AppDelegate: MessagingDelegate {
    nonisolated func messaging(_ messaging: Messaging, didReceiveRegistration installationId: String?) {
        guard let installationId, !installationId.isEmpty else { return }
        AppLog.success(AppLog.pushNotifications, "FCM FID registered")
        #if DEBUG
            print("[OBRit][Push] FID: \(installationId)")
        #endif
        Task { @MainActor in
            do {
                try await writeService.registerNotificationDevice(fid: installationId)
                AppLog.success(AppLog.pushNotifications, "Notification device registered")
            } catch {
                AppLog.failure(AppLog.pushNotifications, "Notification device registration", error)
            }
        }
    }
}

extension AppDelegate: UNUserNotificationCenterDelegate {
    nonisolated func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        Task { @MainActor in
            Messaging.messaging().appDidReceiveMessage(notification.request.content.userInfo)
            AppLog.success(AppLog.pushNotifications, "Foreground notification received")
            completionHandler([.banner, .list, .sound, .badge])
        }
    }

    nonisolated func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        Task { @MainActor in
            Messaging.messaging().appDidReceiveMessage(response.notification.request.content.userInfo)
            AppLog.success(AppLog.pushNotifications, "Notification opened")
            completionHandler()
        }
    }
}
