package com.obrit.feature.register.screen.manual

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.obrit.feature.register.viewmodel.ManualRegisterSideEffect
import com.obrit.feature.register.viewmodel.ManualRegisterViewModel
import com.obrit.obrit.shared.model.categories.Category
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ManualRegisterScreen(
    navigation: ManualRegisterNavigation,
    modifier: Modifier = Modifier,
    viewModel: ManualRegisterViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()

    ManualRegisterScreenContent(
        state = state,
        action =
            ManualRegisterScreenAction(
                onCategoryConfirm = viewModel::onCategorySelect,
                onDirectRegistered = viewModel::onDirectRegistered,
                onNameChange = viewModel::onNameChange,
                onQuantityChange = viewModel::onQuantityChange,
                onSubmit = viewModel::onSubmit,
                onBack = viewModel::onBack,
            ),
        modifier = modifier,
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ManualRegisterSideEffect.OnRegistered -> navigation.onRegistered(sideEffect.itemName)
            is ManualRegisterSideEffect.OnBack -> navigation.onBack()
        }
    }
}

data class ManualRegisterNavigation(
    val onBack: () -> Unit,
    val onRegistered: (itemName: String) -> Unit,
)

internal data class ManualRegisterScreenAction(
    val onCategoryConfirm: (Category) -> Unit,
    val onDirectRegistered: (Category) -> Unit,
    val onNameChange: (String) -> Unit,
    val onQuantityChange: (Int) -> Unit,
    val onSubmit: () -> Unit,
    val onBack: () -> Unit,
)
