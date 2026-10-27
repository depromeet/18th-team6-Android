package com.obrit.obrit.shared.network.response.notification

import com.obrit.obrit.shared.model.notifications.NotificationSettings
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 서버는 `enabled`와 `leadDays`도 함께 주지만 둘 다 deprecated인 구버전 앱 호환용 고정값이라 받지 않는다.
 * `permissionStatus`는 기기 등록 상태를 보여줄 화면이 아직 없어 함께 생략한다.
 */
@Serializable
data class NotificationSettingsResponse(
    @SerialName("preReplacementEnabled") val preReplacementEnabled: Boolean,
    @SerialName("overdueEnabled") val overdueEnabled: Boolean,
    @SerialName("lowStockEnabled") val lowStockEnabled: Boolean,
    @SerialName("dispatchTime") val dispatchTime: String,
)

fun NotificationSettingsResponse.toNotificationSettings() =
    NotificationSettings(
        isReplacementUpcomingEnabled = preReplacementEnabled,
        isReplacementOverdueEnabled = overdueEnabled,
        isSpareShortageEnabled = lowStockEnabled,
        sendTime = dispatchTime,
    )
