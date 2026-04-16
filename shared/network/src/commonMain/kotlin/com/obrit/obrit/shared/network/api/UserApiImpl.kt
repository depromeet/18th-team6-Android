package com.obrit.obrit.shared.network.api

import com.obrit.obrit.shared.network.request.SignUpRequest
import com.obrit.obrit.shared.network.response.SignUpResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class UserApiImpl(
    private val httpClient: HttpClient,
) : UserApi {
    override suspend fun signUp(request: SignUpRequest): SignUpResponse {
        return httpClient.post("api/v1/samples/sign-up") {
            setBody(request)
        }.body()
    }
}
