package com.obrit.feature.notification.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.component.topbar.OBRitDepthTopBar
import com.obrit.android.core.designsystem.component.topbar.OBRitTopBarAction
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.notification.component.NotificationCard
import com.obrit.feature.notification.component.NotificationLoadFailed
import com.obrit.feature.notification.component.NotificationLoading
import com.obrit.feature.notification.component.NotificationPermissionBanner
import com.obrit.feature.notification.viewmodel.NotificationGroup
import com.obrit.feature.notification.viewmodel.NotificationItem
import com.obrit.feature.notification.viewmodel.NotificationUiState
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
internal fun NotificationScreenContent(
    state: NotificationUiState,
    isNotificationEnabled: Boolean,
    action: NotificationScreenAction,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.gray900)
                .statusBarsPadding(),
    ) {
        OBRitDepthTopBar(
            title = NOTIFICATION_TITLE,
            onBackClick = action.onBackClick,
            trailingAction =
                OBRitTopBarAction(
                    iconRes = R.drawable.ic_gear,
                    contentDescription = NOTIFICATION_SETTINGS_DESCRIPTION,
                    onClick = action.onSettingsClick,
                ),
        )

        when (state) {
            is NotificationUiState.Loading -> NotificationLoading()
            is NotificationUiState.LoadFailed -> NotificationLoadFailed(onRetryClick = action.onRetryClick)
            is NotificationUiState.Success ->
                NotificationList(
                    groups = state.groups,
                    isNotificationEnabled = isNotificationEnabled,
                    onPermissionBannerClick = action.onPermissionBannerClick,
                    onItemClick = action.onItemClick,
                )
        }
    }
}

@Composable
private fun NotificationList(
    groups: List<NotificationGroup>,
    isNotificationEnabled: Boolean,
    onPermissionBannerClick: () -> Unit,
    onItemClick: (NotificationItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .navigationBarsPadding(),
        contentPadding =
            PaddingValues(
                start = AtomSpacing.S5.dp,
                end = AtomSpacing.S5.dp,
                top = AtomSpacing.S2_5.dp,
                bottom = AtomSpacing.S5.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S5.dp),
    ) {
        if (!isNotificationEnabled) {
            item {
                NotificationPermissionBanner(onClick = onPermissionBannerClick)
            }
        }

        if (groups.isEmpty()) {
            item {
                NotificationEmpty()
            }
        }

        groups.forEachIndexed { groupIndex, group ->
            item(key = group.header) {
                NotificationSection(
                    group = group,
                    isLatest = groupIndex == 0,
                    onItemClick = onItemClick,
                )
            }
        }
    }
}

@Composable
private fun NotificationSection(
    group: NotificationGroup,
    isLatest: Boolean,
    onItemClick: (NotificationItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S4.dp),
    ) {
        Text(
            text = group.header,
            style = typography.xl.copy(fontWeight = FontWeight.Bold),
            color = if (isLatest) colors.green300 else colors.gray450,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2_5.dp)) {
            group.items.forEach { item ->
                NotificationCard(
                    badge = item.badge,
                    timeText = item.timeText,
                    title = item.title,
                    subtitle = item.subtitle,
                    isUnread = item.isUnread,
                    onClick = { onItemClick(item) },
                )
            }
        }
    }
}

@Composable
private fun NotificationEmpty(modifier: Modifier = Modifier) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Text(
        text = NOTIFICATION_EMPTY_TEXT,
        style = typography.base,
        color = colors.gray500,
        textAlign = TextAlign.Center,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = AtomSpacing.S10.dp),
    )
}

private const val NOTIFICATION_TITLE = "알림"
private const val NOTIFICATION_SETTINGS_DESCRIPTION = "알림 설정"
private const val NOTIFICATION_EMPTY_TEXT = "아직 도착한 알림이 없어요"

@Preview(name = "NotificationScreenContent", showBackground = true)
@Composable
@Suppress("LongMethod")
private fun NotificationScreenContentPreview() {
    OBRitTheme(dynamicColor = false) {
        NotificationScreenContent(
            state =
                NotificationUiState.Success(
                    groups =
                        listOf(
                            NotificationGroup(
                                header = "오늘",
                                items =
                                    listOf(
                                        NotificationItem(
                                            id = 1L,
                                            badge = null,
                                            timeText = "2시간 전",
                                            title = "칫솔 교체 시기가 지난 지 3일 째예요",
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
                                            id = 2L,
                                            badge = "여분 부족",
                                            timeText = "1일 전",
                                            title = "샴푸 여분이 모두 떨어졌어요",
                                            subtitle = "남은 여분 : 0개",
                                            isUnread = false,
                                        ),
                                    ),
                            ),
                        ),
                ),
            isNotificationEnabled = false,
            action =
                NotificationScreenAction(
                    onBackClick = {},
                    onSettingsClick = {},
                    onRetryClick = {},
                    onPermissionBannerClick = {},
                    onItemClick = {},
                ),
        )
    }
}
