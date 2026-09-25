package com.obrit.feature.notification.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
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
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
                .padding(vertical = AtomSpacing.S3.dp),
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
        OBRitToggle(checked = checked, onCheckedChange = null)
    }
}

@Composable
internal fun NotificationSettingsValueRow(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = AtomSpacing.S3.dp, bottom = AtomSpacing.S3.dp, end = AtomSpacing.S1.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = typography.xl.copy(fontWeight = FontWeight.SemiBold),
            color = colors.common00,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = typography.base.copy(fontWeight = FontWeight.Medium),
            color = colors.gray300,
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = colors.gray300,
            modifier =
                Modifier
                    .padding(start = AtomSpacing.S1.dp)
                    .size(AtomSpacing.S5.dp),
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
