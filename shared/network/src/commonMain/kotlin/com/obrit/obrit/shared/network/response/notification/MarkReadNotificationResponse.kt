package com.obrit.obrit.shared.network.response.notification

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * 읽음 처리 응답. 읽음 상태를 바꾸면서 이동할 딥링크를 함께 준다.
 *
 * [itemId]는 묶음·공지 알림이면 null이고 그때 [deepLink]는 홈을 가리킨다.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class MarkReadNotificationResponse(
    @SerialName("id") val id: Long,
    @SerialName("isRead") @JsonNames("read") val isRead: Boolean,
    @SerialName("readAt") val readAt: String,
    @SerialName("itemId") val itemId: Long? = null,
    @SerialName("deepLink") val deepLink: String,
)
