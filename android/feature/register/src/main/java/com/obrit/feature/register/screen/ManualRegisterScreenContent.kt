package com.obrit.feature.register.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.obrit.feature.register.viewmodel.ManualRegisterUiState

@Composable
internal fun ManualRegisterScreenContent(
    state: ManualRegisterUiState,
    action: ManualRegisterScreenAction,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is ManualRegisterUiState.Editing -> {
            ManualRegisterEditingContent(
                state = state,
                action = action,
                modifier = modifier,
            )
        }
    }
}
