package com.obrit.obrit.shared.network.di

import com.obrit.obrit.shared.network.client.createHttpClient
import com.obrit.obrit.shared.network.client.createJson
import com.obrit.obrit.shared.network.config.NetworkConfiguration
import com.obrit.obrit.shared.network.config.NetworkConfiguration.Companion.DEFAULT_NETWORK_CONFIGURATION
import com.obrit.obrit.shared.network.source.AgentRemoteDataSource
import com.obrit.obrit.shared.network.source.AgentRemoteDataSourceImpl
import com.obrit.obrit.shared.network.source.AgentSessionRemoteDataSource
import com.obrit.obrit.shared.network.source.AgentSessionRemoteDataSourceImpl
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single<NetworkConfiguration> { DEFAULT_NETWORK_CONFIGURATION }
    single<Json> { createJson() }
    single<HttpClient> { createHttpClient(get(), get()) }
    single<AgentRemoteDataSource> { AgentRemoteDataSourceImpl(get()) }
    single<AgentSessionRemoteDataSource> { AgentSessionRemoteDataSourceImpl(get()) }
}
