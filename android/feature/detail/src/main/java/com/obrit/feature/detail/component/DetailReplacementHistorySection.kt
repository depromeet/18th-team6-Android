@file:Suppress("LongMethod", "LongParameterList", "MagicNumber", "TooManyFunctions")

package com.obrit.feature.detail.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitColor
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.detail.viewmodel.DetailReplacementRecordUiState
import java.time.LocalDate
import java.util.Locale
import kotlin.math.round

@Composable
internal fun DetailReplacementHistorySection(
    replacementRecords: List<DetailReplacementRecordUiState>,
    averageReplacementIntervalDays: Double,
    modifier: Modifier = Modifier,
) {
    val records = replacementRecords.takeLast(MAX_REPLACEMENT_RECORD_COUNT)
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(REPLACEMENT_HISTORY_CARD_SHAPE)
                .background(colors.gray850)
                .border(
                    border = BorderStroke(width = 1.dp, color = colors.gray800),
                    shape = REPLACEMENT_HISTORY_CARD_SHAPE,
                ).padding(REPLACEMENT_HISTORY_CARD_PADDING),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(REPLACEMENT_HISTORY_HEADER_GAP)) {
                Text(
                    text = "교체 주기 기록",
                    style = typography.xl2.copy(fontWeight = FontWeight.Bold),
                    color = colors.common00,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Text(
                    text = replacementHistorySubtitle(colors = colors),
                    style = typography.base.copy(fontWeight = FontWeight.Medium),
                    color = colors.gray300,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            DetailReplacementHistoryChart(
                records = records,
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            )

            HorizontalDivider(
                color = colors.gray750,
                modifier = Modifier.padding(top = 16.dp),
            )

            DetailAverageReplacementInterval(
                averageReplacementIntervalDays = averageReplacementIntervalDays,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            )
        }
    }
}

@Composable
private fun DetailReplacementHistoryChart(
    records: List<DetailReplacementRecordUiState>,
    modifier: Modifier = Modifier,
) {
    val chartRecords = records.toChartRecords()
    val maxUsageDays =
        chartRecords
            .mapNotNull { record -> record?.usageDays?.coerceAtLeast(0) }
            .maxOrNull()
            ?: 0

    BoxWithConstraints(modifier = modifier) {
        val gap =
            REPLACEMENT_HISTORY_BAR_GAP.coerceAtMost(
                maxWidth / REPLACEMENT_HISTORY_GAP_SCALE,
            )
        val barWidth =
            ((maxWidth - gap * (MAX_REPLACEMENT_RECORD_COUNT - 1).toFloat()) / MAX_REPLACEMENT_RECORD_COUNT)
                .coerceAtLeast(0.dp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(gap),
            verticalAlignment = Alignment.Bottom,
        ) {
            chartRecords.forEach { record ->
                DetailReplacementHistoryBarItem(
                    record = record,
                    progressRatio = record.toMaxUsageProgressRatio(maxUsageDays),
                    modifier = Modifier.width(barWidth),
                )
            }
        }
    }
}

@Composable
private fun DetailReplacementHistoryBarItem(
    record: DetailReplacementRecordUiState?,
    progressRatio: Double,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val isCurrent = record?.isCurrent == true
    val contentColor = if (isCurrent) colors.green300 else colors.gray600

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = record.toUsageDaysText(),
            modifier = Modifier.fillMaxWidth(),
            style = typography.xs.copy(fontWeight = FontWeight.SemiBold),
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(REPLACEMENT_HISTORY_LABEL_BAR_GAP))
        DetailReplacementHistoryBar(
            progressRatio = progressRatio,
            isCurrent = isCurrent,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(REPLACEMENT_HISTORY_BAR_HEIGHT),
        )
        Spacer(modifier = Modifier.height(REPLACEMENT_HISTORY_BAR_DATE_GAP))
        Text(
            text = record.toDateText(),
            modifier = Modifier.fillMaxWidth(),
            style = typography.xs.copy(fontWeight = FontWeight.SemiBold),
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DetailReplacementHistoryBar(
    progressRatio: Double,
    isCurrent: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val trackColor = colors.green850
    val progressColor = if (isCurrent) colors.green300 else colors.green800
    val fillFraction = progressRatio.toProgressFraction()

    Box(
        modifier =
            modifier
                .clip(REPLACEMENT_HISTORY_BAR_SHAPE)
                .background(trackColor),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fillFraction)
                    .background(progressColor),
        )
    }
}

@Composable
private fun DetailAverageReplacementInterval(
    averageReplacementIntervalDays: Double,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Text(
        text =
            averageReplacementIntervalText(
                colors = colors,
                averageReplacementIntervalDays = averageReplacementIntervalDays,
            ),
        modifier = modifier,
        style = typography.xs.copy(fontWeight = FontWeight.SemiBold),
        color = colors.gray600,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
    )
}

private fun replacementHistorySubtitle(colors: OBRitColor) =
    buildAnnotatedString {
        append("교체 기록은 ")
        withStyle(SpanStyle(color = colors.green300)) {
            append("최근 5회")
        }
        append("까지 제공해요")
    }

private fun averageReplacementIntervalText(
    colors: OBRitColor,
    averageReplacementIntervalDays: Double,
) = buildAnnotatedString {
    append("평균 교체 주기 ")
    withStyle(SpanStyle(color = colors.common00)) {
        append("${averageReplacementIntervalDays.toAverageDaysText()}일")
    }
}

private fun List<DetailReplacementRecordUiState>.toChartRecords(): List<DetailReplacementRecordUiState?> {
    val latestRecords = takeLast(MAX_REPLACEMENT_RECORD_COUNT)
    val emptyRecordCount = MAX_REPLACEMENT_RECORD_COUNT - latestRecords.size

    return List<DetailReplacementRecordUiState?>(emptyRecordCount) { null } + latestRecords
}

private fun DetailReplacementRecordUiState?.toMaxUsageProgressRatio(maxUsageDays: Int): Double =
    when {
        this == null || maxUsageDays <= 0 -> MIN_PROGRESS_RATIO
        else -> usageDays.coerceAtLeast(0).toDouble() / maxUsageDays.toDouble()
    }

private fun DetailReplacementRecordUiState?.toDateText(): String =
    when {
        this == null -> EMPTY_DATE_TEXT
        isCurrent -> "현재"
        dateLabel.isNotBlank() -> dateLabel
        endedDate != null -> endedDate.toString()
        startedDate != null -> startedDate.toString()
        else -> EMPTY_DATE_TEXT
    }

private fun DetailReplacementRecordUiState?.toUsageDaysText(): String =
    this?.usageDaysLabel?.toUsageDaysText(usageDays) ?: EMPTY_USAGE_DAYS_TEXT

private fun String.toUsageDaysText(fallbackUsageDays: Int): String {
    val label = ifBlank { fallbackUsageDays.coerceAtLeast(0).toString() }

    return if (label.endsWith("일")) {
        label
    } else {
        "${label}일"
    }
}

private fun Double.toProgressFraction(): Float =
    when {
        isNaN() || isInfinite() -> MIN_PROGRESS_RATIO.toFloat()
        else -> coerceIn(MIN_PROGRESS_RATIO, MAX_PROGRESS_RATIO).toFloat()
    }

private fun Double.toAverageDaysText(): String {
    val safeValue =
        if (isNaN() || isInfinite()) {
            0.0
        } else {
            coerceAtLeast(0.0)
        }
    val roundedValue = round(safeValue * AVERAGE_SCALE) / AVERAGE_SCALE

    return if (roundedValue % 1.0 == 0.0) {
        roundedValue.toInt().toString()
    } else {
        String.format(Locale.US, "%.1f", roundedValue)
    }
}

private const val MAX_REPLACEMENT_RECORD_COUNT = 5
private const val MIN_PROGRESS_RATIO = 0.0
private const val MAX_PROGRESS_RATIO = 1.0
private const val AVERAGE_SCALE = 10.0
private const val REPLACEMENT_HISTORY_GAP_SCALE = 30f
private const val EMPTY_USAGE_DAYS_TEXT = "-일"
private const val EMPTY_DATE_TEXT = "-/-"
private val REPLACEMENT_HISTORY_CARD_SHAPE = RoundedCornerShape(28.dp)
private val REPLACEMENT_HISTORY_CARD_PADDING = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
private val REPLACEMENT_HISTORY_HEADER_GAP = 14.dp
private val REPLACEMENT_HISTORY_BAR_GAP = 12.dp
private val REPLACEMENT_HISTORY_LABEL_BAR_GAP = 4.dp
private val REPLACEMENT_HISTORY_BAR_DATE_GAP = 4.dp
private val REPLACEMENT_HISTORY_BAR_HEIGHT = 86.dp
private val REPLACEMENT_HISTORY_BAR_SHAPE = RoundedCornerShape(8.dp)

@Preview(name = "DetailReplacementHistorySection Brand", showBackground = true, widthDp = 412)
@Composable
private fun DetailReplacementHistorySectionBrandPreview() {
    DetailReplacementHistorySectionPreviewContainer {
        DetailReplacementHistorySection(
            replacementRecords = detailReplacementHistoryBrandPreviewRecords(),
            averageReplacementIntervalDays = 33.8,
        )
    }
}

@Preview(name = "DetailReplacementHistorySection Max Usage", showBackground = true, widthDp = 320)
@Composable
private fun DetailReplacementHistorySectionMaxUsagePreview() {
    DetailReplacementHistorySectionPreviewContainer {
        DetailReplacementHistorySection(
            replacementRecords = detailReplacementHistoryMaxUsagePreviewRecords(),
            averageReplacementIntervalDays = 41.7,
        )
    }
}

@Preview(name = "DetailReplacementHistorySection Empty Slots", showBackground = true, widthDp = 320)
@Composable
private fun DetailReplacementHistorySectionEmptySlotsPreview() {
    DetailReplacementHistorySectionPreviewContainer {
        DetailReplacementHistorySection(
            replacementRecords = detailReplacementHistoryEmptySlotPreviewRecords(),
            averageReplacementIntervalDays = 8.0,
        )
    }
}

@Composable
private fun DetailReplacementHistorySectionPreviewContainer(content: @Composable () -> Unit) {
    OBRitTheme(dynamicColor = false) {
        Box(
            modifier =
                Modifier
                    .background(LocalOBRitColor.current.gray900)
                    .padding(20.dp),
        ) {
            content()
        }
    }
}

private fun detailReplacementHistoryBrandPreviewRecords(): List<DetailReplacementRecordUiState> =
    listOf(
        detailReplacementHistoryPreviewRecord(
            id = 1L,
            startedDate = LocalDate.of(2026, 1, 1),
            endedDate = LocalDate.of(2026, 2, 3),
            usageDays = 33,
            dateLabel = "2/3",
            progressDisplayRatio = 0.99,
        ),
        detailReplacementHistoryPreviewRecord(
            id = 2L,
            startedDate = LocalDate.of(2026, 2, 3),
            endedDate = LocalDate.of(2026, 3, 10),
            usageDays = 35,
            dateLabel = "3/10",
            progressDisplayRatio = 0.75,
        ),
        detailReplacementHistoryPreviewRecord(
            id = 3L,
            startedDate = LocalDate.of(2026, 3, 10),
            endedDate = LocalDate.of(2026, 4, 14),
            usageDays = 35,
            dateLabel = "4/14",
            progressDisplayRatio = 0.84,
        ),
        detailReplacementHistoryPreviewRecord(
            id = 4L,
            startedDate = LocalDate.of(2026, 4, 14),
            endedDate = LocalDate.of(2026, 5, 1),
            usageDays = 17,
            dateLabel = "5/1",
            progressDisplayRatio = 1.0,
        ),
        detailReplacementHistoryPreviewRecord(
            id = null,
            startedDate = LocalDate.of(2026, 5, 1),
            endedDate = null,
            usageDays = 25,
            dateLabel = "현재",
            progressDisplayRatio = 0.62,
            isCurrent = true,
        ),
    )

private fun detailReplacementHistoryMaxUsagePreviewRecords(): List<DetailReplacementRecordUiState> =
    listOf(
        detailReplacementHistoryPreviewRecord(
            id = 1L,
            startedDate = LocalDate.of(2026, 1, 1),
            endedDate = LocalDate.of(2026, 1, 31),
            usageDays = 30,
            dateLabel = "1/31",
            progressDisplayRatio = 1.0,
        ),
        detailReplacementHistoryPreviewRecord(
            id = 2L,
            startedDate = LocalDate.of(2026, 1, 31),
            endedDate = LocalDate.of(2026, 3, 17),
            usageDays = 45,
            dateLabel = "3/17",
            progressDisplayRatio = 1.0,
        ),
        detailReplacementHistoryPreviewRecord(
            id = 3L,
            startedDate = LocalDate.of(2026, 3, 17),
            endedDate = LocalDate.of(2026, 5, 6),
            usageDays = 50,
            dateLabel = "5/6",
            progressDisplayRatio = 1.0,
        ),
        detailReplacementHistoryPreviewRecord(
            id = 4L,
            startedDate = LocalDate.of(2026, 5, 6),
            endedDate = LocalDate.of(2026, 5, 16),
            usageDays = 10,
            dateLabel = "5/16",
            progressDisplayRatio = 0.33,
        ),
        detailReplacementHistoryPreviewRecord(
            id = null,
            startedDate = LocalDate.of(2026, 5, 16),
            endedDate = null,
            usageDays = 20,
            dateLabel = "현재",
            progressDisplayRatio = 0.66,
            isCurrent = true,
        ),
    )

private fun detailReplacementHistoryEmptySlotPreviewRecords(): List<DetailReplacementRecordUiState> =
    listOf(
        detailReplacementHistoryPreviewRecord(
            id = 1L,
            startedDate = LocalDate.of(2026, 3, 1),
            endedDate = LocalDate.of(2026, 3, 9),
            usageDays = 8,
            dateLabel = "3/9",
            progressDisplayRatio = 0.5,
        ),
        detailReplacementHistoryPreviewRecord(
            id = null,
            startedDate = LocalDate.of(2026, 3, 9),
            endedDate = null,
            usageDays = 12,
            dateLabel = "현재",
            progressDisplayRatio = 1.2,
            isCurrent = true,
        ),
    )

private fun detailReplacementHistoryPreviewRecord(
    id: Long?,
    startedDate: LocalDate,
    endedDate: LocalDate?,
    usageDays: Int,
    dateLabel: String,
    progressDisplayRatio: Double,
    isCurrent: Boolean = false,
): DetailReplacementRecordUiState =
    DetailReplacementRecordUiState(
        id = id,
        startedDate = startedDate,
        endedDate = endedDate,
        usageDays = usageDays,
        usageDaysLabel = usageDays.toString(),
        dateLabel = dateLabel,
        progressDisplayRatio = progressDisplayRatio,
        isCurrent = isCurrent,
    )
