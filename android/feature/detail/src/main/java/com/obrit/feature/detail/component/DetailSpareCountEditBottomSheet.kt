@file:Suppress("LongParameterList", "TooManyFunctions")

package com.obrit.feature.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.component.bottomsheet.OBRitBottomSheet
import com.obrit.android.core.designsystem.component.button.OBRitLargeFilledButton
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitColor
import com.obrit.android.core.designsystem.theme.OBRitTheme

@Composable
internal fun DetailSpareCountEditBottomSheet(
    title: String,
    initialCount: Int,
    onCompleteClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val safeInitialCount = initialCount.coerceIn(MIN_SPARE_COUNT, MAX_SPARE_COUNT)
    var count by remember(safeInitialCount) { mutableStateOf(safeInitialCount) }
    var hasPressedCountButton by remember(safeInitialCount) { mutableStateOf(false) }

    DetailSpareCountEditBottomSheetContent(
        title = title,
        count = count,
        isCompleteEnabled = hasPressedCountButton || count != safeInitialCount,
        onDecreaseClick = {
            if (count > MIN_SPARE_COUNT) {
                count -= 1
                hasPressedCountButton = true
            }
        },
        onIncreaseClick = {
            if (count < MAX_SPARE_COUNT) {
                count += 1
                hasPressedCountButton = true
            }
        },
        onCompleteClick = {
            onCompleteClick(count)
        },
        modifier = modifier,
    )
}

@Composable
private fun DetailSpareCountEditBottomSheetContent(
    title: String,
    count: Int,
    isCompleteEnabled: Boolean,
    onDecreaseClick: () -> Unit,
    onIncreaseClick: () -> Unit,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OBRitBottomSheet(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(DETAIL_SPARE_COUNT_EDIT_TITLE_TOP_PADDING))
        DetailSpareCountEditTitle(title = title)
        Spacer(modifier = Modifier.height(DETAIL_SPARE_COUNT_EDIT_CONTROLS_TOP_PADDING))
        DetailSpareCountEditControls(
            count = count,
            onDecreaseClick = onDecreaseClick,
            onIncreaseClick = onIncreaseClick,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(DETAIL_SPARE_COUNT_EDIT_BUTTON_TOP_PADDING))
        DetailSpareCountEditCompleteButton(
            isEnabled = isCompleteEnabled,
            onClick = onCompleteClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun DetailSpareCountEditTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Text(
        text =
            buildAnnotatedString {
                withStyle(SpanStyle(color = colors.green300)) {
                    append(title)
                }
                append(" 여분 갯수를 조정해주세요")
            },
        modifier = modifier.fillMaxWidth(),
        style = typography.xl4.copy(fontWeight = FontWeight.Bold),
        color = colors.common00,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun DetailSpareCountEditControls(
    count: Int,
    onDecreaseClick: () -> Unit,
    onIncreaseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DetailSpareCountEditControlButton(
            text = "-",
            enabled = count > MIN_SPARE_COUNT,
            onClick = onDecreaseClick,
        )
        Spacer(modifier = Modifier.width(DETAIL_SPARE_COUNT_EDIT_CONTROL_GAP))
        Text(
            text = count.toString(),
            modifier = Modifier.width(DETAIL_SPARE_COUNT_EDIT_COUNT_WIDTH),
            style = typography.xl7.copy(fontWeight = FontWeight.Bold),
            color = colors.common00,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
        Spacer(modifier = Modifier.width(DETAIL_SPARE_COUNT_EDIT_CONTROL_GAP))
        DetailSpareCountEditControlButton(
            text = "+",
            enabled = count < MAX_SPARE_COUNT,
            onClick = onIncreaseClick,
        )
    }
}

@Composable
private fun DetailSpareCountEditControlButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current

    Button(
        onClick = onClick,
        modifier = modifier.size(DETAIL_SPARE_COUNT_EDIT_CONTROL_BUTTON_SIZE),
        enabled = enabled,
        shape = DETAIL_SPARE_COUNT_EDIT_CONTROL_BUTTON_SHAPE,
        colors = spareCountEditControlButtonColors(LocalOBRitColor.current),
        elevation = null,
        contentPadding = PaddingValues(0.dp),
    ) {
        Text(
            text = text,
            style = typography.xl6.copy(fontWeight = FontWeight.Medium),
            maxLines = 1,
        )
    }
}

@Composable
private fun DetailSpareCountEditCompleteButton(
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current

    OBRitLargeFilledButton(
        onClick = onClick,
        colors = spareCountEditCompleteButtonColors(LocalOBRitColor.current),
        modifier = modifier.height(DETAIL_SPARE_COUNT_EDIT_COMPLETE_BUTTON_HEIGHT),
        enabled = isEnabled,
    ) {
        Text(
            text = DETAIL_SPARE_COUNT_EDIT_COMPLETE_BUTTON_TEXT,
            style = typography.xl3.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun DetailSpareCountEditPreviewContainer(content: @Composable () -> Unit) {
    OBRitTheme(dynamicColor = false) {
        val colors = LocalOBRitColor.current

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(colors.backgroundDefaultDimDefault)
                    .padding(top = DETAIL_SPARE_COUNT_EDIT_PREVIEW_TOP_PADDING),
            contentAlignment = Alignment.BottomCenter,
        ) {
            content()
        }
    }
}

@Preview(
    name = "DetailSpareCountEditBottomSheet Disabled",
    showBackground = true,
    widthDp = 412,
)
@Composable
private fun DetailSpareCountEditBottomSheetDisabledPreview() {
    DetailSpareCountEditPreviewContainer {
        DetailSpareCountEditBottomSheetContent(
            title = "칫솔",
            count = 3,
            isCompleteEnabled = false,
            onDecreaseClick = {},
            onIncreaseClick = {},
            onCompleteClick = {},
        )
    }
}

@Preview(
    name = "DetailSpareCountEditBottomSheet Enabled",
    showBackground = true,
    widthDp = 412,
)
@Composable
private fun DetailSpareCountEditBottomSheetEnabledPreview() {
    DetailSpareCountEditPreviewContainer {
        DetailSpareCountEditBottomSheetContent(
            title = "칫솔",
            count = 4,
            isCompleteEnabled = true,
            onDecreaseClick = {},
            onIncreaseClick = {},
            onCompleteClick = {},
        )
    }
}

@Composable
private fun spareCountEditControlButtonColors(colors: OBRitColor): ButtonColors =
    ButtonDefaults.buttonColors(
        containerColor = colors.gray800,
        contentColor = colors.common00,
        disabledContainerColor = colors.gray850,
        disabledContentColor = colors.gray600,
    )

@Composable
private fun spareCountEditCompleteButtonColors(colors: OBRitColor): ButtonColors =
    ButtonDefaults.buttonColors(
        containerColor = colors.green300,
        contentColor = colors.common1000,
        disabledContainerColor = colors.gray800,
        disabledContentColor = colors.gray600,
    )

private val DETAIL_SPARE_COUNT_EDIT_CONTROL_BUTTON_SHAPE = RoundedCornerShape(22.dp)
private val DETAIL_SPARE_COUNT_EDIT_TITLE_TOP_PADDING = 46.dp
private val DETAIL_SPARE_COUNT_EDIT_CONTROLS_TOP_PADDING = 20.dp
private val DETAIL_SPARE_COUNT_EDIT_BUTTON_TOP_PADDING = 20.dp
private val DETAIL_SPARE_COUNT_EDIT_CONTROL_GAP = 24.dp
private val DETAIL_SPARE_COUNT_EDIT_COUNT_WIDTH = 100.dp
private val DETAIL_SPARE_COUNT_EDIT_CONTROL_BUTTON_SIZE = 80.dp
private val DETAIL_SPARE_COUNT_EDIT_COMPLETE_BUTTON_HEIGHT = 72.dp
private val DETAIL_SPARE_COUNT_EDIT_PREVIEW_TOP_PADDING = 80.dp
private const val DETAIL_SPARE_COUNT_EDIT_COMPLETE_BUTTON_TEXT = "수정 완료"
private const val MIN_SPARE_COUNT = 0
private const val MAX_SPARE_COUNT = 99
