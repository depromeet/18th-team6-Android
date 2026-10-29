package com.obrit.feature.notification.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.obrit.shared.data.repository.NotificationRepository
import com.obrit.obrit.shared.model.notifications.Notification
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

sealed interface NotificationUiState {
    data object Loading : NotificationUiState

    data object LoadFailed : NotificationUiState

    @Immutable
    data class Success(
        val groups: List<NotificationGroup>,
    ) : NotificationUiState
}

sealed interface NotificationSideEffect {
    /** 서버가 읽음 처리 응답으로 준 이동 경로. 앱이 직접 만들지 않는다(명세서 8.2). */
    data class OpenDeepLink(
        val deepLink: String,
    ) : NotificationSideEffect
}

@Immutable
data class NotificationGroup(
    val header: String,
    val items: List<NotificationItem>,
)

@Immutable
data class NotificationItem(
    /** 카드가 속한 알림의 ID. 읽음 처리는 카드가 아니라 이 단위로 한다. */
    val notificationId: Long,
    /** 카드 고유 ID. 카드 저장 이전의 옛 알림은 null이다. */
    val entryId: Long?,
    // 공지와 옛 알림은 라벨이 없다. 값이 없으면 카드에서 뱃지 자리를 그리지 않는다.
    val badge: String?,
    val timeText: String,
    val title: String,
    val subtitle: String?,
    val isUnread: Boolean,
    val deepLink: String,
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
     * 읽음 표시와 이동을 서버 응답보다 먼저 반영한다. 실패해도 되돌리지 않는다.
     * 화면을 다시 열면 서버 값으로 목록을 새로 받으므로 어긋난 상태는 그때 스스로 정리된다.
     *
     * 이동은 카드가 들고 있는 딥링크를 쓴다. 묶음 알림의 읽음 처리 응답은 홈을 가리켜서
     * 그걸 쓰면 어느 카드를 눌러도 홈으로 가버린다.
     */
    fun onItemClick(item: NotificationItem) =
        intent {
            reduceOn<NotificationUiState.Success> { state.withItemRead(item.notificationId) }
            postSideEffect(NotificationSideEffect.OpenDeepLink(item.deepLink))

            notificationRepository.markAsRead(item.notificationId)
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

/** 읽음은 알림 단위라 묶음 안의 카드 하나를 눌러도 같은 알림의 카드가 모두 읽음이 된다. */
private fun NotificationUiState.Success.withItemRead(notificationId: Long): NotificationUiState.Success =
    copy(
        groups =
            groups.map { group ->
                group.copy(
                    items =
                        group.items.map { item ->
                            if (item.notificationId == notificationId) item.copy(isUnread = false) else item
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
        notificationId = notificationId,
        entryId = entryId,
        badge = label,
        timeText = timeText,
        title = title,
        subtitle = toSubtitle(),
        isUnread = !isRead,
        deepLink = deepLink,
    )

/**
 * 여분이 없을 때는 남은 수량을, 그 외에는 다음 교체 예상일을 보여준다.
 *
 * 여분 부족 알림은 수량이 명시적으로 0일 때만 발송되므로(명세서 5.5) 수량을 따로 받지 않아도 0으로 단정할 수 있다.
 */
private fun Notification.toSubtitle(): String? =
    if (hasLowStock) {
        NO_SPARE_SUBTITLE
    } else {
        nextReplacementDate?.toNextReplacementSubtitleOrNull()
    }

/** 서버는 `2026-10-28` 형식으로 준다. 디자인은 `다음 예상 교체일 : 10월 28일`로 보여준다. */
private fun String.toNextReplacementSubtitleOrNull(): String? =
    runCatching { LocalDate.parse(this).format(NextReplacementDateFormatter) }
        .getOrNull()
        ?.let { date -> "다음 예상 교체일 : $date" }

private const val NO_SPARE_SUBTITLE = "남은 여분 : 0개"

private val NextReplacementDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M월 d일", Locale.KOREAN)

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
