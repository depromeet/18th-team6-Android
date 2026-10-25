package com.obrit.obrit.shared.network.request.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateNotificationSettingsRequest(
    @SerialName("preReplacementEnabled") val preReplacementEnabled: Boolean,
    @SerialName("overdueEnabled") val overdueEnabled: Boolean,
    @SerialName("lowStockEnabled") val lowStockEnabled: Boolean,
    @SerialName("dispatchTime") val dispatchTime: String,
    @SerialName("permissionStatus") val permissionStatus: String,
)
