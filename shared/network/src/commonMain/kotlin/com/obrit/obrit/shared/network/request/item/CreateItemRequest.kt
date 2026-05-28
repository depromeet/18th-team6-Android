package com.obrit.obrit.shared.network.request.item

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateItemRequest(
    @SerialName("categoryId") val categoryId: Long,
    @SerialName("name") val name: String,
    @SerialName("spareQuantity") val count: Int? = null,
    @SerialName("lastReplacementPeriod") val lastReplacementPeriod: String? = null,
    @SerialName("replacementIntervalDays") val replacementIntervalDays: Int? = null,
)
