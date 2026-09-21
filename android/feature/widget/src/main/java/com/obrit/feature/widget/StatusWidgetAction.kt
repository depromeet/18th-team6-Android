package com.obrit.feature.widget

import android.content.Context
import android.content.Intent
import androidx.glance.action.Action
import androidx.glance.appwidget.action.actionStartActivity

/** 위젯을 눌러 앱을 열었을 때 홈으로 보내달라는 표시. */
const val EXTRA_OPEN_HOME = "com.obrit.feature.widget.OPEN_HOME"

/**
 * 위젯을 누르면 앱을 열고 홈 화면으로 보낸다.
 *
 * 위젯 모듈은 app 모듈을 참조할 수 없으므로 실행할 Activity를 런처 intent에서 역으로 찾는다.
 * 앱이 이미 떠 있을 때도 [EXTRA_OPEN_HOME]을 전달하려면 플래그를 직접 지정해야 한다.
 * 런처 intent를 그대로 쓰면 기존 task를 앞으로 가져오기만 하고 extra는 전달되지 않는다.
 *
 * @return 실행할 Activity를 찾지 못하면 null
 */
internal fun openHomeAction(context: Context): Action? {
    val launcherComponent =
        context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.component
            ?: return null

    val intent =
        Intent().apply {
            component = launcherComponent
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_OPEN_HOME, true)
        }
    return actionStartActivity(intent)
}
