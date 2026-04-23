package com.obrit.obrit.shared.network.response.session

import com.obrit.obrit.shared.model.sessions.Session
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionResponse(
    @SerialName("id") val id: Int?,
    @SerialName("name") val name: String?,
)

fun SessionResponse.toSession() = Session(
    id = id ?: -1,
    name = name.orEmpty(),
)
