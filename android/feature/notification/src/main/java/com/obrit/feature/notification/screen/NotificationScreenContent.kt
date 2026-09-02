package com.obrit.feature.notification.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.component.topbar.OBRitDepthTopBar
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.notification.component.NotificationCard
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

internal data class NotificationGroup(
    val header: String,
    val items: List<NotificationItem>,
)

internal data class NotificationItem(
    val id: Long,
    val badge: String,
    val timeText: String,
    val title: String,
    val subtitle: String?,
    val isUnread: Boolean,
)

@Composable
@Suppress("LongMethod")
internal fun NotificationScreenContent(
    groups: List<NotificationGroup>,
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
        )

        LazyColumn(
            modifier =
                Modifier
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
                    NotificationPermissionBanner(onClick = action.onPermissionBannerClick)
                }
            }

            groups.forEachIndexed { groupIndex, group ->
                item(key = group.header) {
                    NotificationSection(
                        group = group,
                        isLatest = groupIndex == 0,
                        onItemClick = action.onItemClick,
                    )
                }
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
private fun NotificationPermissionBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AtomRadius.Small.dp))
                .background(colors.green850)
                .clickable(onClick = onClick)
                .padding(
                    horizontal = AtomSpacing.S3.dp,
                    vertical = AtomSpacing.S2.dp,
                ),
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S1_5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_topbar_bell),
            contentDescription = null,
            tint = colors.green300,
            modifier = Modifier.size(NotificationBannerIconSize),
        )
        Text(
            text = NOTIFICATION_PERMISSION_BANNER_TEXT,
            style = typography.s.copy(fontWeight = FontWeight.SemiBold),
            color = colors.green300,
        )
    }
}

private val NotificationBannerIconSize = AtomSpacing.S5.dp

private const val NOTIFICATION_TITLE = "알림"
private const val NOTIFICATION_PERMISSION_BANNER_TEXT = "알림받기를 설정하고 유용한 알림들을 받아보세요!"

@Preview(name = "NotificationScreenContent", showBackground = true)
@Composable
@Suppress("LongMethod")
private fun NotificationScreenContentPreview() {
    OBRitTheme(dynamicColor = false) {
        NotificationScreenContent(
            groups =
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
            isNotificationEnabled = false,
            action =
                NotificationScreenAction(
                    onBackClick = {},
                    onPermissionBannerClick = {},
                    onItemClick = {},
                ),
        )
    }
}
