package com.obrit.obrit

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.widget.EXTRA_OPEN_HOME
import com.obrit.feature.widget.requestStatusWidgetUpdate
import com.obrit.obrit.navigation.OBRitNavigation
import com.obrit.obrit.navigation.deeplink.ObritDeepLink
import com.obrit.obrit.navigation.deeplink.obritDeepLink

class MainActivity : ComponentActivity() {
    /** 위젯으로 들어올 때마다 값이 올라가고, 그때마다 홈으로 되돌린다. */
    private val openHomeSignal = mutableIntStateOf(0)

    /**
     * 네비게이션이 준비될 때까지 딥링크를 들고 있다가 한 번 넘긴다(명세서 8.2).
     *
     * 종료 상태에서 알림을 누르면 Activity가 새로 만들어지므로 onCreate에서, 이미 떠 있으면
     * singleTop이라 onNewIntent로 들어온다.
     */
    private var pendingDeepLink by mutableStateOf<ObritDeepLink?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        handleIntent(intent)

        setContent {
            OBRitTheme {
                OBRitNavigation(
                    deepLink = pendingDeepLink,
                    onDeepLinkConsume = { pendingDeepLink = null },
                    openHomeSignal = openHomeSignal.intValue,
                )
            }
        }
    }

    // 앱이 이미 떠 있는 상태에서 위젯이나 알림을 누르면 새 intent가 여기로 들어온다.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    // 화면을 벗어나는 시점이 곧 위젯이 보이는 시점이다.
    // 앱에서 소모품을 바꾼 뒤 홈으로 나가면 여기서 위젯이 최신화된다.
    override fun onStop() {
        super.onStop()
        requestStatusWidgetUpdate(this)
    }

    /**
     * 위젯과 알림은 서로 다른 표시를 달고 들어오므로 둘을 함께 읽어도 섞이지 않는다.
     * 위젯 intent에는 딥링크가 없어 [pendingDeepLink]가 null이 되고, 알림 intent에는 위젯 표시가 없다.
     */
    private fun handleIntent(intent: Intent) {
        consumeOpenHomeRequest(intent)
        pendingDeepLink = intent.obritDeepLink()
    }

    private fun consumeOpenHomeRequest(intent: Intent) {
        if (!intent.getBooleanExtra(EXTRA_OPEN_HOME, false)) return
        // 같은 intent가 재전달돼도 두 번 처리하지 않는다.
        intent.removeExtra(EXTRA_OPEN_HOME)
        openHomeSignal.intValue++
    }
}
