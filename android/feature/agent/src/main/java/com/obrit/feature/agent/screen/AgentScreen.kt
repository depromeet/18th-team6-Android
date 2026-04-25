package com.obrit.feature.agent.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.obrit.feature.agent.viewmodel.AgentSideEffect
import com.obrit.feature.agent.viewmodel.AgentViewModel
import com.obrit.obrit.shared.model.agents.error.CreateAgentError
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun AgentScreen(
    modifier: Modifier = Modifier,
    viewModel: AgentViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()

    AgentScreenContent(
        state = state,
        modifier = modifier,
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is AgentSideEffect.OnAgentClick -> {}
            is AgentSideEffect.OnAgentLongClick -> {}
            is AgentSideEffect.OnMenuClick -> {}
            is AgentSideEffect.ShowSnackbar -> {
                // SnackbarHost.show(sideEffect.message)
            }
            is AgentSideEffect.OnError -> {
                when (sideEffect.error) {
                    is CreateAgentError.InvalidAgentType -> {
                        viewModel.showSnackbar(TODO("stringResource(...)"))
                    }
                    TODO("Else errors") -> {
                    }
                }
            }
        }
    }
}
