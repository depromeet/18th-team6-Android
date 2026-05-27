package com.obrit.obrit.shared.model.home

import com.obrit.obrit.shared.model.ReplacementDate

data class HomeBucketItem(
    val id: Long,
    val name: String,
    val iconUrl: String,
    val count: Int,
    val nextReplacementDate: ReplacementDate?,
    val status: HomeStatusLevel,
)
