package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.request.notification.RegisterDeviceRequest

interface NotificationRemoteDataSource {
    suspend fun registerDevice(request: RegisterDeviceRequest)
}
