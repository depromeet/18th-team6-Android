package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.sessions.Session
import com.obrit.obrit.shared.model.sessions.error.GetSessionsError
import com.obrit.obrit.shared.network.error.runCatchingWith
import com.obrit.obrit.shared.network.response.session.toSession
import com.obrit.obrit.shared.network.source.AgentSessionRemoteDataSource

internal class AgentSessionRepositoryImpl(
    private val agentSessionRemoteDataSource: AgentSessionRemoteDataSource,
) : AgentSessionRepository {
    override suspend fun getSessions(): Result<List<Session>> =
        runCatchingWith(GetSessionsError()) {
            agentSessionRemoteDataSource.getSessions().sessions.map { it.toSession() }
        }
}
