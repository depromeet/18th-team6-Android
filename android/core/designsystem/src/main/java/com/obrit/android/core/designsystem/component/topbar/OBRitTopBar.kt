@file:Suppress("TooManyFunctions")

package com.obrit.android.core.designsystem.component.topbar

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
fun OBRitHomeTopBar(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    showAlertAndMyPage: Boolean = false,
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

            if (!showAlertAndMyPage) {
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
}

@Composable
fun OBRitCloseTopBar(
    title: String,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    onMoreClick: (() -> Unit)? = null,
) {
    TopBarWithTitle(
        title = title,
        leadingIconRes = R.drawable.ic_topbar_close,
        leadingContentDescription = OBRIT_TOP_BAR_CLOSE_DESCRIPTION,
        onLeadingClick = onCloseClick,
        onMoreClick = onMoreClick,
        modifier = modifier,
    )
}

@Composable
fun OBRitDepthTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onMoreClick: (() -> Unit)? = null,
) {
    TopBarWithTitle(
        title = title,
        leadingIconRes = R.drawable.ic_topbar_arrow_left,
        leadingContentDescription = OBRIT_TOP_BAR_BACK_DESCRIPTION,
        onLeadingClick = onBackClick,
        onMoreClick = onMoreClick,
        modifier = modifier,
    )
}

@Composable
@Suppress("LongParameterList")
fun OBRitSearchTopBar(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = OBRIT_TOP_BAR_SEARCH_PLACEHOLDER,
    focusRequester: FocusRequester = remember { FocusRequester() },
    onSearch: () -> Unit = {},
) {
    TopBarRoot(modifier = modifier) {
        TopBarIconButton(
            iconRes = R.drawable.ic_topbar_arrow_left,
            onClick = onBackClick,
            contentDescription = OBRIT_TOP_BAR_BACK_DESCRIPTION,
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = OBRitTopBarSidePadding),
        )
        TopBarSearchInput(
            query = query,
            onQueryChange = onQueryChange,
            placeholder = placeholder,
            focusRequester = focusRequester,
            onSearch = onSearch,
            modifier =
                Modifier
                    .padding(
                        start = OBRitTopBarSearchInputStartPadding,
                        end = OBRitTopBarSidePadding,
                    ).fillMaxWidth(),
        )
    }
}

@Composable
@Suppress("LongParameterList")
private fun TopBarWithTitle(
    title: String,
    @DrawableRes leadingIconRes: Int,
    leadingContentDescription: String?,
    onLeadingClick: () -> Unit,
    onMoreClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    TopBarRoot(modifier = modifier) {
        TopBarIconButton(
            iconRes = leadingIconRes,
            onClick = onLeadingClick,
            contentDescription = leadingContentDescription,
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = OBRitTopBarSidePadding),
        )
        TopBarTitle(
            text = title,
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = OBRitTopBarTitleHorizontalPadding),
        )
        if (onMoreClick != null) {
            TopBarIconButton(
                iconRes = R.drawable.ic_topbar_more_vert,
                onClick = onMoreClick,
                contentDescription = OBRIT_TOP_BAR_MORE_DESCRIPTION,
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = OBRitTopBarSidePadding),
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

@Composable
private fun TopBarTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Text(
        text = text,
        style = typography.xl.copy(fontWeight = FontWeight.Bold),
        color = colors.common00,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}

// 동일 디자인이 feature/register의 CategorySelectionBottomSheet.kt의 CategorySearchField에도 복사되어 있다.
// Figma SSOT에 검색창이 정식 컴포넌트로 등록되면 designsystem으로 추출하고 양쪽을 교체할 것.
@Composable
@Suppress("LongMethod", "LongParameterList")
private fun TopBarSearchInput(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    placeholder: String,
    focusRequester: FocusRequester,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val textStyle =
        typography.xl.copy(
            fontWeight = FontWeight.Medium,
            color = colors.common00,
        )
    val placeholderStyle = textStyle.copy(color = colors.gray700)

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier =
            modifier
                .focusRequester(focusRequester)
                .clip(RoundedCornerShape(AtomRadius.Middle.dp))
                .border(
                    width = OBRitTopBarSearchBorderWidth,
                    color = colors.gray300,
                    shape = RoundedCornerShape(AtomRadius.Middle.dp),
                ).padding(
                    horizontal = AtomSpacing.S5.dp,
                    vertical = AtomSpacing.S4.dp,
                ),
        singleLine = true,
        textStyle = textStyle,
        cursorBrush = SolidColor(colors.common00),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
    ) { innerTextField ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (query.text.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = placeholderStyle,
                        maxLines = 1,
                    )
                }
                innerTextField()
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_topbar_search),
                contentDescription = null,
                tint = colors.common00,
                modifier =
                    Modifier
                        .size(OBRitTopBarIconSize)
                        .clickable(onClick = onSearch),
            )
        }
    }
}

private val OBRitTopBarHeight = AtomSpacing.S14.dp
private val OBRitTopBarIconButtonSize = AtomSpacing.S10.dp
private val OBRitTopBarIconSize = AtomSpacing.S6.dp
private val OBRitTopBarSidePadding = AtomSpacing.S3.dp
private val OBRitTopBarLogoStartPadding = AtomSpacing.S5.dp
private val OBRitTopBarLogoHeight = AtomSpacing.S6.dp
private val OBRitTopBarSearchInputStartPadding = AtomSpacing.S16.dp
private val OBRitTopBarTitleHorizontalPadding = AtomSpacing.S14.dp
private val OBRitTopBarSearchBorderWidth = 1.4f.dp

private const val OBRIT_TOP_BAR_LOGO_DESCRIPTION = "OBRit"
private const val OBRIT_TOP_BAR_SEARCH_DESCRIPTION = "검색"
private const val OBRIT_TOP_BAR_NOTIFICATION_DESCRIPTION = "알림"
private const val OBRIT_TOP_BAR_PROFILE_DESCRIPTION = "프로필"
private const val OBRIT_TOP_BAR_CLOSE_DESCRIPTION = "닫기"
private const val OBRIT_TOP_BAR_BACK_DESCRIPTION = "뒤로"
private const val OBRIT_TOP_BAR_MORE_DESCRIPTION = "더보기"
private const val OBRIT_TOP_BAR_SEARCH_PLACEHOLDER = "원하시는 소모품을 검색해보세요"

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

@Preview(name = "OBRitCloseTopBar", showBackground = false)
@Composable
private fun OBRitCloseTopBarPreview() {
    OBRitTopBarPreviewContainer {
        OBRitCloseTopBar(
            title = "PageTitle",
            onCloseClick = {},
            onMoreClick = {},
        )
    }
}

@Preview(name = "OBRitDepthTopBar", showBackground = false)
@Composable
private fun OBRitDepthTopBarPreview() {
    OBRitTopBarPreviewContainer {
        OBRitDepthTopBar(
            title = "PageTitle",
            onBackClick = {},
            onMoreClick = {},
        )
    }
}

@Preview(name = "OBRitSearchTopBar", showBackground = false)
@Composable
private fun OBRitSearchTopBarPreview() {
    OBRitTopBarPreviewContainer {
        OBRitSearchTopBar(
            query = TextFieldValue(""),
            onQueryChange = {},
            onBackClick = {},
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
