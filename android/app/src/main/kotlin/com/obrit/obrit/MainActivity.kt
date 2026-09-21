package com.obrit.obrit

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableIntStateOf
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.widget.EXTRA_OPEN_HOME
import com.obrit.feature.widget.requestStatusWidgetUpdate
import com.obrit.obrit.navigation.OBRitNavigation

class MainActivity : ComponentActivity() {
    /** 위젯으로 들어올 때마다 값이 올라가고, 그때마다 홈으로 되돌린다. */
    private val openHomeSignal = mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        consumeOpenHomeRequest(intent)

        setContent {
            OBRitTheme {
                OBRitNavigation(openHomeSignal = openHomeSignal.intValue)
            }
        }
    }

    // 앱이 이미 떠 있는 상태에서 위젯을 누르면 새 intent가 여기로 들어온다.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        consumeOpenHomeRequest(intent)
    }

    // 화면을 벗어나는 시점이 곧 위젯이 보이는 시점이다.
    // 앱에서 소모품을 바꾼 뒤 홈으로 나가면 여기서 위젯이 최신화된다.
    override fun onStop() {
        super.onStop()
        requestStatusWidgetUpdate(this)
    }

    private fun consumeOpenHomeRequest(intent: Intent) {
        if (!intent.getBooleanExtra(EXTRA_OPEN_HOME, false)) return
        // 같은 intent가 재전달돼도 두 번 처리하지 않는다.
        intent.removeExtra(EXTRA_OPEN_HOME)
        openHomeSignal.intValue++
    }
}
