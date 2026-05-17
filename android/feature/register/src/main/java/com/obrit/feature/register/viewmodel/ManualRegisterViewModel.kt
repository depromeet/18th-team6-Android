package com.obrit.feature.register.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import org.orbitmvi.orbit.viewmodel.container

class ManualRegisterViewModel :
    BaseContainerHost<ManualRegisterUiState, ManualRegisterSideEffect>() {
    override val container =
        container<ManualRegisterUiState, ManualRegisterSideEffect>(
            ManualRegisterUiState.Editing(),
        )

    fun onNameChange(value: String) =
        intent {
            reduceOn<ManualRegisterUiState.Editing> {
                state.copy(name = value)
            }
        }

    fun onSpareCountChange(value: String) =
        intent {
            reduceOn<ManualRegisterUiState.Editing> {
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

sealed interface ManualRegisterUiState {
    @Immutable
    data class Editing(
        val name: String = "",
        val spareCount: String = "",
    ) : ManualRegisterUiState {
        val isSubmitEnabled: Boolean
            get() = name.isNotBlank()
    }
}

sealed interface ManualRegisterSideEffect {
    data object OnRegistered : ManualRegisterSideEffect

    data object OnBack : ManualRegisterSideEffect
}
