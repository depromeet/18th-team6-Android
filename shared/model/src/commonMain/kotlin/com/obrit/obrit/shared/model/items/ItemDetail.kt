package com.obrit.obrit.shared.model.items

import com.obrit.obrit.shared.model.ReplacementDate

data class ItemDetail(
    val id: Long,
    val name: String,
    val category: ItemDetailCategory,
    val iconUrl: String?,
    val status: ItemDetailStatus,
    val dDay: Int?,
    val dDayLabel: String,
    val spareQuantity: Int,
    val lastReplacedDate: ReplacementDate?,
    val nextReplacementDate: ReplacementDate?,
    val usedDays: Int,
    val myAverageCycleDays: Double,
    val recommendedCycleDays: Int,
    val progressPercentage: Double,
    val recentReplacements: List<ItemDetailReplacement>,
)
