package com.obrit.feature.agent.di

import com.obrit.feature.agent.viewmodel.AgentViewModel
import com.obrit.feature.agent.viewmodel.ConsumableDetailViewModel
import com.obrit.feature.agent.viewmodel.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val agentFeatureModule =
    module {
        viewModelOf(::AgentViewModel)
        viewModelOf(::HomeViewModel)
        viewModelOf(::ConsumableDetailViewModel)
    }
