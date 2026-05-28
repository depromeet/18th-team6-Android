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
        val userId =
            cachedUserId ?: userIdStorage.load()?.also { savedUserId ->
                cachedUserId = savedUserId
            } ?: registerUser()

        return userId
    }

    private suspend fun registerUser(): Long {
        val registeredUser =
            userRemoteDataSource.register(
                RegisterUserRequest(
                    type = UUID_AUTH_TYPE,
                    value = deviceUuidProvider.get(),
                ),
            )

        return registeredUser.userId.also { userId ->
            cachedUserId = userId
            userIdStorage.save(userId)
        }
    }
}

private const val UUID_AUTH_TYPE = "uuid"
