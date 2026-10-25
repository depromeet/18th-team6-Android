package com.obrit.obrit.di

import com.obrit.obrit.shared.model.notifications.NotificationPreferences
import com.obrit.obrit.shared.model.notifications.UpdateNotificationSettingsParams

/** Swift-facing notification settings facade that unwraps Kotlin Result. */
class SharedNotificationSettingsService(
    private val repositoryProvider: SharedRepositoryProvider = SharedRepositoryProvider(),
) {
    @Throws(Throwable::class)
    suspend fun getSettings(): NotificationPreferences =
        repositoryProvider
            .notificationSettingsRepository()
            .getSettings()
            .getOrThrow()

    @Throws(Throwable::class)
    suspend fun updateSettings(settings: UpdateNotificationSettingsParams): NotificationPreferences =
        repositoryProvider
            .notificationSettingsRepository()
            .updateSettings(settings)
            .getOrThrow()
}
