package com.obrit.feature.register.screen.manual

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.obrit.feature.register.viewmodel.ManualRegisterSideEffect
import com.obrit.feature.register.viewmodel.ManualRegisterViewModel
import com.obrit.obrit.shared.model.categories.Category
import com.obrit.obrit.shared.model.items.ReplacementPeriod
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

    LaunchedEffect(pendingCategory?.category?.id) {
        val handle = pendingCategory ?: return@LaunchedEffect
        val category = handle.category ?: return@LaunchedEffect
        viewModel.applyPendingCategory(category)
        handle.onConsumed()
    }

    ManualRegisterScreenContent(
        state = state,
        action =
            ManualRegisterScreenAction(
                onCategoryConfirm = viewModel::onCategorySelect,
                onNameChange = viewModel::onNameChange,
                onQuantityChange = viewModel::onQuantityChange,
                onLastReplacementPeriodChange = viewModel::onLastReplacementPeriodChange,
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
    val category: Category?,
    val onConsumed: () -> Unit,
)

internal data class ManualRegisterScreenAction(
    val onCategoryConfirm: (Category) -> Unit,
    val onNameChange: (String) -> Unit,
    val onQuantityChange: (Int) -> Unit,
    val onLastReplacementPeriodChange: (ReplacementPeriod?) -> Unit,
    val onSubmit: () -> Unit,
    val onBack: () -> Unit,
    val onDirectRegister: () -> Unit,
)
