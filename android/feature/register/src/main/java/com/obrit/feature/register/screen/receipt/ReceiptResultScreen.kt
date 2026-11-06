package com.obrit.feature.register.screen.receipt

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.obrit.feature.register.viewmodel.ReceiptDraftItem
import com.obrit.feature.register.viewmodel.ReceiptResultSideEffect
import com.obrit.feature.register.viewmodel.ReceiptResultViewModel
import com.obrit.obrit.shared.model.categories.Category
import com.obrit.obrit.shared.model.receipts.ReceiptAnalysis
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ReceiptResultScreen(
    analysis: ReceiptAnalysis,
    navigation: ReceiptResultNavigation,
    modifier: Modifier = Modifier,
    viewModel: ReceiptResultViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()

    LaunchedEffect(analysis) {
        viewModel.init(analysis)
    }

    ReceiptResultScreenContent(
        state = state,
        action =
            ReceiptResultScreenAction(
                onDeleteItem = viewModel::onDeleteItem,
                onCategoryConfirm = viewModel::onCategoryConfirm,
                onDirectRegistered = viewModel::onDirectRegistered,
                onNext = viewModel::onNext,
                onBack = viewModel::onBack,
            ),
        modifier = modifier,
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ReceiptResultSideEffect.OnNext -> navigation.onNext(sideEffect.receiptImageUrl, sideEffect.items)
            is ReceiptResultSideEffect.OnBack -> navigation.onBack()
        }
    }
}

data class ReceiptResultNavigation(
    val onBack: () -> Unit,
    val onNext: (receiptImageUrl: String, items: List<ReceiptDraftItem>) -> Unit,
)

internal data class ReceiptResultScreenAction(
    val onDeleteItem: (Long) -> Unit,
    val onCategoryConfirm: (Category) -> Unit,
    val onDirectRegistered: (Category) -> Unit,
    val onNext: () -> Unit,
    val onBack: () -> Unit,
)
