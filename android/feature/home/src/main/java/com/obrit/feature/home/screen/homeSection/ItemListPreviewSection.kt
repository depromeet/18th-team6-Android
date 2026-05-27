@file:Suppress("TooManyFunctions")

package com.obrit.feature.home.screen.homeSection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import coil.compose.AsyncImage
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.component.bottomsheet.OBRitBottomSheet
import com.obrit.android.core.designsystem.component.button.OBRitButtonDefaults
import com.obrit.android.core.designsystem.component.button.OBRitLargeFilledButton
import com.obrit.android.core.designsystem.component.card.OBRitCardLevel
import com.obrit.android.core.designsystem.component.card.OBRitCardList
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.home.viewmodel.ConsumableListSortOrder
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import com.obrit.obrit.shared.model.home.HomeItemCard
import kotlin.math.roundToInt

private const val LIST_PREVIEW_COUNT = 3

@Composable
internal fun ItemListPreviewSection(
    items: List<HomeItemCard>,
    sortOrder: ConsumableListSortOrder,
    onSortOrderChange: (ConsumableListSortOrder) -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current
    var showSortSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.padding(horizontal = AtomSpacing.S5.dp, vertical = AtomSpacing.S5.dp),
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S3.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SortDropdown(
                sortOrder = sortOrder,
                onClick = { showSortSheet = true },
            )

            Text(
                text = "미리보기",
                style = typography.xl,
                color = LocalOBRitColor.current.common00,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            )
        }

        PreviewItemList(items = items.take(LIST_PREVIEW_COUNT))
        OBRitLargeFilledButton(
            text = "더보기",
            onClick = onMoreClick,
            colors = OBRitButtonDefaults.defaultButtonColors(),
            modifier = Modifier.fillMaxWidth(),
        )
    }

    if (showSortSheet) {
        SortBottomSheet(
            sortOrder = sortOrder,
            onSortOrderChange = {
                onSortOrderChange(it)
                showSortSheet = false
            },
            onDismiss = { showSortSheet = false },
        )
    }
}

@Composable
private fun SortDropdown(
    sortOrder: ConsumableListSortOrder,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val typography = LocalOBRitTypography.current
    val colors = LocalOBRitColor.current
    val context = LocalContext.current
    val backgroundDrawable =
        ContextCompat.getDrawable(context, R.drawable.ic_filter_chip_background)
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(AtomRadius.Small.dp))
                .drawBehind {
                    backgroundDrawable?.let { drawable ->
                        drawable.setBounds(0, 0, size.width.roundToInt(), size.height.roundToInt())
                        drawIntoCanvas { drawable.draw(it.nativeCanvas) }
                    }
                }.then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
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
        SheetContainer(onDismiss = onDismiss) {
            SortBottomSheetContent(
                sortOrder = sortOrder,
                onSortOrderChange = onSortOrderChange,
            )
        }
    }
}

@Composable
private fun SheetContainer(
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(SORT_SHEET_SCRIM_COLOR)
                .clickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter,
    ) {
        OBRitBottomSheet(
            modifier = Modifier.clickable(onClick = {}),
            content = content,
        )
    }
}

@Composable
private fun SortBottomSheetContent(
    sortOrder: ConsumableListSortOrder,
    onSortOrderChange: (ConsumableListSortOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current
    val colors = LocalOBRitColor.current
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
                        .clickable { onSortOrderChange(order) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = order.displayName,
                    style = typography.xl.copy(fontWeight = FontWeight.Bold),
                    color = colors.common00,
                    modifier = Modifier.weight(1f),
                )
                if (sortOrder == order) {
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

@Composable
private fun PreviewItemList(
    items: List<HomeItemCard>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
    ) {
        items.forEach { item ->
            QuickItemListItem(item = item)
        }
    }
}

@Composable
internal fun QuickItemListItem(item: HomeItemCard) {
    OBRitCardList(
        level = homeItemCardLevel(item),
        title = item.name,
        daysInUseLabel = "${item.daysInUse}일",
        replaceLabel = item.replacementDday,
        sparesLabel = "여분 ${item.spareQuantity}개",
        image = {
            if (item.iconUrl.isNotEmpty()) {
                AsyncImage(
                    model = item.iconUrl,
                    contentDescription = item.name,
                )
            }
        },
    )
}

@Suppress("MagicNumber")
private fun homeItemCardLevel(item: HomeItemCard): OBRitCardLevel {
    val hasSpare = item.spareQuantity > 0
    return if (hasSpare) OBRitCardLevel.L1 else OBRitCardLevel.L2
}

@Suppress("MagicNumber")
private val SORT_SHEET_SCRIM_COLOR = Color(0x99000000)

@Suppress("MagicNumber")
@Preview(showBackground = true, backgroundColor = 0xFF1D1B20, widthDp = 412)
@Composable
private fun ItemListPreview() {
    OBRitTheme {
        ItemListPreviewSection(
            items =
                listOf(
                    HomeItemCard(1L, "면도기", 30, "교체 D+7", 0, ""),
                    HomeItemCard(2L, "칫솔", 27, "교체 D-day", 1, ""),
                    HomeItemCard(3L, "샴푸", 22, "교체 D-2", 0, ""),
                    HomeItemCard(4L, "필터", 10, "교체 D-10", 3, ""),
                ),
            sortOrder = ConsumableListSortOrder.REPLACE_IMMINENT,
            onSortOrderChange = {},
            onMoreClick = {},
        )
    }
}
