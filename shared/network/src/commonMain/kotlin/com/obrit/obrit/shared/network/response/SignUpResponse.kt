package com.obrit.obrit.shared.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignUpResponse(
    @SerialName("userId") val userId: String,
    @SerialName("email") val email: String,
    @SerialName("nickname") val nickname: String,
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String,
)
