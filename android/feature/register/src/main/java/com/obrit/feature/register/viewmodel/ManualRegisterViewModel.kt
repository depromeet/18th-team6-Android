package com.obrit.feature.register.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.obrit.shared.data.repository.CategoryRepository
import com.obrit.obrit.shared.data.repository.ItemRepository
import com.obrit.obrit.shared.model.categories.Category
import com.obrit.obrit.shared.model.items.CreateItemParams
import com.obrit.obrit.shared.model.items.ReplacementPeriod
import org.orbitmvi.orbit.viewmodel.container

class ManualRegisterViewModel(
    private val categoryRepository: CategoryRepository,
    private val itemRepository: ItemRepository,
) : BaseContainerHost<ManualRegisterUiState, ManualRegisterSideEffect>() {
    override val container =
        container<ManualRegisterUiState, ManualRegisterSideEffect>(
            ManualRegisterUiState(),
        ) {
            loadCategories()
        }

    private fun loadCategories() =
        intent {
            categoryRepository.getCategories()
                .onSuccess { categories ->
                    reduce { state.copy(categories = categories) }
                }
        }

    fun onCategorySelect(category: Category) =
        intent {
            reduce {
                state.copy(
                    selectedCategoryId = category.id,
                    categoryName = category.name,
                    existingCount = category.totalSpareQuantity,
                )
            }
        }

    fun onNameChange(value: String) =
        intent {
            reduce { state.copy(name = value) }
        }

    fun onQuantityChange(value: Int) =
        intent {
            reduce { state.copy(quantity = value) }
        }

    fun onLastReplacementPeriodChange(value: ReplacementPeriod?) =
        intent {
            reduce { state.copy(lastReplacementPeriod = value) }
        }

    fun applyPendingCategory(category: Category) =
        intent {
            reduce {
                state.copy(
                    categories = state.categories + category,
                    selectedCategoryId = category.id,
                    categoryName = category.name,
                    existingCount = category.totalSpareQuantity,
                )
            }
        }

    fun onSubmit() =
        intent {
            val current = state
            val categoryId = current.selectedCategoryId ?: return@intent
            if (current.isSubmitting) return@intent
            reduce { state.copy(isSubmitting = true) }
            val params =
                CreateItemParams(
                    categoryId = categoryId,
                    name = current.name,
                    spareQuantity = current.quantity,
                    lastReplacementPeriod = current.lastReplacementPeriod,
                )
            itemRepository.createItem(params)
                .onSuccess {
                    reduce { ManualRegisterUiState(categories = state.categories) }
                    postSideEffect(ManualRegisterSideEffect.OnRegistered)
                    loadCategories()
                }
                .onFailure {
                    reduce { state.copy(isSubmitting = false) }
                }
        }

    fun onBack() =
        intent {
            postSideEffect(ManualRegisterSideEffect.OnBack)
        }

    fun onDirectRegister() =
        intent {
            postSideEffect(ManualRegisterSideEffect.OnNavigateToDirectRegister)
        }
}

@Immutable
data class ManualRegisterUiState(
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: Long? = null,
    val categoryName: String = "",
    val name: String = "",
    val quantity: Int = 0,
    val existingCount: Int = 0,
    val lastReplacementPeriod: ReplacementPeriod? = null,
    val isSubmitting: Boolean = false,
) {
    val totalCount: Int
        get() = existingCount + quantity

    val isSubmitEnabled: Boolean
        get() =
            selectedCategoryId != null &&
                name.isNotBlank() &&
                lastReplacementPeriod != null &&
                quantity > 0 &&
                !isSubmitting
}

sealed interface ManualRegisterSideEffect {
    data object OnRegistered : ManualRegisterSideEffect

    data object OnBack : ManualRegisterSideEffect

    data object OnNavigateToDirectRegister : ManualRegisterSideEffect
}
