package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.agents.Agent
import com.obrit.obrit.shared.model.agents.AgentType
import com.obrit.obrit.shared.model.agents.PatchAgentParams

interface AgentRepository {
    suspend fun getAgent(id: Int): Result<Agent>

    suspend fun getAgents(): Result<List<Agent>>

    suspend fun createAgent(
        name: String,
        description: String,
        type: AgentType,
    ): Result<Agent>

    suspend fun deleteAgent(id: Int): Result<Unit>

    suspend fun patchAgent(params: PatchAgentParams): Result<Agent>
}
