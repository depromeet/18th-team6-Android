package com.obrit.feature.notification.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.component.badge.BadgeType
import com.obrit.android.core.designsystem.component.badge.OBRitBadge
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
@Suppress("LongMethod", "LongParameterList")
internal fun NotificationCard(
    badge: String,
    timeText: String,
    title: String,
    subtitle: String?,
    isUnread: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(NotificationCardShape)
                .background(NotificationCardBackground)
                .border(
                    width = NotificationCardBorderWidth,
                    color = NotificationCardBorder,
                    shape = NotificationCardShape,
                ).clickable(onClick = onClick)
                .padding(
                    horizontal = AtomSpacing.S4.dp,
                    vertical = NotificationCardVerticalPadding,
                ),
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OBRitBadge(text = badge, type = BadgeType.Red800Filled)

            Row(
                horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S1.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = timeText,
                    style = typography.xs,
                    color = colors.common00.copy(alpha = NOTIFICATION_TIME_ALPHA),
                )
                if (isUnread) {
                    Box(
                        modifier =
                            Modifier
                                .size(NotificationUnreadDotSize)
                                .clip(CircleShape)
                                .background(colors.red300),
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S2_5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(NotificationThumbnailSize)
                            .clip(CircleShape)
                            .background(colors.gray750),
                )
                Text(
                    text = title,
                    style = typography.xl.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.common00,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = typography.base,
                    color = colors.gray500,
                )
            }
        }
    }
}

// 아래 두 색은 디자인 토큰(AtomColors)에 없다. Figma 알림 카드 스펙에 맞추기 위해 임시로 추가한 값이다.
// 디자인시스템에 정식 토큰이 등록되면 그것으로 교체할 것.
private val NotificationCardBackground = Color(0xFF2C2B2D)
private val NotificationCardBorder = Color(0x804E565E)

private val NotificationCardShape = RoundedCornerShape(AtomRadius.ExtraLarge.dp)
private val NotificationCardBorderWidth = 1.dp
private val NotificationCardVerticalPadding = 14.dp
private val NotificationThumbnailSize = AtomSpacing.S8.dp
private val NotificationUnreadDotSize = AtomSpacing.S1.dp
private const val NOTIFICATION_TIME_ALPHA = 0.3f

@Preview(name = "NotificationCard", showBackground = true)
@Composable
private fun NotificationCardPreview() {
    OBRitTheme(dynamicColor = false) {
        NotificationCard(
            badge = "교체 D+3",
            timeText = "2시간전",
            title = "칫솔 교체 시기가 지난 지 3일 째예요",
            subtitle = "다음 예상 교체일 : 5월 24일",
            isUnread = true,
            onClick = {},
        )
    }
}

@Preview(name = "NotificationCard without subtitle", showBackground = true)
@Composable
private fun NotificationCardWithoutSubtitlePreview() {
    OBRitTheme(dynamicColor = false) {
        NotificationCard(
            badge = "여분 경고",
            timeText = "3일 전",
            title = "면도기 여분이 1개 남았어요",
            subtitle = null,
            isUnread = false,
            onClick = {},
        )
    }
}
