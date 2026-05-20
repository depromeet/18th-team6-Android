package com.obrit.feature.home.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.obrit.feature.home.viewmodel.HomeSideEffect
import com.obrit.feature.home.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()

    HomeScreenContent(
        state = state,
        action =
            HomeScreenAction(
                onSearchClick = viewModel::onSearchClick,
                onNotificationClick = viewModel::onNotificationClick,
                onProfileClick = viewModel::onProfileClick,
            ),
        modifier = modifier,
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is HomeSideEffect.OnSearchClick -> onSearchClick()
            is HomeSideEffect.OnNotificationClick -> onNotificationClick()
            is HomeSideEffect.OnProfileClick -> onProfileClick()
        }
    }
}

internal data class HomeScreenAction(
    val onSearchClick: () -> Unit,
    val onNotificationClick: () -> Unit,
    val onProfileClick: () -> Unit,
)
