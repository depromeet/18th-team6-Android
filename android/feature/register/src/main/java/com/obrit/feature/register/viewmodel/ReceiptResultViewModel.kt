package com.obrit.feature.register.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.obrit.shared.data.repository.CategoryRepository
import com.obrit.obrit.shared.model.categories.Category
import org.orbitmvi.orbit.viewmodel.container

class ReceiptResultViewModel(
    private val categoryRepository: CategoryRepository,
) : BaseContainerHost<ReceiptResultUiState, ReceiptResultSideEffect>() {
    override val container =
        container<ReceiptResultUiState, ReceiptResultSideEffect>(
            ReceiptResultUiState(),
        ) {
            loadCategories()
        }

    private fun loadCategories() =
        intent {
            categoryRepository
                .getCategories()
                .onSuccess { categories ->
                    reduce { state.copy(categories = categories) }
                }
        }

    fun onDeleteItem(id: Long) =
        intent {
            reduce { state.copy(items = state.items.filterNot { it.id == id }) }
        }

    fun onCategoryConfirm(category: Category) =
        intent {
            reduce { state.copy(items = state.items + category.toReceiptResultItem(nextItemId(state.items))) }
        }

    fun applyPendingCategory(category: Category) =
        intent {
            reduce {
                state.copy(
                    categories = state.categories + category,
                    items = state.items + category.toReceiptResultItem(nextItemId(state.items)),
                )
            }
        }

    fun onNext() =
        intent {
            postSideEffect(ReceiptResultSideEffect.OnNext(state.items.map { it.name }))
        }

    fun onBack() =
        intent {
            postSideEffect(ReceiptResultSideEffect.OnBack)
        }

    fun onDirectRegister() =
        intent {
            postSideEffect(ReceiptResultSideEffect.OnNavigateToDirectRegister)
        }
}

@Immutable
data class ReceiptResultUiState(
    // 분석 API 연동 시 mock 구매일/아이템을 실제 분석 결과로 교체.
    val purchaseDate: String = RECEIPT_RESULT_MOCK_PURCHASE_DATE,
    val items: List<ReceiptResultItem> = mockReceiptResultItems(),
    val categories: List<Category> = emptyList(),
) {
    val isNextEnabled: Boolean
        get() = items.isNotEmpty()
}

@Immutable
data class ReceiptResultItem(
    val id: Long,
    val name: String,
    val iconUrl: String,
    val recognizedCount: Int,
)

sealed interface ReceiptResultSideEffect {
    data class OnNext(
        val itemNames: List<String>,
    ) : ReceiptResultSideEffect

    data object OnBack : ReceiptResultSideEffect

    data object OnNavigateToDirectRegister : ReceiptResultSideEffect
}

private fun nextItemId(items: List<ReceiptResultItem>): Long = (items.maxOfOrNull { it.id } ?: 0L) + 1

private fun Category.toReceiptResultItem(id: Long): ReceiptResultItem =
    ReceiptResultItem(id = id, name = name, iconUrl = iconUrl, recognizedCount = 0)

private const val RECEIPT_RESULT_MOCK_PURCHASE_DATE = "2026. 01. 01"

private fun mockReceiptResultItems(): List<ReceiptResultItem> =
    listOf("면도기", "정수기 필터", "칫솔", "세탁 세제").mapIndexed { index, name ->
        ReceiptResultItem(id = index + 1L, name = name, iconUrl = "", recognizedCount = 0)
    }
