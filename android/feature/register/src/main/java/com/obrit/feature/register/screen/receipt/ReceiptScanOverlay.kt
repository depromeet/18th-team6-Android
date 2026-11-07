package com.obrit.feature.register.screen.receipt

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * 어두운 dim 위에 사각형 뷰파인더 창을 뚫는 오버레이(상태2).
 * 전체를 검정으로 칠한 뒤 가운데를 [BlendMode.Clear]로 투명하게 도려낸다.
 *
 * 위치는 Figma 절대좌표를 그대로 쓰지 않고 **적응형 안전영역**으로 환산한다:
 * 상단 바 아래 ~ 하단 컨트롤 위 영역(시스템 인셋 포함)을 밴드로 잡고,
 * Figma의 가로 여백(46)과 비율(320:460)을 유지해 그 밴드 중앙에 배치한다.
 * → 어떤 화면 비율에서도 셔터/상단 바와 겹치지 않는다.
 */
@Composable
internal fun ReceiptScanOverlay(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val statusTopPx = WindowInsets.statusBars.getTop(density).toFloat()
    val navBottomPx = WindowInsets.navigationBars.getBottom(density).toFloat()

    Canvas(
        modifier =
            modifier
                .fillMaxSize()
                // BlendMode.Clear가 레이어 내부에서만 작동하도록 오프스크린 합성.
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen),
    ) {
        val bandTop = statusTopPx + TOP_BAR_HEIGHT.dp.toPx()
        val bandBottom = size.height - navBottomPx - (CONTROLS_BLOCK_HEIGHT + CONTROLS_BOTTOM_PADDING).dp.toPx()
        val bandHeight = bandBottom - bandTop

        val margin = CUTOUT_SIDE_MARGIN.dp.toPx()
        val cutoutWidth = size.width - margin * 2
        val cutoutHeight = (cutoutWidth * CUTOUT_ASPECT).coerceAtMost(bandHeight)
        val top = bandTop + (bandHeight - cutoutHeight) / 2f
        val radius = CUTOUT_CORNER_RADIUS.dp.toPx()

        drawRect(color = Color.Black.copy(alpha = DIM_ALPHA))
        drawRoundRect(
            color = Color.Transparent,
            topLeft = Offset(margin, top),
            size = Size(cutoutWidth, cutoutHeight),
            cornerRadius = CornerRadius(radius, radius),
            blendMode = BlendMode.Clear,
        )
    }
}

// 컷아웃이 놓일 밴드 계산용 — 상단 바/하단 컨트롤 점유 높이(ReceiptCameraScreen과 일치).
private const val TOP_BAR_HEIGHT = 56
private const val CONTROLS_BLOCK_HEIGHT = 68
private const val CONTROLS_BOTTOM_PADDING = 65

// Figma 컷아웃: 가로 여백 46, 크기 320×460(비율 460/320).
private const val CUTOUT_SIDE_MARGIN = 46
private const val CUTOUT_ASPECT = 460f / 320f
private const val CUTOUT_CORNER_RADIUS = 16
private const val DIM_ALPHA = 0.6f
