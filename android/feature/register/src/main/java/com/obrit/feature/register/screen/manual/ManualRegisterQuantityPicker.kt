package com.obrit.feature.register.screen.manual

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.register.viewmodel.MANUAL_REGISTER_MAX_QUANTITY
import com.obrit.feature.register.viewmodel.MANUAL_REGISTER_MIN_QUANTITY
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlin.math.abs

@Composable
internal fun ManualRegisterQuantityPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AtomRadius.Middle.dp))
                .background(colors.gray800)
                .padding(AtomSpacing.S1_5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        QuantityStepButton(
            iconRes = R.drawable.ic_stepper_minus,
            description = QUANTITY_DECREASE_DESCRIPTION,
            enabled = value > MANUAL_REGISTER_MIN_QUANTITY,
            onClick = { onValueChange(value - 1) },
        )
        QuantityNumberStrip(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
        )
        QuantityStepButton(
            iconRes = R.drawable.ic_stepper_plus,
            description = QUANTITY_INCREASE_DESCRIPTION,
            enabled = value < MANUAL_REGISTER_MAX_QUANTITY,
            onClick = { onValueChange(value + 1) },
        )
    }
}

@Composable
private fun QuantityNumberStrip(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = value.toQuantityIndex())
    QuantityScrollSyncEffect(listState = listState, value = value, onValueChange = onValueChange)
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val cellWidth = maxWidth / QUANTITY_VISIBLE_COUNT
        QuantitySelectionBox()
        LazyRow(
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState),
            contentPadding = PaddingValues(horizontal = (maxWidth - cellWidth) / 2),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(count = QUANTITY_COUNT) { index ->
                val number = index + MANUAL_REGISTER_MIN_QUANTITY
                QuantityNumber(
                    number = number,
                    distance = abs(number - value),
                    width = cellWidth,
                    onClick = { onValueChange(number) },
                )
            }
        }
    }
}

// 밀어서 바뀐 값은 폼으로 올리고, 버튼과 탭으로 바뀐 값은 목록을 해당 숫자로 움직인다.
@Composable
private fun QuantityScrollSyncEffect(
    listState: LazyListState,
    value: Int,
    onValueChange: (Int) -> Unit,
) {
    var isProgrammaticScroll by remember { mutableStateOf(false) }
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val isDragged by listState.interactionSource.collectIsDraggedAsState()
    LaunchedEffect(isDragged) {
        if (isDragged) isProgrammaticScroll = false
    }
    LaunchedEffect(listState) {
        snapshotFlow { listState.centeredItemIndex() }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { index ->
                if (!isProgrammaticScroll) currentOnValueChange(index + MANUAL_REGISTER_MIN_QUANTITY)
            }
    }
    LaunchedEffect(value) {
        val isUserScrolling = listState.isScrollInProgress && !isProgrammaticScroll
        if (isUserScrolling || listState.centeredItemIndex() == value.toQuantityIndex()) return@LaunchedEffect
        isProgrammaticScroll = true
        listState.animateScrollToItem(value.toQuantityIndex())
        isProgrammaticScroll = false
    }
}

private fun LazyListState.centeredItemIndex(): Int? {
    val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
    return layoutInfo.visibleItemsInfo
        .minByOrNull { item -> abs(item.offset + item.size / 2 - viewportCenter) }
        ?.index
}

private fun Int.toQuantityIndex(): Int = this - MANUAL_REGISTER_MIN_QUANTITY

@Composable
private fun QuantitySelectionBox() {
    Box(
        modifier =
            Modifier
                .size(QUANTITY_SELECTED_SIZE)
                .clip(RoundedCornerShape(AtomRadius.Small.dp))
                .background(LocalOBRitColor.current.gray700),
    )
}

@Composable
private fun QuantityNumber(
    number: Int,
    distance: Int,
    width: Dp,
    onClick: () -> Unit,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Box(
        modifier =
            Modifier
                .width(width)
                .height(QUANTITY_SELECTED_SIZE)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = number.toString(),
            style =
                typography.lg.copy(
                    color = if (distance == 0) colors.common00 else quantityNumberColor(distance),
                    fontWeight = if (distance == 0) FontWeight.Bold else FontWeight.Medium,
                ),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun quantityNumberColor(distance: Int): Color {
    val colors = LocalOBRitColor.current
    return when (distance) {
        1 -> colors.gray600
        2 -> colors.gray700
        else -> colors.gray750
    }
}

@Composable
private fun QuantityStepButton(
    @DrawableRes iconRes: Int,
    description: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalOBRitColor.current
    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(AtomRadius.Small.dp))
                .background(colors.gray900)
                .clickable(enabled = enabled, onClick = onClick)
                .padding(AtomSpacing.S2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = description,
            tint = if (enabled) colors.common00 else colors.gray700,
            modifier = Modifier.size(AtomSpacing.S7.dp),
        )
    }
}

private const val QUANTITY_VISIBLE_COUNT = 7
private const val QUANTITY_COUNT = MANUAL_REGISTER_MAX_QUANTITY - MANUAL_REGISTER_MIN_QUANTITY + 1
private const val QUANTITY_DECREASE_DESCRIPTION = "수량 줄이기"
private const val QUANTITY_INCREASE_DESCRIPTION = "수량 늘리기"

private val QUANTITY_SELECTED_SIZE = 36.dp

@Preview(name = "ManualRegisterQuantityPicker", showBackground = false)
@Composable
private fun ManualRegisterQuantityPickerPreview() {
    OBRitTheme(dynamicColor = false) {
        ManualRegisterQuantityPicker(value = 4, onValueChange = {})
    }
}
