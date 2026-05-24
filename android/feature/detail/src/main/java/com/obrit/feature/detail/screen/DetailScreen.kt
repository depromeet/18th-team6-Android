package com.obrit.feature.detail.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.obrit.feature.detail.viewmodel.DetailSideEffect
import com.obrit.feature.detail.viewmodel.DetailViewModel
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun DetailScreen(
    id: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()

    LaunchedEffect(id) {
        viewModel.loadAgent(id)
    }

    DetailScreenContent(
        state = state,
        action =
            DetailScreenAction(
                onBackClick = viewModel::onBackClick,
                onRetryClick = {
                    viewModel.loadAgent(id)
                },
            ),
        modifier = modifier,
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is DetailSideEffect.NavigateBack -> onBackClick()
        }
    }
}

internal data class DetailScreenAction(
    val onBackClick: () -> Unit,
    val onRetryClick: () -> Unit,
)
