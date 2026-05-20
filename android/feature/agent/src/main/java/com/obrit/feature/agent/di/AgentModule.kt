package com.obrit.feature.agent.di

import com.obrit.feature.agent.viewmodel.AgentViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val agentFeatureModule =
    module {
        viewModelOf(::AgentViewModel)
    }
