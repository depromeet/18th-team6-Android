package com.obrit.feature.register.screen.category

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import com.obrit.obrit.shared.model.categories.Category

internal data class CategoryCellStyle(
    val imageSize: Dp,
    val selectedBorderWidth: Dp,
    val nameStyle: TextStyle,
)

@Composable
internal fun CategoryGridRow(
    categories: List<Category>,
    isSelected: (Category) -> Boolean,
    onClick: (Category) -> Unit,
    style: CategoryCellStyle,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        categories.forEach { category ->
            CategoryGridCell(
                category = category,
                selected = isSelected(category),
                onClick = { onClick(category) },
                style = style,
            )
        }
        repeat(CATEGORY_GRID_COLUMNS - categories.size) {
            Spacer(modifier = Modifier.width(style.imageSize))
        }
    }
}

@Composable
private fun CategoryGridCell(
    category: Category,
    selected: Boolean,
    onClick: () -> Unit,
    style: CategoryCellStyle,
) {
    val colors = LocalOBRitColor.current
    Column(
        modifier =
            Modifier
                .width(style.imageSize)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
    ) {
        CategoryGridCellImage(iconUrl = category.iconUrl, selected = selected, style = style)
        Text(
            text = category.name,
            style = style.nameStyle.copy(color = colors.common00),
            textAlign = TextAlign.Center,
            maxLines = CATEGORY_CELL_NAME_MAX_LINES,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CategoryGridCellImage(
    iconUrl: String,
    selected: Boolean,
    style: CategoryCellStyle,
) {
    val colors = LocalOBRitColor.current
    Box(
        modifier =
            Modifier
                .size(style.imageSize)
                .clip(CircleShape)
                .background(colors.gray750)
                .then(
                    if (selected) {
                        Modifier.border(style.selectedBorderWidth, colors.green300, CircleShape)
                    } else {
                        Modifier
                    },
                ),
    ) {
        AsyncImage(
            model = iconUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
internal fun CategoryRailItem(
    name: String,
    selected: Boolean,
    unselectedColor: Color,
    onClick: () -> Unit,
    hasSelection: Boolean = false,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AtomRadius.Small.dp))
                .background(if (selected) colors.gray800 else Color.Transparent)
                .clickable(onClick = onClick)
                .padding(horizontal = AtomSpacing.S3.dp, vertical = AtomSpacing.S2_5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S1.dp),
    ) {
        Text(
            text = name,
            style =
                typography.xl.copy(
                    color = if (selected) colors.common00 else unselectedColor,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                ),
            modifier = Modifier.weight(1f, fill = false),
        )
        if (hasSelection) {
            Box(
                modifier =
                    Modifier
                        .size(CATEGORY_RAIL_DOT_SIZE)
                        .clip(CircleShape)
                        .background(colors.green300),
            )
        }
    }
}

@Composable
internal fun CategoryEmptyStateCard(
    modifier: Modifier = Modifier,
    onDirectRegisterClick: (() -> Unit)? = null,
    errorMessage: String? = null,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val description = errorMessage ?: CATEGORY_SHEET_EMPTY_DESCRIPTION.takeIf { onDirectRegisterClick != null }
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(AtomRadius.ExtraLarge.dp))
                .background(colors.gray850)
                .padding(horizontal = AtomSpacing.S5.dp, vertical = AtomSpacing.S20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S5.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
        ) {
            Text(
                text = CATEGORY_SHEET_EMPTY_TITLE,
                style = typography.xl3.copy(fontWeight = FontWeight.Bold, color = colors.common00),
                textAlign = TextAlign.Center,
            )
            if (description != null) {
                Text(
                    text = description,
                    style =
                        typography.base.copy(
                            fontWeight = FontWeight.Medium,
                            color = colors.gray300.copy(alpha = CATEGORY_EMPTY_DESCRIPTION_ALPHA),
                        ),
                    textAlign = TextAlign.Center,
                )
            }
        }
        if (onDirectRegisterClick != null) {
            CategoryEmptyStateButton(onClick = onDirectRegisterClick)
        }
    }
}

@Composable
private fun CategoryEmptyStateButton(onClick: () -> Unit) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Row(
        modifier =
            Modifier
                .clip(RoundedCornerShape(AtomRadius.Small.dp))
                .background(colors.common00)
                .clickable(onClick = onClick)
                .padding(horizontal = AtomSpacing.S3.dp, vertical = AtomSpacing.S2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S0_5.dp),
    ) {
        Text(
            text = CATEGORY_SHEET_EMPTY_BUTTON_LABEL,
            style = typography.base.copy(fontWeight = FontWeight.SemiBold, color = colors.common1000),
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = colors.common1000,
            modifier = Modifier.size(CATEGORY_EMPTY_BUTTON_ICON_SIZE),
        )
    }
}

internal const val CATEGORY_GRID_COLUMNS = 3

private const val CATEGORY_CELL_NAME_MAX_LINES = 2
private const val CATEGORY_SHEET_EMPTY_TITLE = "소모품 검색 결과가 없어요!"
private const val CATEGORY_SHEET_EMPTY_DESCRIPTION = "하단 버튼을 통해 직접 소모품을 등록할 수 있어요."
private const val CATEGORY_SHEET_EMPTY_BUTTON_LABEL = "직접 등록하기"
private const val CATEGORY_EMPTY_DESCRIPTION_ALPHA = 0.64f

private val CATEGORY_RAIL_DOT_SIZE = 7.dp
private val CATEGORY_EMPTY_BUTTON_ICON_SIZE = AtomSpacing.S4.dp
