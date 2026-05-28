package com.obrit.obrit.shared.model.items

enum class LastReplacementPeriod(
    val value: String,
) {
    WITHIN_WEEK("WITHIN_WEEK"),
    WITHIN_MONTH("WITHIN_MONTH"),
    WITHIN_THREE_MONTHS("WITHIN_THREE_MONTHS"),
    OVER_THREE_MONTHS("OVER_THREE_MONTHS"),
    ;

    companion object {
        fun fromValue(value: String): LastReplacementPeriod? = entries.firstOrNull { period -> period.value == value }
    }
}
