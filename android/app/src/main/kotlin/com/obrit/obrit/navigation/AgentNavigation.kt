@file:Suppress("LongMethod")

package com.obrit.obrit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import com.obrit.feature.detail.screen.DetailEditScreen
import com.obrit.feature.detail.screen.DetailEditSubmitResult
import com.obrit.feature.detail.screen.DetailScreen
import com.obrit.obrit.navigation.route.AgentRoute

@Composable
fun AgentNavigation(modifier: Modifier = Modifier) {
    val agentBackStack = rememberNavBackStack(AgentRoute.Agents)
    var detailEditResult by remember { mutableStateOf<DetailEditSubmitResult?>(null) }

    OBRitNavDisplay(
        backStack = agentBackStack,
        modifier = modifier,
        onBack = agentBackStack::removeLastOrNull,
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
                entry<AgentRoute.AgentDetail> { route ->
                    DetailScreen(
                        id = route.id,
                        onBackClick = {
                            agentBackStack.removeLastOrNull()
                        },
                        onEditClick = { consumableId ->
                            agentBackStack.add(AgentRoute.AgentDetailEdit(consumableId))
                        },
                        editResult = detailEditResult,
                        onEditResultConsume = {
                            detailEditResult = null
                        },
                        modifier = Modifier,
                    )
                }
                entry<AgentRoute.AgentDetailEdit> { route ->
                    DetailEditScreen(
                        consumableId = route.id,
                        onCloseClick = {
                            agentBackStack.removeLastOrNull()
                        },
                        onCompleteClick = { result ->
                            detailEditResult = result
                            agentBackStack.removeLastOrNull()
                        },
                        modifier = Modifier,
                    )
                }
            },
    )
}
