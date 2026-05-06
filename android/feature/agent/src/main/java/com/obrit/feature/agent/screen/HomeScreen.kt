package com.obrit.feature.agent.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.obrit.feature.agent.viewmodel.DeviceTilt
import com.obrit.feature.agent.viewmodel.HomeConsumableUiModel
import com.obrit.feature.agent.viewmodel.HomeSideEffect
import com.obrit.feature.agent.viewmodel.HomeStatusFilter
import com.obrit.feature.agent.viewmodel.HomeUsageUiModel
import com.obrit.feature.agent.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onConsumableClick: (HomeConsumableUiModel) -> Unit = {},
    onUsageClick: (HomeUsageUiModel) -> Unit = {},
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
                onStatusFilterClick = viewModel::selectStatusFilter,
                onPreviewSortClick = viewModel::cyclePreviewSort,
                onPreviewMoreClick = viewModel::togglePreviewExpanded,
                onConsumableClick = onConsumableClick,
                onUsageClick = onUsageClick,
                onHomeTabClick = viewModel::onHomeTabClick,
                onListTabClick = viewModel::onListTabClick,
                onAddClick = viewModel::onAddClick,
                onOrbDragged = viewModel::onOrbDragged,
                onDeviceTiltChanged = viewModel::onDeviceTiltChanged,
            ),
        modifier = modifier,
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is HomeSideEffect.ShowSnackbar -> {
                // SnackbarHost.show(sideEffect.message)
            }
        }
    }
}

internal data class HomeScreenAction(
    val onSearchClick: () -> Unit,
    val onNotificationClick: () -> Unit,
    val onProfileClick: () -> Unit,
    val onStatusFilterClick: (HomeStatusFilter) -> Unit,
    val onPreviewSortClick: () -> Unit,
    val onPreviewMoreClick: () -> Unit,
    val onConsumableClick: (HomeConsumableUiModel) -> Unit,
    val onUsageClick: (HomeUsageUiModel) -> Unit,
    val onHomeTabClick: () -> Unit,
    val onListTabClick: () -> Unit,
    val onAddClick: () -> Unit,
    val onOrbDragged: (Float, Float, Float) -> Unit,
    val onDeviceTiltChanged: (DeviceTilt) -> Unit,
)
