package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.request.notification.RegisterDeviceRequest
import com.obrit.obrit.shared.network.request.notification.UpdateNotificationSettingsRequest
import com.obrit.obrit.shared.network.response.notification.MarkReadNotificationResponse
import com.obrit.obrit.shared.network.response.notification.NotificationResponse
import com.obrit.obrit.shared.network.response.notification.NotificationSettingsResponse

interface NotificationRemoteDataSource {
    suspend fun registerDevice(request: RegisterDeviceRequest)

    suspend fun getNotifications(): List<NotificationResponse>

    suspend fun markAsRead(notificationId: Long): MarkReadNotificationResponse

    suspend fun markAllAsRead()

    suspend fun getSettings(): NotificationSettingsResponse

    suspend fun updateSettings(request: UpdateNotificationSettingsRequest): NotificationSettingsResponse
}
