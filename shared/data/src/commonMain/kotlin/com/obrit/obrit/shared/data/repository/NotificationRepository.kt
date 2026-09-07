package com.obrit.obrit.shared.data.repository

interface NotificationRepository {
    suspend fun registerDevice(fid: String): Result<Unit>
}
