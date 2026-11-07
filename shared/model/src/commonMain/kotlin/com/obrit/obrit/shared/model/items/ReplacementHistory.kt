package com.obrit.obrit.shared.model.items

import com.obrit.obrit.shared.model.ReplacementDate

data class ReplacementHistory(
    val id: Long,
    val replacedDate: ReplacementDate,
)
