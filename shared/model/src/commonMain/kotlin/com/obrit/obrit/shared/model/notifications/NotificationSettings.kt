package com.obrit.obrit.shared.model.notifications

/**
 * 사용자가 고르는 알림 설정. 알림 기능 명세서 3장의 전역 설정과 같다.
 *
 * 세 유형은 서로 독립이고 [sendTime]은 세 유형이 함께 쓰는 공통 발송 시각이다.
 * 사용자가 고르는 일수는 없다. 사전·지연 일수는 서버가 정한다.
 *
 * [sendTime]은 `HH:mm` 형식의 30분 단위 문자열이다(예: `09:00`).
 * 이 프로젝트의 다른 날짜·시각 모델과 같이 문자열로 두고 표시용 변환은 각 플랫폼이 한다.
 */
data class NotificationSettings(
    val isReplacementUpcomingEnabled: Boolean,
    val isReplacementOverdueEnabled: Boolean,
    val isSpareShortageEnabled: Boolean,
    val sendTime: String,
)
