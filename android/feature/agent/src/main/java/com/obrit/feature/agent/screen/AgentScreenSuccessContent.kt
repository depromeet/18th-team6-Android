package com.obrit.feature.agent.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import com.obrit.feature.agent.viewmodel.AgentUiState

@Composable
internal fun AgentScreenSuccessContent(
    state: AgentUiState.Success,
    action: AgentScreenAction,
    modifier: Modifier = Modifier,
) {
    // Keep placeholder parameters observed until the success UI consumes them.
    key(state, action) {
        Box(modifier = modifier)
    }
}
