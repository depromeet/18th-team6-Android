package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.request.user.RegisterUserRequest
import com.obrit.obrit.shared.network.response.ApiResponse
import com.obrit.obrit.shared.network.response.requireData
import com.obrit.obrit.shared.network.response.user.RegisterUserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class UserRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : UserRemoteDataSource {
    override suspend fun register(request: RegisterUserRequest): RegisterUserResponse =
        httpClient
            .post(USERS_PATH) {
                setBody(request)
            }.body<ApiResponse<RegisterUserResponse>>()
            .requireData()
}

private const val USERS_PATH = "users"
