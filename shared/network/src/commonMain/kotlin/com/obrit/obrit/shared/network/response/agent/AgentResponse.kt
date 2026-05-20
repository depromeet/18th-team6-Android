package com.obrit.obrit.shared.network.response.agent

import com.obrit.obrit.shared.model.agents.Agent
import com.obrit.obrit.shared.model.agents.AgentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class AgentResponse(
    @SerialName("id") val id: Int?,
    @SerialName("name") val name: String?,
    @SerialName("description") val description: String?,
    @SerialName("createdAt") val timestamp: Long?,
    @SerialName("type") val type: String?,
)

fun AgentResponse.toAgent() =
    Agent(
        id = id ?: -1,
        name = name.orEmpty(),
        description = description.orEmpty(),
        timestamp = Instant.fromEpochMilliseconds(timestamp ?: 0),
        type =
            when (type) {
                "ANALYST" -> AgentType.ANALYST
                "IMPLEMENTER" -> AgentType.IMPLEMENTER
                "REVIEWER" -> AgentType.REVIEWER
                else -> AgentType.UNKNOWN
            },
    )
