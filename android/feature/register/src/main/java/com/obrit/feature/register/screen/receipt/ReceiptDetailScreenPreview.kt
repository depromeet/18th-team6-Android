package com.obrit.feature.register.screen.receipt

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.register.viewmodel.ReceiptDetailForm
import com.obrit.feature.register.viewmodel.ReceiptDetailUiState

@Preview(name = "ReceiptDetailScreen", showBackground = false)
@Composable
private fun ReceiptDetailScreenPreview() {
    OBRitTheme(dynamicColor = false) {
        ReceiptDetailScreenContent(
            state =
                ReceiptDetailUiState(
                    forms =
                        listOf(
                            ReceiptDetailForm(id = 1L, name = "면도기"),
                            ReceiptDetailForm(id = 2L, name = "정수기 필터"),
                            ReceiptDetailForm(id = 3L, name = "칫솔"),
                        ),
                ),
            action =
                ReceiptDetailScreenAction(
                    onNameChange = { _, _ -> },
                    onPeriodChange = { _, _ -> },
                    onQuantityChange = { _, _ -> },
                    onSubmit = {},
                    onBack = {},
                ),
        )
    }
}

@Preview(name = "ReceiptDetailScreen Single", showBackground = false)
@Composable
private fun ReceiptDetailScreenSinglePreview() {
    OBRitTheme(dynamicColor = false) {
        ReceiptDetailScreenContent(
            state = ReceiptDetailUiState(forms = listOf(ReceiptDetailForm(id = 1L, name = "면도기"))),
            action =
                ReceiptDetailScreenAction(
                    onNameChange = { _, _ -> },
                    onPeriodChange = { _, _ -> },
                    onQuantityChange = { _, _ -> },
                    onSubmit = {},
                    onBack = {},
                ),
        )
    }
}
