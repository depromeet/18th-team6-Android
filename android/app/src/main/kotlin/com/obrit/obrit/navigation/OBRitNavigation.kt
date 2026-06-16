package com.obrit.obrit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.obrit.obrit.navigation.route.AgentRoute
import com.obrit.obrit.navigation.route.HomeRoute
// import com.obrit.obrit.navigation.route.OnboardingRoute  // TODO: 임시 진입점 복원 시 주석 해제
import com.obrit.obrit.navigation.route.RegisterRoute
// import com.obrit.obrit.storage.OnboardingStorage  // TODO: 임시 진입점 복원 시 주석 해제
// import org.koin.compose.koinInject  // TODO: 임시 진입점 복원 시 주석 해제

@Composable
fun OBRitNavigation(modifier: Modifier = Modifier) {
    // TODO: 영수증 등록 UI 개발용 임시 진입점 — 작업 완료 후 아래 두 줄로 복원
    // val onboardingStorage = koinInject<OnboardingStorage>()
    // val backStack = rememberNavBackStack(if (onboardingStorage.isCompleted()) HomeRoute.Home else OnboardingRoute.Start)
    val backStack = rememberNavBackStack(RegisterRoute.ManualRegister)
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = backStack::removeLastOrNull,
        entryProvider =
            entryProvider {
                entry<HomeRoute.Home> {
                    HomeNavigation(
                        onRegisterClick = { backStack.add(RegisterRoute.ManualRegister) },
                        modifier = Modifier,
                    )
                }
                entry<AgentRoute.Agents> {
                    AgentNavigation(modifier = Modifier)
                }
                entry<RegisterRoute.ManualRegister> {
                    RegisterNavigation(
                        onExit = { backStack.removeLastOrNull() },
                        modifier = Modifier,
                    )
                }
                // TODO: 임시 진입점 복원 시 주석 해제
                // entry<OnboardingRoute.Start> {
                //     OnboardingNavigation(
                //         onOnboardingComplete = {
                //             onboardingStorage.setCompleted()
                //             backStack.clear()
                //             backStack.add(HomeRoute.Home)
                //         },
                //         modifier = Modifier,
                //     )
                // }
            },
    )
}
