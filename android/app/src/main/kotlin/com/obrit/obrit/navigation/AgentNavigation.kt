package com.obrit.obrit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.obrit.feature.agent.screen.AgentScreen
import com.obrit.feature.detail.screen.DetailScreen
import com.obrit.obrit.navigation.route.AgentRoute

@Composable
fun AgentNavigation(modifier: Modifier = Modifier) {
    val agentBackStack = rememberNavBackStack(AgentRoute.Agents)

    NavDisplay(
        backStack = agentBackStack,
        modifier = modifier,
        onBack = agentBackStack::removeLastOrNull,
        entryProvider =
            entryProvider {
                entry<AgentRoute.Agents> {
                    AgentScreen(
                        onAgentClick = { agent ->
                            agentBackStack.add(AgentRoute.AgentDetail(agent.id))
                        },
                        modifier = Modifier,
                    )
                }
                entry<AgentRoute.AgentDetail> { route ->
                    DetailScreen(
                        id = route.id,
                        onBackClick = {
                            agentBackStack.removeLastOrNull()
                        },
                        modifier = Modifier,
                    )
                }
            },
    )
}
