package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.notifications.NotificationDevice

interface NotificationDeviceRepository {
    suspend fun registerDevice(fid: String): Result<NotificationDevice>
}
