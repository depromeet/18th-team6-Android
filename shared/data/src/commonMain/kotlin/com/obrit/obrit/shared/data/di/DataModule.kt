package com.obrit.obrit.shared.data.di

import com.obrit.obrit.shared.data.repository.AgentRepository
import com.obrit.obrit.shared.data.repository.AgentRepositoryImpl
import com.obrit.obrit.shared.data.repository.AgentSessionRepository
import com.obrit.obrit.shared.data.repository.AgentSessionRepositoryImpl
import com.obrit.obrit.shared.data.repository.CategoryRepository
import com.obrit.obrit.shared.data.repository.CategoryRepositoryImpl
import com.obrit.obrit.shared.data.repository.HomeRepository
import com.obrit.obrit.shared.data.repository.HomeRepositoryImpl
import com.obrit.obrit.shared.data.repository.ItemRepository
import com.obrit.obrit.shared.data.repository.ItemRepositoryImpl
import org.koin.dsl.module

val dataModule =
    module {
        single<AgentRepository> { AgentRepositoryImpl(get()) }
        single<AgentSessionRepository> { AgentSessionRepositoryImpl(get()) }
        single<ItemRepository> { ItemRepositoryImpl(get()) }
        single<CategoryRepository> { CategoryRepositoryImpl(get()) }
        single<HomeRepository> { HomeRepositoryImpl(get()) }
    }
