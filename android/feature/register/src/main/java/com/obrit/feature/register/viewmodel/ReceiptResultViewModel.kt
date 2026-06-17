package com.obrit.feature.register.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.obrit.shared.data.repository.CategoryRepository
import com.obrit.obrit.shared.model.categories.Category
import com.obrit.obrit.shared.model.receipts.ReceiptAnalysis
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

    fun init(analysis: ReceiptAnalysis) =
        intent {
            reduce {
                state.copy(
                    purchaseDate = analysis.purchasedDate.toDisplayDate(),
                    receiptImageUrl = analysis.receiptImageUrl,
                    items =
                        analysis.items.mapIndexed { index, item ->
                            ReceiptResultItem(
                                id = index + 1L,
                                name = item.suggestedCategoryName,
                                suggestedName = item.suggestedName,
                                iconUrl = "",
                                recognizedCount = item.quantity,
                                quantity = item.quantity,
                                categoryId = item.categoryId,
                                newCategoryName = if (item.categoryId == null) item.suggestedCategoryName else null,
                                newCategoryDefaultReplacementIntervalDays =
                                    if (item.categoryId == null) item.suggestedReplacementIntervalDays else null,
                            )
                        },
                )
            }
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
            postSideEffect(
                ReceiptResultSideEffect.OnNext(
                    receiptImageUrl = state.receiptImageUrl,
                    items = state.items.map { it.toDraftItem() },
                ),
            )
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
    val purchaseDate: String = "",
    val receiptImageUrl: String = "",
    val items: List<ReceiptResultItem> = emptyList(),
    val categories: List<Category> = emptyList(),
) {
    val isNextEnabled: Boolean
        get() = items.isNotEmpty()
}

@Immutable
data class ReceiptResultItem(
    val id: Long,
    // 1페이지 카드에 표시하는 이름(분석 항목은 suggestedCategoryName).
    val name: String,
    // Detail(2페이지) 이름 프리필용(분석 항목은 suggestedName).
    val suggestedName: String,
    val iconUrl: String,
    val recognizedCount: Int,
    val quantity: Int = RECEIPT_RESULT_DEFAULT_QUANTITY,
    val categoryId: Long? = null,
    val newCategoryName: String? = null,
    val newCategoryDefaultReplacementIntervalDays: Int? = null,
)

sealed interface ReceiptResultSideEffect {
    data class OnNext(
        val receiptImageUrl: String,
        val items: List<ReceiptDraftItem>,
    ) : ReceiptResultSideEffect

    data object OnBack : ReceiptResultSideEffect

    data object OnNavigateToDirectRegister : ReceiptResultSideEffect
}

private fun nextItemId(items: List<ReceiptResultItem>): Long = (items.maxOfOrNull { it.id } ?: 0L) + 1

private fun Category.toReceiptResultItem(id: Long): ReceiptResultItem =
    ReceiptResultItem(
        id = id,
        name = name,
        suggestedName = name,
        iconUrl = iconUrl,
        recognizedCount = 0,
        quantity = RECEIPT_RESULT_DEFAULT_QUANTITY,
        categoryId = this.id,
    )

private fun ReceiptResultItem.toDraftItem(): ReceiptDraftItem =
    ReceiptDraftItem(
        name = suggestedName,
        quantity = quantity,
        categoryId = categoryId,
        newCategoryName = newCategoryName,
        newCategoryDefaultReplacementIntervalDays = newCategoryDefaultReplacementIntervalDays,
    )

// 분석 응답의 ISO 날짜(yyyy-MM-dd)를 UI 표기(yyyy. MM. dd)로 변환한다. null이면 빈 문자열.
private fun String?.toDisplayDate(): String = this?.replace("-", ". ").orEmpty()

private const val RECEIPT_RESULT_DEFAULT_QUANTITY = 1
