package com.obrit.feature.home.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.obrit.feature.home.viewmodel.ConsumableListSortOrder
import com.obrit.feature.home.viewmodel.HomeSideEffect
import com.obrit.feature.home.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Suppress("LongParameterList")
@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit = {},
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
                onListSortOrderChange = viewModel::onListSortOrderChange,
                onDdayFilterChange = viewModel::onDdayFilterChange,
                onSpareFilterChange = viewModel::onSpareFilterChange,
                onMoreClick = viewModel::onMoreClick,
                onLoadMoreItems = viewModel::onLoadMoreItems,
                onRegisterClick = onRegisterClick,
            ),
        modifier = modifier,
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is HomeSideEffect.OnSearchClick -> onSearchClick()
            is HomeSideEffect.OnNotificationClick -> onNotificationClick()
            is HomeSideEffect.OnProfileClick -> onProfileClick()
            is HomeSideEffect.OnMoreClick -> onMoreClick()
        }
    }
}

internal data class HomeScreenAction(
    val onSearchClick: () -> Unit,
    val onNotificationClick: () -> Unit,
    val onProfileClick: () -> Unit,
    val onListSortOrderChange: (ConsumableListSortOrder) -> Unit,
    val onDdayFilterChange: (Int) -> Unit,
    val onSpareFilterChange: (Int) -> Unit,
    val onMoreClick: () -> Unit,
    val onLoadMoreItems: () -> Unit,
    val onRegisterClick: () -> Unit,
)
