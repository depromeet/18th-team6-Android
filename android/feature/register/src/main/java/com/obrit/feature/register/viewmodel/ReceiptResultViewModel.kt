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
            // Result 컴포지션은 Detail로 갔다 오면 dispose 후 재진입하며 init이 다시 호출된다.
            // VM은 백스택에서 살아있으므로, 사용자의 삭제/추가가 덮어써지지 않게 VM 수명당 1회만 시드한다.
            if (state.isInitialized) return@intent
            reduce {
                state.copy(
                    isInitialized = true,
                    purchaseDate = analysis.purchasedDate.toDisplayDate(),
                    receiptImageUrl = analysis.receiptImageUrl,
                    nextId = analysis.items.size + 1L,
                    items =
                        analysis.items.mapIndexed { index, item ->
                            ReceiptResultItem(
                                id = index + 1L,
                                name = item.suggestedCategoryName,
                                suggestedName = item.suggestedName,
                                iconUrl = item.iconUrl,
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
            reduce {
                state.copy(
                    items = state.items + category.toReceiptResultItem(state.nextId),
                    nextId = state.nextId + 1,
                )
            }
        }

    fun applyPendingCategory(category: Category) =
        intent {
            reduce {
                state.copy(
                    categories = state.categories + category,
                    items = state.items + category.toReceiptResultItem(state.nextId),
                    nextId = state.nextId + 1,
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
    // 추가 항목에 부여할 다음 id. 삭제 후 재추가 시 id 재사용을 막기 위해 단조 증가만 한다.
    val nextId: Long = 1L,
    // 분석 결과 시드 완료 여부. 컴포지션 재진입 시 init 재실행으로 편집이 덮어써지는 것을 막는다.
    val isInitialized: Boolean = false,
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
        id = id,
        name = suggestedName,
        quantity = quantity,
        // 교체일자는 Detail(2페이지) 전용. Result→draft 시점엔 항상 null이며 병합 단계에서 보존값이 채워진다.
        lastReplacementPeriod = null,
        categoryId = categoryId,
        newCategoryName = newCategoryName,
        newCategoryDefaultReplacementIntervalDays = newCategoryDefaultReplacementIntervalDays,
    )

// 분석 응답의 ISO 날짜(yyyy-MM-dd)를 UI 표기(yyyy. MM. dd)로 변환한다. null이면 빈 문자열.
private fun String?.toDisplayDate(): String = this?.replace("-", ". ").orEmpty()

private const val RECEIPT_RESULT_DEFAULT_QUANTITY = 1
