package com.obrit.obrit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
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
    var homeToastMessage by remember { mutableStateOf<String?>(null) }
    val onManualRegister: (String) -> Unit = { itemName ->
        homeToastMessage = ITEM_ADDED_TOAST_FORMAT.format(itemName)
        backStack.removeLastOrNull()
    }

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
                        toastMessage = homeToastMessage,
                        onToastConsume = { homeToastMessage = null },
                        modifier = Modifier,
                    )
                }
                entry<RegisterRoute.ManualRegister> {
                    RegisterNavigation(
                        onExit = { backStack.removeLastOrNull() },
                        onManualRegister = onManualRegister,
                        startDestination = RegisterRoute.ManualRegister,
                        modifier = Modifier,
                    )
                }
                entry<RegisterRoute.ReceiptCamera> {
                    RegisterNavigation(
                        onExit = { backStack.removeLastOrNull() },
                        onManualRegister = onManualRegister,
                        startDestination = RegisterRoute.ReceiptCamera,
                        modifier = Modifier,
                    )
                }
                entry<OnboardingRoute.Start> {
                    OnboardingNavigation(
                        // 권한 응답 전에 앱을 종료해도 다음 실행에 재등록하지 않도록 등록 성공 시점에 저장한다.
                        onRegister = onboardingStorage::setCompleted,
                        onOnboardingComplete = { registeredCount ->
                            homeToastMessage = ONBOARDING_REGISTERED_TOAST_FORMAT.format(registeredCount)
                            backStack.clear()
                            backStack.add(HomeRoute.Home)
                        },
                        modifier = Modifier,
                    )
                }
            },
    )
}

private const val ITEM_ADDED_TOAST_FORMAT = "%s을(를) 추가했어요"

// 소모품 추가·온보딩 합의사항 미확정: 온보딩 기능명세서 4.4의 완료 화면 제목을 임시로 쓴다.
private const val ONBOARDING_REGISTERED_TOAST_FORMAT = "%d개의 소모품을 등록했어요"
