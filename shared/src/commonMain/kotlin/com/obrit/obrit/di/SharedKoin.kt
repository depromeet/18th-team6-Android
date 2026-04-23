package com.obrit.obrit.di

import com.obrit.obrit.shared.data.di.dataModule
import com.obrit.obrit.shared.network.di.networkModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

fun sharedModules(): List<Module> = listOf(
    networkModule,
    dataModule,
)

fun initKoin(
    appDeclaration: KoinAppDeclaration = {},
) {
    startKoin {
        appDeclaration()
        modules(sharedModules())
    }
}
