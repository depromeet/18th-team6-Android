package com.obrit.obrit.di

import com.obrit.obrit.shared.data.di.dataModule
import com.obrit.obrit.shared.network.di.networkModule
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.error.KoinApplicationAlreadyStartedException
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.mp.KoinPlatformTools

fun sharedModules(): List<Module> =
    listOf(
        networkModule,
        dataModule,
    )

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(sharedModules())
    }
}

fun startSharedKoin() {
    ensureSharedKoin()
}

internal fun ensureSharedKoin(): Koin {
    KoinPlatformTools.defaultContext().getOrNull()?.let { return it }

    return try {
        startKoin {
            modules(sharedModules())
        }.koin
    } catch (_: KoinApplicationAlreadyStartedException) {
        KoinPlatformTools.defaultContext().get()
    }
}
