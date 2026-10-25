package com.obrit.obrit.shared.model.notifications

data class NotificationPreferences(
    val preReplacementEnabled: Boolean,
    val overdueEnabled: Boolean,
    val lowStockEnabled: Boolean,
    val dispatchTime: String,
    val permissionStatus: NotificationPermissionStatus,
)
