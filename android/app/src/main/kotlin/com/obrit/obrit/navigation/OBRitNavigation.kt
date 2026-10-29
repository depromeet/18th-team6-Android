package com.obrit.obrit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import com.obrit.obrit.navigation.deeplink.ObritDeepLink
import com.obrit.obrit.navigation.route.HomeRoute
import com.obrit.obrit.navigation.route.OnboardingRoute
import com.obrit.obrit.navigation.route.RegisterRoute
import com.obrit.obrit.storage.OnboardingStorage
import org.koin.compose.koinInject

@Suppress("LongMethod")
@Composable
fun OBRitNavigation(
    deepLink: ObritDeepLink?,
    onDeepLinkConsume: () -> Unit,
    modifier: Modifier = Modifier,
    openHomeSignal: Int = 0,
) {
    val onboardingStorage = koinInject<OnboardingStorage>()
    val isOnboardingCompleted = onboardingStorage.isCompleted()
    val backStack =
        rememberNavBackStack(
            if (isOnboardingCompleted) HomeRoute.Home else OnboardingRoute.Start,
        )

    val currentOnDeepLinkConsume by rememberUpdatedState(onDeepLinkConsume)

    // 위젯을 눌러 들어오면 이전에 보던 화면이 아니라 홈에서 시작한다.
    // 온보딩을 마치지 않은 사용자는 건드리지 않는다.
    LaunchedEffect(openHomeSignal) {
        if (openHomeSignal > 0 && isOnboardingCompleted) {
            backStack.clear()
            backStack.add(HomeRoute.Home)
        }
    }

    LaunchedEffect(deepLink) {
        if (deepLink == null) return@LaunchedEffect

        // 온보딩 전 사용자에게는 소모품이 없다. 딥링크를 버리고 온보딩을 그대로 진행한다.
        if (!isOnboardingCompleted) {
            currentOnDeepLinkConsume()
            return@LaunchedEffect
        }

        // 등록처럼 홈 밖에 있을 때 알림을 누르면 먼저 홈으로 돌아와야 아래 화면을 열 수 있다.
        if (backStack.lastOrNull() != HomeRoute.Home) {
            backStack.clear()
            backStack.add(HomeRoute.Home)
        }
    }

    OBRitNavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = backStack::removeLastOrNull,
        entryProvider =
            entryProvider {
                entry<HomeRoute.Home> {
                    HomeNavigation(
                        deepLink = deepLink.takeIf { isOnboardingCompleted },
                        onDeepLinkConsume = onDeepLinkConsume,
                        onReceiptRegisterClick = { backStack.add(RegisterRoute.ReceiptCamera) },
                        onManualRegisterClick = { backStack.add(RegisterRoute.ManualRegister) },
                        modifier = Modifier,
                    )
                }
                entry<RegisterRoute.ManualRegister> {
                    RegisterNavigation(
                        onExit = { backStack.removeLastOrNull() },
                        startDestination = RegisterRoute.ManualRegister,
                        modifier = Modifier,
                    )
                }
                entry<RegisterRoute.ReceiptCamera> {
                    RegisterNavigation(
                        onExit = { backStack.removeLastOrNull() },
                        startDestination = RegisterRoute.ReceiptCamera,
                        modifier = Modifier,
                    )
                }
                entry<OnboardingRoute.Start> {
                    OnboardingNavigation(
                        onOnboardingComplete = {
                            onboardingStorage.setCompleted()
                            backStack.clear()
                            backStack.add(HomeRoute.Home)
                        },
                        modifier = Modifier,
                    )
                }
            },
    )
}
