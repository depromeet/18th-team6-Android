package com.obrit.obrit.shared.network.request.item

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PatchItemRequest(
    @SerialName("categoryId") val categoryId: Long? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("spareQuantity") val count: Int? = null,
    @SerialName("lastReplacedDate") val lastReplacedDate: String? = null,
    @SerialName("replacementIntervalDays") val replacementIntervalDays: Int? = null,
    @SerialName("iconId") val iconId: Long? = null,
)
