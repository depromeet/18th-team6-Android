package com.obrit.feature.notification.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.obrit.shared.data.repository.NotificationRepository
import com.obrit.obrit.shared.model.notifications.NotificationSettings
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * 설정 화면이 켜고 끄는 알림 유형. Figma `알림설정`(2996:9732) 기준이다.
 *
 * 서버의 `preReplacementEnabled`, `overdueEnabled`, `lowStockEnabled`와 1:1로 대응한다.
 */
enum class NotificationSettingType(
    val title: String,
    val description: String,
) {
    ReplacementUpcoming("교체 예정 알림", "교체 예정일이 다가오면 미리 알려드려요"),
    ReplacementOverdue("교체 지연 알림", "교체 예정일이 지나면 다시 알려드려요"),
    SpareShortage("여분 부족 알림", "여분이 없고 교체 예정일이 가까운 항목을 알려드려요"),
}

sealed interface NotificationSettingsUiState {
    data object Loading : NotificationSettingsUiState

    data object LoadFailed : NotificationSettingsUiState

    /** 세 유형 모두 기본 꺼짐이고 기본 시각은 오전 9시다(명세서 3장). */
    @Immutable
    data class Success(
        val enabledTypes: Set<NotificationSettingType>,
        val sendTime: LocalTime,
    ) : NotificationSettingsUiState
}

sealed interface NotificationSettingsSideEffect {
    /** 저장에 실패하면 서버 값으로 되돌린 뒤 알린다(명세서 12.1). */
    data object SaveFailed : NotificationSettingsSideEffect
}

class NotificationSettingsViewModel internal constructor(
    private val notificationRepository: NotificationRepository,
) : BaseContainerHost<NotificationSettingsUiState, NotificationSettingsSideEffect>() {
    override val container =
        container<NotificationSettingsUiState, NotificationSettingsSideEffect>(
            NotificationSettingsUiState.Loading,
        ) {
            loadSettings()
        }

    fun retryLoad() =
        intent {
            reduce { NotificationSettingsUiState.Loading }
            loadSettings()
        }

    fun onTypeChange(
        type: NotificationSettingType,
        isEnabled: Boolean,
    ) = save { current ->
        current.copy(
            enabledTypes = if (isEnabled) current.enabledTypes + type else current.enabledTypes - type,
        )
    }

    fun onSendTimeChange(sendTime: LocalTime) = save { current -> current.copy(sendTime = sendTime) }

    /**
     * 바뀐 값을 화면에 먼저 반영하고 서버에 저장한다. 토글은 바로 반응해야 하기 때문이다.
     * 저장에 실패하면 서버가 돌려준 값으로 되돌리고 side effect로 알린다(명세서 12.1).
     */
    private fun save(transform: (NotificationSettingsUiState.Success) -> NotificationSettingsUiState.Success) =
        intent {
            val current = state as? NotificationSettingsUiState.Success ?: return@intent
            val next = transform(current)
            reduce { next }

            notificationRepository
                .updateSettings(next.toSettings())
                .onSuccess { saved -> reduce { saved.toSuccess() } }
                .onFailure {
                    reduce { current }
                    postSideEffect(NotificationSettingsSideEffect.SaveFailed)
                }
        }

    private suspend fun Syntax<NotificationSettingsUiState, NotificationSettingsSideEffect>.loadSettings() {
        notificationRepository
            .getSettings()
            .onSuccess { settings -> reduce { settings.toSuccess() } }
            .onFailure { reduce { NotificationSettingsUiState.LoadFailed } }
    }
}

private fun NotificationSettings.toSuccess(): NotificationSettingsUiState.Success =
    NotificationSettingsUiState.Success(
        enabledTypes =
            buildSet {
                if (isReplacementUpcomingEnabled) add(NotificationSettingType.ReplacementUpcoming)
                if (isReplacementOverdueEnabled) add(NotificationSettingType.ReplacementOverdue)
                if (isSpareShortageEnabled) add(NotificationSettingType.SpareShortage)
            },
        sendTime = sendTime.toLocalTimeOrDefault(),
    )

private fun NotificationSettingsUiState.Success.toSettings(): NotificationSettings =
    NotificationSettings(
        isReplacementUpcomingEnabled = NotificationSettingType.ReplacementUpcoming in enabledTypes,
        isReplacementOverdueEnabled = NotificationSettingType.ReplacementOverdue in enabledTypes,
        isSpareShortageEnabled = NotificationSettingType.SpareShortage in enabledTypes,
        sendTime = sendTime.format(SendTimeApiFormatter),
    )

/** 서버는 `09:00` 형식으로 주고받는다. 값이 깨져도 화면이 죽지 않도록 기본 시각으로 떨어뜨린다. */
private fun String.toLocalTimeOrDefault(): LocalTime =
    runCatching { LocalTime.parse(this, SendTimeApiFormatter) }.getOrDefault(DefaultSendTime)

private val SendTimeApiFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private const val DEFAULT_SEND_HOUR = 9
private val DefaultSendTime: LocalTime = LocalTime.of(DEFAULT_SEND_HOUR, 0)
