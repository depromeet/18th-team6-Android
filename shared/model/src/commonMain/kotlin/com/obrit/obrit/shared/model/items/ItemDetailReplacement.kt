package com.obrit.obrit.shared.model.items

import com.obrit.obrit.shared.model.ReplacementDate

data class ItemDetailReplacement(
    val id: Long,
    val date: ReplacementDate,
    val cycleDays: Int,
    val current: Boolean,
)
