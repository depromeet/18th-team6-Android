package com.obrit.obrit.shared.network.request.item

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PatchItemRequest(
    @SerialName("name") val name: String? = null,
    @SerialName("count") val count: Int? = null,
    @SerialName("lastReplacedDate") val lastReplacedDate: String? = null,
    @SerialName("replacementIntervalDays") val replacementIntervalDays: Int? = null,
)
