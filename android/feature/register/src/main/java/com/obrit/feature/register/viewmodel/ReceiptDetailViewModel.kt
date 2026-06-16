package com.obrit.feature.register.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.obrit.shared.model.items.ReplacementPeriod
import org.orbitmvi.orbit.viewmodel.container

class ReceiptDetailViewModel : BaseContainerHost<ReceiptDetailUiState, ReceiptDetailSideEffect>() {
    override val container =
        container<ReceiptDetailUiState, ReceiptDetailSideEffect>(ReceiptDetailUiState())

    fun initForms(names: List<String>) =
        intent {
            reduce {
                state.copy(
                    forms =
                        names.mapIndexed { index, name ->
                            ReceiptDetailForm(id = index + 1L, name = name)
                        },
                )
            }
        }

    fun onNameChange(
        id: Long,
        value: String,
    ) = updateForm(id) { it.copy(name = value) }

    fun onPeriodChange(
        id: Long,
        period: ReplacementPeriod,
    ) = updateForm(id) { it.copy(lastReplacementPeriod = period) }

    fun onQuantityChange(
        id: Long,
        value: Int,
    ) = updateForm(id) { it.copy(quantity = value) }

    fun onSubmit() =
        intent {
            if (!state.isSubmitEnabled) return@intent
            // 분석 API 연동 시 itemRepository.createItems()로 bulk 등록 교체 (categoryId 필요).
            postSideEffect(ReceiptDetailSideEffect.OnComplete)
        }

    fun onBack() =
        intent {
            postSideEffect(ReceiptDetailSideEffect.OnBack)
        }

    private fun updateForm(
        id: Long,
        transform: (ReceiptDetailForm) -> ReceiptDetailForm,
    ) = intent {
        reduce {
            state.copy(
                forms = state.forms.map { form -> if (form.id == id) transform(form) else form },
            )
        }
    }
}

@Immutable
data class ReceiptDetailUiState(
    val forms: List<ReceiptDetailForm> = emptyList(),
) {
    val isSubmitEnabled: Boolean
        get() = forms.isNotEmpty() && forms.all { it.isComplete }
}

@Immutable
data class ReceiptDetailForm(
    val id: Long,
    val name: String,
    val lastReplacementPeriod: ReplacementPeriod? = null,
    val quantity: Int = RECEIPT_DETAIL_DEFAULT_QUANTITY,
) {
    val isComplete: Boolean
        get() =
            name.isNotBlank() &&
                name.length <= RECEIPT_DETAIL_NAME_MAX_LENGTH &&
                lastReplacementPeriod != null &&
                quantity >= RECEIPT_DETAIL_DEFAULT_QUANTITY
}

sealed interface ReceiptDetailSideEffect {
    data object OnComplete : ReceiptDetailSideEffect

    data object OnBack : ReceiptDetailSideEffect
}

internal const val RECEIPT_DETAIL_NAME_MAX_LENGTH = 15
private const val RECEIPT_DETAIL_DEFAULT_QUANTITY = 1
