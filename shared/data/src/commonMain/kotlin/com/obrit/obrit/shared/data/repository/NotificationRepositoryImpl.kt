package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.network.error.runCatchingWith
import com.obrit.obrit.shared.network.request.notification.RegisterDeviceRequest
import com.obrit.obrit.shared.network.source.NotificationRemoteDataSource

internal class NotificationRepositoryImpl(
    private val notificationRemoteDataSource: NotificationRemoteDataSource,
) : NotificationRepository {
    override suspend fun registerDevice(fid: String): Result<Unit> =
        runCatchingWith {
            notificationRemoteDataSource.registerDevice(RegisterDeviceRequest(fid = fid))
        }
}
