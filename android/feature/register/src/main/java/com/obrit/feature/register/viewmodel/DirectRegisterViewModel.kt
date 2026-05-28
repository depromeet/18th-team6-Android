package com.obrit.feature.register.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.obrit.shared.data.repository.CategoryRepository
import com.obrit.obrit.shared.model.categories.Category
import com.obrit.obrit.shared.model.categories.CategoryIcon
import org.orbitmvi.orbit.viewmodel.container

class DirectRegisterViewModel(
    private val categoryRepository: CategoryRepository,
) : BaseContainerHost<DirectRegisterUiState, DirectRegisterSideEffect>() {
    override val container =
        container<DirectRegisterUiState, DirectRegisterSideEffect>(
            DirectRegisterUiState(),
        ) {
            loadIcons()
        }

    private fun loadIcons() =
        intent {
            categoryRepository.getCategoryIcons()
                .onSuccess { icons ->
                    reduce { state.copy(icons = icons) }
                }
        }

    fun onNameChange(value: String) =
        intent {
            reduce { state.copy(name = value) }
        }

    fun onIconSelect(iconId: Long) =
        intent {
            reduce { state.copy(selectedIconId = iconId) }
        }

    fun onSubmit() =
        intent {
            val current = state
            val iconId = current.selectedIconId ?: return@intent
            if (current.isSubmitting) return@intent
            reduce { state.copy(isSubmitting = true) }
            categoryRepository.createCategory(name = current.name, iconId = iconId)
                .onSuccess { category ->
                    reduce { state.copy(isSubmitting = false) }
                    postSideEffect(DirectRegisterSideEffect.OnRegistered(category))
                }
                .onFailure {
                    reduce { state.copy(isSubmitting = false) }
                }
        }

    fun onBack() =
        intent {
            postSideEffect(DirectRegisterSideEffect.OnBack)
        }
}

@Immutable
data class DirectRegisterUiState(
    val icons: List<CategoryIcon> = emptyList(),
    val name: String = "",
    val selectedIconId: Long? = null,
    val isSubmitting: Boolean = false,
) {
    val isSubmitEnabled: Boolean
        get() =
            name.isNotBlank() &&
                name.length <= DIRECT_REGISTER_NAME_MAX_LENGTH &&
                selectedIconId != null &&
                !isSubmitting
}

sealed interface DirectRegisterSideEffect {
    data class OnRegistered(val category: Category) : DirectRegisterSideEffect

    data object OnBack : DirectRegisterSideEffect
}

internal const val DIRECT_REGISTER_NAME_MAX_LENGTH = 15
