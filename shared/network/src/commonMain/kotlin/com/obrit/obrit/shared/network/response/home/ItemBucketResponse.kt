package com.obrit.obrit.shared.network.response.home

import com.obrit.obrit.shared.model.home.HomeBucketGroup
import com.obrit.obrit.shared.model.home.HomeBucketType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemBucketResponse(
    @SerialName("bucket") val bucket: String,
    @SerialName("count") val count: Int,
    @SerialName("items") val items: List<BucketItemResponse>,
)

fun ItemBucketResponse.toHomeBucketGroup() =
    HomeBucketGroup(
        bucket =
            when (bucket) {
                "NONE_OVERDUE" -> HomeBucketType.NONE_OVERDUE
                "NONE_WARN" -> HomeBucketType.NONE_WARN
                "HAS_OVERDUE" -> HomeBucketType.HAS_OVERDUE
                "HAS_WARN" -> HomeBucketType.HAS_WARN
                "NONE_SAFE" -> HomeBucketType.NONE_SAFE
                "HAS_SAFE" -> HomeBucketType.HAS_SAFE
                else -> HomeBucketType.UNKNOWN
            },
        count = count,
        items = items.map { item -> item.toHomeBucketItem() },
    )
