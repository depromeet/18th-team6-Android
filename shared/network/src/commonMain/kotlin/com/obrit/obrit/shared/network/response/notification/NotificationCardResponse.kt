package com.obrit.obrit.shared.network.response.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 알림함에 보여줄 카드 한 장. 모든 값은 발송 당시 스냅샷이라 이후 소모품 변경을 반영하지 않는다.
 *
 * 공지 알림은 [itemName], [label], [nextReplacementDate], [itemId]가 모두 null이다.
 */
@Serializable
data class NotificationCardResponse(
    @SerialName("entryId") val entryId: Long,
    @SerialName("type") val type: String,
    @SerialName("title") val title: String,
    @SerialName("content") val content: String? = null,
    @SerialName("itemName") val itemName: String? = null,
    @SerialName("label") val label: String? = null,
    @SerialName("hasLowStock") val hasLowStock: Boolean = false,
    @SerialName("nextReplacementDate") val nextReplacementDate: String? = null,
    @SerialName("itemId") val itemId: Long? = null,
    @SerialName("deepLink") val deepLink: String,
)
