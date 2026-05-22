package com.obrit.feature.register.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.obrit.feature.register.viewmodel.ManualRegisterSideEffect
import com.obrit.feature.register.viewmodel.ManualRegisterViewModel
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ManualRegisterScreen(
    onBack: () -> Unit,
    onRegistered: () -> Unit,
    onDirectRegister: () -> Unit,
    modifier: Modifier = Modifier,
    pendingCategoryName: String? = null,
    onPendingCategoryConsumed: () -> Unit = {},
    viewModel: ManualRegisterViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()

    LaunchedEffect(pendingCategoryName) {
        if (!pendingCategoryName.isNullOrBlank()) {
            viewModel.onCategoryChange(pendingCategoryName)
            onPendingCategoryConsumed()
        }
    }

    ManualRegisterScreenContent(
        state = state,
        action =
            ManualRegisterScreenAction(
                onCategoryChange = viewModel::onCategoryChange,
                onNameChange = viewModel::onNameChange,
                onSpareCountChange = viewModel::onSpareCountChange,
                onLastReplaceDateChange = viewModel::onLastReplaceDateChange,
                onSubmit = viewModel::onSubmit,
                onBack = viewModel::onBack,
                onDirectRegister = viewModel::onDirectRegister,
            ),
        modifier = modifier,
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ManualRegisterSideEffect.OnRegistered -> onRegistered()
            is ManualRegisterSideEffect.OnBack -> onBack()
            is ManualRegisterSideEffect.OnNavigateToDirectRegister -> onDirectRegister()
        }
    }
}

internal data class ManualRegisterScreenAction(
    val onCategoryChange: (String) -> Unit,
    val onNameChange: (String) -> Unit,
    val onSpareCountChange: (String) -> Unit,
    val onLastReplaceDateChange: (String) -> Unit,
    val onSubmit: () -> Unit,
    val onBack: () -> Unit,
    val onDirectRegister: () -> Unit,
)
