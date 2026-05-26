package com.obrit.feature.register.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import org.orbitmvi.orbit.viewmodel.container

class ManualRegisterViewModel : BaseContainerHost<ManualRegisterUiState, ManualRegisterSideEffect>() {
    override val container =
        container<ManualRegisterUiState, ManualRegisterSideEffect>(
            ManualRegisterUiState(),
        )

    fun onCategoryChange(value: String) =
        intent {
            reduce {
                state.copy(categoryName = value)
            }
        }

    fun onNameChange(value: String) =
        intent {
            reduce {
                state.copy(name = value)
            }
        }

    fun onQuantityChange(value: Int) =
        intent {
            reduce {
                state.copy(quantity = value)
            }
        }

    fun onLastReplaceDateChange(value: String) =
        intent {
            reduce {
                state.copy(lastReplaceDate = value)
            }
        }

    fun onSubmit() =
        intent {
            reduce { ManualRegisterUiState() }
            postSideEffect(ManualRegisterSideEffect.OnRegistered)
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
    val categoryName: String = "",
    val name: String = "",
    val quantity: Int = 0,
    val existingCount: Int = 0,
    val lastReplaceDate: String = "",
) {
    val totalCount: Int
        get() = existingCount + quantity

    val isSubmitEnabled: Boolean
        get() = categoryName.isNotBlank() && name.isNotBlank() && lastReplaceDate.isNotBlank() && quantity > 0
}

sealed interface ManualRegisterSideEffect {
    data object OnRegistered : ManualRegisterSideEffect

    data object OnBack : ManualRegisterSideEffect

    data object OnNavigateToDirectRegister : ManualRegisterSideEffect
}
