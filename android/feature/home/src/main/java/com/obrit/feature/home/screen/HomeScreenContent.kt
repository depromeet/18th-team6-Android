package com.obrit.feature.home.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.obrit.feature.home.viewmodel.HomeUiState

@Composable
internal fun HomeScreenContent(
    state: HomeUiState,
    action: HomeScreenAction,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is HomeUiState.Success ->
            HomeScreenSuccessContent(
                state = state,
                action = action,
                modifier = modifier,
            )
        is HomeUiState.Loading -> HomeScreenLoadingContent(modifier = modifier)
        is HomeUiState.LoadFailed -> HomeScreenFailureContent(modifier = modifier)
    }
}
