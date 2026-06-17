package com.obrit.feature.register.screen.receipt

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.obrit.feature.register.viewmodel.ReceiptDetailSideEffect
import com.obrit.feature.register.viewmodel.ReceiptDetailViewModel
import com.obrit.feature.register.viewmodel.ReceiptDraftItem
import com.obrit.obrit.shared.model.items.ReplacementPeriod
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ReceiptDetailScreen(
    items: List<ReceiptDraftItem>,
    receiptImageUrl: String,
    navigation: ReceiptDetailNavigation,
    modifier: Modifier = Modifier,
    viewModel: ReceiptDetailViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()

    LaunchedEffect(items, receiptImageUrl) {
        viewModel.initForms(items, receiptImageUrl)
    }

    ReceiptDetailScreenContent(
        state = state,
        action =
            ReceiptDetailScreenAction(
                onNameChange = viewModel::onNameChange,
                onPeriodChange = viewModel::onPeriodChange,
                onQuantityChange = viewModel::onQuantityChange,
                onSubmit = viewModel::onSubmit,
                onBack = viewModel::onBack,
            ),
        modifier = modifier,
    )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ReceiptDetailSideEffect.OnComplete -> navigation.onComplete()
            is ReceiptDetailSideEffect.OnBack -> navigation.onBack()
        }
    }
}

data class ReceiptDetailNavigation(
    val onBack: () -> Unit,
    val onComplete: () -> Unit,
)

internal data class ReceiptDetailScreenAction(
    val onNameChange: (id: Long, value: String) -> Unit,
    val onPeriodChange: (id: Long, period: ReplacementPeriod) -> Unit,
    val onQuantityChange: (id: Long, value: Int) -> Unit,
    val onSubmit: () -> Unit,
    val onBack: () -> Unit,
)
