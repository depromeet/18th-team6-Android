package com.obrit.obrit.shared.network.response.home

import com.obrit.obrit.shared.model.home.HomeBucketGroup
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeBucketsResponse(
    @SerialName("buckets") val buckets: List<ItemBucketResponse>,
)

fun HomeBucketsResponse.toHomeBucketGroups(): List<HomeBucketGroup> = buckets.map { bucket -> bucket.toHomeBucketGroup() }
