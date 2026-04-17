package com.obrit.obrit.shared.network.config

@ConsistentCopyVisibility
internal data class NetworkConfiguration private constructor(
    val baseUrl: String,
    val enableLogging: Boolean = false,
) {

    internal companion object {
        val DEFAULT_NETWORK_CONFIGURATION = NetworkConfiguration(
            baseUrl = "https://example.com/", // TODO local.properties
            enableLogging = true,
        )
    }
}
