package com.obrit.feature.home.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.obrit.android.core.designsystem.component.snackbar.OBRitSnackbar
import com.obrit.android.core.designsystem.component.snackbar.SnackbarIcon
import com.obrit.feature.home.viewmodel.ConsumableListSortOrder
import com.obrit.feature.home.viewmodel.HomeSideEffect
import com.obrit.feature.home.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
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
    toastMessage: String? = null,
    onToastConsume: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()
    HomeResumeRefreshEffect(onResume = viewModel::onHomeResumed)

    Box(modifier = modifier.fillMaxSize()) {
        HomeScreenContent(
            state = state,
            action = viewModel.toScreenAction(onItemClick),
            modifier = Modifier.fillMaxSize(),
        )
        HomeToastHost(
            message = toastMessage,
            onConsume = onToastConsume,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is HomeSideEffect.OnSearchClick -> onSearchClick()
            is HomeSideEffect.OnNotificationClick -> onNotificationClick()
            is HomeSideEffect.OnProfileClick -> onProfileClick()
            is HomeSideEffect.OnMoreClick -> onMoreClick()
            is HomeSideEffect.OnReceiptRegisterClick -> onReceiptRegisterClick()
            is HomeSideEffect.OnManualRegisterClick -> onManualRegisterClick()
        }
    }
}

private fun HomeViewModel.toScreenAction(onItemClick: (Long) -> Unit): HomeScreenAction =
    HomeScreenAction(
        onSearchClick = ::onSearchClick,
        onNotificationClick = ::onNotificationClick,
        onProfileClick = ::onProfileClick,
        onListSortOrderChange = ::onListSortOrderChange,
        onDdayFilterChange = ::onDdayFilterChange,
        onSpareFilterChange = ::onSpareFilterChange,
        onFilterApply = ::onFilterApply,
        onMoreClick = ::onMoreClick,
        onListTabSelect = ::onListTabSelected,
        onLoadMoreItems = ::onLoadMoreItems,
        onReceiptRegisterClick = ::onReceiptRegisterClick,
        onManualRegisterClick = ::onManualRegisterClick,
        onItemClick = onItemClick,
    )

@Composable
private fun HomeToastHost(
    message: String?,
    onConsume: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentOnConsume by rememberUpdatedState(onConsume)
    LaunchedEffect(message) {
        if (message == null) return@LaunchedEffect
        delay(HOME_TOAST_DURATION_MS)
        currentOnConsume()
    }
    if (message == null) return

    OBRitSnackbar(
        message = message,
        icon = SnackbarIcon.Success,
        modifier =
            modifier
                .navigationBarsPadding()
                .padding(bottom = HOME_TOAST_BOTTOM_PADDING),
    )
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
    val onListTabSelect: () -> Unit,
    val onLoadMoreItems: () -> Unit,
    val onReceiptRegisterClick: () -> Unit,
    val onManualRegisterClick: () -> Unit,
    val onItemClick: (Long) -> Unit,
)

private const val HOME_TOAST_DURATION_MS = 2_500L

private val HOME_TOAST_BOTTOM_PADDING = 94.dp
