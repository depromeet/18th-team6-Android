@file:Suppress("TooManyFunctions")

package com.obrit.feature.home.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.component.bottomsheet.OBRitBottomSheet
import com.obrit.android.core.designsystem.component.button.OBRitButtonDefaults
import com.obrit.android.core.designsystem.component.button.OBRitLargeFilledButton
import com.obrit.android.core.designsystem.component.slider.OBRitSlider
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.home.screen.homeSection.QuickItemListItem
import com.obrit.feature.home.viewmodel.ConsumableListSortOrder
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import com.obrit.obrit.shared.model.home.HomeItemCard
import kotlin.math.roundToInt

@Suppress("LongMethod", "LongParameterList")
@Composable
internal fun ItemListScreenContent(
    items: List<HomeItemCard>,
    hasNext: Boolean,
    sortOrder: ConsumableListSortOrder,
    ddayRange: IntRange,
    ddayFilterMax: Int,
    spareRange: IntRange,
    spareFilterMax: Int,
    action: ItemListScreenAction,
    modifier: Modifier = Modifier,
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    val currentOnLoadMoreItems by rememberUpdatedState(action.onLoadMoreItems)
    val reachedBottom by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            layoutInfo.totalItemsCount > 0 && lastVisibleIndex >= layoutInfo.totalItemsCount - 1
        }
    }
    LaunchedEffect(reachedBottom) {
        if (reachedBottom && hasNext) currentOnLoadMoreItems()
    }
    Box(modifier = modifier) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    start = AtomSpacing.S5.dp,
                    end = AtomSpacing.S5.dp,
                    top = LIST_FILTER_BAR_HEIGHT.dp + AtomSpacing.S3.dp,
                    bottom = AtomSpacing.S3.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
        ) {
            if (items.isEmpty()) {
                item { ConsumableListEmptyState(modifier = Modifier.fillParentMaxWidth()) }
            } else {
                items(items) { item -> QuickItemListItem(item = item, onItemClick = action.onItemClick) }
            }
        }
        ListFilterBar(
            sortOrder = sortOrder,
            ddayRange = ddayRange,
            ddayFilterMax = ddayFilterMax,
            spareRange = spareRange,
            spareFilterMax = spareFilterMax,
            onFilterClick = { showFilterSheet = true },
            onDdayCloseClick = { action.onDdayFilterChange(ddayRange.last) },
            onSpareCloseClick = { action.onSpareFilterChange(spareRange.last) },
            onSortClick = { showSortSheet = true },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart),
        )
    }
    if (showFilterSheet) {
        ListFilterBottomSheet(
            ddayRange = ddayRange,
            ddayFilterMax = ddayFilterMax,
            spareRange = spareRange,
            spareFilterMax = spareFilterMax,
            onFilterApply = { dday, spare ->
                action.onFilterApply(dday, spare)
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false },
        )
    }

    if (showSortSheet) {
        SortBottomSheet(
            sortOrder = sortOrder,
            onSortOrderChange = {
                action.onSortOrderChange(it)
                showSortSheet = false
            },
            onDismiss = { showSortSheet = false },
        )
    }
}

@Suppress("LongParameterList")
@Composable
private fun ListFilterBar(
    sortOrder: ConsumableListSortOrder,
    ddayRange: IntRange,
    ddayFilterMax: Int,
    spareRange: IntRange,
    spareFilterMax: Int,
    onFilterClick: () -> Unit,
    onDdayCloseClick: () -> Unit,
    onSpareCloseClick: () -> Unit,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    Row(
        modifier =
            modifier
                .background(colors.gray900)
                .padding(horizontal = AtomSpacing.S5.dp, vertical = AtomSpacing.S3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
    ) {
        FilterIconButton(onClick = onFilterClick)
        DdayFilterChip(
            ddayRange = ddayRange,
            filterMax = ddayFilterMax,
            onCloseClick = onDdayCloseClick,
            onOpenSheet = onFilterClick,
        )
        SpareFilterChip(
            spareRange = spareRange,
            filterMax = spareFilterMax,
            onCloseClick = onSpareCloseClick,
            onOpenSheet = onFilterClick,
        )
        Spacer(modifier = Modifier.weight(1f))
        SortFilterByOrder(sortOrder = sortOrder, onSortClick = onSortClick)
    }
}

@Composable
private fun FilterIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    Box(
        modifier =
            modifier
                .size(FILTER_ICON_BUTTON_SIZE.dp)
                .clip(RoundedCornerShape(AtomRadius.Middle.dp))
                .background(colors.gray800)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_filter),
            contentDescription = null,
            tint = colors.common00,
            modifier = Modifier.size(AtomSpacing.S4.dp),
        )
    }
}

@Composable
private fun DdayFilterChip(
    ddayRange: IntRange,
    filterMax: Int,
    onCloseClick: () -> Unit,
    onOpenSheet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSelected = filterMax < ddayRange.last
    val label = if (isSelected) ddayLabel(filterMax) else "디데이"
    FilterChip(
        label = label,
        isSelected = isSelected,
        showDropdown = true,
        onIconClick = if (isSelected) onCloseClick else onOpenSheet,
        modifier = modifier,
    )
}

@Composable
private fun SpareFilterChip(
    spareRange: IntRange,
    filterMax: Int,
    onCloseClick: () -> Unit,
    onOpenSheet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSelected = filterMax < spareRange.last
    val label = if (isSelected) "${filterMax}개 이하" else "여분"
    FilterChip(
        label = label,
        isSelected = isSelected,
        showDropdown = true,
        onIconClick = if (isSelected) onCloseClick else onOpenSheet,
        modifier = modifier,
    )
}

@Suppress("LongMethod")
@Composable
private fun FilterChip(
    label: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    showDropdown: Boolean = false,
    onIconClick: (() -> Unit)? = null,
) {
    val typography = LocalOBRitTypography.current
    val colors = LocalOBRitColor.current
    val contentColor = if (isSelected) colors.common1000 else colors.common00
    val context = LocalContext.current
    val backgroundDrawable =
        remember {
            ContextCompat.getDrawable(context, R.drawable.ic_filter_chip_background)
        }
    backgroundDrawable?.state =
        if (isSelected) intArrayOf(android.R.attr.state_selected) else intArrayOf()
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(AtomRadius.Small.dp))
                .drawBehind {
                    backgroundDrawable?.let { drawable ->
                        drawable.setBounds(0, 0, size.width.roundToInt(), size.height.roundToInt())
                        drawIntoCanvas { drawable.draw(it.nativeCanvas) }
                    }
                }.then(if (onIconClick != null) Modifier.clickable(onClick = onIconClick) else Modifier)
                .padding(horizontal = AtomSpacing.S3.dp, vertical = AtomSpacing.S2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S1.dp),
    ) {
        Text(
            text = label,
            style = typography.base.copy(fontWeight = FontWeight.SemiBold),
            color = contentColor,
            maxLines = 1,
        )
        if (showDropdown) {
            Icon(
                painter =
                    painterResource(
                        id = if (isSelected) R.drawable.ic_close else R.drawable.ic_dropdown_chevron_down,
                    ),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(AtomSpacing.S4.dp),
            )
        }
    }
}

@Composable
private fun SortFilterByOrder(
    sortOrder: ConsumableListSortOrder,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current
    val colors = LocalOBRitColor.current
    val shape = RoundedCornerShape(AtomRadius.Small.dp)

    Row(
        modifier =
            modifier
                .border(BorderStroke(FILTER_CHIP_STROKE_WIDTH.dp, colors.gray700), shape)
                .clip(shape)
                .clickable(onClick = onSortClick)
                .padding(horizontal = AtomSpacing.S3.dp, vertical = AtomSpacing.S2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S1.dp),
    ) {
        Text(
            text = sortOrder.displayName,
            style = typography.base.copy(fontWeight = FontWeight.SemiBold),
            color = colors.common00,
            maxLines = 1,
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_dropdown_chevron_down),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(AtomSpacing.S4.dp),
        )
    }
}

@Suppress("LongParameterList")
@Composable
private fun ListFilterBottomSheet(
    ddayRange: IntRange,
    ddayFilterMax: Int,
    spareRange: IntRange,
    spareFilterMax: Int,
    onFilterApply: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var pendingDday by remember { mutableIntStateOf(ddayFilterMax) }
    var pendingSpare by remember { mutableIntStateOf(spareFilterMax) }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        FilterBottomSheet(onDismiss = onDismiss) {
            FilterSheetContent(
                ddayRange = ddayRange,
                pendingDday = pendingDday,
                spareRange = spareRange,
                pendingSpare = pendingSpare,
                onDdayChange = { pendingDday = it },
                onSpareChange = { pendingSpare = it },
                onReset = {
                    pendingDday = ddayRange.last
                    pendingSpare = spareRange.last
                },
                onApply = { onFilterApply(pendingDday, pendingSpare) },
            )
        }
    }
}

@Composable
private fun FilterBottomSheet(
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(FILTER_SHEET_SCRIM_COLOR)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                ),
        contentAlignment = Alignment.BottomCenter,
    ) {
        OBRitBottomSheet(
            modifier =
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                ),
            content = content,
        )
    }
}

@Suppress("LongParameterList")
@Composable
private fun FilterSheetContent(
    ddayRange: IntRange,
    pendingDday: Int,
    spareRange: IntRange,
    pendingSpare: Int,
    onDdayChange: (Int) -> Unit,
    onSpareChange: (Int) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S6.dp),
    ) {
        FilterSliderSection(
            title = "교체 디데이",
            valueLabel = ddayLabel(pendingDday),
            sliderValue = pendingDday,
            sliderRange = ddayRange,
            onValueChange = onDdayChange,
        )
        FilterSliderSection(
            title = "여분",
            valueLabel = "${pendingSpare}개 이하",
            sliderValue = pendingSpare,
            sliderRange = spareRange,
            onValueChange = onSpareChange,
        )
        FilterButtonRow(onReset = onReset, onApply = onApply)
    }
}

@Suppress("LongParameterList")
@Composable
private fun FilterSliderSection(
    title: String,
    valueLabel: String,
    sliderValue: Int,
    sliderRange: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current
    val colors = LocalOBRitColor.current
    val effectiveValue =
        if (sliderRange.first == sliderRange.last) {
            sliderRange.last.toFloat()
        } else {
            sliderValue.coerceIn(sliderRange).toFloat()
        }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp)) {
        Text(
            text = title,
            style = typography.xl.copy(color = colors.gray300, fontWeight = FontWeight.Bold),
        )
        FilterValueLabel(valueLabel = valueLabel)
        OBRitSlider(
            value = effectiveValue,
            onValueChange = { onValueChange(it.roundToInt()) },
            valueRange = sliderRange.first.toFloat()..sliderRange.last.toFloat(),
            steps = (sliderRange.last - sliderRange.first - 1).coerceAtLeast(0),
        )
    }
}

@Composable
private fun FilterValueLabel(
    valueLabel: String,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current
    val colors = LocalOBRitColor.current
    val suffixIndex = valueLabel.lastIndexOf(" 이하")
    val prefix = if (suffixIndex >= 0) valueLabel.substring(0, suffixIndex) else valueLabel
    Row(modifier = modifier, verticalAlignment = Alignment.Bottom) {
        Text(
            text = prefix,
            style = typography.xl6.copy(color = colors.common00, fontWeight = FontWeight.Bold),
        )
        if (suffixIndex >= 0) {
            Text(
                text = " 이하",
                style =
                    typography.xl2.copy(
                        color = colors.gray500,
                        fontWeight = FontWeight.Bold,
                    ),
                modifier = Modifier.padding(bottom = 2.dp),
            )
        }
    }
}

@Composable
private fun FilterButtonRow(
    onReset: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(LIST_FILTER_BAR_HEIGHT.dp)
                    .clip(RoundedCornerShape(AtomRadius.Middle.dp))
                    .background(colors.gray800)
                    .clickable(onClick = onReset),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_reset),
                contentDescription = null,
                tint = colors.common00,
                modifier = Modifier.size(LIST_RESET_ICON_SIZE.dp),
            )
        }
        OBRitLargeFilledButton(
            onClick = onApply,
            colors = OBRitButtonDefaults.commonButtonColors(),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = "적용",
                style =
                    LocalOBRitTypography.current.xl.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                color = colors.common1000,
            )
        }
    }
}

@Composable
private fun ConsumableListEmptyState(modifier: Modifier = Modifier) {
    val typography = LocalOBRitTypography.current
    val colors = LocalOBRitColor.current
    Column(
        modifier =
            modifier.padding(
                horizontal = AtomSpacing.S5.dp,
                vertical = EMPTY_STATE_VERTICAL_PADDING.dp,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
    ) {
        Text(
            text = "아직 등록된 소모품이 없어요",
            style = typography.xl3.copy(fontWeight = FontWeight.Bold),
            color = colors.common00,
        )
        Text(
            text = "가지고 계신 소모품을 등록하고 관리해 보세요",
            style = typography.base.copy(fontWeight = FontWeight.Medium),
            color = colors.gray300.copy(alpha = EMPTY_STATE_SUBTITLE_ALPHA),
        )
    }
}

@Composable
private fun SortBottomSheet(
    sortOrder: ConsumableListSortOrder,
    onSortOrderChange: (ConsumableListSortOrder) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        FilterBottomSheet(onDismiss = onDismiss) {
            SortBottomSheetContent(
                sortOrder = sortOrder,
                onSortOrderChange = onSortOrderChange,
            )
        }
    }
}

@Suppress("LongMethod")
@Composable
private fun SortBottomSheetContent(
    sortOrder: ConsumableListSortOrder,
    onSortOrderChange: (ConsumableListSortOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current
    val colors = LocalOBRitColor.current
    var clickedSortOrder by remember { mutableStateOf(sortOrder) }
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = AtomSpacing.S4.dp, horizontal = AtomSpacing.S5.dp),
    ) {
        ConsumableListSortOrder.entries.forEach { order ->
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = AtomSpacing.S4.dp)
                        .clickable {
                            clickedSortOrder = order
                            onSortOrderChange(order)
                        },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = order.displayName,
                    style = typography.xl.copy(fontWeight = FontWeight.Bold),
                    color = colors.common00,
                    modifier = Modifier.weight(1f),
                )
                if (clickedSortOrder == order) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sort_selected),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(AtomSpacing.S6.dp),
                    )
                }
            }
        }
    }
}

@Suppress("MagicNumber")
@Preview(showBackground = true, backgroundColor = 0xFF1C1B1F, widthDp = 412)
@Composable
private fun SortBottomSheetContentPreview() {
    OBRitTheme {
        SortBottomSheetContent(
            sortOrder = ConsumableListSortOrder.REPLACE_IMMINENT,
            onSortOrderChange = {},
        )
    }
}

private fun ddayLabel(days: Int): String = if (days >= 0) "D-$days 이하" else "D+${-days} 이하"

@Suppress("MagicNumber")
private val FILTER_SHEET_SCRIM_COLOR = Color(0x99000000)
private const val LIST_FILTER_BAR_HEIGHT = AtomSpacing.S14
private const val FILTER_ICON_BUTTON_SIZE = 38f
private const val FILTER_CHIP_STROKE_WIDTH = 1f
private const val LIST_RESET_ICON_SIZE = AtomSpacing.S6
private const val EMPTY_STATE_VERTICAL_PADDING = 96f
private const val EMPTY_STATE_SUBTITLE_ALPHA = 0.64f
