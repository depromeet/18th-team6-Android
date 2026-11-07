package com.obrit.obrit.shared.model.items

data class CreateItemParams(
    val categoryId: Long? = null,
    val name: String,
    val spareQuantity: Int? = null,
    val lastReplacementPeriod: ReplacementPeriod? = null,
    val replacementIntervalDays: Int? = null,
    val newCategoryName: String? = null,
    val newCategoryDefaultReplacementIntervalDays: Int? = null,
)
