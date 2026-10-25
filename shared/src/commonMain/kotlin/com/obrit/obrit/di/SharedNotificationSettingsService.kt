package com.obrit.obrit.di

import com.obrit.obrit.shared.model.notifications.NotificationPreferences

/** Swift-facing notification settings facade that unwraps Kotlin Result. */
class SharedNotificationSettingsService(
    private val repositoryProvider: SharedRepositoryProvider = SharedRepositoryProvider(),
) {
    @Throws(Throwable::class)
    suspend fun getSettings(): NotificationPreferences =
        logged(event = "GET /notifications/settings") {
            repositoryProvider
                .notificationSettingsRepository()
                .getSettings()
                .getOrThrow()
        }

    @Throws(Throwable::class)
    suspend fun updateSettings(settings: NotificationPreferences): NotificationPreferences =
        logged(event = "PUT /notifications/settings", details = "request=$settings") {
            repositoryProvider
                .notificationSettingsRepository()
                .updateSettings(settings)
                .getOrThrow()
        }

    private suspend fun logged(
        event: String,
        details: String = "",
        block: suspend () -> NotificationPreferences,
    ): NotificationPreferences {
        SharedLog.enter(scope = LOG_SCOPE, event = event, details = details)
        val result = runCatching { block() }
        result.onSuccess { response ->
            SharedLog.success(scope = LOG_SCOPE, event = event, details = "response=$response")
        }
        result.onFailure { throwable ->
            SharedLog.failure(scope = LOG_SCOPE, event = event, throwable = throwable)
        }
        return result.getOrThrow()
    }

    private companion object {
        const val LOG_SCOPE = "SharedNotificationSettingsService"
    }
}
