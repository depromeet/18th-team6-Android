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
    override suspend fun get(): Long {
        userIdStorage.load()?.let { return it }

        val registeredUser =
            userRemoteDataSource.register(
                RegisterUserRequest(
                    type = UUID_AUTH_TYPE,
                    value = deviceUuidProvider.get(),
                ),
            )

        userIdStorage.save(registeredUser.id)
        return registeredUser.id
    }
}

private const val UUID_AUTH_TYPE = "uuid"
