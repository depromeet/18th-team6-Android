package com.obrit.obrit

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.navigation.OBRitNavigation
import com.obrit.obrit.navigation.deeplink.ObritDeepLink
import com.obrit.obrit.navigation.deeplink.obritDeepLink

class MainActivity : ComponentActivity() {
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
        pendingDeepLink = intent.obritDeepLink()

        setContent {
            OBRitTheme {
                OBRitNavigation(
                    deepLink = pendingDeepLink,
                    onDeepLinkConsume = { pendingDeepLink = null },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingDeepLink = intent.obritDeepLink()
    }
}
