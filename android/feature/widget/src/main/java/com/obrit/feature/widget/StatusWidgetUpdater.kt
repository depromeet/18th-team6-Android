package com.obrit.feature.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

/**
 * 홈에 올라간 위젯을 다시 불러오게 한다.
 *
 * 실제 조회는 [StatusWidgetReceiver]가 자기 수명으로 처리하므로 호출한 쪽의 coroutine scope가
 * 취소돼도 갱신은 끝까지 진행된다. 이 함수 자체는 즉시 반환한다.
 */
fun requestStatusWidgetUpdate(context: Context) {
    val widgetIds =
        AppWidgetManager
            .getInstance(context)
            .getAppWidgetIds(ComponentName(context, StatusWidgetReceiver::class.java))

    // 홈에 올려둔 위젯이 없으면 조회할 이유도 없다.
    if (widgetIds.isEmpty()) return

    // AppWidgetProvider는 위젯 id가 실려 있을 때만 onUpdate를 부른다.
    val intent =
        Intent(context, StatusWidgetReceiver::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
        }
    context.sendBroadcast(intent)
}
