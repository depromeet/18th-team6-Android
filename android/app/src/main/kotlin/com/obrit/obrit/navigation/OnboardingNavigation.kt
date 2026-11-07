package com.obrit.obrit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import com.obrit.feature.register.screen.onboarding.OnboardingSelectScreen
import com.obrit.feature.register.screen.onboarding.OnboardingStartScreen
import com.obrit.obrit.navigation.route.OnboardingRoute
import com.obrit.obrit.notification.rememberNotificationPermissionRequest

@Composable
fun OnboardingNavigation(
    onRegister: () -> Unit,
    onOnboardingComplete: (registeredCount: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val onboardingBackStack = rememberNavBackStack(OnboardingRoute.Start)
    var registeredCount by remember { mutableIntStateOf(0) }
    // 소모품 등록을 마친 시점이라 알림의 목적을 설명할 근거가 있다. 권한 응답을 받은 뒤 홈으로 넘어간다.
    val requestNotificationPermission =
        rememberNotificationPermissionRequest(onFinish = { onOnboardingComplete(registeredCount) })
    OBRitNavDisplay(
        backStack = onboardingBackStack,
        modifier = modifier,
        onBack = { onboardingBackStack.removeLastOrNull() },
        entryProvider =
            entryProvider {
                entry<OnboardingRoute.Start> {
                    OnboardingStartScreen(
                        onStartOnboarding = { onboardingBackStack.add(OnboardingRoute.Select) },
                        modifier = Modifier,
                    )
                }
                entry<OnboardingRoute.Select> {
                    OnboardingSelectScreen(
                        onBack = { onboardingBackStack.removeLastOrNull() },
                        onRegister = { count ->
                            registeredCount = count
                            onRegister()
                            requestNotificationPermission()
                        },
                        modifier = Modifier,
                    )
                }
            },
    )
}
