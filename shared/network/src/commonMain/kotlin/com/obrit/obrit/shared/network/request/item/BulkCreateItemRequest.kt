package com.obrit.obrit.shared.network.request.item

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BulkCreateItemRequest(
    @SerialName("items") val items: List<CreateItemRequest>,
    @SerialName("receiptImageUrl") val receiptImageUrl: String? = null,
)
