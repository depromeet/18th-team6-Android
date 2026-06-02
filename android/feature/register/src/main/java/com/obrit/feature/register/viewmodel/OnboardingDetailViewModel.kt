package com.obrit.feature.register.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.obrit.shared.data.repository.CategoryRepository
import com.obrit.obrit.shared.data.repository.ItemRepository
import com.obrit.obrit.shared.model.categories.Category
import com.obrit.obrit.shared.model.items.CreateItemParams
import com.obrit.obrit.shared.model.items.ReplacementPeriod
import org.orbitmvi.orbit.viewmodel.container

class OnboardingDetailViewModel(
    private val categoryRepository: CategoryRepository,
    private val itemRepository: ItemRepository,
) : BaseContainerHost<OnboardingDetailUiState, OnboardingDetailSideEffect>() {
    override val container =
        container<OnboardingDetailUiState, OnboardingDetailSideEffect>(
            OnboardingDetailUiState(),
        )

    fun loadCategories(selectedCategoryIds: List<Long>) =
        intent {
            reduce { state.copy(rows = emptyList(), isSubmitting = false) }
            categoryRepository
                .getCategories()
                .onSuccess { categories ->
                    val rows =
                        selectedCategoryIds
                            .mapNotNull { id ->
                                categories.find { it.id == id }
                            }.map { category ->
                                OnboardingDetailRow(category = category, period = null)
                            }
                    reduce { state.copy(rows = rows) }
                }
        }

    fun onPeriodChange(
        categoryId: Long,
        period: ReplacementPeriod,
    ) = intent {
        reduce {
            state.copy(
                rows =
                    state.rows.map { row ->
                        if (row.category.id == categoryId) row.copy(period = period) else row
                    },
            )
        }
    }

    fun onSubmit() =
        intent {
            if (!state.isSubmitEnabled) return@intent
            reduce { state.copy(isSubmitting = true) }
            val params =
                state.rows.map { row ->
                    CreateItemParams(
                        categoryId = row.category.id,
                        name = row.category.name,
                        spareQuantity = 0,
                        lastReplacementPeriod = row.period,
                    )
                }
            itemRepository
                .createItems(params)
                .onSuccess { postSideEffect(OnboardingDetailSideEffect.OnComplete) }
                .onFailure { reduce { state.copy(isSubmitting = false) } }
        }

    fun onBack() =
        intent {
            postSideEffect(OnboardingDetailSideEffect.OnBack)
        }
}

@Immutable
data class OnboardingDetailUiState(
    val rows: List<OnboardingDetailRow> = emptyList(),
    val isSubmitting: Boolean = false,
) {
    val isSubmitEnabled: Boolean
        get() = rows.isNotEmpty() && rows.all { it.period != null } && !isSubmitting
}

data class OnboardingDetailRow(
    val category: Category,
    val period: ReplacementPeriod?,
)

sealed interface OnboardingDetailSideEffect {
    data object OnBack : OnboardingDetailSideEffect

    data object OnComplete : OnboardingDetailSideEffect
}
