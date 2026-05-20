package com.obrit.feature.register.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import org.orbitmvi.orbit.viewmodel.container

class ManualRegisterViewModel :
    BaseContainerHost<ManualRegisterUiState, ManualRegisterSideEffect>() {
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

    fun onSpareCountChange(value: String) =
        intent {
            reduce {
                state.copy(spareCount = value)
            }
        }

    fun onSubmit() =
        intent {
            postSideEffect(ManualRegisterSideEffect.OnRegistered)
        }

    fun onBack() =
        intent {
            postSideEffect(ManualRegisterSideEffect.OnBack)
        }
}

@Immutable
data class ManualRegisterUiState(
    val categoryName: String = "",
    val name: String = "",
    val spareCount: String = "",
) {
    val isSubmitEnabled: Boolean
        get() = categoryName.isNotBlank() && name.isNotBlank()
}

sealed interface ManualRegisterSideEffect {
    data object OnRegistered : ManualRegisterSideEffect

    data object OnBack : ManualRegisterSideEffect
}
