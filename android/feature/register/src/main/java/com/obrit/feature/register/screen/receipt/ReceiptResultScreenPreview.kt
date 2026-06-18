package com.obrit.feature.register.screen.receipt

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.register.viewmodel.ReceiptResultUiState

@Preview(name = "ReceiptResultScreen", showBackground = false)
@Composable
private fun ReceiptResultScreenPreview() {
    OBRitTheme(dynamicColor = false) {
        ReceiptResultScreenContent(
            state = ReceiptResultUiState(),
            action =
                ReceiptResultScreenAction(
                    onDeleteItem = {},
                    onCategoryConfirm = {},
                    onNext = {},
                    onBack = {},
                    onDirectRegister = {},
                ),
        )
    }
}

@Preview(name = "ReceiptResultScreen Empty", showBackground = false)
@Composable
private fun ReceiptResultScreenEmptyPreview() {
    OBRitTheme(dynamicColor = false) {
        ReceiptResultScreenContent(
            state = ReceiptResultUiState(items = emptyList()),
            action =
                ReceiptResultScreenAction(
                    onDeleteItem = {},
                    onCategoryConfirm = {},
                    onNext = {},
                    onBack = {},
                    onDirectRegister = {},
                ),
        )
    }
}
