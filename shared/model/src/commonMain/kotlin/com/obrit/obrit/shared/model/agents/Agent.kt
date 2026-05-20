package com.obrit.obrit.shared.model.agents

import kotlin.time.Instant

data class Agent(
    val id: Int,
    val name: String,
    val description: String,
    val timestamp: Instant,
    val type: AgentType,
)
