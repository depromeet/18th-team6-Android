package com.obrit.obrit.shared.model.items

import com.obrit.obrit.shared.model.ReplacementDate

data class CreateItemParams(
    val categoryId: Long,
    val name: String,
    val count: Int? = null,
    val lastReplacedDate: ReplacementDate? = null,
    val replacementIntervalDays: Int? = null,
)
