package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.response.session.SessionsResponse

interface AgentSessionRemoteDataSource {

    suspend fun getSessions(): SessionsResponse
}
