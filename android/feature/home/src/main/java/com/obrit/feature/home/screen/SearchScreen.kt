package com.obrit.feature.home.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.obrit.feature.home.viewmodel.SearchSideEffect
import com.obrit.feature.home.viewmodel.SearchViewModel
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onItemClick: (Long) -> Unit = {},
    viewModel: SearchViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onScreenOpen()
    }

    SearchScreenContent(
        state = state,
        action =
            SearchScreenAction(
                onQueryChange = viewModel::onQueryChange,
                onKeywordClick = viewModel::onKeywordClick,
                onRemoveKeyword = viewModel::onRemoveKeyword,
                onSearch = viewModel::onSearch,
                onBackClick = viewModel::onBackClick,
                onItemClick = onItemClick,
            ),
        modifier = modifier,
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SearchSideEffect.NavigateBack -> onBackClick()
        }
    }
}

internal data class SearchScreenAction(
    val onQueryChange: (String) -> Unit,
    val onKeywordClick: (String) -> Unit,
    val onRemoveKeyword: (String) -> Unit,
    val onSearch: () -> Unit,
    val onBackClick: () -> Unit,
    val onItemClick: (Long) -> Unit,
)
