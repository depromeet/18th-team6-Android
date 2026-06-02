package com.obrit.obrit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.obrit.feature.register.screen.complete.RegisterCompleteScreen
import com.obrit.feature.register.screen.onboarding.OnboardingDetailScreen
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
                    RegisterCompleteScreen(
                        onExit = onOnboardingComplete,
                        modifier = Modifier,
                    )
                }
            },
    )
}
