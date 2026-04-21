package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.sessions.Session

interface SessionRepository {

    suspend fun getSessions(): Result<List<Session>>
}
