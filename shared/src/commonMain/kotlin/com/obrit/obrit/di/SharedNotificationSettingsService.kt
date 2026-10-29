package com.obrit.obrit.di

import com.obrit.obrit.shared.model.notifications.NotificationSettings

/** Swift-facing notification settings facade that unwraps Kotlin Result. */
class SharedNotificationSettingsService(
    private val repositoryProvider: SharedRepositoryProvider = SharedRepositoryProvider(),
) {
    @Throws(Throwable::class)
    suspend fun getSettings(): NotificationSettings =
        repositoryProvider
            .notificationRepository()
            .getSettings()
            .getOrThrow()

    @Throws(Throwable::class)
    suspend fun updateSettings(settings: NotificationSettings): NotificationSettings =
        repositoryProvider
            .notificationRepository()
            .updateSettings(settings)
            .getOrThrow()
}
