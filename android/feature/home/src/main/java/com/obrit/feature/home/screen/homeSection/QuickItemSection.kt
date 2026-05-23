@file:Suppress("TooManyFunctions")

package com.obrit.feature.home.screen.homeSection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.obrit.android.core.designsystem.component.button.OBRitButtonDefaults
import com.obrit.android.core.designsystem.component.button.OBRitLargeFilledButton
import com.obrit.android.core.designsystem.component.card.OBRitCardLevel
import com.obrit.android.core.designsystem.component.card.OBRitCardList
import com.obrit.android.core.designsystem.component.dropdown.OBRitDropdown
import com.obrit.android.core.designsystem.component.dropdown.OBRitDropdownMenu
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.home.viewmodel.Bucket
import com.obrit.feature.home.viewmodel.BucketLevel
import com.obrit.feature.home.viewmodel.BucketStatus
import com.obrit.feature.home.viewmodel.ConsumableListSortOrder
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private const val LIST_PREVIEW_COUNT = 3

@Composable
internal fun QuickItemSection(
    buckets: List<Bucket>,
    sortOrder: ConsumableListSortOrder,
    onSortOrderChange: (ConsumableListSortOrder) -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current
    var expanded by remember { mutableStateOf(false) }
    val sortedBuckets = remember(buckets, sortOrder) { sortItems(buckets, sortOrder) }

    Column(
        modifier = modifier.padding(horizontal = AtomSpacing.S5.dp, vertical = AtomSpacing.S5.dp),
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S3.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SortDropdown(
                sortOrder = sortOrder,
                expanded = expanded,
                onExpand = { expanded = !expanded },
                onSelect = { order ->
                    onSortOrderChange(order)
                    expanded = false
                },
            )

            Text(
                text = "미리보기",
                style = typography.xl,
                color = LocalOBRitColor.current.common00,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            )
        }

        QuickItemList(buckets = sortedBuckets.take(LIST_PREVIEW_COUNT))
        OBRitLargeFilledButton(
            text = "더보기",
            onClick = onMoreClick,
            colors = OBRitButtonDefaults.defaultButtonColors(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun SortDropdown(
    sortOrder: ConsumableListSortOrder,
    expanded: Boolean,
    onExpand: () -> Unit,
    onSelect: (ConsumableListSortOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = remember { ConsumableListSortOrder.entries.map { it.displayName } }
    val selectedIndex = ConsumableListSortOrder.entries.indexOf(sortOrder)
    val density = LocalDensity.current
    val triggerHeightPx = remember(density) { with(density) { AtomSpacing.S14.dp.roundToPx() } }

    Box(modifier = modifier.width(IntrinsicSize.Max)) {
        OBRitDropdown(
            value = sortOrder.displayName,
            onClick = onExpand,
            expanded = expanded,
        )
        if (expanded) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(x = 0, y = triggerHeightPx),
                onDismissRequest = onExpand,
            ) {
                OBRitDropdownMenu(
                    items = items,
                    selectedIndex = selectedIndex,
                    onItemClick = { index ->
                        onSelect(ConsumableListSortOrder.entries[index])
                    },
                )
            }
        }
    }
}

@Composable
private fun QuickItemList(
    buckets: List<Bucket>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
    ) {
        buckets.forEach { bucket ->
            QuickItemListItem(bucket = bucket)
        }
    }
}

@Composable
internal fun QuickItemListItem(bucket: Bucket) {
    val daysUntil = remember(bucket.replacementDate) { daysUntil(bucket.replacementDate) }
    OBRitCardList(
        level = bucketListLevel(bucket),
        title = bucket.title,
        daysInUseLabel = "${bucket.daysInUse}일",
        replaceLabel = replaceLabel(daysUntil),
        sparesLabel = "여분 ${bucket.spare}개",
    )
}

private fun bucketListLevel(bucket: Bucket): OBRitCardLevel =
    when (bucket.level) {
        BucketLevel.HAS_OVERDUE -> OBRitCardLevel.L1
        BucketLevel.NONE_OVERDUE -> OBRitCardLevel.L2
        BucketLevel.NONE_WARN -> OBRitCardLevel.L3
        BucketLevel.HAS_WARN -> OBRitCardLevel.L4
        BucketLevel.NONE_SAFE -> OBRitCardLevel.L5
        BucketLevel.HAS_SAFE -> OBRitCardLevel.L6
    }

private fun replaceLabel(daysUntil: Int): String =
    when {
        daysUntil == 0 -> "교체 D-day"
        daysUntil > 0 -> "교체 D-$daysUntil"
        else -> "교체 D+${-daysUntil}"
    }

private fun daysUntil(replacementDate: String): Int =
    ChronoUnit.DAYS
        .between(LocalDate.now(), LocalDate.parse(replacementDate))
        .toInt()

private fun sortItems(
    buckets: List<Bucket>,
    sortOrder: ConsumableListSortOrder,
): List<Bucket> =
    when (sortOrder) {
        ConsumableListSortOrder.REPLACE_IMMINENT -> buckets.sortedBy { daysUntil(it.replacementDate) }
        ConsumableListSortOrder.LEAST_SPARE -> buckets.sortedBy { it.spare }
        ConsumableListSortOrder.OLDEST_REPLACEMENT -> buckets.sortedByDescending { it.daysInUse }
        ConsumableListSortOrder.ALPHABETICAL -> buckets.sortedBy { it.title }
    }

@Suppress("MagicNumber")
@Preview(showBackground = true, backgroundColor = 0xFF1D1B20, widthDp = 412)
@Composable
private fun QuickItem() {
    OBRitTheme {
        QuickItemSection(
            buckets =
                listOf(
                    Bucket(
                        BucketStatus.DANGER,
                        "면도기",
                        0,
                        "2026-05-20",
                        BucketLevel.HAS_OVERDUE,
                        30,
                    ),
                    Bucket(BucketStatus.DANGER, "칫솔", 1, "2026-05-26", BucketLevel.NONE_WARN, 27),
                    Bucket(BucketStatus.WARN, "샴푸", 0, "2026-05-25", BucketLevel.NONE_SAFE, 22),
                    Bucket(BucketStatus.WARN, "필터", 3, "2026-05-30", BucketLevel.HAS_SAFE, 10),
                ),
            sortOrder = ConsumableListSortOrder.REPLACE_IMMINENT,
            onSortOrderChange = {},
            onMoreClick = {},
        )
    }
}
