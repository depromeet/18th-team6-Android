package com.obrit.obrit.shared.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiResponse<T>(
    @SerialName("success") val success: Boolean? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: T? = null,
)

internal fun <T> ApiResponse<T>.requireData(): T {
    if (success != true) {
        error(message.orEmpty().ifBlank { "API response failed." })
    }

    return data ?: error(message.orEmpty().ifBlank { "API response data is missing." })
}
