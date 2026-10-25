package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.request.notification.RegisterDeviceRequest
import com.obrit.obrit.shared.network.response.notification.RegisterDeviceResponse

interface NotificationDeviceRemoteDataSource {
    suspend fun registerDevice(request: RegisterDeviceRequest): RegisterDeviceResponse
}
