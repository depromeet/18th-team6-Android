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
                "DANGER" -> HomeBucketType.DANGER
                "WARNING" -> HomeBucketType.WARNING
                else -> HomeBucketType.UNKNOWN
            },
        count = count,
        items = items.map { item -> item.toHomeBucketItem() },
    )
