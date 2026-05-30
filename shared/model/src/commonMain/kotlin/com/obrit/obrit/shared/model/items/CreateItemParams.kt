package com.obrit.obrit.shared.model.items

data class CreateItemParams(
    val categoryId: Long,
    val name: String,
    val spareQuantity: Int? = null,
    val lastReplacementPeriod: ReplacementPeriod? = null,
    val replacementIntervalDays: Int? = null,
)
