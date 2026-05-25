package com.obrit.obrit.di

import com.obrit.obrit.shared.data.repository.AgentRepository
import com.obrit.obrit.shared.data.repository.AgentSessionRepository
import com.obrit.obrit.shared.data.repository.CategoryRepository
import com.obrit.obrit.shared.data.repository.HomeRepository
import com.obrit.obrit.shared.data.repository.ItemRepository

class SharedRepositoryProvider {
    fun agentRepository(): AgentRepository =
        ensureSharedKoin().get()

    fun agentSessionRepository(): AgentSessionRepository =
        ensureSharedKoin().get()

    fun categoryRepository(): CategoryRepository =
        ensureSharedKoin().get()

    fun homeRepository(): HomeRepository =
        ensureSharedKoin().get()

    fun itemRepository(): ItemRepository =
        ensureSharedKoin().get()
}
