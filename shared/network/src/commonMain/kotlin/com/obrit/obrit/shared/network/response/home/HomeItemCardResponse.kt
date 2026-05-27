package com.obrit.obrit.shared.network.response.home

import com.obrit.obrit.shared.model.home.HomeItemCard
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeItemCardResponse(
    @SerialName("itemId") val itemId: Long,
    @SerialName("name") val name: String,
    @SerialName("daysInUse") val daysInUse: Int,
    @SerialName("replacementDday") val replacementDday: String,
    @SerialName("spareQuantity") val spareQuantity: Int,
    @SerialName("iconUrl") val iconUrl: String,
)

fun HomeItemCardResponse.toHomeItemCard() =
    HomeItemCard(
        itemId = itemId,
        name = name,
        daysInUse = daysInUse,
        replacementDday = replacementDday,
        spareQuantity = spareQuantity,
        iconUrl = iconUrl,
    )
