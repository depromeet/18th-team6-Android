package com.obrit.obrit.shared.network.config

import com.obrit.obrit.shared.network.request.user.RegisterUserRequest
import com.obrit.obrit.shared.network.source.UserRemoteDataSource

internal interface UserIdProvider {
    suspend fun get(): Long
}

internal class DefaultUserIdProvider(
    private val deviceUuidProvider: DeviceUuidProvider,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userIdStorage: UserIdStorage,
) : UserIdProvider {
    private var cachedUserId: Long? = null

    override suspend fun get(): Long {
        cachedUserId?.let { userId -> return userId }
        userIdStorage.load()?.let { userId ->
            cachedUserId = userId
            return userId
        }

        val registeredUser =
            userRemoteDataSource.register(
                RegisterUserRequest(
                    type = UUID_AUTH_TYPE,
                    value = deviceUuidProvider.get(),
                ),
            )

        cachedUserId = registeredUser.userId
        userIdStorage.save(registeredUser.userId)
        return registeredUser.userId
    }
}

private const val UUID_AUTH_TYPE = "uuid"
