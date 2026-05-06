package com.obrit.feature.agent.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.obrit.feature.agent.viewmodel.ConsumableDetailSideEffect
import com.obrit.feature.agent.viewmodel.ConsumableDetailViewModel
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ConsumableDetailScreen(
    consumableId: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConsumableDetailViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()

    LaunchedEffect(consumableId) {
        viewModel.load(consumableId)
    }

    ConsumableDetailScreenContent(
        state = state,
        action =
            ConsumableDetailScreenAction(
                onBackClick = onBackClick,
                onMoreClick = viewModel::onMoreClick,
                onSpareManageClick = viewModel::onSpareManageClick,
                onReplacementCompleteClick = viewModel::onReplacementCompleteClick,
            ),
        modifier = modifier,
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ConsumableDetailSideEffect.ShowSnackbar -> {
                // SnackbarHost.show(sideEffect.message)
            }
        }
    }
}

internal data class ConsumableDetailScreenAction(
    val onBackClick: () -> Unit,
    val onMoreClick: () -> Unit,
    val onSpareManageClick: () -> Unit,
    val onReplacementCompleteClick: () -> Unit,
)
