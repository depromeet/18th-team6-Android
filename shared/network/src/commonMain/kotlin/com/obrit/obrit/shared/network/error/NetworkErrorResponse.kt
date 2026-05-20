package com.obrit.obrit.shared.network.error

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class NetworkErrorResponse(
    @SerialName("code") val code: Int = 0,
    @SerialName("message") val message: String = "",
)
