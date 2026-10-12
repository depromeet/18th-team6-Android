package com.obrit.obrit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import com.obrit.obrit.navigation.route.AgentRoute
import com.obrit.obrit.navigation.route.HomeRoute
import com.obrit.obrit.navigation.route.OnboardingRoute
import com.obrit.obrit.navigation.route.RegisterRoute
import com.obrit.obrit.storage.OnboardingStorage
import org.koin.compose.koinInject

@Suppress("LongMethod")
@Composable
fun OBRitNavigation(
    modifier: Modifier = Modifier,
    openHomeSignal: Int = 0,
) {
    val onboardingStorage = koinInject<OnboardingStorage>()
    val backStack =
        rememberNavBackStack(
            if (onboardingStorage.isCompleted()) HomeRoute.Home else OnboardingRoute.Start,
        )

    // 위젯을 눌러 들어오면 이전에 보던 화면이 아니라 홈에서 시작한다.
    // 온보딩을 마치지 않은 사용자는 건드리지 않는다.
    LaunchedEffect(openHomeSignal) {
        if (openHomeSignal > 0 && onboardingStorage.isCompleted()) {
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
                        onReceiptRegisterClick = { backStack.add(RegisterRoute.ReceiptCamera) },
                        onManualRegisterClick = { backStack.add(RegisterRoute.ManualRegister) },
                        modifier = Modifier,
                    )
                }
                entry<AgentRoute.Agents> {
                    AgentNavigation(modifier = Modifier)
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
