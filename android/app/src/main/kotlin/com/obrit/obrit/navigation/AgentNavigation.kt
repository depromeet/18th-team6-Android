package com.obrit.obrit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.obrit.feature.agent.screen.AgentScreen
import com.obrit.obrit.navigation.route.AgentRoute

@Composable
fun AgentNavigation(modifier: Modifier = Modifier) {
    val agentBackStack = rememberNavBackStack(AgentRoute.Agents)

    NavDisplay(
        backStack = agentBackStack,
        modifier = modifier,
        entryProvider =
            entryProvider {
                entry<AgentRoute.Agents> {
//                    AgentScreen(
//                        onAgentClick = { agent ->
//                            agentBackStack.add(AgentRoute.AgentDetail(agent.id))
//                        },
//                        modifier = Modifier,
//                    )
                }
                entry<AgentRoute.AgentDetail> {
                    // AgentDetailScreen(modifier = Modifier)
                }
            },
    )
}
