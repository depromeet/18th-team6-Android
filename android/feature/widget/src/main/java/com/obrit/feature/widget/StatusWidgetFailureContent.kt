package com.obrit.feature.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.obrit.obrit.shared.designsystem.tokens.semantic.SemanticColors

/**
 * 데이터를 읽지 못했을 때 그리는 화면.
 *
 * TODO(#160): 시안이 없어 임시로 문구만 둔다. 마지막 성공값 캐싱 여부와 함께 정해야 한다.
 */
@Composable
internal fun StatusWidgetFailureContent(
    openHomeAction: Action?,
    modifier: GlanceModifier = GlanceModifier,
) {
    val metrics = statusWidgetMetricsFor(LocalSize.current)
    Box(
        modifier = modifier.fillMaxSize().clickableIfPresent(openHomeAction),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                GlanceModifier
                    .width(metrics.cardWidth)
                    .height(metrics.cardHeight)
                    .background(ColorProvider(Color(SemanticColors.Background.Default.Default)))
                    .padding(metrics.paddingHorizontal),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "상태를 불러오지 못했어요",
                style =
                    TextStyle(
                        color = ColorProvider(Color(SemanticColors.Text.Default.Secondary)),
                        fontSize = metrics.labelFontSize,
                    ),
            )
        }
    }
}
