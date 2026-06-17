package com.obrit.feature.register.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.obrit.shared.data.repository.ItemRepository
import com.obrit.obrit.shared.model.items.CreateItemParams
import com.obrit.obrit.shared.model.items.ReplacementPeriod
import org.orbitmvi.orbit.viewmodel.container

class ReceiptDetailViewModel(
    private val itemRepository: ItemRepository,
) : BaseContainerHost<ReceiptDetailUiState, ReceiptDetailSideEffect>() {
    override val container =
        container<ReceiptDetailUiState, ReceiptDetailSideEffect>(ReceiptDetailUiState())

    fun initForms(
        items: List<ReceiptDraftItem>,
        receiptImageUrl: String,
    ) = intent {
        reduce {
            state.copy(
                receiptImageUrl = receiptImageUrl,
                forms =
                    items.mapIndexed { index, item ->
                        ReceiptDetailForm(
                            id = index + 1L,
                            name = item.name,
                            quantity = item.quantity,
                            categoryId = item.categoryId,
                            newCategoryName = item.newCategoryName,
                            newCategoryDefaultReplacementIntervalDays = item.newCategoryDefaultReplacementIntervalDays,
                        )
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
            reduce { state.copy(isSubmitting = true) }
            val params =
                state.forms.map { form ->
                    CreateItemParams(
                        categoryId = form.categoryId,
                        name = form.name,
                        spareQuantity = form.quantity,
                        lastReplacementPeriod = form.lastReplacementPeriod,
                        newCategoryName = form.newCategoryName,
                        newCategoryDefaultReplacementIntervalDays = form.newCategoryDefaultReplacementIntervalDays,
                    )
                }
            itemRepository
                .createItems(params, receiptImageUrl = state.receiptImageUrl.ifBlank { null })
                .onSuccess { postSideEffect(ReceiptDetailSideEffect.OnComplete) }
                .onFailure { reduce { state.copy(isSubmitting = false) } }
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
    val receiptImageUrl: String = "",
    val isSubmitting: Boolean = false,
) {
    val isSubmitEnabled: Boolean
        get() = forms.isNotEmpty() && forms.all { it.isComplete } && !isSubmitting
}

@Immutable
data class ReceiptDetailForm(
    val id: Long,
    val name: String,
    val lastReplacementPeriod: ReplacementPeriod? = null,
    val quantity: Int = RECEIPT_DETAIL_DEFAULT_QUANTITY,
    val categoryId: Long? = null,
    val newCategoryName: String? = null,
    val newCategoryDefaultReplacementIntervalDays: Int? = null,
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
