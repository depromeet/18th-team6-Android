package com.obrit.obrit.shared.model.agents

data class PatchAgentParams(
    val id: Int,
    val name: String,
    val description: String,
    val type: AgentType
)
