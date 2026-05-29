@file:Suppress("LongMethod", "MagicNumber", "TooManyFunctions")

package com.obrit.feature.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.component.modal.OBRitModal
import com.obrit.android.core.designsystem.component.modal.OBRitModalAppearance
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme

internal data class DetailHeaderSectionAction(
    val onBackClick: () -> Unit,
    val onMoreClick: () -> Unit,
    val onMoreMenuDismiss: () -> Unit,
    val onEditClick: () -> Unit,
    val onDeleteClick: () -> Unit,
    val onDeleteConfirmClick: () -> Unit,
    val onDeleteCancelClick: () -> Unit,
)

@Composable
internal fun DetailHeaderSection(
    title: String,
    isMoreMenuExpanded: Boolean,
    isDeleteConfirmVisible: Boolean,
    action: DetailHeaderSectionAction,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        DetailHeaderTopBar(
            title = title,
            onBackClick = action.onBackClick,
            onMoreClick = action.onMoreClick,
            modifier = Modifier.fillMaxWidth(),
        )

        DetailHeaderMoreMenu(
            isExpanded = isMoreMenuExpanded && !isDeleteConfirmVisible,
            onDismissRequest = action.onMoreMenuDismiss,
            onEditClick = {
                action.onMoreMenuDismiss()
                action.onEditClick()
            },
            onDeleteClick = {
                action.onMoreMenuDismiss()
                action.onDeleteClick()
            },
        )
    }

    DetailDeleteConfirmPopup(
        isVisible = isDeleteConfirmVisible,
        onConfirmClick = action.onDeleteConfirmClick,
        onCancelClick = action.onDeleteCancelClick,
    )
}

@Composable
private fun DetailHeaderTopBar(
    title: String,
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Box(
        modifier =
            modifier
                .height(DETAIL_HEADER_TOP_BAR_HEIGHT)
                .background(colors.gray900),
    ) {
        DetailHeaderIconButton(
            iconRes = R.drawable.ic_topbar_arrow_left,
            contentDescription = DETAIL_HEADER_BACK_DESCRIPTION,
            onClick = onBackClick,
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = DETAIL_HEADER_HORIZONTAL_PADDING),
        )

        Text(
            text = title,
            style = typography.xl.copy(fontWeight = FontWeight.Bold),
            color = colors.common00,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = DETAIL_HEADER_TITLE_HORIZONTAL_PADDING),
        )

        DetailHeaderIconButton(
            iconRes = R.drawable.ic_topbar_more_vert,
            contentDescription = DETAIL_HEADER_MORE_DESCRIPTION,
            onClick = onMoreClick,
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = DETAIL_HEADER_HORIZONTAL_PADDING),
        )
    }
}

@Composable
private fun DetailHeaderIconButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current

    Box(
        modifier =
            modifier
                .size(DETAIL_HEADER_ICON_BUTTON_SIZE)
                .clip(RoundedCornerShape(DETAIL_HEADER_ICON_BUTTON_CORNER_RADIUS))
                .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = colors.common00,
            modifier = Modifier.size(DETAIL_HEADER_ICON_SIZE),
        )
    }
}

@Composable
private fun DetailHeaderMoreMenu(
    isExpanded: Boolean,
    onDismissRequest: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!isExpanded) {
        return
    }

    val moreMenuOffset =
        with(LocalDensity.current) {
            IntOffset(x = 0, y = DETAIL_HEADER_TOP_BAR_HEIGHT.roundToPx())
        }

    Popup(
        alignment = Alignment.TopEnd,
        offset = moreMenuOffset,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
    ) {
        DetailHeaderMoreMenuSurface(
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick,
            modifier = modifier.padding(end = DETAIL_HEADER_MORE_MENU_END_PADDING),
        )
    }
}

@Composable
private fun DetailHeaderMoreMenuSurface(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val menuBackgroundColor = colors.common00.copy(alpha = DETAIL_MORE_MENU_BACKGROUND_ALPHA)

    Box(
        modifier =
            modifier
                .size(
                    width = DETAIL_MORE_MENU_WIDTH,
                    height = DETAIL_MORE_MENU_HEIGHT,
                ).clip(RoundedCornerShape(DETAIL_MORE_MENU_CORNER_RADIUS))
                .background(menuBackgroundColor),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            DetailHeaderMoreMenuItem(
                icon = DetailEditIcon,
                text = DETAIL_MORE_MENU_EDIT_TEXT,
                contentColor = colors.common00,
                onClick = onEditClick,
                textStyle = typography.xl.copy(fontWeight = FontWeight.Bold),
                iconOffsetY = DETAIL_MORE_MENU_TOP_ITEM_CONTENT_OFFSET_Y,
                textOffsetY = DETAIL_MORE_MENU_TOP_ITEM_CONTENT_OFFSET_Y,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(DETAIL_MORE_MENU_ITEM_HEIGHT),
            )
            DetailHeaderMoreMenuItem(
                icon = DetailDeleteIcon,
                text = DETAIL_MORE_MENU_DELETE_TEXT,
                contentColor = colors.red300,
                onClick = onDeleteClick,
                textStyle = typography.xl.copy(fontWeight = FontWeight.Bold),
                iconOffsetY = DETAIL_MORE_MENU_BOTTOM_ITEM_CONTENT_OFFSET_Y,
                textOffsetY = DETAIL_MORE_MENU_BOTTOM_ITEM_CONTENT_OFFSET_Y,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(DETAIL_MORE_MENU_ITEM_HEIGHT),
            )
        }
        Box(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = DETAIL_MORE_MENU_DIVIDER_OFFSET_Y)
                    .width(DETAIL_MORE_MENU_DIVIDER_WIDTH)
                    .height(DETAIL_MORE_MENU_DIVIDER_HEIGHT)
                    .clip(RoundedCornerShape(DETAIL_MORE_MENU_DIVIDER_CORNER_RADIUS))
                    .background(colors.gray400),
        )
    }
}

@Composable
@Suppress("LongParameterList")
private fun DetailHeaderMoreMenuItem(
    icon: ImageVector,
    text: String,
    contentColor: Color,
    onClick: () -> Unit,
    textStyle: TextStyle,
    iconOffsetY: Dp,
    textOffsetY: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clickable(
                    role = Role.Button,
                    onClick = onClick,
                ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier =
                Modifier
                    .offset(
                        x = DETAIL_MORE_MENU_ICON_OFFSET_X,
                        y = iconOffsetY,
                    ).size(DETAIL_MORE_MENU_ICON_SIZE),
        )
        Text(
            text = text,
            style = textStyle,
            color = contentColor,
            maxLines = 1,
            modifier =
                Modifier.offset(
                    x = DETAIL_MORE_MENU_TEXT_OFFSET_X,
                    y = textOffsetY,
                ),
        )
    }
}

@Composable
private fun DetailDeleteConfirmPopup(
    isVisible: Boolean,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!isVisible) {
        return
    }

    Popup(
        popupPositionProvider = DetailDeleteConfirmPopupPositionProvider,
        onDismissRequest = onCancelClick,
        properties = PopupProperties(focusable = true),
    ) {
        DetailDeleteConfirmModal(
            onConfirmClick = onConfirmClick,
            onCancelClick = onCancelClick,
            modifier = modifier,
        )
    }
}

private object DetailDeleteConfirmPopupPositionProvider : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset =
        IntOffset(
            x = ((windowSize.width - popupContentSize.width) / 2).coerceAtLeast(0),
            y = ((windowSize.height - popupContentSize.height) / 2).coerceAtLeast(0),
        )
}

@Composable
private fun DetailDeleteConfirmModal(
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OBRitModal(
        title = DETAIL_DELETE_CONFIRM_TITLE,
        description = DETAIL_DELETE_CONFIRM_DESCRIPTION,
        primaryButtonText = DETAIL_DELETE_CONFIRM_BUTTON_TEXT,
        onPrimaryButtonClick = onConfirmClick,
        appearance = OBRitModalAppearance.Dark,
        showImage = false,
        secondaryButtonText = DETAIL_DELETE_CANCEL_BUTTON_TEXT,
        onSecondaryButtonClick = onCancelClick,
        modifier = modifier,
    )
}

@Preview(name = "DetailHeaderSection", widthDp = 412, heightDp = 56)
@Composable
private fun DetailHeaderSectionPreview() {
    OBRitTheme(dynamicColor = false) {
        DetailHeaderSection(
            title = "{Title}",
            isMoreMenuExpanded = false,
            isDeleteConfirmVisible = false,
            action =
                DetailHeaderSectionAction(
                    onBackClick = {},
                    onMoreClick = {},
                    onMoreMenuDismiss = {},
                    onEditClick = {},
                    onDeleteClick = {},
                    onDeleteConfirmClick = {},
                    onDeleteCancelClick = {},
                ),
        )
    }
}

@Preview(name = "DetailHeaderMoreMenu", widthDp = 120, heightDp = 104)
@Composable
private fun DetailHeaderMoreMenuPreview() {
    OBRitTheme(dynamicColor = false) {
        Box(modifier = Modifier.background(LocalOBRitColor.current.gray900)) {
            DetailHeaderMoreMenuSurface(
                onEditClick = {},
                onDeleteClick = {},
            )
        }
    }
}

@Preview(name = "DetailDeleteConfirmDialog")
@Composable
private fun DetailDeleteConfirmDialogPreview() {
    OBRitTheme(dynamicColor = false) {
        DetailDeleteConfirmModal(
            onConfirmClick = {},
            onCancelClick = {},
        )
    }
}

private val DETAIL_HEADER_TOP_BAR_HEIGHT = 56.dp
private val DETAIL_HEADER_HORIZONTAL_PADDING = 12.dp
private val DETAIL_HEADER_TITLE_HORIZONTAL_PADDING = 56.dp
private val DETAIL_HEADER_ICON_BUTTON_SIZE = 40.dp
private val DETAIL_HEADER_ICON_SIZE = 24.dp
private val DETAIL_HEADER_ICON_BUTTON_CORNER_RADIUS = 999.dp
private val DETAIL_HEADER_MORE_MENU_END_PADDING = 12.dp
private val DETAIL_MORE_MENU_WIDTH = 120.dp
private val DETAIL_MORE_MENU_HEIGHT = 104.dp
private val DETAIL_MORE_MENU_CORNER_RADIUS = 16.dp
private val DETAIL_MORE_MENU_ITEM_HEIGHT = 52.dp
private val DETAIL_MORE_MENU_ICON_SIZE = 24.dp
private val DETAIL_MORE_MENU_ICON_OFFSET_X = 14.dp
private val DETAIL_MORE_MENU_TEXT_OFFSET_X = 49.dp
private val DETAIL_MORE_MENU_TOP_ITEM_CONTENT_OFFSET_Y = 18.dp
private val DETAIL_MORE_MENU_BOTTOM_ITEM_CONTENT_OFFSET_Y = 10.dp
private val DETAIL_MORE_MENU_DIVIDER_WIDTH = 92.dp
private val DETAIL_MORE_MENU_DIVIDER_HEIGHT = 1.dp
private val DETAIL_MORE_MENU_DIVIDER_OFFSET_Y = 51.5.dp
private val DETAIL_MORE_MENU_DIVIDER_CORNER_RADIUS = 999.dp
private const val DETAIL_HEADER_BACK_DESCRIPTION = "뒤로"
private const val DETAIL_HEADER_MORE_DESCRIPTION = "더보기"
private const val DETAIL_MORE_MENU_EDIT_TEXT = "편집"
private const val DETAIL_MORE_MENU_DELETE_TEXT = "삭제"
private const val DETAIL_DELETE_CONFIRM_TITLE = "해당 소모품을 삭제하시겠습니까?"
private const val DETAIL_DELETE_CONFIRM_DESCRIPTION = ""
private const val DETAIL_DELETE_CONFIRM_BUTTON_TEXT = "삭제"
private const val DETAIL_DELETE_CANCEL_BUTTON_TEXT = "아니요"
private const val DETAIL_MORE_MENU_BACKGROUND_ALPHA = 0.2f

private val DetailEditIcon: ImageVector by lazy {
    ImageVector
        .Builder(
            name = "DetailEditIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(4f, 20.0003f)
                horizontalLineTo(8f)
                lineTo(18.5f, 9.5003f)
                curveTo(18.7626f, 9.2377f, 18.971f, 8.9259f, 19.1131f, 8.5827f)
                curveTo(19.2553f, 8.2395f, 19.3284f, 7.8717f, 19.3284f, 7.5003f)
                curveTo(19.3284f, 7.1289f, 19.2553f, 6.7611f, 19.1131f, 6.4179f)
                curveTo(18.971f, 6.0747f, 18.7626f, 5.7629f, 18.5f, 5.5003f)
                curveTo(18.2374f, 5.2377f, 17.9256f, 5.0293f, 17.5824f, 4.8872f)
                curveTo(17.2392f, 4.745f, 16.8714f, 4.6719f, 16.5f, 4.6719f)
                curveTo(16.1286f, 4.6719f, 15.7608f, 4.745f, 15.4176f, 4.8872f)
                curveTo(15.0744f, 5.0293f, 14.7626f, 5.2377f, 14.5f, 5.5003f)
                lineTo(4f, 16.0003f)
                verticalLineTo(20.0003f)
                close()
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(13.5f, 6.5f)
                lineTo(17.5f, 10.5f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(16f, 19f)
                horizontalLineTo(22f)
            }
        }.build()
}

private val DetailDeleteIcon: ImageVector by lazy {
    ImageVector
        .Builder(
            name = "DetailDeleteIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(4f, 7f)
                horizontalLineTo(20f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(10f, 11f)
                verticalLineTo(17f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(14f, 11f)
                verticalLineTo(17f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(5f, 7f)
                lineTo(6f, 19f)
                curveTo(6f, 19.5304f, 6.2107f, 20.0391f, 6.5858f, 20.4142f)
                curveTo(6.9609f, 20.7893f, 7.4696f, 21f, 8f, 21f)
                horizontalLineTo(16f)
                curveTo(16.5304f, 21f, 17.0391f, 20.7893f, 17.4142f, 20.4142f)
                curveTo(17.7893f, 20.0391f, 18f, 19.5304f, 18f, 19f)
                lineTo(19f, 7f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(9f, 7f)
                verticalLineTo(4f)
                curveTo(9f, 3.7348f, 9.1054f, 3.4804f, 9.2929f, 3.2929f)
                curveTo(9.4804f, 3.1054f, 9.7348f, 3f, 10f, 3f)
                horizontalLineTo(14f)
                curveTo(14.2652f, 3f, 14.5196f, 3.1054f, 14.7071f, 3.2929f)
                curveTo(14.8946f, 3.4804f, 15f, 3.7348f, 15f, 4f)
                verticalLineTo(7f)
            }
        }.build()
}
