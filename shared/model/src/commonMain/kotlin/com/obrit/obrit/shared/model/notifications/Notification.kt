package com.obrit.obrit.shared.model.notifications

/**
 * 알림함에 쌓이는 알림 한 건.
 *
 * [createdAt]은 서버가 주는 ISO-8601 지역 시각 문자열이다(예: `2026-09-07T16:26:51.904144`).
 * 이 프로젝트의 다른 날짜 모델과 같이 문자열로 두고, 표시용 변환은 각 플랫폼 presentation 계층에서 한다.
 */
data class Notification(
    val id: Long,
    val title: String,
    val content: String?,
    val isRead: Boolean,
    val createdAt: String,
)
