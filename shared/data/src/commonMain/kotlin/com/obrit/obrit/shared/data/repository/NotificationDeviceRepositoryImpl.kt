package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.notifications.NotificationDevice
import com.obrit.obrit.shared.network.error.runCatchingWith
import com.obrit.obrit.shared.network.request.notification.RegisterDeviceRequest
import com.obrit.obrit.shared.network.response.notification.toNotificationDevice
import com.obrit.obrit.shared.network.source.NotificationDeviceRemoteDataSource

internal class NotificationDeviceRepositoryImpl(
    private val remoteDataSource: NotificationDeviceRemoteDataSource,
) : NotificationDeviceRepository {
    override suspend fun registerDevice(fid: String): Result<NotificationDevice> =
        runCatchingWith {
            require(fid.isNotBlank()) { "FID must not be blank." }
            remoteDataSource.registerDevice(RegisterDeviceRequest(fid)).toNotificationDevice()
        }
}
