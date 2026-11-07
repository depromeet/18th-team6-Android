package com.obrit.obrit.shared.network.response.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterDeviceResponse(
    @SerialName("id") val id: Long,
    @SerialName("userId") val userId: Long,
    @SerialName("fid") val fid: String,
    @SerialName("createdAt") val createdAt: String,
)
