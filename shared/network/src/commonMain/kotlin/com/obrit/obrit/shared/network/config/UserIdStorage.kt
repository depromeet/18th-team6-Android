package com.obrit.obrit.shared.network.config

import org.koin.core.module.Module

internal interface UserIdStorage {
    fun load(): Long?

    fun save(userId: Long)
}

internal expect val userIdStorageModule: Module
