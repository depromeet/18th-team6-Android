package com.obrit.obrit.shared.network.response.agent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AgentsResponse(
    @SerialName("agents") val agents: List<AgentResponse>,
)
