package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.response.session.SessionsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class SessionRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : SessionRemoteDataSource {

    override suspend fun getSessions(): SessionsResponse {
        return httpClient.get("api/v1/sessions").body()
    }
}
