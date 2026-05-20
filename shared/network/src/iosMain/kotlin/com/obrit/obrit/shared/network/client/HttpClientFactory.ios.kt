package com.obrit.obrit.shared.network.client

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

internal actual fun platformHttpClientEngineFactory(): HttpClientEngineFactory<*> = Darwin
