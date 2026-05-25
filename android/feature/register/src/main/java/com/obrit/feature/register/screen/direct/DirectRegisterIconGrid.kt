package com.obrit.feature.register.screen.direct

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
internal fun IconGridField(
    label: String,
    totalCount: Int,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit,
    headerSlot: @Composable (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AtomSpacing.S3.dp)) {
        headerSlot(label)
        IconGrid(
            totalCount = totalCount,
            selectedIndex = selectedIndex,
            onSelect = onSelect,
        )
    }
}

@Composable
private fun IconGrid(
    totalCount: Int,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit,
) {
    val rowCount = (totalCount + ICON_GRID_COLUMNS - 1) / ICON_GRID_COLUMNS
    Column(verticalArrangement = Arrangement.spacedBy(ICON_GRID_ROW_GAP)) {
        repeat(rowCount) { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                repeat(ICON_GRID_COLUMNS) { columnIndex ->
                    val itemIndex = rowIndex * ICON_GRID_COLUMNS + columnIndex
                    if (itemIndex < totalCount) {
                        IconCircle(
                            selected = itemIndex == selectedIndex,
                            onClick = { onSelect(itemIndex) },
                        )
                    } else {
                        Spacer(modifier = Modifier.size(ICON_CIRCLE_SIZE))
                    }
                }
            }
        }
    }
}

@Composable
private fun IconCircle(
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalOBRitColor.current
    val borderModifier =
        if (selected) {
            Modifier.border(
                width = ICON_CIRCLE_SELECTED_BORDER_WIDTH,
                color = colors.green300,
                shape = CircleShape,
            )
        } else {
            Modifier
        }
    // 추후 GET /categories/icons 연동 시 Image / AsyncImage 추가.
    Box(
        modifier =
            Modifier
                .size(ICON_CIRCLE_SIZE)
                .clip(CircleShape)
                .background(colors.gray750)
                .then(borderModifier)
                .clickable(onClick = onClick),
    )
}

private const val ICON_GRID_COLUMNS = 5
private val ICON_CIRCLE_SIZE = 60.dp
private val ICON_CIRCLE_SELECTED_BORDER_WIDTH = 2.dp
private val ICON_GRID_ROW_GAP = AtomSpacing.S3.dp
