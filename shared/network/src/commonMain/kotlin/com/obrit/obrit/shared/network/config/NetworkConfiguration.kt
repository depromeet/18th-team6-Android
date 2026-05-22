package com.obrit.obrit.shared.network.config

internal const val NETWORK_BASE_URL = "https://orbit-dep.site/"

internal data class NetworkConfiguration(
    val baseUrl: String,
    val deviceUuid: String,
    val enableLogging: Boolean = false,
)
