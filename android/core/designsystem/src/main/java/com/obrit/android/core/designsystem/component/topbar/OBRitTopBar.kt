package com.obrit.android.core.designsystem.component.topbar

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
fun OBRitHomeTopBar(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopBarRoot(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.ic_topbar_obrit_logo),
            contentDescription = OBRIT_TOP_BAR_LOGO_DESCRIPTION,
            contentScale = ContentScale.Fit,
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = OBRitTopBarLogoStartPadding)
                    .height(OBRitTopBarLogoHeight),
        )
        Row(
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = OBRitTopBarSidePadding),
        ) {
            TopBarIconButton(
                iconRes = R.drawable.ic_topbar_search,
                onClick = onSearchClick,
                contentDescription = OBRIT_TOP_BAR_SEARCH_DESCRIPTION,
            )
            TopBarIconButton(
                iconRes = R.drawable.ic_topbar_bell,
                onClick = onNotificationClick,
                contentDescription = OBRIT_TOP_BAR_NOTIFICATION_DESCRIPTION,
            )
            TopBarIconButton(
                iconRes = R.drawable.ic_topbar_person,
                onClick = onProfileClick,
                contentDescription = OBRIT_TOP_BAR_PROFILE_DESCRIPTION,
            )
        }
    }
}

@Composable
private fun TopBarRoot(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = LocalOBRitColor.current
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(OBRitTopBarHeight)
                .background(colors.gray900),
        content = content,
    )
}

@Composable
private fun TopBarIconButton(
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    Box(
        modifier =
            modifier
                .size(OBRitTopBarIconButtonSize)
                .clip(RoundedCornerShape(AtomRadius.ExtraLarge.dp))
                .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = colors.common00,
            modifier = Modifier.size(OBRitTopBarIconSize),
        )
    }
}

private val OBRitTopBarHeight = AtomSpacing.S14.dp
private val OBRitTopBarIconButtonSize = AtomSpacing.S10.dp
private val OBRitTopBarIconSize = AtomSpacing.S6.dp
private val OBRitTopBarSidePadding = AtomSpacing.S3.dp
private val OBRitTopBarLogoStartPadding = AtomSpacing.S5.dp
private val OBRitTopBarLogoHeight = AtomSpacing.S6.dp
private const val OBRIT_TOP_BAR_LOGO_DESCRIPTION = "OBRit"
private const val OBRIT_TOP_BAR_SEARCH_DESCRIPTION = "검색"
private const val OBRIT_TOP_BAR_NOTIFICATION_DESCRIPTION = "알림"
private const val OBRIT_TOP_BAR_PROFILE_DESCRIPTION = "프로필"

@Preview(name = "OBRitHomeTopBar", showBackground = false)
@Composable
private fun OBRitHomeTopBarPreview() {
    OBRitTopBarPreviewContainer {
        OBRitHomeTopBar(
            onSearchClick = {},
            onNotificationClick = {},
            onProfileClick = {},
        )
    }
}

@Composable
private fun OBRitTopBarPreviewContainer(content: @Composable () -> Unit) {
    OBRitTheme(dynamicColor = false) {
        Box(modifier = Modifier.background(LocalOBRitColor.current.gray900)) {
            content()
        }
    }
}
