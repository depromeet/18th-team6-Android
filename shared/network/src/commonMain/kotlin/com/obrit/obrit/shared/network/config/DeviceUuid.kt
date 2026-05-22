package com.obrit.obrit.shared.network.config

import org.koin.core.module.Module

internal interface DeviceUuidProvider {
    fun get(): String
}

internal expect val deviceUuidModule: Module
