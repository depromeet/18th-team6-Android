package com.obrit.obrit.shared.network.request.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterUserRequest(
    @SerialName("type") val type: String,
    @SerialName("value") val value: String,
)
