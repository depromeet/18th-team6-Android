package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.request.notification.UpdateNotificationSettingsRequest
import com.obrit.obrit.shared.network.response.notification.NotificationSettingsResponse

interface NotificationSettingsRemoteDataSource {
    suspend fun getSettings(): NotificationSettingsResponse

    suspend fun updateSettings(request: UpdateNotificationSettingsRequest): NotificationSettingsResponse
}
