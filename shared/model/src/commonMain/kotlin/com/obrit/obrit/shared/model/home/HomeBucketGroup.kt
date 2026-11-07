package com.obrit.obrit.shared.model.home

data class HomeBucketGroup(
    val bucket: HomeBucketType,
    val count: Int,
    val items: List<HomeBucketItem>,
)
