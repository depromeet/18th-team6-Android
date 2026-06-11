package com.obrit.obrit.navigation.route

import kotlinx.serialization.Serializable

@Serializable
sealed interface AgentRoute : Route {
    @Serializable
    data object Agents : AgentRoute

    @Serializable
    data class AgentDetail(
        val id: Int,
    ) : AgentRoute

    @Serializable
    data class AgentDetailEdit(
        val id: Long,
    ) : AgentRoute
}
