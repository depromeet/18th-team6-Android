package com.obrit.feature.register.screen.direct

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.obrit.feature.register.viewmodel.DirectRegisterSideEffect
import com.obrit.feature.register.viewmodel.DirectRegisterViewModel
import com.obrit.obrit.shared.model.categories.Category
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun DirectRegisterScreen(
    onBack: () -> Unit,
    onRegister: (Category) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DirectRegisterViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()

    DirectRegisterScreenContent(
        state = state,
        action =
            DirectRegisterScreenAction(
                onNameChange = viewModel::onNameChange,
                onIconSelect = viewModel::onIconSelect,
                onSubmit = viewModel::onSubmit,
                onBack = viewModel::onBack,
            ),
        modifier = modifier,
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is DirectRegisterSideEffect.OnRegistered -> onRegister(sideEffect.category)
            is DirectRegisterSideEffect.OnBack -> onBack()
        }
    }
}

internal data class DirectRegisterScreenAction(
    val onNameChange: (String) -> Unit,
    val onIconSelect: (Long) -> Unit,
    val onSubmit: () -> Unit,
    val onBack: () -> Unit,
)
