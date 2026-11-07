package com.obrit.obrit.shared.model.items

import com.obrit.obrit.shared.model.ReplacementDate

data class Item(
    val id: Long,
    val categoryId: Long,
    val categoryName: String,
    val name: String,
    val count: Int,
    val replacementIntervalDays: Int,
    val lastReplacedDate: ReplacementDate?,
    val nextReplacementDate: ReplacementDate?,
)
