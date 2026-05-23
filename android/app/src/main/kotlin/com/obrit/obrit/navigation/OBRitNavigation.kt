package com.obrit.obrit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.obrit.feature.home.screen.HomeScreen
import com.obrit.obrit.navigation.route.AgentRoute
import com.obrit.obrit.navigation.route.HomeRoute
import com.obrit.obrit.navigation.route.RegisterRoute

@Composable
fun OBRitNavigation(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(HomeRoute)

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = backStack::removeLastOrNull,
        entryProvider =
            entryProvider {
                entry<HomeRoute> {
                    HomeScreen(
                        onSearchClick = {},
                        onNotificationClick = {},
                        onProfileClick = {},
                        onRegisterClick = { backStack.add(RegisterRoute.ManualRegister) },
                        modifier = Modifier,
                    )
                }
                entry<AgentRoute.Agents> {
                    AgentNavigation(modifier = Modifier)
                }
                entry<RegisterRoute> {
                    RegisterNavigation(
                        onExit = { backStack.removeLastOrNull() },
                        modifier = Modifier,
                    )
                }
            },
    )
}
