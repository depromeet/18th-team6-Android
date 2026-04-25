package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.request.agent.CreateAgentRequest
import com.obrit.obrit.shared.network.request.agent.PatchAgentRequest
import com.obrit.obrit.shared.network.response.agent.AgentResponse
import com.obrit.obrit.shared.network.response.agent.AgentsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class AgentRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : AgentRemoteDataSource {
    override suspend fun getAgent(id: Int): AgentResponse = httpClient.get("api/v1/agents/$id").body()

    override suspend fun getAgents(): AgentsResponse = httpClient.get("api/v1/agents").body()

    override suspend fun createAgent(request: CreateAgentRequest): AgentResponse =
        httpClient
            .post("api/v1/agents") {
                setBody(request)
            }.body()

    override suspend fun deleteAgent(id: Int) {
        httpClient.delete("api/v1/agents/$id")
    }

    override suspend fun patchAgent(
        id: Int,
        request: PatchAgentRequest,
    ): AgentResponse = httpClient.patch("api/v1/agents/$id").body()
}
