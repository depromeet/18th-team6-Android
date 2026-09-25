package com.obrit.obrit.shared.network.response.notification

import com.obrit.obrit.shared.model.notifications.Notification
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponse(
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String,
    @SerialName("content") val content: String? = null,
    @SerialName("isRead") val isRead: Boolean = false,
    @SerialName("createdAt") val createdAt: String,
)

fun NotificationResponse.toNotification() =
    Notification(
        id = id,
        title = title,
        content = content,
        isRead = isRead,
        createdAt = createdAt,
    )
