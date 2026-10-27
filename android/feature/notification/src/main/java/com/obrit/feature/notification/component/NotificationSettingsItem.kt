package com.obrit.feature.notification.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.component.toggle.OBRitToggle
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
internal fun NotificationSettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S4.dp),
    ) {
        Text(
            text = title,
            style = typography.lg.copy(fontWeight = FontWeight.Medium),
            color = colors.gray300,
        )
        content()
    }
}

@Composable
internal fun NotificationSettingsCard(
    verticalPadding: Dp,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AtomRadius.ExtraLarge.dp))
                .background(LocalOBRitColor.current.gray850)
                .padding(horizontal = AtomSpacing.S4.dp, vertical = verticalPadding),
        content = content,
    )
}

/** 토글 자체(44x24)는 권장 터치 영역보다 작아서 행 전체가 조작을 받는다. */
@Composable
internal fun NotificationSettingsToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .toggleable(
                    value = checked,
                    role = Role.Switch,
                    onValueChange = onCheckedChange,
                ).padding(vertical = AtomSpacing.S3.dp),
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NotificationSettingsRowLabel(title = title, description = description)
        OBRitToggle(checked = checked, onCheckedChange = null)
    }
}

/**
 * 왼쪽에 제목·설명, 오른쪽에 현재 값과 화살표를 두는 행. 누르면 값을 고르는 바텀시트가 올라온다.
 *
 * [enabled]가 false면 값과 화살표가 흐려지고 탭도 받지 않는다.
 */
@Composable
@Suppress("LongParameterList")
internal fun NotificationSettingsValueRow(
    title: String,
    description: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val valueColor = if (enabled) colors.common00 else colors.gray700

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(enabled = enabled, onClick = onClick)
                .padding(top = AtomSpacing.S3.dp, bottom = AtomSpacing.S3.dp, end = AtomSpacing.S1.dp),
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NotificationSettingsRowLabel(title = title, description = description)
        Text(
            text = value,
            style = typography.base.copy(fontWeight = FontWeight.Medium),
            color = valueColor,
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = if (enabled) colors.gray300 else colors.gray700,
            modifier =
                Modifier
                    .padding(start = AtomSpacing.S1.dp)
                    .size(AtomSpacing.S5.dp),
        )
    }
}

/** 토글 행과 값 행이 같은 제목·설명 묶음을 쓴다. */
@Composable
private fun RowScope.NotificationSettingsRowLabel(
    title: String,
    description: String,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S0_5.dp),
    ) {
        Text(
            text = title,
            style = typography.xl.copy(fontWeight = FontWeight.SemiBold),
            color = colors.common00,
        )
        Text(
            text = description,
            style = typography.xs.copy(fontWeight = FontWeight.Medium),
            color = colors.gray500,
        )
    }
}

/** Figma는 행 아래 1px 경계선을 긋고 다음 행과 2px 띄운다. */
@Composable
internal fun NotificationSettingsDivider(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .padding(bottom = AtomSpacing.S0_5.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(LocalOBRitColor.current.gray750),
    )
}
