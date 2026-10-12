package com.obrit.feature.widget

import android.content.Context
import android.graphics.Typeface
import android.text.TextPaint
import android.util.TypedValue
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.obrit.android.feature.widget.R
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import com.obrit.obrit.shared.designsystem.tokens.atom.typography.AtomText
import com.obrit.obrit.shared.designsystem.tokens.semantic.SemanticColors

private const val NOTICE_DESIGN_WIDTH = 109f
private const val RETRY_BUTTON_WIDTH = 52f
private const val FONT_SIZE_SEARCH_STEPS = 12

/** 실패 및 빈 상태에서 공통으로 사용하는 안내 레이아웃. */
@Composable
internal fun StatusWidgetNoticeContent(
    @DrawableRes iconRes: Int,
    message: String,
    modifier: GlanceModifier = GlanceModifier,
    retryAction: Action? = null,
    isLoading: Boolean = false,
) {
    val metrics = statusWidgetMetricsFor(LocalSize.current)
    val scale = metrics.cardWidth.value / NOTICE_DESIGN_WIDTH
    val textStyle = noticeTextStyle(scale)

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier =
                GlanceModifier
                    .width(metrics.cardWidth)
                    .height(metrics.cardHeight)
                    .background(ColorProvider(Color(SemanticColors.Background.Default.Default)))
                    .padding(
                        start = (AtomSpacing.S1_5 * scale).dp,
                        end = (AtomSpacing.S1_5 * scale).dp,
                        bottom = scale.dp,
                    ),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically,
        ) {
            NoticeIcon(iconRes = iconRes, isLoading = isLoading, modifier = GlanceModifier.size((AtomSpacing.S8 * scale).dp))
            Spacer(modifier = GlanceModifier.height((AtomSpacing.S1 * scale).dp))
            NoticeMessage(message = message, scale = scale, textStyle = textStyle)
            if (retryAction != null) {
                Spacer(modifier = GlanceModifier.height((AtomSpacing.S3 * scale).dp))
                RetryButton(scale = scale, textStyle = textStyle, retryAction = retryAction)
            }
        }
    }
}

@Composable
private fun NoticeIcon(
    @DrawableRes iconRes: Int,
    isLoading: Boolean,
    modifier: GlanceModifier = GlanceModifier,
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = modifier,
            color = ColorProvider(Color(SemanticColors.Icon.Default.Default)),
        )
    } else {
        Image(provider = ImageProvider(iconRes), contentDescription = null, modifier = modifier)
    }
}

@Composable
private fun NoticeMessage(
    message: String,
    scale: Float,
    textStyle: TextStyle,
    modifier: GlanceModifier = GlanceModifier,
) {
    val fontSize = fittedNoticeFontSize(LocalContext.current, message, scale)
    Box(
        modifier = modifier.fillMaxWidth().height((AtomText.S2xs.LineHeight * scale).dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = textStyle.copy(fontSize = fontSize.sp),
            maxLines = 1,
        )
    }
}

/** Glance의 시스템 글꼴과 SP 변환을 사용해 한 줄 영역 안에 들어가는 크기를 구한다. */
private fun fittedNoticeFontSize(
    context: Context,
    message: String,
    scale: Float,
): Float {
    val displayMetrics = context.resources.displayMetrics
    val availableWidth =
        (NOTICE_DESIGN_WIDTH - AtomSpacing.S1_5 * 2 - AtomSpacing.S0_5) * scale * displayMetrics.density
    val paint = TextPaint().apply { typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL) }
    var minimum = 0f
    var maximum = AtomText.S2xs.FontSize * scale
    // SP 변환은 큰 글꼴 설정에서 비선형일 수 있어 실제 픽셀 폭으로 비교한다.
    repeat(FONT_SIZE_SEARCH_STEPS) {
        val candidate = (minimum + maximum) / 2
        paint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, candidate, displayMetrics)
        if (paint.measureText(message) <= availableWidth) {
            minimum = candidate
        } else {
            maximum = candidate
        }
    }
    return minimum
}

@Composable
private fun RetryButton(
    scale: Float,
    textStyle: TextStyle,
    retryAction: Action,
    modifier: GlanceModifier = GlanceModifier,
) {
    Box(
        modifier =
            modifier
                .width((RETRY_BUTTON_WIDTH * scale).dp)
                .height((AtomSpacing.S6 * scale).dp)
                .background(
                    imageProvider = ImageProvider(R.drawable.widget_retry_background),
                    colorFilter =
                        ColorFilter.tint(
                            ColorProvider(Color(SemanticColors.Background.Default.DefaultHover)),
                        ),
                ).clickable(retryAction),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "다시 시도",
            style = textStyle,
            maxLines = 1,
        )
    }
}

private fun noticeTextStyle(scale: Float): TextStyle =
    TextStyle(
        color = ColorProvider(Color(SemanticColors.Text.Default.Default)),
        fontSize = (AtomText.S2xs.FontSize * scale).sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
    )
