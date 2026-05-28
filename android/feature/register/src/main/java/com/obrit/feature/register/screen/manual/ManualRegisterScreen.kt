package com.obrit.feature.register.screen.manual

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
    navigation: ManualRegisterNavigation,
    modifier: Modifier = Modifier,
    pendingCategory: PendingCategory? = null,
    viewModel: ManualRegisterViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()

    LaunchedEffect(pendingCategory?.name) {
        val handle = pendingCategory ?: return@LaunchedEffect
        val name = handle.name
        if (!name.isNullOrBlank()) {
            viewModel.onCategoryChange(name)
            handle.onConsumed()
        }
    }

    ManualRegisterScreenContent(
        state = state,
        action =
            ManualRegisterScreenAction(
                onCategoryChange = viewModel::onCategoryChange,
                onNameChange = viewModel::onNameChange,
                onQuantityChange = viewModel::onQuantityChange,
                onLastReplaceDateChange = viewModel::onLastReplaceDateChange,
                onSubmit = viewModel::onSubmit,
                onBack = viewModel::onBack,
                onDirectRegister = viewModel::onDirectRegister,
            ),
        modifier = modifier,
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ManualRegisterSideEffect.OnRegistered -> navigation.onRegistered()
            is ManualRegisterSideEffect.OnBack -> navigation.onBack()
            is ManualRegisterSideEffect.OnNavigateToDirectRegister -> navigation.onDirectRegister()
        }
    }
}

data class ManualRegisterNavigation(
    val onBack: () -> Unit,
    val onRegistered: () -> Unit,
    val onDirectRegister: () -> Unit,
)

data class PendingCategory(
    val name: String?,
    val onConsumed: () -> Unit,
)

internal data class ManualRegisterScreenAction(
    val onCategoryChange: (String) -> Unit,
    val onNameChange: (String) -> Unit,
    val onQuantityChange: (Int) -> Unit,
    val onLastReplaceDateChange: (String) -> Unit,
    val onSubmit: () -> Unit,
    val onBack: () -> Unit,
    val onDirectRegister: () -> Unit,
)
