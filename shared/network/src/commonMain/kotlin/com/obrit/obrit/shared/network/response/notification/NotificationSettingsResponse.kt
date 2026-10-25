package com.obrit.obrit.shared.network.response.notification

import com.obrit.obrit.shared.model.notifications.NotificationPermissionStatus
import com.obrit.obrit.shared.model.notifications.NotificationPreferences
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationSettingsResponse(
    @SerialName("preReplacementEnabled") val preReplacementEnabled: Boolean,
    @SerialName("overdueEnabled") val overdueEnabled: Boolean,
    @SerialName("lowStockEnabled") val lowStockEnabled: Boolean,
    @SerialName("dispatchTime") val dispatchTime: String,
    @SerialName("permissionStatus") val permissionStatus: String,
)

fun NotificationSettingsResponse.toNotificationPreferences() =
    NotificationPreferences(
        preReplacementEnabled = preReplacementEnabled,
        overdueEnabled = overdueEnabled,
        lowStockEnabled = lowStockEnabled,
        dispatchTime = dispatchTime,
        permissionStatus =
            NotificationPermissionStatus.entries.firstOrNull { it.name == permissionStatus }
                ?: NotificationPermissionStatus.UNKNOWN,
    )
