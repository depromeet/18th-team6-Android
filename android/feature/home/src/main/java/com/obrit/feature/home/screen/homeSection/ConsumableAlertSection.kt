@file:Suppress("TooManyFunctions")

package com.obrit.feature.home.screen.homeSection

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
import com.obrit.android.core.designsystem.component.card.OBRitCardGrid
import com.obrit.android.core.designsystem.component.card.OBRitCardLevel
import com.obrit.android.core.designsystem.component.chip.OBRitChip
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.home.viewmodel.Bucket
import com.obrit.feature.home.viewmodel.BucketLevel
import com.obrit.feature.home.viewmodel.BucketStatus
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
internal fun ConsumableAlertSection(
    buckets: List<Bucket>,
    modifier: Modifier = Modifier,
) {
    if (buckets.isEmpty()) return
    var selectedStatus by remember { mutableStateOf(BucketStatus.DANGER) }
    val filteredBuckets =
        remember(buckets, selectedStatus) {
            buckets.filter { it.status == selectedStatus }
        }

    Column(
        modifier = modifier.padding(vertical = AtomSpacing.S5.dp),
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S3.dp),
    ) {
        BucketFilterChipRow(
            buckets = buckets,
            selectedStatus = selectedStatus,
            onStatusSelect = { selectedStatus = it },
        )
        BucketCardRow(buckets = filteredBuckets)
    }
}

@Composable
private fun BucketFilterChipRow(
    buckets: List<Bucket>,
    selectedStatus: BucketStatus,
    onStatusSelect: (BucketStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = AtomSpacing.S5.dp),
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
    ) {
        BucketStatus.entries.forEach { status ->
            val count = buckets.count { it.status == status }
            if (count > 0) {
                OBRitChip(
                    text = status.displayName,
                    number = count,
                    selected = status == selectedStatus,
                    onClick = { onStatusSelect(status) },
                )
            }
        }
    }
}

@Composable
private fun BucketCardRow(
    buckets: List<Bucket>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = AtomSpacing.S5.dp),
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S3.dp),
    ) {
        items(buckets) { bucket ->
            BucketCard(bucket = bucket)
        }
    }
}

@Composable
private fun BucketCard(bucket: Bucket) {
    val daysUntil = remember(bucket.replacementDate) { daysUntil(bucket.replacementDate) }
    OBRitCardGrid(
        level = bucketCardLevel(bucket = bucket),
        title = bucket.title,
        stockCount = bucket.spare,
        daysLabel = daysLabel(daysUntil),
    )
}

private val BucketStatus.displayName: String
    get() =
        when (this) {
            BucketStatus.DANGER -> "위험"
            BucketStatus.WARN -> "경고"
        }

private fun bucketCardLevel(bucket: Bucket): OBRitCardLevel =
    when (bucket.level) {
        BucketLevel.HAS_OVERDUE -> OBRitCardLevel.L1
        BucketLevel.NONE_OVERDUE -> OBRitCardLevel.L2
        BucketLevel.HAS_OVERDUE -> OBRitCardLevel.L3
        BucketLevel.HAS_WARN -> OBRitCardLevel.L4
        BucketLevel.NONE_SAFE -> OBRitCardLevel.L5
        else -> OBRitCardLevel.L6
    }

private fun daysLabel(daysUntil: Int): String =
    when {
        daysUntil == 0 -> "D-day"
        daysUntil > 0 -> "D-$daysUntil"
        else -> "D+${-daysUntil}"
    }

private fun daysUntil(replacementDate: String): Int =
    ChronoUnit.DAYS
        .between(LocalDate.now(), LocalDate.parse(replacementDate))
        .toInt()

@Suppress("MagicNumber")
@Preview(showBackground = true, backgroundColor = 0xFF1D1B20)
@Composable
private fun ConsumableAlertSectionPreview() {
    OBRitTheme {
        ConsumableAlertSection(
            buckets =
                listOf(
                    Bucket(
                        BucketStatus.DANGER,
                        "면도기",
                        0,
                        "2026-05-23",
                        BucketLevel.NONE_OVERDUE,
                        30,
                    ),
                    Bucket(
                        BucketStatus.DANGER,
                        "칫솔",
                        1,
                        "2026-05-26",
                        BucketLevel.NONE_SAFE,
                        27,
                    ),
                    Bucket(
                        BucketStatus.WARN,
                        "필터",
                        3,
                        "2026-05-26",
                        BucketLevel.HAS_SAFE,
                        10,
                    ),
                ),
        )
    }
}
