package com.obrit.feature.widget

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Figma 시안(109x115dp)의 치수. 런처 셀은 이보다 크고 기기마다 다르므로
 * 셀 크기에서 배율을 구해 이 값들을 환산한다.
 */
private object Design {
    const val WIDTH = 109f
    const val HEIGHT = 115f

    const val PADDING_HORIZONTAL = 8f
    const val PADDING_TOP = 2f
    const val PADDING_BOTTOM = 7f

    /** 숫자 한 줄의 높이. 시안에서 "2"는 y=2, "0"은 y=40이라 간격이 38dp다. */
    const val COUNT_ROW_HEIGHT = 38f

    /**
     * 시안은 32f지만 셀이 시안보다 크면 배율이 곱해져 숫자만 비대해 보인다.
     * 행 높이와 분리돼 있어 이 값만 낮추면 배치는 그대로 두고 글자만 작아진다.
     */
    const val NUMBER_FONT = 26f
    const val LABEL_FONT = 10f

    /** 숫자 오른쪽 끝(x=28)과 라벨 시작(x=49) 사이. */
    const val LABEL_GAP = 21f

    /**
     * 폰트가 기본으로 잡는 line box 높이 비율. 시안의 line height 44 / font size 32.
     * 행 높이가 이보다 작으면 숫자가 행 밖으로 넘쳐 아래가 잘리고,
     * 라벨과의 중앙 정렬 기준도 함께 틀어진다.
     */
    const val LINE_BOX_RATIO = 1.37f

    /** 숫자 블록 끝(y=78)과 게이지 시작(y=81) 사이. */
    const val CONTENT_GAP = 3f

    /** 원 중심 간격 9.346dp, 지름 8.31dp에서 나온 값들. */
    const val DOT_CELL = 9.346f
    const val DOT_GAP = 1.04f
}

/** 시안 비율을 [cellSize]에 맞춰 환산한 실제 치수. */
internal data class StatusWidgetMetrics(
    val cardWidth: Dp,
    val cardHeight: Dp,
    val paddingHorizontal: Dp,
    val paddingTop: Dp,
    val paddingBottom: Dp,
    val countRowHeight: Dp,
    val numberFontSize: TextUnit,
    val labelFontSize: TextUnit,
    val labelGap: Dp,
    val contentGap: Dp,
    val dotCellSize: Dp,
    val dotGap: Dp,
)

/**
 * 시안과 같은 가로세로비를 유지하면서 [cellSize] 안에 들어가는 카드 치수를 만든다.
 *
 * 런처 셀은 시안보다 세로로 길기 때문에 카드를 셀 전체에 채우면 비율이 깨진다.
 * 가로를 기준으로 맞추되 세로가 모자라면 세로에 맞춰 줄인다.
 */
internal fun statusWidgetMetricsFor(cellSize: DpSize): StatusWidgetMetrics {
    val widthFromHeight = cellSize.height * (Design.WIDTH / Design.HEIGHT)
    val cardWidth = minOf(cellSize.width, widthFromHeight)
    val scale = cardWidth.value / Design.WIDTH

    return StatusWidgetMetrics(
        cardWidth = cardWidth,
        cardHeight = (Design.HEIGHT * scale).dp,
        paddingHorizontal = (Design.PADDING_HORIZONTAL * scale).dp,
        paddingTop = (Design.PADDING_TOP * scale).dp,
        paddingBottom = (Design.PADDING_BOTTOM * scale).dp,
        // 숫자가 행 밖으로 넘치면 잘리므로 line box보다 작게는 두지 않는다.
        countRowHeight =
            (maxOf(Design.COUNT_ROW_HEIGHT, Design.NUMBER_FONT * Design.LINE_BOX_RATIO) * scale).dp,
        numberFontSize = (Design.NUMBER_FONT * scale).sp,
        labelFontSize = (Design.LABEL_FONT * scale).sp,
        labelGap = (Design.LABEL_GAP * scale).dp,
        contentGap = (Design.CONTENT_GAP * scale).dp,
        dotCellSize = (Design.DOT_CELL * scale).dp,
        dotGap = (Design.DOT_GAP * scale).dp,
    )
}
