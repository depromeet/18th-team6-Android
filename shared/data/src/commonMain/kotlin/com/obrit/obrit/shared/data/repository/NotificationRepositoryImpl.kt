package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.notifications.Notification
import com.obrit.obrit.shared.model.notifications.NotificationSettings
import com.obrit.obrit.shared.network.error.runCatchingWith
import com.obrit.obrit.shared.network.request.notification.RegisterDeviceRequest
import com.obrit.obrit.shared.network.request.notification.toUpdateRequest
import com.obrit.obrit.shared.network.response.notification.toNotificationSettings
import com.obrit.obrit.shared.network.response.notification.toNotifications
import com.obrit.obrit.shared.network.source.NotificationRemoteDataSource

internal class NotificationRepositoryImpl(
    private val notificationRemoteDataSource: NotificationRemoteDataSource,
) : NotificationRepository {
    override suspend fun registerDevice(fid: String): Result<Unit> =
        runCatchingWith {
            notificationRemoteDataSource.registerDevice(RegisterDeviceRequest(fid = fid))
        }

    override suspend fun getNotifications(): Result<List<Notification>> =
        runCatchingWith {
            notificationRemoteDataSource.getNotifications().flatMap { response -> response.toNotifications() }
        }

    override suspend fun markAsRead(notificationId: Long): Result<String> =
        runCatchingWith {
            notificationRemoteDataSource.markAsRead(notificationId).deepLink
        }

    override suspend fun markAllAsRead(): Result<Unit> =
        runCatchingWith {
            notificationRemoteDataSource.markAllAsRead()
        }

    override suspend fun getSettings(): Result<NotificationSettings> =
        runCatchingWith {
            notificationRemoteDataSource.getSettings().toNotificationSettings()
        }

    override suspend fun updateSettings(settings: NotificationSettings): Result<NotificationSettings> =
        runCatchingWith {
            notificationRemoteDataSource.updateSettings(settings.toUpdateRequest()).toNotificationSettings()
        }
}
