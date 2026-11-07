package com.obrit.obrit.shared.network.response.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterUserResponse(
    @SerialName("userId") val userId: Long,
    @SerialName("uuid") val uuid: String,
)
