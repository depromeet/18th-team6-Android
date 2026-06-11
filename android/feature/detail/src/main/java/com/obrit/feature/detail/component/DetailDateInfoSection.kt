@file:Suppress("LongMethod", "LongParameterList", "MagicNumber", "TooManyFunctions")

package com.obrit.feature.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitColor
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.detail.viewmodel.DetailColorTone
import com.obrit.feature.detail.viewmodel.DetailDDayDirection
import com.obrit.feature.detail.viewmodel.DetailUiState
import java.time.LocalDate

@Composable
internal fun DetailDateInfoSection(
    state: DetailUiState.ConsumableSuccess,
    modifier: Modifier = Modifier,
) {
    DetailDateInfoSection(
        lastReplacedDate = state.lastReplacedDate,
        nextReplacementDate = state.nextReplacementDate,
        dDayLabel = state.dDayLabel,
        dDayDirection = state.dDayDirection,
        colorTone = state.colorTone,
        modifier = modifier,
    )
}

@Composable
internal fun DetailDateInfoSection(
    lastReplacedDate: LocalDate?,
    nextReplacementDate: LocalDate?,
    dDayLabel: String,
    dDayDirection: DetailDDayDirection,
    colorTone: DetailColorTone,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val accentColor = colorTone.toAccentColor(colors)
    val accentTitleColor = colorTone.toAccentTitleColor(colors)

    BoxWithConstraints(
        modifier =
            modifier
                .fillMaxWidth()
                .height(DATE_INFO_CARD_HEIGHT)
                .clip(DETAIL_DATE_INFO_CARD_SHAPE)
                .background(colors.gray850),
    ) {
        val dividerCenterX = maxWidth * DATE_INFO_DIVIDER_X_RATIO

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DetailDateInfoItem(
                title = "최근 교체일",
                dateText = lastReplacedDate.toDetailDateText(),
                titleColor = colors.gray600,
                dateColor = colors.common00,
                modifier =
                    Modifier
                        .width(dividerCenterX)
                        .fillMaxHeight(),
            )

            DetailDateInfoItem(
                title = "다음 교체 예정일",
                dateText = nextReplacementDate.toDetailDateText(),
                titleColor = accentTitleColor,
                dateColor = accentColor,
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(),
            ) {
                DetailDDayBadge(
                    label =
                        detailDDayBadgeLabel(
                            nextReplacementDate = nextReplacementDate,
                            dDayLabel = dDayLabel,
                            dDayDirection = dDayDirection,
                        ),
                    contentColor = accentColor,
                    containerColor = colorTone.toBadgeContainerColor(colors),
                )
            }
        }

        Spacer(
            modifier =
                Modifier
                    .offset(x = dividerCenterX - (DATE_INFO_DIVIDER_WIDTH * 0.5f))
                    .width(DATE_INFO_DIVIDER_WIDTH)
                    .height(DATE_INFO_DIVIDER_HEIGHT)
                    .align(Alignment.CenterStart)
                    .clip(DETAIL_DATE_INFO_DIVIDER_SHAPE)
                    .background(colors.gray700),
        )
    }
}

@Composable
private fun DetailDateInfoItem(
    title: String,
    dateText: String,
    titleColor: Color,
    dateColor: Color,
    modifier: Modifier = Modifier,
    badge: (@Composable () -> Unit)? = null,
) {
    val typography = LocalOBRitTypography.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            style = typography.base.copy(fontWeight = FontWeight.Medium),
            color = titleColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(DATE_INFO_BADGE_SPACING),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = dateText,
                style = typography.xl3.copy(fontWeight = FontWeight.Bold),
                color = dateColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            badge?.invoke()
        }
    }
}

@Composable
private fun DetailDDayBadge(
    label: String,
    contentColor: Color,
    containerColor: Color,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current

    Text(
        text = label,
        modifier =
            modifier
                .clip(DETAIL_DATE_INFO_BADGE_SHAPE)
                .background(containerColor)
                .padding(horizontal = BADGE_HORIZONTAL_PADDING, vertical = BADGE_VERTICAL_PADDING),
        style = typography.xs.copy(fontWeight = FontWeight.Bold),
        color = contentColor,
        maxLines = 1,
    )
}

private fun LocalDate?.toDetailDateText(): String =
    this?.let { date ->
        "${date.monthValue}월 ${date.dayOfMonth}일"
    } ?: UNKNOWN_DATE_TEXT

private fun detailDDayBadgeLabel(
    nextReplacementDate: LocalDate?,
    dDayLabel: String,
    dDayDirection: DetailDDayDirection,
): String =
    when {
        nextReplacementDate == null -> UNKNOWN_D_DAY_TEXT
        dDayLabel.isNotBlank() -> dDayLabel
        dDayDirection == DetailDDayDirection.TODAY -> TODAY_D_DAY_TEXT
        dDayDirection == DetailDDayDirection.OVERDUE -> UNKNOWN_OVERDUE_D_DAY_TEXT
        else -> UNKNOWN_D_DAY_TEXT
    }

private fun DetailColorTone.toAccentColor(colors: OBRitColor): Color =
    when (this) {
        DetailColorTone.BRAND -> colors.green300
        DetailColorTone.WARNING -> colors.red300
    }

private fun DetailColorTone.toAccentTitleColor(colors: OBRitColor): Color =
    when (this) {
        DetailColorTone.BRAND -> colors.green600
        DetailColorTone.WARNING -> colors.red600
    }

private fun DetailColorTone.toBadgeContainerColor(colors: OBRitColor): Color =
    when (this) {
        DetailColorTone.BRAND -> colors.green800
        DetailColorTone.WARNING -> colors.red800
    }

private val DETAIL_DATE_INFO_CARD_SHAPE = RoundedCornerShape(16.dp)
private val DETAIL_DATE_INFO_BADGE_SHAPE = RoundedCornerShape(8.dp)
private val DETAIL_DATE_INFO_DIVIDER_SHAPE = RoundedCornerShape(1.dp)
private val DATE_INFO_CARD_HEIGHT = 81.dp
private val DATE_INFO_BADGE_SPACING = 12.dp
private val BADGE_HORIZONTAL_PADDING = 9.dp
private val BADGE_VERTICAL_PADDING = 4.dp
private val DATE_INFO_DIVIDER_WIDTH = 1.dp
private val DATE_INFO_DIVIDER_HEIGHT = 61.dp
private const val DATE_INFO_DIVIDER_X_RATIO = 187f / 372f
private const val UNKNOWN_DATE_TEXT = "날짜 미정"
private const val UNKNOWN_D_DAY_TEXT = "D-?"
private const val UNKNOWN_OVERDUE_D_DAY_TEXT = "D+?"
private const val TODAY_D_DAY_TEXT = "D-Day"

@Preview(name = "DetailDateInfoSection Brand", showBackground = true, widthDp = 412)
@Composable
private fun DetailDateInfoSectionBrandPreview() {
    DetailDateInfoSectionPreviewContainer {
        DetailDateInfoSection(
            lastReplacedDate = LocalDate.of(2026, 5, 1),
            nextReplacementDate = LocalDate.of(2026, 6, 1),
            dDayLabel = "D-N",
            dDayDirection = DetailDDayDirection.UPCOMING,
            colorTone = DetailColorTone.BRAND,
        )
    }
}

@Preview(name = "DetailDateInfoSection Warning", showBackground = true, widthDp = 412)
@Composable
private fun DetailDateInfoSectionWarningPreview() {
    DetailDateInfoSectionPreviewContainer {
        DetailDateInfoSection(
            lastReplacedDate = LocalDate.of(2026, 4, 1),
            nextReplacementDate = LocalDate.of(2026, 5, 1),
            dDayLabel = "D+N",
            dDayDirection = DetailDDayDirection.OVERDUE,
            colorTone = DetailColorTone.WARNING,
        )
    }
}

@Preview(name = "DetailDateInfoSection Unknown", showBackground = true, widthDp = 412)
@Composable
private fun DetailDateInfoSectionUnknownPreview() {
    DetailDateInfoSectionPreviewContainer {
        DetailDateInfoSection(
            lastReplacedDate = null,
            nextReplacementDate = null,
            dDayLabel = "",
            dDayDirection = DetailDDayDirection.UNKNOWN,
            colorTone = DetailColorTone.BRAND,
        )
    }
}

@Composable
private fun DetailDateInfoSectionPreviewContainer(content: @Composable () -> Unit) {
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
