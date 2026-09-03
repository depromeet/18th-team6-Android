package com.obrit.obrit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import com.obrit.feature.register.screen.complete.RegisterCompleteScreen
import com.obrit.feature.register.screen.onboarding.OnboardingDetailScreen
import com.obrit.feature.register.screen.onboarding.OnboardingSelectScreen
import com.obrit.feature.register.screen.onboarding.OnboardingStartScreen
import com.obrit.obrit.navigation.route.OnboardingRoute
import com.obrit.obrit.notification.rememberNotificationPermissionRequest

@Composable
@Suppress("LongMethod")
fun OnboardingNavigation(
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val onboardingBackStack = rememberNavBackStack(OnboardingRoute.Start)
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
                        onNext = { onboardingBackStack.add(OnboardingRoute.Detail(it)) },
                        modifier = Modifier,
                    )
                }
                entry<OnboardingRoute.Detail> { key ->
                    OnboardingDetailScreen(
                        selectedCategoryIds = key.selectedIds,
                        onBack = { onboardingBackStack.removeLastOrNull() },
                        onComplete = { onboardingBackStack.add(OnboardingRoute.Complete) },
                        modifier = Modifier,
                    )
                }
                entry<OnboardingRoute.Complete> {
                    // 알림 정책 7.4: 소모품 등록을 마친 시점이라 알림의 목적을 설명할 근거가 있다.
                    // 완료 화면을 읽을 시간을 준 뒤, CTA를 누르는 시점에 권한을 요청하고 홈으로 넘어간다.
                    val requestNotificationPermission =
                        rememberNotificationPermissionRequest(onFinish = onOnboardingComplete)
                    RegisterCompleteScreen(
                        onExit = requestNotificationPermission,
                        modifier = Modifier,
                    )
                }
            },
    )
}
