package com.obrit.obrit.shared.network.response.item

import com.obrit.obrit.shared.model.ReplacementDate
import com.obrit.obrit.shared.model.items.Item
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemResponse(
    @SerialName("itemId") val id: Long,
    @SerialName("categoryId") val categoryId: Long,
    @SerialName("categoryName") val categoryName: String,
    @SerialName("name") val name: String,
    @SerialName("spareQuantity") val count: Int,
    @SerialName("replacementIntervalDays") val replacementIntervalDays: Int,
    @SerialName("lastReplacedDate") val lastReplacedDate: String? = null,
    @SerialName("nextReplacementDate") val nextReplacementDate: String? = null,
)

fun ItemResponse.toItem() =
    Item(
        id = id,
        categoryId = categoryId,
        categoryName = categoryName,
        name = name,
        count = count,
        replacementIntervalDays = replacementIntervalDays,
        lastReplacedDate = lastReplacedDate?.let(::ReplacementDate),
        nextReplacementDate = nextReplacementDate?.let(::ReplacementDate),
    )
