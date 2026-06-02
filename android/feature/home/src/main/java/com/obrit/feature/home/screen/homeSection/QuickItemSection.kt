@file:Suppress("TooManyFunctions")
@file:JvmName("QuickItemSectionKt")

package com.obrit.feature.home.screen.homeSection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.obrit.android.core.designsystem.component.card.OBRitCardGrid
import com.obrit.android.core.designsystem.component.card.OBRitCardLevel
import com.obrit.android.core.designsystem.component.chip.OBRitChip
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import com.obrit.obrit.shared.model.ReplacementDate
import com.obrit.obrit.shared.model.home.HomeBucketGroup
import com.obrit.obrit.shared.model.home.HomeBucketItem
import com.obrit.obrit.shared.model.home.HomeBucketType
import com.obrit.obrit.shared.model.home.HomeStatusLevel
import com.obrit.obrit.shared.model.home.ItemBucket
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
internal fun QuickItemSection(
    buckets: List<HomeBucketGroup>,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (buckets.isEmpty()) return
    val hasDangerItems = buckets.find { it.bucket == HomeBucketType.DANGER }?.items?.isNotEmpty() == true
    var selectedType by remember { mutableStateOf(if (hasDangerItems) buckets.first().bucket else buckets.last().bucket) }
    val selectedItems =
        remember(buckets, selectedType) {
            buckets.find { it.bucket == selectedType }?.items ?: emptyList()
        }

    Column(
        modifier = modifier.padding(vertical = AtomSpacing.S5.dp),
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S3.dp),
    ) {
        BucketFilterChipRow(
            buckets = buckets,
            selectedType = selectedType,
            onTypeSelect = { selectedType = it },
        )
        BucketCardRow(items = selectedItems, onItemClick = onItemClick)
    }
}

@Composable
private fun BucketFilterChipRow(
    buckets: List<HomeBucketGroup>,
    selectedType: HomeBucketType,
    onTypeSelect: (HomeBucketType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = AtomSpacing.S5.dp),
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
    ) {
        buckets.filter { it.count > 0 }.forEach { group ->
            OBRitChip(
                text = group.bucket.displayName,
                number = group.count,
                selected = group.bucket == selectedType,
                onClick = { onTypeSelect(group.bucket) },
            )
        }
    }
}

@Composable
private fun BucketCardRow(
    items: List<HomeBucketItem>,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = AtomSpacing.S5.dp),
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S3.dp),
    ) {
        items(items) { item ->
            BucketCard(item = item, onItemClick = onItemClick)
        }
    }
}

@Composable
private fun BucketCard(
    item: HomeBucketItem,
    onItemClick: (Long) -> Unit,
) {
    OBRitCardGrid(
        level = itemCardLevel(item.itemBucket),
        title = item.name,
        stockCount = item.spareQuantity,
        daysLabel = daysLabel(item.nextReplacementDate),
        modifier = Modifier.clickable { onItemClick(item.itemId) },
        image = { AsyncImage(model = item.iconUrl, contentDescription = null) },
    )
}

private val HomeBucketType.displayName: String
    get() =
        when (this) {
            HomeBucketType.DANGER -> "위험"
            HomeBucketType.WARNING -> "경고"
            HomeBucketType.UNKNOWN -> ""
        }

private fun itemCardLevel(itemBucket: ItemBucket): OBRitCardLevel =
    when (itemBucket) {
        ItemBucket.NONE_OVERDUE -> OBRitCardLevel.L1
        ItemBucket.NONE_WARN -> OBRitCardLevel.L2
        ItemBucket.HAS_OVERDUE -> OBRitCardLevel.L3
        ItemBucket.HAS_WARN -> OBRitCardLevel.L4
        ItemBucket.NONE_SAFE -> OBRitCardLevel.L5
        ItemBucket.HAS_SAFE -> OBRitCardLevel.L6
    }

private fun daysLabel(replacementDate: ReplacementDate?): String {
    replacementDate ?: return "-"
    val days =
        ChronoUnit.DAYS
            .between(LocalDate.now(), LocalDate.parse(replacementDate.value))
            .toInt()
    return when {
        days == 0 -> "D-day"
        days > 0 -> "D-$days"
        else -> "D+${-days}"
    }
}

@Suppress("MagicNumber")
private val previewBuckets =
    listOf(
        HomeBucketGroup(
            bucket = HomeBucketType.DANGER,
            count = 2,
            items =
                listOf(
                    HomeBucketItem(
                        itemId = 1,
                        name = "면도기",
                        iconUrl = "",
                        spareQuantity = 0,
                        nextReplacementDate = ReplacementDate("2026-05-23"),
                        status = HomeStatusLevel.DANGER,
                        itemBucket = ItemBucket.NONE_OVERDUE,
                    ),
                    HomeBucketItem(
                        itemId = 2,
                        name = "칫솔",
                        iconUrl = "",
                        spareQuantity = 1,
                        nextReplacementDate = ReplacementDate("2026-05-26"),
                        status = HomeStatusLevel.DANGER,
                        itemBucket = ItemBucket.NONE_WARN,
                    ),
                ),
        ),
        HomeBucketGroup(
            bucket = HomeBucketType.WARNING,
            count = 1,
            items =
                listOf(
                    HomeBucketItem(
                        itemId = 3,
                        name = "필터",
                        iconUrl = "",
                        spareQuantity = 3,
                        nextReplacementDate = ReplacementDate("2026-05-26"),
                        status = HomeStatusLevel.WARNING,
                        itemBucket = ItemBucket.NONE_SAFE,
                    ),
                ),
        ),
    )

@Preview(showBackground = true, backgroundColor = 0xFF1D1B20)
@Composable
private fun QuickItemSectionPreview() {
    OBRitTheme {
        QuickItemSection(buckets = previewBuckets, onItemClick = {})
    }
}
