package com.obrit.feature.register.screen.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.obrit.feature.register.viewmodel.OnboardingDetailSideEffect
import com.obrit.feature.register.viewmodel.OnboardingDetailViewModel
import com.obrit.obrit.shared.model.items.ReplacementPeriod
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

internal data class OnboardingDetailScreenAction(
    val onPeriodChange: (categoryId: Long, period: ReplacementPeriod) -> Unit,
    val onSubmit: () -> Unit,
    val onBack: () -> Unit,
)

@Composable
fun OnboardingDetailScreen(
    selectedCategoryIds: List<Long>,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingDetailViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()

    LaunchedEffect(selectedCategoryIds) {
        viewModel.loadCategories(selectedCategoryIds)
    }

    OnboardingDetailScreenContent(
        state = state,
        action =
            OnboardingDetailScreenAction(
                onPeriodChange = viewModel::onPeriodChange,
                onSubmit = viewModel::onSubmit,
                onBack = viewModel::onBack,
            ),
        modifier = modifier,
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is OnboardingDetailSideEffect.OnBack -> onBack()
            is OnboardingDetailSideEffect.OnComplete -> onComplete()
        }
    }
}
