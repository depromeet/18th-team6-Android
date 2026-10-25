package com.obrit.obrit.shared.model.notifications

data class UpdateNotificationSettingsParams(
    val preReplacementEnabled: Boolean,
    val overdueEnabled: Boolean,
    val lowStockEnabled: Boolean,
    val dispatchTime: String,
)
