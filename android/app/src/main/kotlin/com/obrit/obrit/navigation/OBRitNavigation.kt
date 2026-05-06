package com.obrit.obrit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.obrit.feature.agent.screen.ConsumableDetailScreen
import com.obrit.feature.agent.screen.HomeScreen
import com.obrit.obrit.navigation.route.AgentRoute
import com.obrit.obrit.navigation.route.ConsumableDetailRoute
import com.obrit.obrit.navigation.route.HomeRoute

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
                        onConsumableClick = { consumable ->
                            backStack.add(ConsumableDetailRoute(consumable.id))
                        },
                        onUsageClick = { usage ->
                            backStack.add(ConsumableDetailRoute(usage.id))
                        },
                        modifier = Modifier,
                    )
                }
                entry<ConsumableDetailRoute> { route ->
                    ConsumableDetailScreen(
                        consumableId = route.consumableId,
                        onBackClick = {
                            backStack.removeLastOrNull()
                        },
                        modifier = Modifier,
                    )
                }
                entry<AgentRoute> {
                    AgentNavigation(modifier = Modifier)
                }
            },
    )
}
