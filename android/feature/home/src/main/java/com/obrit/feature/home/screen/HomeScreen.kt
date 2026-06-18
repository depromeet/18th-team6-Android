package com.obrit.feature.home.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
    onReceiptRegisterClick: () -> Unit,
    onManualRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit = {},
    onItemClick: (Long) -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()
    HomeResumeRefreshEffect(onResume = viewModel::onHomeResumed)

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
                onFilterApply = viewModel::onFilterApply,
                onMoreClick = viewModel::onMoreClick,
                onLoadMoreItems = viewModel::onLoadMoreItems,
                onReceiptRegisterClick = onReceiptRegisterClick,
                onManualRegisterClick = onManualRegisterClick,
                onItemClick = onItemClick,
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

@Composable
private fun HomeResumeRefreshEffect(onResume: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnResume by rememberUpdatedState(onResume)

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    currentOnResume()
                }
            }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
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
    val onFilterApply: (Int, Int) -> Unit,
    val onMoreClick: () -> Unit,
    val onLoadMoreItems: () -> Unit,
    val onReceiptRegisterClick: () -> Unit,
    val onManualRegisterClick: () -> Unit,
    val onItemClick: (Long) -> Unit,
)
