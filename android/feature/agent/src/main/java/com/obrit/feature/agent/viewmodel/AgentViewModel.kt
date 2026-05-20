package com.obrit.feature.agent.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.android.core.ui.extensions.vmAsync
import com.obrit.obrit.shared.data.repository.AgentRepository
import com.obrit.obrit.shared.data.repository.AgentSessionRepository
import com.obrit.obrit.shared.model.agents.Agent
import com.obrit.obrit.shared.model.agents.AgentType
import com.obrit.obrit.shared.model.agents.PatchAgentParams
import com.obrit.obrit.shared.model.sessions.Session
import org.orbitmvi.orbit.viewmodel.container

class AgentViewModel(
    private val agentRepository: AgentRepository,
    private val agentSessionRepository: AgentSessionRepository,
) : BaseContainerHost<AgentUiState, AgentSideEffect>() {
    override val container =
        container<AgentUiState, AgentSideEffect>(AgentUiState.Loading) {
            val agentsDeferred =
                vmAsync {
                    agentRepository.getAgents()
                }

            val sessionsDeferred =
                vmAsync {
                    agentSessionRepository.getSessions()
                }

            val agentsResult = agentsDeferred.await()
            val sessionsResult = sessionsDeferred.await()
            val agents = agentsResult.getOrNull()
            val sessions = sessionsResult.getOrNull()

            if (agents == null || sessions == null) {
                reduce {
                    AgentUiState.LoadFailed
                }
                return@container
            }

            reduce {
                AgentUiState.Success(
                    agents = agents,
                    sessions = sessions,
                )
            }
        }

    fun createAgent(
        name: String,
        description: String,
        type: AgentType,
    ) = intent {
        agentRepository
            .createAgent(name, description, type)
            .onSuccess {
                reduceOn<AgentUiState.Success> {
                    state.copy(
                        agents = state.agents + it,
                    )
                }
            }.onFailure { e ->
                postSideEffect(AgentSideEffect.OnError(e))
            }
    }

    fun deleteAgent(id: Int) =
        intent {
            agentRepository
                .deleteAgent(id)
                .onSuccess {
                    // 성공에 대한 동작
                }.onFailure { e ->
                    postSideEffect(AgentSideEffect.OnError(e))
                }
        }

    fun patchAgent(
        id: Int,
        name: String,
        description: String,
        type: AgentType,
    ) = intent {
        agentRepository
            .patchAgent(
                PatchAgentParams(
                    id = id,
                    name = name,
                    description = description,
                    type = type,
                ),
            ).onSuccess { patchedAgent ->
                reduceOn<AgentUiState.Success> {
                    state.copy(
                        agents =
                            state.agents.map { agent ->
                                if (agent.id == patchedAgent.id) {
                                    patchedAgent
                                } else {
                                    agent
                                }
                            },
                    )
                }
            }.onFailure { e ->
                postSideEffect(AgentSideEffect.OnError(e))
            }
    }

    fun onAgentClick(agent: Agent) =
        intent {
            postSideEffect(AgentSideEffect.OnAgentClick(agent))
        }

    fun onAgentLongClick(agent: Agent) =
        intent {
            postSideEffect(AgentSideEffect.OnAgentLongClick(agent))
        }

    fun onMenuClick() =
        intent {
            postSideEffect(AgentSideEffect.OnMenuClick)
        }

    fun showSnackbar(message: String) =
        intent {
            postSideEffect(AgentSideEffect.ShowSnackbar(message))
        }
}

sealed interface AgentUiState {
    @Immutable
    data class Success(
        val agents: List<Agent>,
        val sessions: List<Session>,
    ) : AgentUiState

    data object Loading : AgentUiState

    data object LoadFailed : AgentUiState
}

sealed interface AgentSideEffect {
    data class OnAgentClick(
        val agent: Agent,
    ) : AgentSideEffect

    data class OnAgentLongClick(
        val agent: Agent,
    ) : AgentSideEffect

    data object OnMenuClick : AgentSideEffect

    data class ShowSnackbar(
        val message: String,
    ) : AgentSideEffect

    data class OnError(
        val error: Throwable,
    ) : AgentSideEffect
}
