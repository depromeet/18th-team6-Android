package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.agents.Agent
import com.obrit.obrit.shared.model.agents.AgentType
import com.obrit.obrit.shared.model.agents.PatchAgentParams
import com.obrit.obrit.shared.model.agents.error.CreateAgentError
import com.obrit.obrit.shared.model.agents.error.DeleteAgentError
import com.obrit.obrit.shared.model.agents.error.GetAgentError
import com.obrit.obrit.shared.model.agents.error.GetAgentsError
import com.obrit.obrit.shared.model.agents.error.PatchAgentError
import com.obrit.obrit.shared.network.error.runCatchingWith
import com.obrit.obrit.shared.network.request.agent.CreateAgentRequest
import com.obrit.obrit.shared.network.request.agent.PatchAgentRequest
import com.obrit.obrit.shared.network.response.agent.toAgent
import com.obrit.obrit.shared.network.source.AgentRemoteDataSource

internal class AgentRepositoryImpl(
    private val agentRemoteDataSource: AgentRemoteDataSource
) : AgentRepository {

    override suspend fun getAgent(id: Int): Result<Agent> {
        return runCatchingWith(GetAgentError()) {
            agentRemoteDataSource.getAgent(id).toAgent()
        }
    }

    override suspend fun getAgents(): Result<List<Agent>> {
        return runCatchingWith(GetAgentsError()) {
            agentRemoteDataSource.getAgents().agents.map { it.toAgent() }
        }
    }

    override suspend fun createAgent(
        name: String,
        description: String,
        type: AgentType
    ) : Result<Agent> {
        return runCatchingWith(CreateAgentError()) {
            agentRemoteDataSource
                .createAgent(CreateAgentRequest(name, description, type.name)).toAgent()
        }
    }

    override suspend fun deleteAgent(id: Int) : Result<Unit>  {
        return runCatchingWith(DeleteAgentError()) {
            agentRemoteDataSource.deleteAgent(id)
        }
    }

    override suspend fun patchAgent(params: PatchAgentParams) : Result<Agent>  {
        return runCatchingWith(PatchAgentError()) {
            agentRemoteDataSource.patchAgent(
                id = params.id,
                request = PatchAgentRequest(
                    name = params.name,
                    description = params.description,
                    type = params.type.name
                )
            ).toAgent()
        }
    }
}
