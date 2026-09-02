package com.obrit.feature.notification.screen

import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun NotificationScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var isNotificationEnabled by remember {
        mutableStateOf(NotificationManagerCompat.from(context).areNotificationsEnabled())
    }

    // 사용자가 시스템 알림 설정에서 알림을 켜고 돌아오면 안내 배너가 사라져야 한다.
    NotificationResumeEffect {
        isNotificationEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    NotificationScreenContent(
        groups = remember { notificationDummyGroups() },
        isNotificationEnabled = isNotificationEnabled,
        action =
            NotificationScreenAction(
                onBackClick = onBackClick,
                onPermissionBannerClick = {
                    context.startActivity(appNotificationSettingsIntent(context.packageName))
                },
                onItemClick = {},
            ),
        modifier = modifier,
    )
}

internal data class NotificationScreenAction(
    val onBackClick: () -> Unit,
    val onPermissionBannerClick: () -> Unit,
    val onItemClick: (NotificationItem) -> Unit,
)

@Composable
private fun NotificationResumeEffect(onResume: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnResume by rememberUpdatedState(onResume)

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    currentOnResume()
                }
            }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

private fun appNotificationSettingsIntent(packageName: String): Intent =
    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)

// TODO(#101): 서버 알림 목록 API가 나오면 이 더미 전체를 삭제하고 ViewModel + Repository 호출로 교체한다.
@Suppress("LongMethod")
private fun notificationDummyGroups(): List<NotificationGroup> =
    listOf(
        NotificationGroup(
            header = "오늘",
            items =
                listOf(
                    NotificationItem(
                        id = 1L,
                        badge = "교체 D+3",
                        timeText = "2시간전",
                        title = "칫솔 교체 시기가 지난 지 3일 째예요",
                        subtitle = "다음 예상 교체일 : 5월 24일",
                        isUnread = true,
                    ),
                    NotificationItem(
                        id = 2L,
                        badge = "교체 D+3",
                        timeText = "2시간전",
                        title = "수건 교체 시기가 지난 지 3일 째예요",
                        subtitle = "다음 예상 교체일 : 5월 24일",
                        isUnread = true,
                    ),
                ),
        ),
        NotificationGroup(
            header = "어제",
            items =
                listOf(
                    NotificationItem(
                        id = 3L,
                        badge = "여분 부족",
                        timeText = "1일 전",
                        title = "샴푸 여분이 모두 떨어졌어요",
                        subtitle = "남은 여분 : 0개",
                        isUnread = true,
                    ),
                ),
        ),
        NotificationGroup(
            header = "3일전",
            items =
                listOf(
                    NotificationItem(
                        id = 4L,
                        badge = "여분 경고",
                        timeText = "3일 전",
                        title = "면도기 여분이 1개 남았어요",
                        subtitle = null,
                        isUnread = true,
                    ),
                ),
        ),
    )
