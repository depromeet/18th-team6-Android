package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.notifications.NotificationPreferences
import com.obrit.obrit.shared.model.notifications.UpdateNotificationSettingsParams
import com.obrit.obrit.shared.network.error.runCatchingWith
import com.obrit.obrit.shared.network.request.notification.UpdateNotificationSettingsRequest
import com.obrit.obrit.shared.network.response.notification.toNotificationPreferences
import com.obrit.obrit.shared.network.source.NotificationSettingsRemoteDataSource

internal class NotificationSettingsRepositoryImpl(
    private val notificationSettingsRemoteDataSource: NotificationSettingsRemoteDataSource,
) : NotificationSettingsRepository {
    override suspend fun getSettings(): Result<NotificationPreferences> =
        runCatchingWith {
            notificationSettingsRemoteDataSource.getSettings().toNotificationPreferences()
        }

    override suspend fun updateSettings(settings: UpdateNotificationSettingsParams): Result<NotificationPreferences> =
        runCatchingWith {
            require(DISPATCH_TIME_PATTERN.matches(settings.dispatchTime)) {
                "Dispatch time must use HH:mm with a 30-minute interval."
            }
            notificationSettingsRemoteDataSource
                .updateSettings(
                    UpdateNotificationSettingsRequest(
                        preReplacementEnabled = settings.preReplacementEnabled,
                        overdueEnabled = settings.overdueEnabled,
                        lowStockEnabled = settings.lowStockEnabled,
                        dispatchTime = settings.dispatchTime,
                    ),
                ).toNotificationPreferences()
        }
}

private val DISPATCH_TIME_PATTERN = Regex("(?:[01][0-9]|2[0-3]):(?:00|30)")
