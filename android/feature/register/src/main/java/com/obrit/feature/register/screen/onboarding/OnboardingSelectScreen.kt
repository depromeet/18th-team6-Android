package com.obrit.feature.register.screen.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.obrit.feature.register.viewmodel.OnboardingSelectSideEffect
import com.obrit.feature.register.viewmodel.OnboardingSelectViewModel
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

internal data class OnboardingSelectScreenAction(
    val onToggle: (Long) -> Unit,
    val onNewItemAdd: () -> Unit,
    val onSectionSelect: (Long) -> Unit,
    val onQueryChange: (String) -> Unit,
    val onSearch: (String) -> Unit,
    val onSubmit: () -> Unit,
    val onBack: () -> Unit,
)

@Composable
fun OnboardingSelectScreen(
    onBack: () -> Unit,
    onRegister: (registeredCount: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingSelectViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()

    OnboardingSelectScreenContent(
        state = state,
        action =
            OnboardingSelectScreenAction(
                onToggle = viewModel::onToggle,
                onNewItemAdd = viewModel::onNewItemAdd,
                onSectionSelect = viewModel::onSectionSelect,
                onQueryChange = viewModel::onQueryChange,
                onSearch = viewModel::onSearch,
                onSubmit = viewModel::onSubmit,
                onBack = viewModel::onBack,
            ),
        modifier = modifier,
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is OnboardingSelectSideEffect.OnBack -> onBack()
            is OnboardingSelectSideEffect.OnRegistered -> onRegister(sideEffect.registeredCount)
        }
    }
}
