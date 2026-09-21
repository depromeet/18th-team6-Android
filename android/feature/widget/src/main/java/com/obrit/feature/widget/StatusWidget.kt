package com.obrit.feature.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import com.obrit.obrit.shared.data.repository.HomeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

/**
 * 소모품 상태를 보여주는 2x2 홈 화면 위젯.
 *
 * 여기서는 조회하지 않고 [StatusWidgetCache]에 저장된 값만 그린다.
 * `provideContent` 바깥은 세션당 한 번만 실행되므로 그 자리에서 조회하면
 * 세션이 살아 있는 동안 값이 갱신되지 않는다. 조회는 [StatusWidgetReceiver]가 맡는다.
 */
internal class StatusWidget : GlanceAppWidget() {
    /** 게이지를 위젯 폭에 맞춰 그리려면 최소 크기가 아닌 실제 크기가 필요하다. */
    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        val cache = StatusWidgetCache(context)
        // 컴포지션 안에서 만들면 재구성마다 새 Intent가 생겨 RemoteViews가 매번 달라진다.
        val openHome = openHomeAction(context)

        provideContent {
            // 갱신마다 다시 실행되는 구간이라, 여기서 읽어야 최신 값이 그려진다.
            when (val state = cache.load()) {
                null -> StatusWidgetFailureContent(openHomeAction = openHome)
                else -> StatusWidgetContent(state = state, openHomeAction = openHome)
            }
        }
    }
}

class StatusWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StatusWidget()

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        super.onReceive(context, intent)

        // 저장된 값으로 먼저 그려진 뒤, 새로 조회한 값으로 한 번 더 그린다.
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            refreshInBackground(context.applicationContext)
        }
    }

    /**
     * goAsync는 쓰지 않는다. 상위 [GlanceAppWidgetReceiver]가 이미 사용 중이라 중복 호출이 된다.
     * 대신 프로세스 수명을 따르는 scope에서 돌린다. 갱신 경로가 화면 이탈과 주기 갱신이라
     * 호출 시점에 앱 프로세스가 살아 있어 조회가 끊길 여지는 작다.
     */
    private fun refreshInBackground(context: Context) {
        refreshScope.launch {
            val homeRepository = GlobalContext.get().get<HomeRepository>()
            val state = loadStatusWidgetState(homeRepository) ?: return@launch
            StatusWidgetCache(context).save(state)
            // receiver가 들고 있는 인스턴스를 쓴다. 새로 만들면 같은 위젯에 세션이 둘 생긴다.
            glanceAppWidget.updateAll(context)
        }
    }

    private companion object {
        val refreshScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}
