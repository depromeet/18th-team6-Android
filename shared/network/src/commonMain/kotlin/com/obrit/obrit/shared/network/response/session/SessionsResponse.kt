package com.obrit.obrit.shared.network.response.session

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionsResponse(
    @SerialName("sessions") val sessions: List<SessionResponse>,
)
