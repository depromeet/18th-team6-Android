package com.obrit.obrit.shared.network.request.notification

import com.obrit.obrit.shared.model.notifications.NotificationSettings
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateNotificationSettingsRequest(
    @SerialName("preReplacementEnabled") val preReplacementEnabled: Boolean,
    @SerialName("overdueEnabled") val overdueEnabled: Boolean,
    @SerialName("lowStockEnabled") val lowStockEnabled: Boolean,
    @SerialName("dispatchTime") val dispatchTime: String,
)

fun NotificationSettings.toUpdateRequest() =
    UpdateNotificationSettingsRequest(
        preReplacementEnabled = isReplacementUpcomingEnabled,
        overdueEnabled = isReplacementOverdueEnabled,
        lowStockEnabled = isSpareShortageEnabled,
        dispatchTime = sendTime,
    )
