@file:Suppress("LongMethod", "MagicNumber", "TooManyFunctions")

package com.obrit.feature.detail.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.obrit.android.core.designsystem.component.button.OBRitButtonDefaults
import com.obrit.android.core.designsystem.component.button.OBRitMiddleFilledButton
import com.obrit.android.core.designsystem.component.dim.OBRitDim
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitColor
import com.obrit.android.core.designsystem.theme.OBRitTheme

@Immutable
internal data class DetailReplacementCompletionDialogState(
    val kind: DetailReplacementCompletionDialogKind,
    val itemName: String,
    val representativeImageUrl: String?,
    val messageLines: List<String>,
    val summaryTitle: String,
    val summaryValue: String,
    val recordedAtText: String,
)

internal enum class DetailReplacementCompletionDialogKind {
    NextReplacement,
    LowStock,
}

@Composable
internal fun DetailReplacementCompletionDialog(
    state: DetailReplacementCompletionDialogState,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onCancelClick,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        DetailReplacementCompletionContent(
            state = state,
            onConfirmClick = onConfirmClick,
            onCancelClick = onCancelClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun DetailReplacementCompletionContent(
    state: DetailReplacementCompletionDialogState,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current

    Column(
        modifier =
            modifier
                .width(DETAIL_REPLACEMENT_COMPLETION_DIALOG_WIDTH)
                .clip(DETAIL_REPLACEMENT_COMPLETION_DIALOG_SHAPE)
                .background(colors.gray800)
                .padding(
                    start = DETAIL_REPLACEMENT_COMPLETION_HORIZONTAL_PADDING,
                    top = DETAIL_REPLACEMENT_COMPLETION_TOP_PADDING,
                    end = DETAIL_REPLACEMENT_COMPLETION_HORIZONTAL_PADDING,
                    bottom = DETAIL_REPLACEMENT_COMPLETION_BOTTOM_PADDING,
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DETAIL_REPLACEMENT_COMPLETION_SECTION_SPACING),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DETAIL_REPLACEMENT_COMPLETION_BODY_SPACING),
        ) {
            DetailReplacementCompletionBody(state = state)
            DetailReplacementCompletionSummary(state = state)
        }

        DetailReplacementCompletionActions(
            onConfirmClick = onConfirmClick,
            onCancelClick = onCancelClick,
        )
    }
}

@Composable
private fun DetailReplacementCompletionBody(
    state: DetailReplacementCompletionDialogState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DETAIL_REPLACEMENT_COMPLETION_BADGE_TEXT_SPACING),
    ) {
        DetailReplacementCompletionBadge(
            itemName = state.itemName,
            representativeImageUrl = state.representativeImageUrl,
        )
        DetailReplacementCompletionText(state = state)
    }
}

@Composable
private fun DetailReplacementCompletionBadge(
    itemName: String,
    representativeImageUrl: String?,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val fallbackText =
        itemName
            .trim()
            .firstOrNull()
            ?.toString()
            .orEmpty()

    Box(
        modifier = modifier.size(DETAIL_REPLACEMENT_COMPLETION_BADGE_GLOW_SIZE),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush =
                    Brush.radialGradient(
                        colors =
                            listOf(
                                colors.green700.copy(alpha = DETAIL_REPLACEMENT_COMPLETION_GLOW_ALPHA),
                                Color.Transparent,
                            ),
                        center = Offset(size.width * 0.5f, size.height * 0.5f),
                        radius = size.minDimension * DETAIL_REPLACEMENT_COMPLETION_GLOW_RADIUS_RATIO,
                    ),
            )
        }

        Box(
            modifier =
                Modifier
                    .size(DETAIL_REPLACEMENT_COMPLETION_BADGE_SIZE)
                    .shadow(
                        elevation = DETAIL_REPLACEMENT_COMPLETION_BADGE_SHADOW_ELEVATION,
                        shape = CircleShape,
                        clip = false,
                        ambientColor = colors.green700.copy(alpha = DETAIL_REPLACEMENT_COMPLETION_BADGE_SHADOW_ALPHA),
                        spotColor = colors.green700.copy(alpha = DETAIL_REPLACEMENT_COMPLETION_BADGE_SHADOW_ALPHA),
                    ).clip(CircleShape)
                    .background(colors.green300),
            contentAlignment = Alignment.Center,
        ) {
            if (representativeImageUrl.isNullOrBlank() && fallbackText.isNotBlank()) {
                Text(
                    text = fallbackText,
                    style =
                        typography.xl4.copy(
                            color = colors.common1000,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        ),
                    maxLines = 1,
                )
            }

            DetailRemoteImage(
                imageUrl = representativeImageUrl,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(DETAIL_REPLACEMENT_COMPLETION_IMAGE_SIZE)
                        .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
private fun DetailReplacementCompletionText(
    state: DetailReplacementCompletionDialogState,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DETAIL_REPLACEMENT_COMPLETION_TEXT_SPACING),
    ) {
        Text(
            text = "${state.itemName} 교체 완료!",
            modifier = Modifier.fillMaxWidth(),
            style =
                typography.xl5.copy(
                    color = colors.common00,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            state.messageLines.take(DETAIL_REPLACEMENT_COMPLETION_MESSAGE_MAX_LINES).forEach { line ->
                Text(
                    text = line,
                    modifier = Modifier.fillMaxWidth(),
                    style =
                        typography.s.copy(
                            color = colors.gray500,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun DetailReplacementCompletionSummary(
    state: DetailReplacementCompletionDialogState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DETAIL_REPLACEMENT_COMPLETION_SUMMARY_RECORDED_SPACING),
    ) {
        DetailReplacementCompletionSummaryRow(state = state)
        DetailReplacementCompletionRecordedAtText(text = state.recordedAtText)
    }
}

@Composable
private fun DetailReplacementCompletionSummaryRow(
    state: DetailReplacementCompletionDialogState,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val valueColor = state.kind.summaryValueColor(colors)

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(DETAIL_REPLACEMENT_COMPLETION_SUMMARY_SHAPE)
                .background(colors.gray850)
                .padding(
                    horizontal = DETAIL_REPLACEMENT_COMPLETION_SUMMARY_HORIZONTAL_PADDING,
                    vertical = DETAIL_REPLACEMENT_COMPLETION_SUMMARY_VERTICAL_PADDING,
                ),
        horizontalArrangement = Arrangement.spacedBy(DETAIL_REPLACEMENT_COMPLETION_SUMMARY_ITEM_SPACING),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(DETAIL_REPLACEMENT_COMPLETION_SUMMARY_LABEL_SPACING),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = state.kind.summaryIcon,
                contentDescription = null,
                modifier = Modifier.size(DETAIL_REPLACEMENT_COMPLETION_SUMMARY_ICON_SIZE),
                tint = colors.gray400,
            )
            Text(
                text = state.summaryTitle,
                style =
                    typography.base.copy(
                        color = colors.gray500,
                        fontWeight = FontWeight.Bold,
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Text(
            text = state.summaryValue,
            style =
                typography.base.copy(
                    color = valueColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun DetailReplacementCompletionRecordedAtText(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        style =
            typography.xs2.copy(
                color = colors.gray600,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun DetailReplacementCompletionActions(
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DETAIL_REPLACEMENT_COMPLETION_ACTION_SPACING),
    ) {
        OBRitMiddleFilledButton(
            text = DETAIL_REPLACEMENT_COMPLETION_CONFIRM_TEXT,
            onClick = onConfirmClick,
            colors = OBRitButtonDefaults.positiveButtonColors(),
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = DETAIL_REPLACEMENT_COMPLETION_CANCEL_TEXT,
            modifier =
                Modifier
                    .clip(DETAIL_REPLACEMENT_COMPLETION_CANCEL_SHAPE)
                    .clickable(
                        role = Role.Button,
                        onClick = onCancelClick,
                    ).padding(
                        horizontal = DETAIL_REPLACEMENT_COMPLETION_CANCEL_HORIZONTAL_PADDING,
                        vertical = DETAIL_REPLACEMENT_COMPLETION_CANCEL_VERTICAL_PADDING,
                    ),
            style =
                typography.xs2.copy(
                    color = colors.gray500,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    textDecoration = TextDecoration.Underline,
                ),
            maxLines = 1,
        )
    }
}

private fun DetailReplacementCompletionDialogKind.summaryValueColor(colors: OBRitColor): Color =
    when (this) {
        DetailReplacementCompletionDialogKind.NextReplacement -> colors.green300
        DetailReplacementCompletionDialogKind.LowStock -> colors.red300
    }

private val DetailReplacementCompletionDialogKind.summaryIcon: ImageVector
    get() =
        when (this) {
            DetailReplacementCompletionDialogKind.NextReplacement -> DetailReplacementCompletionCalendarIcon
            DetailReplacementCompletionDialogKind.LowStock -> DetailReplacementCompletionBoxIcon
        }

private val DetailReplacementCompletionCalendarIcon: ImageVector by lazy {
    ImageVector
        .Builder(
            name = "DetailReplacementCompletionCalendarIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(8f, 2f)
                verticalLineTo(6f)
                moveTo(16f, 2f)
                verticalLineTo(6f)
                moveTo(4f, 9f)
                horizontalLineTo(20f)
                moveTo(6f, 4f)
                horizontalLineTo(18f)
                curveTo(19.1f, 4f, 20f, 4.9f, 20f, 6f)
                verticalLineTo(18f)
                curveTo(20f, 19.1f, 19.1f, 20f, 18f, 20f)
                horizontalLineTo(6f)
                curveTo(4.9f, 20f, 4f, 19.1f, 4f, 18f)
                verticalLineTo(6f)
                curveTo(4f, 4.9f, 4.9f, 4f, 6f, 4f)
                close()
                moveTo(12f, 13f)
                verticalLineTo(17f)
            }
        }.build()
}

private val DetailReplacementCompletionBoxIcon: ImageVector by lazy {
    ImageVector
        .Builder(
            name = "DetailReplacementCompletionBoxIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(12f, 3f)
                lineTo(21f, 8f)
                verticalLineTo(16f)
                lineTo(12f, 21f)
                lineTo(3f, 16f)
                verticalLineTo(8f)
                close()
                moveTo(3f, 8f)
                lineTo(12f, 13f)
                lineTo(21f, 8f)
                moveTo(12f, 13f)
                verticalLineTo(21f)
                moveTo(7.5f, 5.5f)
                lineTo(16.5f, 10.5f)
            }
        }.build()
}

@Preview(name = "DetailReplacementCompletionDialog Next", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun DetailReplacementCompletionDialogNextPreview() {
    DetailReplacementCompletionDialogPreviewContainer {
        DetailReplacementCompletionContent(
            state =
                DetailReplacementCompletionDialogState(
                    kind = DetailReplacementCompletionDialogKind.NextReplacement,
                    itemName = "칫솔",
                    representativeImageUrl = null,
                    messageLines = listOf("지난번보다 2일 빠르게 교체했어요.", "교체 시기를 잘 지키고 있어요!"),
                    summaryTitle = "다음 교체 예상일",
                    summaryValue = "6월 22일(30일 후)",
                    recordedAtText = "2026. 05. 23 오전 09:30 기록됨",
                ),
            onConfirmClick = {},
            onCancelClick = {},
        )
    }
}

@Preview(name = "DetailReplacementCompletionDialog Low Stock", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun DetailReplacementCompletionDialogLowStockPreview() {
    DetailReplacementCompletionDialogPreviewContainer {
        DetailReplacementCompletionContent(
            state =
                DetailReplacementCompletionDialogState(
                    kind = DetailReplacementCompletionDialogKind.LowStock,
                    itemName = "칫솔",
                    representativeImageUrl = null,
                    messageLines = listOf("칫솔 여분이 얼마 남지 않았어요!", "여분을 확인해주세요"),
                    summaryTitle = "남은 여분 갯수",
                    summaryValue = "1 개",
                    recordedAtText = "2026. 05. 23 오전 09:30 기록됨",
                ),
            onConfirmClick = {},
            onCancelClick = {},
        )
    }
}

@Composable
private fun DetailReplacementCompletionDialogPreviewContainer(content: @Composable () -> Unit) {
    OBRitTheme(dynamicColor = false) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            OBRitDim()
            content()
        }
    }
}

private val DETAIL_REPLACEMENT_COMPLETION_DIALOG_WIDTH = 333.dp
private val DETAIL_REPLACEMENT_COMPLETION_DIALOG_SHAPE = RoundedCornerShape(16.dp)
private val DETAIL_REPLACEMENT_COMPLETION_HORIZONTAL_PADDING = 20.dp
private val DETAIL_REPLACEMENT_COMPLETION_TOP_PADDING = 24.dp
private val DETAIL_REPLACEMENT_COMPLETION_BOTTOM_PADDING = 16.dp
private val DETAIL_REPLACEMENT_COMPLETION_SECTION_SPACING = 20.dp
private val DETAIL_REPLACEMENT_COMPLETION_BODY_SPACING = 20.dp
private val DETAIL_REPLACEMENT_COMPLETION_BADGE_TEXT_SPACING = 24.dp
private val DETAIL_REPLACEMENT_COMPLETION_TEXT_SPACING = 12.dp
private val DETAIL_REPLACEMENT_COMPLETION_BADGE_GLOW_SIZE = 128.dp
private val DETAIL_REPLACEMENT_COMPLETION_BADGE_SIZE = 100.dp
private val DETAIL_REPLACEMENT_COMPLETION_BADGE_SHADOW_ELEVATION = 24.dp
private val DETAIL_REPLACEMENT_COMPLETION_IMAGE_SIZE = 66.dp
private val DETAIL_REPLACEMENT_COMPLETION_SUMMARY_SHAPE = RoundedCornerShape(12.dp)
private val DETAIL_REPLACEMENT_COMPLETION_SUMMARY_HORIZONTAL_PADDING = 16.dp
private val DETAIL_REPLACEMENT_COMPLETION_SUMMARY_VERTICAL_PADDING = 12.dp
private val DETAIL_REPLACEMENT_COMPLETION_SUMMARY_RECORDED_SPACING = 12.dp
private val DETAIL_REPLACEMENT_COMPLETION_SUMMARY_ITEM_SPACING = 12.dp
private val DETAIL_REPLACEMENT_COMPLETION_SUMMARY_LABEL_SPACING = 12.dp
private val DETAIL_REPLACEMENT_COMPLETION_SUMMARY_ICON_SIZE = 24.dp
private val DETAIL_REPLACEMENT_COMPLETION_ACTION_SPACING = 8.dp
private val DETAIL_REPLACEMENT_COMPLETION_CANCEL_SHAPE = RoundedCornerShape(4.dp)
private val DETAIL_REPLACEMENT_COMPLETION_CANCEL_HORIZONTAL_PADDING = 12.dp
private val DETAIL_REPLACEMENT_COMPLETION_CANCEL_VERTICAL_PADDING = 6.dp
private const val DETAIL_REPLACEMENT_COMPLETION_BADGE_SHADOW_ALPHA = 0.45f
private const val DETAIL_REPLACEMENT_COMPLETION_GLOW_ALPHA = 0.55f
private const val DETAIL_REPLACEMENT_COMPLETION_GLOW_RADIUS_RATIO = 0.5f
private const val DETAIL_REPLACEMENT_COMPLETION_MESSAGE_MAX_LINES = 2
private const val DETAIL_REPLACEMENT_COMPLETION_CONFIRM_TEXT = "확인"
private const val DETAIL_REPLACEMENT_COMPLETION_CANCEL_TEXT = "취소하기"
