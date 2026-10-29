package com.obrit.feature.notification.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

/**
 * OS 알림 권한이 꺼져 있을 때 보여주는 안내. 누르면 OS 알림 설정으로 보낸다.
 *
 * 알림 목록과 알림 설정 두 화면이 같이 쓴다. 설정 화면에서는 오른쪽에 화살표가 붙는다(Figma 2853:9424).
 */
@Composable
internal fun NotificationPermissionBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showChevron: Boolean = false,
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
            painter = painterResource(id = R.drawable.ic_bell_20),
            contentDescription = null,
            tint = colors.green300,
            modifier = Modifier.size(NotificationBannerIconSize),
        )
        Text(
            text = NOTIFICATION_PERMISSION_BANNER_TEXT,
            style = typography.s.copy(fontWeight = FontWeight.SemiBold),
            color = colors.green300,
            modifier = Modifier.weight(1f),
        )
        if (showChevron) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right_20),
                contentDescription = null,
                tint = colors.green300,
                modifier = Modifier.size(NotificationBannerIconSize),
            )
        }
    }
}

private val NotificationBannerIconSize = AtomSpacing.S5.dp

private const val NOTIFICATION_PERMISSION_BANNER_TEXT = "알림받기를 설정하고 유용한 알림들을 받아보세요!"
