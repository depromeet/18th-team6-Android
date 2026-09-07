package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.notifications.Notification

interface NotificationRepository {
    suspend fun registerDevice(fid: String): Result<Unit>

    suspend fun getNotifications(): Result<List<Notification>>

    suspend fun markAsRead(notificationId: Long): Result<Unit>
}
