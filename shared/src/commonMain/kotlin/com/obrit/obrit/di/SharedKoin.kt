package com.obrit.obrit.di

import com.obrit.obrit.shared.data.di.dataModule
import com.obrit.obrit.shared.network.config.NetworkConfiguration
import com.obrit.obrit.shared.network.di.networkModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

fun sharedModules(
    configuration: NetworkConfiguration,
): List<Module> = listOf(
    networkModule(configuration),
    dataModule,
)

fun initKoin(
    configuration: NetworkConfiguration,
    appDeclaration: KoinAppDeclaration = {},
): Unit {
    startKoin {
        appDeclaration()
        modules(sharedModules(configuration))
    }
}
