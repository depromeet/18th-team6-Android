package com.obrit.obrit.shared.model.items

import com.obrit.obrit.shared.model.ReplacementDate

data class PatchItemParams(
    val itemId: Long,
    val categoryId: Long? = null,
    val name: String? = null,
    val count: Int? = null,
    val lastReplacedDate: ReplacementDate? = null,
    val replacementIntervalDays: Int? = null,
    val iconId: Long? = null,
)
