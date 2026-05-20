package com.obrit.feature.agent.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.obrit.feature.agent.viewmodel.AgentSideEffect
import com.obrit.feature.agent.viewmodel.AgentViewModel
import com.obrit.obrit.shared.model.agents.Agent
import com.obrit.obrit.shared.model.agents.AgentType
import com.obrit.obrit.shared.model.agents.error.CreateAgentError
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun AgentScreen(
    onAgentClick: (Agent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AgentViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()

    AgentScreenContent(
        state = state,
        action =
            AgentScreenAction(
                onCreateAgent = viewModel::createAgent,
                onDeleteAgent = viewModel::deleteAgent,
                onPatchAgent = viewModel::patchAgent,
                onAgentClick = viewModel::onAgentClick,
                onAgentLongClick = viewModel::onAgentLongClick,
                onMenuClick = viewModel::onMenuClick,
            ),
        modifier = modifier,
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is AgentSideEffect.OnAgentClick -> onAgentClick(sideEffect.agent)
            is AgentSideEffect.OnAgentLongClick -> {}
            is AgentSideEffect.OnMenuClick -> {}
            is AgentSideEffect.ShowSnackbar -> {
                // SnackbarHost.show(sideEffect.message)
            }
            is AgentSideEffect.OnError -> {
                when (sideEffect.error) {
                    is CreateAgentError.InvalidAgentType -> {
                        viewModel.showSnackbar("Invalid agent type")
                    }
                    else -> {
                        viewModel.showSnackbar("Unknown error")
                    }
                }
            }
        }
    }
}

internal data class AgentScreenAction(
    val onCreateAgent: (String, String, AgentType) -> Unit,
    val onDeleteAgent: (Int) -> Unit,
    val onPatchAgent: (Int, String, String, AgentType) -> Unit,
    val onAgentClick: (Agent) -> Unit,
    val onAgentLongClick: (Agent) -> Unit,
    val onMenuClick: () -> Unit,
)
