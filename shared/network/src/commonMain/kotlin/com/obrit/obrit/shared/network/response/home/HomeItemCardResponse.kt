package com.obrit.obrit.shared.network.response.home

import com.obrit.obrit.shared.model.home.HomeItemCard
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeItemCardResponse(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("daysInUse") val daysInUse: Int,
    @SerialName("replacementDday") val replacementDday: String,
    @SerialName("spareQuantity") val spareQuantity: Int,
)

fun HomeItemCardResponse.toHomeItemCard() =
    HomeItemCard(
        id = id,
        name = name,
        daysInUse = daysInUse,
        replacementDday = replacementDday,
        spareQuantity = spareQuantity,
    )
