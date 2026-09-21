package com.obrit.feature.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.obrit.android.feature.widget.R
import com.obrit.obrit.shared.designsystem.tokens.semantic.SemanticColors

private const val UNFILLED_DOT_ALPHA = 0.2f
private const val LABEL_ALPHA = 0.3f

@Composable
internal fun StatusWidgetContent(
    state: StatusWidgetState,
    modifier: GlanceModifier = GlanceModifier,
) {
    val metrics = statusWidgetMetricsFor(LocalSize.current)
    val backgroundColor = Color(SemanticColors.Background.Default.Default)
    val accentColor =
        Color(
            if (state.isGood) {
                SemanticColors.Text.Positive.Default
            } else {
                SemanticColors.Text.Warning.Default
            },
        )

    // 런처 셀은 시안보다 세로로 길다. 셀 전체를 칠하면 비율이 깨지므로
    // 시안 비율의 카드만 그리고 남는 위아래는 투명하게 둔다.
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        StatusWidgetCard(
            state = state,
            metrics = metrics,
            backgroundColor = backgroundColor,
            accentColor = accentColor,
        )
    }
}

@Composable
private fun StatusWidgetCard(
    state: StatusWidgetState,
    metrics: StatusWidgetMetrics,
    backgroundColor: Color,
    accentColor: Color,
) {
    Column(
        modifier =
            GlanceModifier
                .width(metrics.cardWidth)
                .height(metrics.cardHeight)
                .background(ColorProvider(backgroundColor))
                .padding(
                    start = metrics.paddingHorizontal,
                    end = metrics.paddingHorizontal,
                    top = metrics.paddingTop,
                    bottom = metrics.paddingBottom,
                ),
    ) {
        StatusCountRow(
            count = state.dangerCount,
            label = "위험",
            metrics = metrics,
            color = accentColor,
        )
        StatusCountRow(
            count = state.warningCount,
            label = "경고",
            metrics = metrics,
            color = accentColor,
        )
        Spacer(modifier = GlanceModifier.height(metrics.contentGap))
        StatusGauge(
            filledCount = state.filledDotCount,
            metrics = metrics,
            filledColor = accentColor,
            // tint는 alpha를 그대로 합성해주지 않으므로 배경과 미리 섞어 불투명 색으로 넘긴다.
            unfilledColor =
                accentColor.copy(alpha = UNFILLED_DOT_ALPHA).compositeOver(backgroundColor),
        )
    }
}

@Composable
private fun StatusCountRow(
    count: Int,
    label: String,
    metrics: StatusWidgetMetrics,
    color: Color,
) {
    Row(
        // 폰트 기본 line box는 글자보다 크다. 시안의 행 간격을 쓰려면 높이를 직접 정해야 한다.
        modifier = GlanceModifier.height(metrics.countRowHeight),
        verticalAlignment = Alignment.Vertical.CenterVertically,
    ) {
        Text(
            text = count.toString(),
            style =
                TextStyle(
                    color = ColorProvider(color),
                    fontSize = metrics.numberFontSize,
                    fontWeight = FontWeight.Bold,
                ),
        )
        Spacer(modifier = GlanceModifier.width(metrics.labelGap))
        Text(
            text = label,
            style =
                TextStyle(
                    color =
                        ColorProvider(
                            Color(SemanticColors.Text.Default.Default).copy(alpha = LABEL_ALPHA),
                        ),
                    fontSize = metrics.labelFontSize,
                    fontWeight = FontWeight.Bold,
                ),
        )
    }
}

/**
 * 원 30개를 [GAUGE_ROWS]행 [GAUGE_COLUMNS]열 격자로 그린다.
 * 채움은 맨 아래 행부터 위로, 각 행 안에서는 왼쪽부터 차오른다.
 *
 * Glance 컨테이너는 자식을 10개까지만 그리므로 행 안에 Spacer를 두지 않는다.
 */
@Composable
private fun StatusGauge(
    filledCount: Int,
    metrics: StatusWidgetMetrics,
    filledColor: Color,
    unfilledColor: Color,
) {
    Column(modifier = GlanceModifier.fillMaxWidth()) {
        repeat(GAUGE_ROWS) { rowFromTop ->
            val rowFromBottom = GAUGE_ROWS - 1 - rowFromTop
            val filledInRow =
                (filledCount - rowFromBottom * GAUGE_COLUMNS).coerceIn(0, GAUGE_COLUMNS)
            StatusGaugeRow(
                filledInRow = filledInRow,
                metrics = metrics,
                filledColor = filledColor,
                unfilledColor = unfilledColor,
            )
        }
    }
}

@Composable
private fun StatusGaugeRow(
    filledInRow: Int,
    metrics: StatusWidgetMetrics,
    filledColor: Color,
    unfilledColor: Color,
) {
    Row(modifier = GlanceModifier.fillMaxWidth()) {
        repeat(GAUGE_COLUMNS) { index ->
            Image(
                provider = ImageProvider(R.drawable.widget_gauge_dot),
                contentDescription = null,
                modifier =
                    GlanceModifier
                        .size(metrics.dotCellSize)
                        .padding(horizontal = metrics.dotGap / 2, vertical = metrics.dotGap / 2),
                colorFilter =
                    ColorFilter.tint(
                        ColorProvider(if (index < filledInRow) filledColor else unfilledColor),
                    ),
            )
        }
    }
}
