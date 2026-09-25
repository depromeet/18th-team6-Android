package com.obrit.feature.notification.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.obrit.shared.data.repository.NotificationRepository
import com.obrit.obrit.shared.model.notifications.Notification
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

sealed interface NotificationUiState {
    data object Loading : NotificationUiState

    data object LoadFailed : NotificationUiState

    @Immutable
    data class Success(
        val groups: List<NotificationGroup>,
    ) : NotificationUiState
}

/** 이 화면은 일회성 이벤트가 없다. 뒤로가기는 navigation이 직접 처리하고, 알림 탭은 상태만 바꾼다. */
sealed interface NotificationSideEffect

@Immutable
data class NotificationGroup(
    val header: String,
    val items: List<NotificationItem>,
)

@Immutable
data class NotificationItem(
    val id: Long,
    // 서버가 아직 알림 유형과 D-day를 주지 않는다. 값이 없으면 카드에서 뱃지 자리를 그리지 않는다.
    val badge: String?,
    val timeText: String,
    val title: String,
    val subtitle: String?,
    val isUnread: Boolean,
)

class NotificationViewModel internal constructor(
    private val notificationRepository: NotificationRepository,
) : BaseContainerHost<NotificationUiState, NotificationSideEffect>() {
    override val container =
        container<NotificationUiState, NotificationSideEffect>(NotificationUiState.Loading) {
            loadNotifications()
        }

    fun retryLoad() =
        intent {
            reduce { NotificationUiState.Loading }
            loadNotifications()
        }

    /**
     * 읽음 표시를 서버 응답보다 먼저 반영한다. 실패해도 되돌리지 않는다.
     * 화면을 다시 열면 서버 값으로 목록을 새로 받으므로 어긋난 상태는 그때 스스로 정리된다.
     */
    fun onItemClick(item: NotificationItem) =
        intent {
            if (!item.isUnread) {
                return@intent
            }

            reduceOn<NotificationUiState.Success> { state.withItemRead(item.id) }
            notificationRepository.markAsRead(item.id)
        }

    private suspend fun Syntax<NotificationUiState, NotificationSideEffect>.loadNotifications() {
        notificationRepository
            .getNotifications()
            .onSuccess { notifications ->
                reduce { NotificationUiState.Success(notifications.toGroups(LocalDateTime.now())) }
            }.onFailure {
                reduce { NotificationUiState.LoadFailed }
            }
    }
}

private fun NotificationUiState.Success.withItemRead(id: Long): NotificationUiState.Success =
    copy(
        groups =
            groups.map { group ->
                group.copy(
                    items =
                        group.items.map { item ->
                            if (item.id == id) item.copy(isUnread = false) else item
                        },
                )
            },
    )

internal fun List<Notification>.toGroups(now: LocalDateTime): List<NotificationGroup> =
    map { notification -> notification to notification.createdAt.toLocalDateTimeOrNull() }
        // createdAt은 ISO-8601이라 문자열 정렬만으로 최신순이 된다. 파싱에 실패한 값은 맨 뒤로 보낸다.
        .sortedByDescending { (notification, _) -> notification.createdAt }
        .groupBy { (_, createdAt) -> createdAt?.daysAgoFrom(now) }
        .map { (daysAgo, entries) ->
            NotificationGroup(
                header = daysAgo.toGroupHeader(),
                items =
                    entries.map { (notification, createdAt) ->
                        notification.toItem(createdAt.toTimeText(now))
                    },
            )
        }

private fun Notification.toItem(timeText: String): NotificationItem =
    NotificationItem(
        id = id,
        badge = null,
        timeText = timeText,
        title = title,
        subtitle = content,
        isUnread = !isRead,
    )

private fun String.toLocalDateTimeOrNull(): LocalDateTime? = runCatching { LocalDateTime.parse(this) }.getOrNull()

private fun LocalDateTime.daysAgoFrom(now: LocalDateTime): Long = ChronoUnit.DAYS.between(toLocalDate(), now.toLocalDate())

private fun Long?.toGroupHeader(): String =
    when (this) {
        null -> UNKNOWN_DATE_HEADER
        0L -> "오늘"
        1L -> "어제"
        else -> "${this}일 전"
    }

private fun LocalDateTime?.toTimeText(now: LocalDateTime): String {
    val createdAt = this ?: return ""
    val minutes = ChronoUnit.MINUTES.between(createdAt, now).coerceAtLeast(0)

    return when {
        minutes < 1L -> "방금 전"
        minutes < MINUTES_PER_HOUR -> "${minutes}분 전"
        minutes < MINUTES_PER_DAY -> "${minutes / MINUTES_PER_HOUR}시간 전"
        else -> "${minutes / MINUTES_PER_DAY}일 전"
    }
}

private const val UNKNOWN_DATE_HEADER = "이전"
private const val MINUTES_PER_HOUR = 60L
private const val MINUTES_PER_DAY = 1_440L
