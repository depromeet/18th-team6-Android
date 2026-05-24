package com.obrit.feature.detail.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.obrit.shared.data.repository.AgentRepository
import com.obrit.obrit.shared.model.agents.Agent
import org.orbitmvi.orbit.viewmodel.container

class DetailViewModel internal constructor(
    private val agentRepository: AgentRepository,
) : BaseContainerHost<DetailUiState, DetailSideEffect>() {
    override val container = container<DetailUiState, DetailSideEffect>(DetailUiState.Loading)

    fun loadAgent(id: Int) =
        intent {
            reduce {
                DetailUiState.Loading
            }

            agentRepository
                .getAgent(id)
                .onSuccess { agent ->
                    reduce {
                        DetailUiState.Success(agent = agent)
                    }
                }.onFailure {
                    reduce {
                        DetailUiState.LoadFailed
                    }
                }
        }

    fun onBackClick() =
        intent {
            postSideEffect(DetailSideEffect.NavigateBack)
        }
}

sealed interface DetailUiState {
    @Immutable
    data class Success(
        val agent: Agent,
    ) : DetailUiState

    data object Loading : DetailUiState

    data object LoadFailed : DetailUiState
}

sealed interface DetailSideEffect {
    data object NavigateBack : DetailSideEffect
}
