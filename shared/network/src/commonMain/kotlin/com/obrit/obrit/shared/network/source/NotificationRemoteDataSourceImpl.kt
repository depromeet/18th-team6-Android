package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.client.userIdHeader
import com.obrit.obrit.shared.network.config.UserIdProvider
import com.obrit.obrit.shared.network.request.notification.RegisterDeviceRequest
import com.obrit.obrit.shared.network.request.notification.UpdateNotificationSettingsRequest
import com.obrit.obrit.shared.network.response.ApiResponse
import com.obrit.obrit.shared.network.response.notification.MarkReadNotificationResponse
import com.obrit.obrit.shared.network.response.notification.NotificationResponse
import com.obrit.obrit.shared.network.response.notification.NotificationSettingsResponse
import com.obrit.obrit.shared.network.response.notification.RegisterDeviceResponse
import com.obrit.obrit.shared.network.response.requireData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

internal class NotificationRemoteDataSourceImpl(
    private val httpClient: HttpClient,
    private val userIdProvider: UserIdProvider,
) : NotificationRemoteDataSource {
    override suspend fun registerDevice(request: RegisterDeviceRequest) {
        val userId = userIdProvider.get()

        httpClient
            .post(NOTIFICATION_DEVICES_PATH) {
                userIdHeader(userId)
                setBody(request)
            }.body<ApiResponse<RegisterDeviceResponse>>()
            .requireData()
    }

    override suspend fun getNotifications(): List<NotificationResponse> {
        val userId = userIdProvider.get()

        return httpClient
            .get(NOTIFICATIONS_PATH) {
                userIdHeader(userId)
            }.body<ApiResponse<List<NotificationResponse>>>()
            .requireData()
    }

    override suspend fun markAsRead(notificationId: Long): MarkReadNotificationResponse {
        val userId = userIdProvider.get()

        return httpClient
            .put("$NOTIFICATIONS_PATH/$notificationId/read") {
                userIdHeader(userId)
            }.body<ApiResponse<MarkReadNotificationResponse>>()
            .requireData()
    }

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

private const val NOTIFICATIONS_PATH = "notifications"
private const val NOTIFICATION_DEVICES_PATH = "notifications/devices"
private const val NOTIFICATION_SETTINGS_PATH = "notifications/settings"
