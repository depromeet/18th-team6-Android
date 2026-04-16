package com.obrit.obrit.shared.network.config

data class NetworkConfiguration(
    val baseUrl: String,
    val enableLogging: Boolean = false,
) {
    init {
        require(baseUrl.isNotBlank()) {
            "NetworkConfiguration.baseUrl must not be blank."
        }
    }
}
