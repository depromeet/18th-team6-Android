package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.client.userIdHeader
import com.obrit.obrit.shared.network.config.UserIdProvider
import com.obrit.obrit.shared.network.request.notification.RegisterDeviceRequest
import com.obrit.obrit.shared.network.response.ApiResponse
import com.obrit.obrit.shared.network.response.notification.RegisterDeviceResponse
import com.obrit.obrit.shared.network.response.requireData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class NotificationDeviceRemoteDataSourceImpl(
    private val httpClient: HttpClient,
    private val userIdProvider: UserIdProvider,
) : NotificationDeviceRemoteDataSource {
    override suspend fun registerDevice(request: RegisterDeviceRequest): RegisterDeviceResponse {
        val userId = userIdProvider.get()

        return httpClient
            .post(NOTIFICATION_DEVICES_PATH) {
                userIdHeader(userId)
                setBody(request)
            }.body<ApiResponse<RegisterDeviceResponse>>()
            .requireData()
    }
}

private const val NOTIFICATION_DEVICES_PATH = "notifications/devices"
