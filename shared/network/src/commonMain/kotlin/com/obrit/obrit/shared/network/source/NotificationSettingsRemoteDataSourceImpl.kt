package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.client.userIdHeader
import com.obrit.obrit.shared.network.config.UserIdProvider
import com.obrit.obrit.shared.network.request.notification.UpdateNotificationSettingsRequest
import com.obrit.obrit.shared.network.response.ApiResponse
import com.obrit.obrit.shared.network.response.notification.NotificationSettingsResponse
import com.obrit.obrit.shared.network.response.requireData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody

internal class NotificationSettingsRemoteDataSourceImpl(
    private val httpClient: HttpClient,
    private val userIdProvider: UserIdProvider,
) : NotificationSettingsRemoteDataSource {
    override suspend fun getSettings(): NotificationSettingsResponse {
        val userId = userIdProvider.get()

        return httpClient
            .get(NOTIFICATION_SETTINGS_PATH) {
                userIdHeader(userId)
            }.body<ApiResponse<NotificationSettingsResponse>>()
            .requireData()
    }

    override suspend fun updateSettings(request: UpdateNotificationSettingsRequest): NotificationSettingsResponse {
        val userId = userIdProvider.get()

        return httpClient
            .put(NOTIFICATION_SETTINGS_PATH) {
                userIdHeader(userId)
                setBody(request)
            }.body<ApiResponse<NotificationSettingsResponse>>()
            .requireData()
    }
}

private const val NOTIFICATION_SETTINGS_PATH = "notifications/settings"
