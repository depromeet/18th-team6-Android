package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.response.session.SessionsResponse

interface SessionRemoteDataSource {

    suspend fun getSessions(): SessionsResponse
}
