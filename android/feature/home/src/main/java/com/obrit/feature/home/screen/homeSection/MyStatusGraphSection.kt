@file:Suppress("TooManyFunctions")

package com.obrit.feature.home.screen.homeSection

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.model.home.MyStatusSummary

private val CARD_CONTENT_HEIGHT = 68.dp
private val BAR_HEIGHT = 18.dp
private val TICK_WIDTH = 4.dp
private val TICK_SPACING = 5.dp

private data class ScoreBarConfig(
    val redColor: Color,
    val greenColor: Color,
    val markerColor: Color,
    val textMeasurer: TextMeasurer,
    val labelStyle: TextStyle,
)

@Suppress("MagicNumber")
private data class BarGeometry(
    val top: Float,
    val height: Float,
    val markerOverflow: Float,
    val markerWidth: Float,
    val labelGap: Float,
) {
    val bottom: Float get() = top + height
    val markerTop: Float get() = top - markerOverflow
    val markerHeight: Float get() = height + markerOverflow * 2
}

@Composable
internal fun MyStatusGraphSection(
    myStatusSummary: MyStatusSummary,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val textMeasurer = rememberTextMeasurer()
    val barConfig =
        ScoreBarConfig(
            redColor = colors.red300,
            greenColor = colors.green300,
            markerColor = colors.common00,
            textMeasurer = textMeasurer,
            labelStyle = LocalOBRitTypography.current.s.copy(color = colors.common00),
        )
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.common1000)
                .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        MyStatusGraphRow(
            totalCount = myStatusSummary.totalCount,
            needReplaceCount = myStatusSummary.needReplaceCount,
            score = (myStatusSummary.score / 100).toFloat(),
            averageScore = (myStatusSummary.averageScore / 100).toFloat(),
            config = barConfig,
        )
    }
}

@Composable
private fun MyStatusGraphRow(
    totalCount: Int,
    needReplaceCount: Int,
    score: Float,
    averageScore: Float,
    config: ScoreBarConfig,
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        ConsumableStatColumn(totalCount = totalCount, needReplaceCount = needReplaceCount)
        Spacer(modifier = Modifier.width(24.dp))
        Canvas(modifier = Modifier.weight(1f).height(CARD_CONTENT_HEIGHT)) {
            drawScoreBar(score = score.coerceIn(0f, 1f), averageScore = averageScore.coerceIn(0f, 1f), config = config)
        }
    }
}

@Composable
private fun ConsumableStatColumn(
    totalCount: Int,
    needReplaceCount: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
    ) {
        ConsumableStatItem(
            label = "내 소모품",
            value = totalCount.toString(),
            color = colors.common00,
        )
        ConsumableStatItem(
            label = if (needReplaceCount > 0) "교체 위험" else "교체 필요",
            value = needReplaceCount.toString(),
            color = if (needReplaceCount > 0) colors.red300 else colors.gray700,
        )
    }
}

@Composable
private fun ConsumableStatItem(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = typography.xs.copy(fontWeight = FontWeight.Medium),
            color = color,
        )
        Text(
            text = value,
            style = typography.s.copy(fontWeight = FontWeight.Bold),
            color = color,
        )
    }
}

@Suppress("MagicNumber")
private fun DrawScope.drawScoreBar(
    score: Float,
    averageScore: Float,
    config: ScoreBarConfig,
) {
    val barHeight = BAR_HEIGHT.toPx()
    val geo =
        BarGeometry(
            top = (size.height - barHeight) / 2f,
            height = barHeight,
            markerOverflow = 3.dp.toPx(),
            markerWidth = 4.dp.toPx(),
            labelGap = 4.dp.toPx(),
        )
    val myX = score * size.width
    val avgX = averageScore * size.width

    drawGradientWithTicks(geo, config)
    drawMarkerLines(myX, avgX, geo, config.markerColor)
    drawMarkerLabels(myX, avgX, geo, config)
}

private fun DrawScope.drawGradientWithTicks(
    geo: BarGeometry,
    config: ScoreBarConfig,
) {
    drawRect(
        brush = Brush.horizontalGradient(listOf(config.redColor, config.greenColor)),
        topLeft = Offset(0f, geo.top),
        size = Size(size.width, geo.height),
    )
    val tickWidth = TICK_WIDTH.toPx()
    val tickSpacing = TICK_SPACING.toPx()
    var tickX = 0f
    while (tickX < size.width) {
        drawRect(
            color = Color.Black,
            topLeft = Offset(tickX, geo.top),
            size = Size(tickWidth, geo.height),
        )
        tickX += tickSpacing
    }
}

@Suppress("MagicNumber")
private fun DrawScope.drawMarkerLines(
    myX: Float,
    avgX: Float,
    geo: BarGeometry,
    markerColor: Color,
) {
    val corner = CornerRadius(2.dp.toPx())
    val markerSize = Size(geo.markerWidth, geo.markerHeight)
    drawRoundRect(
        color = markerColor,
        topLeft = Offset(myX - geo.markerWidth / 2, geo.markerTop),
        size = markerSize,
        cornerRadius = corner,
    )
    drawRoundRect(
        color = markerColor,
        topLeft = Offset(avgX - geo.markerWidth / 2, geo.markerTop),
        size = markerSize,
        cornerRadius = corner,
    )
}

private fun DrawScope.drawMarkerLabels(
    myX: Float,
    avgX: Float,
    geo: BarGeometry,
    config: ScoreBarConfig,
) {
    val myLabel = config.textMeasurer.measure("내 상태", config.labelStyle)
    val avgLabel = config.textMeasurer.measure("평균", config.labelStyle)
    drawText(
        textLayoutResult = myLabel,
        topLeft =
            Offset(
                x = (myX - myLabel.size.width / 2f).coerceIn(0f, size.width - myLabel.size.width),
                y = geo.top - geo.labelGap - myLabel.size.height,
            ),
    )
    drawText(
        textLayoutResult = avgLabel,
        topLeft =
            Offset(
                x = (avgX - avgLabel.size.width / 2f).coerceIn(0f, size.width - avgLabel.size.width),
                y = geo.bottom + geo.labelGap,
            ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1D1B20)
@Composable
private fun MyStatusGraphSectionPreview() {
    OBRitTheme {
        MyStatusGraphSection(
            myStatusSummary =
                MyStatusSummary(
                    totalCount = 16,
                    needReplaceCount = 4,
                    score = 0.425,
                    averageScore = 0.65,
                ),
        )
    }
}
