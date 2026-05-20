package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.sessions.Session

interface AgentSessionRepository {
    suspend fun getSessions(): Result<List<Session>>
}
