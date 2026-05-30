package com.obrit.feature.home.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.obrit.feature.home.viewmodel.ConsumableListSortOrder
import com.obrit.feature.home.viewmodel.HomeUiState
import com.obrit.feature.home.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

internal data class ConsumableListScreenAction(
    val onBack: () -> Unit,
    val onSortOrderChange: (ConsumableListSortOrder) -> Unit,
    val onDdayFilterChange: (Int) -> Unit,
    val onSpareFilterChange: (Int) -> Unit,
)

@Composable
fun ConsumableListScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()
    val success = state as? HomeUiState.Success ?: return

    ConsumableListScreenContent(
        buckets = success.status.buckets,
        sortOrder = success.listSortOrder,
        ddayRange = success.ddayRange,
        ddayFilterMax = success.ddayFilterMax,
        spareRange = success.spareRange,
        spareFilterMax = success.spareFilterMax,
        action =
            ConsumableListScreenAction(
                onBack = onBack,
                onSortOrderChange = viewModel::onListSortOrderChange,
                onDdayFilterChange = viewModel::onDdayFilterChange,
                onSpareFilterChange = viewModel::onSpareFilterChange,
            ),
        modifier = modifier,
    )
}
