package com.obrit.obrit.shared.network.response.notification

import com.obrit.obrit.shared.model.notifications.Notification
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 알림 한 건. 묶음이면 한 발송 배치 전체를 뜻하고 [cards]에 소모품별 카드가 들어온다.
 *
 * 묶음일 때 [label], [nextReplacementDate], [itemId]는 null이고 [deepLink]는 홈을 가리킨다.
 * 소모품별 값은 [cards] 안에만 있다.
 */
@Serializable
data class NotificationResponse(
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String,
    @SerialName("content") val content: String? = null,
    @SerialName("isRead") val isRead: Boolean = false,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("label") val label: String? = null,
    @SerialName("nextReplacementDate") val nextReplacementDate: String? = null,
    @SerialName("itemId") val itemId: Long? = null,
    @SerialName("deepLink") val deepLink: String,
    @SerialName("isBundled") val isBundled: Boolean = false,
    @SerialName("cards") val cards: List<NotificationCardResponse> = emptyList(),
)

/**
 * 알림함은 묶음 여부와 무관하게 소모품별 낱개 카드로 보여준다. 묶음 한 건이 카드 여러 장이 된다.
 *
 * 카드를 저장하기 전에 발송된 옛 알림은 [NotificationResponse.cards]가 비어 있어서
 * 바깥 값으로 카드 한 장을 만든다. 그렇게 하지 않으면 옛 알림이 목록에서 사라진다.
 */
fun NotificationResponse.toNotifications(): List<Notification> =
    if (cards.isEmpty()) {
        listOf(
            Notification(
                notificationId = id,
                entryId = null,
                title = content ?: title,
                label = label,
                nextReplacementDate = nextReplacementDate,
                // 카드 저장 이전의 옛 알림에는 여분 정보가 없다.
                hasLowStock = false,
                itemId = itemId,
                deepLink = deepLink,
                isRead = isRead,
                createdAt = createdAt,
            ),
        )
    } else {
        cards.map { card ->
            Notification(
                notificationId = id,
                entryId = card.entryId,
                title = card.content ?: card.title,
                label = card.label,
                nextReplacementDate = card.nextReplacementDate,
                hasLowStock = card.hasLowStock,
                itemId = card.itemId,
                deepLink = card.deepLink,
                isRead = isRead,
                createdAt = createdAt,
            )
        }
    }
