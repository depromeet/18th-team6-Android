package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.request.notification.RegisterDeviceRequest
import com.obrit.obrit.shared.network.response.notification.NotificationResponse

interface NotificationRemoteDataSource {
    suspend fun registerDevice(request: RegisterDeviceRequest)

    suspend fun getNotifications(): List<NotificationResponse>

    suspend fun markAsRead(notificationId: Long)
}
