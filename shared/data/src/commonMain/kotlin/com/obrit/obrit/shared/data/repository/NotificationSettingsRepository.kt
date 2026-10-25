package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.notifications.NotificationPreferences

interface NotificationSettingsRepository {
    suspend fun getSettings(): Result<NotificationPreferences>

    suspend fun updateSettings(settings: NotificationPreferences): Result<NotificationPreferences>
}
