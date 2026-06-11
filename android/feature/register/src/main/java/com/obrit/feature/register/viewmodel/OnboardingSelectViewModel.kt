package com.obrit.feature.register.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.obrit.shared.data.repository.CategoryRepository
import com.obrit.obrit.shared.model.categories.Category
import org.orbitmvi.orbit.viewmodel.container

class OnboardingSelectViewModel(
    private val categoryRepository: CategoryRepository,
) : BaseContainerHost<OnboardingSelectUiState, OnboardingSelectSideEffect>() {
    override val container =
        container<OnboardingSelectUiState, OnboardingSelectSideEffect>(
            OnboardingSelectUiState(),
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

    fun onToggle(categoryId: Long) =
        intent {
            reduce {
                val selected =
                    if (categoryId in state.selectedIds) {
                        state.selectedIds - categoryId
                    } else {
                        state.selectedIds + categoryId
                    }
                state.copy(selectedIds = selected)
            }
        }

    fun onNext() =
        intent {
            if (state.selectedIds.isEmpty()) return@intent
            postSideEffect(OnboardingSelectSideEffect.OnNext(state.selectedIds.toList()))
        }

    fun onBack() =
        intent {
            postSideEffect(OnboardingSelectSideEffect.OnBack)
        }
}

@Immutable
data class OnboardingSelectUiState(
    val categories: List<Category> = emptyList(),
    val selectedIds: Set<Long> = emptySet(),
) {
    val selectedCount: Int
        get() = selectedIds.size

    val isNextEnabled: Boolean
        get() = selectedIds.isNotEmpty()
}

sealed interface OnboardingSelectSideEffect {
    data object OnBack : OnboardingSelectSideEffect

    data class OnNext(
        val selectedIds: List<Long>,
    ) : OnboardingSelectSideEffect
}
