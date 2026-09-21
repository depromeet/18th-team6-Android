package com.obrit.feature.widget

import android.content.Context

/**
 * 마지막으로 성공한 조회 결과를 남겨둔다.
 *
 * 위젯은 하루 한 번 갱신되는데, 그 시점에 네트워크가 안 되면 화면에 이미 떠 있던 값이
 * 실패 문구로 바뀐다. 사용자가 계속 보는 화면이라 그 편이 빈 값보다 나쁘므로,
 * 조회에 실패하면 직전 값을 그대로 다시 그린다.
 */
internal class StatusWidgetCache(
    context: Context,
) {
    private val preferences =
        context.applicationContext.getSharedPreferences(WIDGET_PREFERENCES, Context.MODE_PRIVATE)

    fun load(): StatusWidgetState? {
        if (!preferences.contains(KEY_GOOD_RATIO)) return null
        return StatusWidgetState(
            dangerCount = preferences.getInt(KEY_DANGER_COUNT, 0),
            warningCount = preferences.getInt(KEY_WARNING_COUNT, 0),
            goodRatio = preferences.getFloat(KEY_GOOD_RATIO, 0f),
        )
    }

    fun save(state: StatusWidgetState) {
        preferences
            .edit()
            .putInt(KEY_DANGER_COUNT, state.dangerCount)
            .putInt(KEY_WARNING_COUNT, state.warningCount)
            .putFloat(KEY_GOOD_RATIO, state.goodRatio)
            .apply()
    }

    private companion object {
        const val WIDGET_PREFERENCES = "obritStatusWidget"
        const val KEY_DANGER_COUNT = "dangerCount"
        const val KEY_WARNING_COUNT = "warningCount"
        const val KEY_GOOD_RATIO = "goodRatio"
    }
}
