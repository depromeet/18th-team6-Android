package com.obrit.feature.register.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.obrit.shared.data.repository.CategoryRepository
import com.obrit.obrit.shared.data.repository.ItemRepository
import com.obrit.obrit.shared.model.categories.Category
import com.obrit.obrit.shared.model.categories.error.CreateCategoryError
import org.orbitmvi.orbit.viewmodel.container

class DirectRegisterViewModel(
    private val categoryRepository: CategoryRepository,
    private val itemRepository: ItemRepository,
) : BaseContainerHost<DirectRegisterUiState, DirectRegisterSideEffect>() {
    override val container =
        container<DirectRegisterUiState, DirectRegisterSideEffect>(
            DirectRegisterUiState(),
        ) {
            loadExistingNames()
        }

    private fun loadExistingNames() =
        intent {
            itemRepository
                .getItems()
                .onSuccess { items ->
                    reduce { state.copy(existingNames = items.map { it.name.trim() }.toSet()) }
                }
        }

    // ViewModel이 화면 수명을 따르므로 시트를 다시 열 때 이전 입력을 지운다.
    fun onOpen(initialName: String) =
        intent {
            reduce {
                state.copy(
                    name = initialName,
                    isSubmitting = false,
                    isDuplicateName = false,
                )
            }
        }

    fun onNameChange(value: String) =
        intent {
            reduce { state.copy(name = value, isDuplicateName = false) }
        }

    fun onSubmit() =
        intent {
            val current = state
            if (!current.isSubmitEnabled) return@intent
            reduce { state.copy(isSubmitting = true) }
            categoryRepository
                .createCategory(name = current.name)
                .onSuccess { category ->
                    reduce { state.copy(isSubmitting = false) }
                    postSideEffect(DirectRegisterSideEffect.OnRegistered(category))
                }.onFailure { throwable ->
                    reduce {
                        state.copy(
                            isSubmitting = false,
                            isDuplicateName = throwable is CreateCategoryError.DuplicatedName,
                        )
                    }
                }
        }
}

@Immutable
data class DirectRegisterUiState(
    val name: String = "",
    val existingNames: Set<String> = emptySet(),
    val isSubmitting: Boolean = false,
    val isDuplicateName: Boolean = false,
) {
    val nameError: ItemNameError?
        get() = if (isDuplicateName) ItemNameError.Duplicated else validateItemName(name, existingNames)

    val isSubmitEnabled: Boolean
        get() = name.isNotBlank() && nameError == null && !isSubmitting
}

sealed interface DirectRegisterSideEffect {
    data class OnRegistered(
        val category: Category,
    ) : DirectRegisterSideEffect
}
