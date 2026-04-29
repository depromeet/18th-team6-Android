package com.obrit.obrit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.obrit.obrit.navigation.route.AgentRoute

@Composable
fun OBRitNavigation(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack()

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = backStack::removeLastOrNull,
        entryProvider =
            entryProvider {
                entry<AgentRoute> {
                    AgentNavigation(modifier = Modifier)
                }
            },
    )
}
