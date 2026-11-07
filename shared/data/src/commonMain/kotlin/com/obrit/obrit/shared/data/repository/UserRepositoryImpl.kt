package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.network.config.DeviceUuidProvider
import com.obrit.obrit.shared.network.error.runCatchingWith
import com.obrit.obrit.shared.network.request.user.RegisterUserRequest
import com.obrit.obrit.shared.network.source.UserRemoteDataSource

internal class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val deviceUuidProvider: DeviceUuidProvider,
) : UserRepository {
    override suspend fun register(): Result<Long> =
        runCatchingWith {
            userRemoteDataSource
                .register(
                    RegisterUserRequest(
                        type = UUID_AUTH_TYPE,
                        value = deviceUuidProvider.get(),
                    ),
                ).userId
        }
}

private const val UUID_AUTH_TYPE = "uuid"
