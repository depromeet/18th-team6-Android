package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.sessions.Session
import com.obrit.obrit.shared.model.sessions.error.GetSessionsError
import com.obrit.obrit.shared.network.error.runCatchingWith
import com.obrit.obrit.shared.network.response.session.toSession
import com.obrit.obrit.shared.network.source.SessionRemoteDataSource

internal class SessionRepositoryImpl(
    private val sessionRemoteDataSource: SessionRemoteDataSource,
) : SessionRepository {

    override suspend fun getSessions(): Result<List<Session>> {
        return runCatchingWith(GetSessionsError()) {
            sessionRemoteDataSource.getSessions().sessions.map { it.toSession() }
        }
    }
}
