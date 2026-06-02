package com.obrit.feature.register.screen.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.obrit.android.core.designsystem.component.dropdown.OBRitDropdown
import com.obrit.android.core.designsystem.component.dropdown.OBRitDropdownMenu
import com.obrit.obrit.shared.model.items.ReplacementPeriod

@Composable
internal fun ReplacementPeriodDropdown(
    selected: ReplacementPeriod?,
    onChange: (ReplacementPeriod) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = REPLACEMENT_PERIOD_DEFAULT_PLACEHOLDER,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var triggerSize by remember { mutableStateOf(IntSize.Zero) }
    val displayLabel =
        selected
            ?.let { period ->
                REPLACEMENT_PERIOD_OPTIONS.first { it.second == period }.first
            }.orEmpty()
    Box(modifier = modifier) {
        OBRitDropdown(
            value = displayLabel,
            onClick = { expanded = !expanded },
            placeholder = placeholder,
            expanded = expanded,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .onSizeChanged { triggerSize = it },
        )
        if (expanded) {
            ReplacementPeriodPopup(
                selected = selected,
                triggerWidth = triggerSize.width,
                onChange = { period ->
                    onChange(period)
                    expanded = false
                },
                onDismiss = { expanded = false },
            )
        }
    }
}

@Composable
private fun ReplacementPeriodPopup(
    selected: ReplacementPeriod?,
    triggerWidth: Int,
    onChange: (ReplacementPeriod) -> Unit,
    onDismiss: () -> Unit,
) {
    val density = LocalDensity.current
    val menuGapPx = with(density) { REPLACEMENT_PERIOD_MENU_GAP.roundToPx() }
    val positionProvider = remember(menuGapPx) { ReplacementPeriodMenuPositionProvider(menuGapPx) }
    Popup(
        popupPositionProvider = positionProvider,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        OBRitDropdownMenu(
            items = REPLACEMENT_PERIOD_OPTIONS.map { it.first },
            selectedIndex =
                REPLACEMENT_PERIOD_OPTIONS
                    .indexOfFirst { it.second == selected }
                    .takeIf { it >= 0 },
            onItemClick = { index -> onChange(REPLACEMENT_PERIOD_OPTIONS[index].second) },
            modifier = with(density) { Modifier.width(triggerWidth.toDp()) },
        )
    }
}

private class ReplacementPeriodMenuPositionProvider(
    private val verticalGapPx: Int,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val x = anchorBounds.left
        val belowY = anchorBounds.bottom + verticalGapPx
        val aboveY = anchorBounds.top - popupContentSize.height - verticalGapPx
        val fitsBelow = belowY + popupContentSize.height <= windowSize.height
        val y = if (fitsBelow) belowY else aboveY.coerceAtLeast(0)
        return IntOffset(x, y)
    }
}

internal val REPLACEMENT_PERIOD_OPTIONS =
    listOf(
        "1주일 이내" to ReplacementPeriod.WITHIN_WEEK,
        "2-4주 전" to ReplacementPeriod.WITHIN_MONTH,
        "1-3개월 전" to ReplacementPeriod.WITHIN_THREE_MONTHS,
        "3개월 이전" to ReplacementPeriod.OVER_THREE_MONTHS,
    )

private const val REPLACEMENT_PERIOD_DEFAULT_PLACEHOLDER = "마지막 교체 일자를 등록해주세요"
private val REPLACEMENT_PERIOD_MENU_GAP = 6.dp
