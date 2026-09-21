package com.obrit.feature.widget

import kotlin.math.roundToInt

internal const val GAUGE_TOTAL_DOTS = 30
internal const val GAUGE_COLUMNS = 10
internal const val GAUGE_ROWS = 3

private const val GOOD_RATIO_THRESHOLD = 0.5f

/**
 * 위젯이 그리는 상태.
 *
 * @param goodRatio 양호 비율. 0f~1f의 원본 실수값이며 반올림하지 않는다.
 */
internal data class StatusWidgetState(
    val dangerCount: Int,
    val warningCount: Int,
    val goodRatio: Float,
) {
    /** 양호가 50% 이상이면 그린, 미만이면 주황으로 표시한다. */
    val isGood: Boolean
        get() = goodRatio >= GOOD_RATIO_THRESHOLD

    /** 양호일 때는 양호 비율을, 아닐 때는 경고+위험 비율을 표시한다. */
    private val displayRatio: Float
        get() = if (isGood) goodRatio else 1f - goodRatio

    /** 반올림은 비율이 아니라 원 개수를 구하는 이 계산에서만 적용한다. */
    val filledDotCount: Int
        get() = (displayRatio * GAUGE_TOTAL_DOTS).roundToInt()
}
