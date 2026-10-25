package com.obrit.obrit.shared.network.response.notification

import com.obrit.obrit.shared.model.notifications.NotificationDevice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterDeviceResponse(
    @SerialName("id") val id: Long,
    @SerialName("userId") val userId: Long,
    @SerialName("fid") val fid: String,
)

fun RegisterDeviceResponse.toNotificationDevice() =
    NotificationDevice(
        id = id,
        userId = userId,
        fid = fid,
    )
