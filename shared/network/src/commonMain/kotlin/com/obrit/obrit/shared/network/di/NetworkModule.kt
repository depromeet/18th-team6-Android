package com.obrit.obrit.shared.network.di

import com.obrit.obrit.shared.network.client.createHttpClient
import com.obrit.obrit.shared.network.client.createJson
import com.obrit.obrit.shared.network.config.DeviceUuidProvider
import com.obrit.obrit.shared.network.config.NETWORK_BASE_URL
import com.obrit.obrit.shared.network.config.NetworkConfiguration
import com.obrit.obrit.shared.network.config.deviceUuidModule
import com.obrit.obrit.shared.network.source.AgentRemoteDataSource
import com.obrit.obrit.shared.network.source.AgentRemoteDataSourceImpl
import com.obrit.obrit.shared.network.source.AgentSessionRemoteDataSource
import com.obrit.obrit.shared.network.source.AgentSessionRemoteDataSourceImpl
import com.obrit.obrit.shared.network.source.CategoryRemoteDataSource
import com.obrit.obrit.shared.network.source.CategoryRemoteDataSourceImpl
import com.obrit.obrit.shared.network.source.HomeRemoteDataSource
import com.obrit.obrit.shared.network.source.HomeRemoteDataSourceImpl
import com.obrit.obrit.shared.network.source.ItemRemoteDataSource
import com.obrit.obrit.shared.network.source.ItemRemoteDataSourceImpl
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule =
    module {
        includes(deviceUuidModule)

        single<NetworkConfiguration> {
            NetworkConfiguration(
                baseUrl = NETWORK_BASE_URL,
                deviceUuid = get<DeviceUuidProvider>().get(),
            )
        }
        single<Json> { createJson() }
        single<HttpClient> { createHttpClient(get(), get()) }
        single<AgentRemoteDataSource> { AgentRemoteDataSourceImpl(get()) }
        single<AgentSessionRemoteDataSource> { AgentSessionRemoteDataSourceImpl(get()) }
        single<ItemRemoteDataSource> { ItemRemoteDataSourceImpl(get()) }
        single<CategoryRemoteDataSource> { CategoryRemoteDataSourceImpl(get()) }
        single<HomeRemoteDataSource> { HomeRemoteDataSourceImpl(get()) }
    }
