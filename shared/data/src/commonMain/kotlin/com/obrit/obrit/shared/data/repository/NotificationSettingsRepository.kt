package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.notifications.NotificationPreferences
import com.obrit.obrit.shared.model.notifications.UpdateNotificationSettingsParams

interface NotificationSettingsRepository {
    suspend fun getSettings(): Result<NotificationPreferences>

    suspend fun updateSettings(settings: UpdateNotificationSettingsParams): Result<NotificationPreferences>
}
