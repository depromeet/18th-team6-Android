package com.obrit.obrit.shared.model.items

data class CreateItemParams(
    val categoryId: Long,
    val name: String,
    val count: Int? = null,
    val lastReplacementPeriod: LastReplacementPeriod? = null,
    val replacementIntervalDays: Int? = null,
)
