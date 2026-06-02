package com.obrit.obrit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.obrit.feature.register.screen.onboarding.OnboardingSelectScreen
import com.obrit.feature.register.screen.onboarding.OnboardingStartScreen
import com.obrit.obrit.navigation.route.OnboardingRoute

@Composable
fun OnboardingNavigation(
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val onboardingBackStack = rememberNavBackStack(OnboardingRoute.Start)

    NavDisplay(
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
                        // 선택한 카테고리를 2단계(수량·교체주기 입력)로 넘기는 처리는
                        // 2단계 화면이 생길 때 연결한다.
                        onNext = { onOnboardingComplete() },
                        modifier = Modifier,
                    )
                }
            },
    )
}
