package com.obrit.obrit.shared.network.response.home

import com.obrit.obrit.shared.model.ReplacementDate
import com.obrit.obrit.shared.model.home.HomeBucketItem
import com.obrit.obrit.shared.model.home.HomeStatusLevel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BucketItemResponse(
    @SerialName("itemId") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("spareQuantity") val count: Int,
    @SerialName("iconUrl") val iconUrl: String,
    @SerialName("nextReplacementDate") val nextReplacementDate: String? = null,
    @SerialName("status") val status: String,
)

fun BucketItemResponse.toHomeBucketItem() =
    HomeBucketItem(
        id = id,
        name = name,
        iconUrl = iconUrl,
        count = count,
        nextReplacementDate = nextReplacementDate?.let(::ReplacementDate),
        status =
            when (status) {
                "GOOD" -> HomeStatusLevel.GOOD
                "WARNING" -> HomeStatusLevel.WARNING
                "DANGER" -> HomeStatusLevel.DANGER
                else -> HomeStatusLevel.UNKNOWN
            },
    )
