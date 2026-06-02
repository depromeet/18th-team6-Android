package com.obrit.feature.register.screen.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.obrit.feature.register.viewmodel.OnboardingSelectSideEffect
import com.obrit.feature.register.viewmodel.OnboardingSelectViewModel
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun OnboardingSelectScreen(
    onBack: () -> Unit,
    onNext: (selectedIds: List<Long>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingSelectViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()

    OnboardingSelectScreenContent(
        state = state,
        action =
            OnboardingSelectScreenAction(
                onToggle = viewModel::onToggle,
                onNext = viewModel::onNext,
                onBack = viewModel::onBack,
            ),
        modifier = modifier,
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is OnboardingSelectSideEffect.OnBack -> onBack()
            is OnboardingSelectSideEffect.OnNext -> onNext(sideEffect.selectedIds)
        }
    }
}
