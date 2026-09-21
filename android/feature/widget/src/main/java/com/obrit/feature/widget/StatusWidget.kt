package com.obrit.feature.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent

/**
 * 소모품 상태를 보여주는 2x2 홈 화면 위젯.
 *
 * 현재는 고정된 샘플 상태를 그린다. 실제 데이터 연동은 별도 작업에서 붙인다.
 */
internal class StatusWidget : GlanceAppWidget() {
    /** 게이지를 위젯 폭에 맞춰 그리려면 최소 크기가 아닌 실제 크기가 필요하다. */
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        provideContent {
            StatusWidgetContent(state = SampleState)
        }
    }

    private companion object {
        val SampleState =
            StatusWidgetState(
                dangerCount = 2,
                warningCount = 0,
                goodRatio = 0.7f,
            )
    }
}

class StatusWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StatusWidget()
}
