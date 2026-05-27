package com.obrit.obrit.shared.model.home

import com.obrit.obrit.shared.model.ReplacementDate

data class HomeBucketItem(
    val itemId: Long,
    val name: String,
    val spareQuantity: Int,
    val nextReplacementDate: ReplacementDate?,
    val status: HomeStatusLevel,
)
