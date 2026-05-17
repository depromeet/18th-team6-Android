package com.obrit.feature.register.screen

import androidx.compose.runtime.Composable
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
    modifier: Modifier = Modifier,
    viewModel: ManualRegisterViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()

    ManualRegisterScreenContent(
        state = state,
        action =
            ManualRegisterScreenAction(
                onNameChange = viewModel::onNameChange,
                onSpareCountChange = viewModel::onSpareCountChange,
                onSubmit = viewModel::onSubmit,
                onBack = viewModel::onBack,
            ),
        modifier = modifier,
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ManualRegisterSideEffect.OnRegistered -> onRegistered()
            is ManualRegisterSideEffect.OnBack -> onBack()
        }
    }
}

internal data class ManualRegisterScreenAction(
    val onNameChange: (String) -> Unit,
    val onSpareCountChange: (String) -> Unit,
    val onSubmit: () -> Unit,
    val onBack: () -> Unit,
)
