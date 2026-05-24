package com.obrit.obrit.shared.network.request.item

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateReplacementRequest(
    @SerialName("replacedDate") val replacedDate: String? = null,
)
