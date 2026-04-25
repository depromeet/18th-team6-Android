package com.obrit.feature.agent.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.obrit.feature.agent.viewmodel.AgentUiState

@Composable
internal fun AgentScreenContent(
    state: AgentUiState,
    action: AgentScreenAction,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is AgentUiState.Success -> {
            AgentScreenSuccessContent(
                state = state,
                action = action,
                modifier = modifier,
            )
        }
        is AgentUiState.Loading -> {
            AgentScreenLoadingContent(modifier = modifier)
        }
        is AgentUiState.LoadFailed -> {
            AgentScreenFailureContent(modifier = modifier)
        }
    }
}
