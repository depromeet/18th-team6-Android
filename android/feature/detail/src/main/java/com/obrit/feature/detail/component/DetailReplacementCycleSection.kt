@file:Suppress("LongMethod")

package com.obrit.feature.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.component.badge.BadgeType
import com.obrit.android.core.designsystem.component.badge.OBRitBadge
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.feature.detail.viewmodel.DetailColorTone
import com.obrit.feature.detail.viewmodel.DetailDDayDirection
import java.util.Locale
import kotlin.math.round

@Composable
@Suppress("LongParameterList")
internal fun DetailReplacementCycleSection(
    averageReplacementIntervalDays: Double,
    recommendedReplacementIntervalDays: Int,
    currentUsageDays: Int,
    modifier: Modifier = Modifier,
    dDayLabel: String = "",
    dDayDirection: DetailDDayDirection = DetailDDayDirection.UNKNOWN,
    colorTone: DetailColorTone = DetailColorTone.BRAND,
) {
    val warningBadgeLabel =
        dDayLabel.takeIf { label ->
            dDayDirection == DetailDDayDirection.OVERDUE &&
                colorTone == DetailColorTone.WARNING &&
                label.isNotBlank()
        }

    DetailCardBackground(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            DetailReplacementCycleHeader(
                modifier =
                    Modifier.padding(
                        start = DETAIL_REPLACEMENT_CYCLE_HORIZONTAL_PADDING,
                        top = DETAIL_REPLACEMENT_CYCLE_TOP_PADDING,
                        end = DETAIL_REPLACEMENT_CYCLE_HORIZONTAL_PADDING,
                    ),
            )
            Spacer(modifier = Modifier.height(DETAIL_REPLACEMENT_CYCLE_HEADER_BOTTOM_PADDING))
            DetailReplacementCycleRow(
                label = "나의 평균 교체 주기",
                value = averageReplacementIntervalDays.toDaysLabel(),
            )
            DetailReplacementCycleDivider()
            DetailReplacementCycleRow(
                label = "권장 교체 주기",
                value = recommendedReplacementIntervalDays.toDaysLabel(),
            )
            DetailReplacementCycleDivider()
            DetailReplacementCycleRow(
                label = "현재 사용 상태",
                value = currentUsageDays.toCurrentUsageDaysLabel(),
                badgeLabel = warningBadgeLabel,
                rowHeight =
                    if (warningBadgeLabel == null) {
                        DETAIL_REPLACEMENT_CYCLE_ROW_HEIGHT
                    } else {
                        DETAIL_REPLACEMENT_CYCLE_WARNING_ROW_HEIGHT
                    },
            )
            DetailReplacementCycleDivider()
            Spacer(modifier = Modifier.height(DETAIL_REPLACEMENT_CYCLE_BOTTOM_PADDING))
        }
    }
}

@Composable
private fun DetailReplacementCycleHeader(modifier: Modifier = Modifier) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Column(modifier = modifier) {
        Text(
            text = "교체 주기",
            style = typography.xl2.copy(fontWeight = FontWeight.Bold),
            color = colors.common00,
        )
        Text(
            text = "전체적인 상태를 빠르게 확인해보세요!",
            style = typography.base.copy(fontWeight = FontWeight.Medium),
            color = colors.gray300,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun DetailReplacementCycleRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    badgeLabel: String? = null,
    rowHeight: Dp = DETAIL_REPLACEMENT_CYCLE_ROW_HEIGHT,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(rowHeight)
                .padding(horizontal = DETAIL_REPLACEMENT_CYCLE_HORIZONTAL_PADDING),
        horizontalArrangement = Arrangement.spacedBy(DETAIL_REPLACEMENT_CYCLE_ROW_HORIZONTAL_GAP),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = typography.xl.copy(fontWeight = FontWeight.Medium),
            color = colors.gray500,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        DetailReplacementCycleRowValue(
            value = value,
            badgeLabel = badgeLabel,
        )
    }
}

@Composable
private fun DetailReplacementCycleRowValue(
    value: String,
    modifier: Modifier = Modifier,
    badgeLabel: String? = null,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(DETAIL_REPLACEMENT_CYCLE_VALUE_HORIZONTAL_GAP),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (badgeLabel != null) {
            OBRitBadge(
                text = badgeLabel,
                type = BadgeType.Red800Filled,
            )
        }
        Text(
            text = value,
            style = typography.xl.copy(fontWeight = FontWeight.SemiBold),
            color = colors.common00,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun DetailReplacementCycleDivider(modifier: Modifier = Modifier) {
    val colors = LocalOBRitColor.current

    Spacer(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = DETAIL_REPLACEMENT_CYCLE_DIVIDER_HORIZONTAL_PADDING)
                .height(DETAIL_REPLACEMENT_CYCLE_DIVIDER_HEIGHT)
                .background(colors.gray750),
    )
}

private fun Double.toDaysLabel(): String = "${toAverageDaysText()}일"

private fun Int.toDaysLabel(): String = "${coerceAtLeast(0)}일"

private fun Int.toCurrentUsageDaysLabel(): String = "${coerceAtLeast(0)}일째"

private fun Double.toAverageDaysText(): String {
    val safeValue =
        if (isNaN() || isInfinite()) {
            0.0
        } else {
            coerceAtLeast(0.0)
        }
    val roundedValue = round(safeValue * AVERAGE_DAYS_TEXT_SCALE) / AVERAGE_DAYS_TEXT_SCALE

    return if (roundedValue % 1.0 == 0.0) {
        roundedValue.toInt().toString()
    } else {
        String.format(Locale.US, "%.1f", roundedValue)
    }
}

private val DETAIL_REPLACEMENT_CYCLE_HORIZONTAL_PADDING = 20.dp
private val DETAIL_REPLACEMENT_CYCLE_DIVIDER_HORIZONTAL_PADDING = 5.dp
private val DETAIL_REPLACEMENT_CYCLE_TOP_PADDING = 24.dp
private val DETAIL_REPLACEMENT_CYCLE_BOTTOM_PADDING = 16.dp
private val DETAIL_REPLACEMENT_CYCLE_ROW_HORIZONTAL_GAP = 16.dp
private val DETAIL_REPLACEMENT_CYCLE_VALUE_HORIZONTAL_GAP = 8.dp
private val DETAIL_REPLACEMENT_CYCLE_HEADER_BOTTOM_PADDING = 2.dp
private val DETAIL_REPLACEMENT_CYCLE_ROW_HEIGHT = 55.dp
private val DETAIL_REPLACEMENT_CYCLE_WARNING_ROW_HEIGHT = 57.dp
private val DETAIL_REPLACEMENT_CYCLE_DIVIDER_HEIGHT = 1.dp
private const val AVERAGE_DAYS_TEXT_SCALE = 10.0
