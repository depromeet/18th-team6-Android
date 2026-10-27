package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.notifications.Notification
import com.obrit.obrit.shared.model.notifications.NotificationSettings

interface NotificationRepository {
    suspend fun registerDevice(fid: String): Result<Unit>

    suspend fun getNotifications(): Result<List<Notification>>

    /** 읽음 처리하고 서버가 정한 이동할 딥링크를 돌려준다. */
    suspend fun markAsRead(notificationId: Long): Result<String>

    suspend fun getSettings(): Result<NotificationSettings>

    suspend fun updateSettings(settings: NotificationSettings): Result<NotificationSettings>
}
