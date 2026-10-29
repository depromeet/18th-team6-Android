package com.obrit.obrit.shared.model.notifications

/**
 * 알림함에 보여줄 카드 한 장. 묶음 알림은 카드 여러 장으로 펼쳐진다.
 *
 * [notificationId]는 카드가 속한 알림의 ID다. 읽음 처리는 카드가 아니라 이 단위로 한다.
 * 묶음 안의 카드 하나를 읽으면 같은 알림의 카드가 모두 읽음이 된다.
 * [entryId]는 카드 고유 ID이고 카드 저장 이전의 옛 알림은 null이다.
 *
 * [createdAt]은 서버가 주는 ISO-8601 지역 시각 문자열이고
 * [nextReplacementDate]는 `2026-10-28` 형식의 날짜다. 표시용 변환은 각 플랫폼이 한다.
 */
data class Notification(
    val notificationId: Long,
    val entryId: Long?,
    val title: String,
    val label: String?,
    val nextReplacementDate: String?,
    /** 발송 당시 여분이 없었는지. 여분 부족 알림은 수량이 0일 때만 발송되므로 참이면 수량은 0이다. */
    val hasLowStock: Boolean,
    val itemId: Long?,
    val deepLink: String,
    val isRead: Boolean,
    val createdAt: String,
)
