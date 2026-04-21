package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.request.agent.CreateAgentRequest
import com.obrit.obrit.shared.network.request.agent.PatchAgentRequest
import com.obrit.obrit.shared.network.response.agent.AgentResponse
import com.obrit.obrit.shared.network.response.agent.AgentsResponse

interface AgentRemoteDataSource {

    suspend fun getAgent(id: Int): AgentResponse

    suspend fun getAgents(): AgentsResponse

    suspend fun createAgent(request: CreateAgentRequest): AgentResponse

    suspend fun deleteAgent(id: Int)

    suspend fun patchAgent(id: Int, request: PatchAgentRequest): AgentResponse
}
