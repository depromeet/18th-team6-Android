package com.obrit.obrit.shared.network.request.agent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateAgentRequest(
    @SerialName("name") val name: String?,
    @SerialName("description") val description: String?,
    @SerialName("type") val type: String?
)
