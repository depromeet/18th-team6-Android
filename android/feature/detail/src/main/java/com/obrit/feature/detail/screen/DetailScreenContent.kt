package com.obrit.feature.detail.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.obrit.feature.detail.viewmodel.DetailUiState

@Composable
internal fun DetailScreenContent(
    state: DetailUiState,
    action: DetailScreenAction,
    isMoreMenuExpanded: Boolean,
    isSpareSheetVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is DetailUiState.Success -> {
            DetailScreenSuccessContent(
                state = state,
                action = action,
                isMoreMenuExpanded = isMoreMenuExpanded,
                isSpareSheetVisible = isSpareSheetVisible,
                modifier = modifier,
            )
        }
        is DetailUiState.Loading -> {
            DetailScreenLoadingContent(modifier = modifier)
        }
        is DetailUiState.LoadFailed -> {
            DetailScreenFailureContent(
                action = action,
                modifier = modifier,
            )
        }
    }
}
