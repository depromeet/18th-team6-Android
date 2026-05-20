package com.obrit.obrit.shared.data.di

import com.obrit.obrit.shared.data.repository.AgentRepository
import com.obrit.obrit.shared.data.repository.AgentRepositoryImpl
import com.obrit.obrit.shared.data.repository.AgentSessionRepository
import com.obrit.obrit.shared.data.repository.AgentSessionRepositoryImpl
import org.koin.dsl.module

val dataModule =
    module {
        single<AgentRepository> { AgentRepositoryImpl(get()) }
        single<AgentSessionRepository> { AgentSessionRepositoryImpl(get()) }
    }
