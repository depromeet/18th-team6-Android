package com.obrit.feature.register.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import org.orbitmvi.orbit.viewmodel.container

class DirectRegisterViewModel :
    BaseContainerHost<DirectRegisterUiState, DirectRegisterSideEffect>() {
    override val container =
        container<DirectRegisterUiState, DirectRegisterSideEffect>(
            DirectRegisterUiState(),
        )

    fun onNameChange(value: String) =
        intent {
            reduce {
                state.copy(name = value)
            }
        }

    fun onIconSelect(index: Int) =
        intent {
            reduce {
                state.copy(selectedIconIndex = index)
            }
        }

    fun onSubmit() =
        intent {
            postSideEffect(
                DirectRegisterSideEffect.OnRegistered(
                    name = state.name,
                    selectedIconIndex = state.selectedIconIndex,
                ),
            )
        }

    fun onBack() =
        intent {
            postSideEffect(DirectRegisterSideEffect.OnBack)
        }
}

@Immutable
data class DirectRegisterUiState(
    val name: String = "",
    // 추후 GET /categories/icons 연동 시 selectedIconId: String? 으로 교체
    val selectedIconIndex: Int? = null,
) {
    val isSubmitEnabled: Boolean
        get() =
            name.isNotBlank() &&
                name.length <= DIRECT_REGISTER_NAME_MAX_LENGTH &&
                selectedIconIndex != null
}

sealed interface DirectRegisterSideEffect {
    data class OnRegistered(
        val name: String,
        val selectedIconIndex: Int?,
    ) : DirectRegisterSideEffect

    data object OnBack : DirectRegisterSideEffect
}

internal const val DIRECT_REGISTER_NAME_MAX_LENGTH = 15

// 추후 GET /categories/icons 응답으로 교체. 일단 placeholder 41개 (5x8 + 1).
internal const val DIRECT_REGISTER_ICON_PLACEHOLDER_COUNT = 41
