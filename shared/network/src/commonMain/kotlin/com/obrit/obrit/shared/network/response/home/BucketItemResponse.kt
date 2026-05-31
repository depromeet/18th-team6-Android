package com.obrit.obrit.shared.network.response.home

import com.obrit.obrit.shared.model.ReplacementDate
import com.obrit.obrit.shared.model.home.HomeBucketItem
import com.obrit.obrit.shared.model.home.HomeStatusLevel
import com.obrit.obrit.shared.model.home.ItemBucket
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BucketItemResponse(
    @SerialName("itemId") val itemId: Long,
    @SerialName("name") val name: String,
    @SerialName("spareQuantity") val spareQuantity: Int,
    @SerialName("nextReplacementDate") val nextReplacementDate: String? = null,
    @SerialName("status") val status: String,
    @SerialName("itemBucket") val itemBucket: String,
)

fun BucketItemResponse.toHomeBucketItem() =
    HomeBucketItem(
        itemId = itemId,
        name = name,
        spareQuantity = spareQuantity,
        nextReplacementDate = nextReplacementDate?.let(::ReplacementDate),
        status =
            when (status) {
                "GOOD" -> HomeStatusLevel.GOOD
                "WARNING" -> HomeStatusLevel.WARNING
                "DANGER" -> HomeStatusLevel.DANGER
                else -> HomeStatusLevel.UNKNOWN
            },
        itemBucket = itemBucket.toItemBucket(),
    )

internal fun String.toItemBucket(): ItemBucket =
    when (this) {
        "NONE_OVERDUE" -> ItemBucket.NONE_OVERDUE
        "NONE_WARN" -> ItemBucket.NONE_WARN
        "HAS_OVERDUE" -> ItemBucket.HAS_OVERDUE
        "HAS_WARN" -> ItemBucket.HAS_WARN
        "NONE_SAFE" -> ItemBucket.NONE_SAFE
        "HAS_SAFE" -> ItemBucket.HAS_SAFE
        else -> ItemBucket.NONE_SAFE
    }
