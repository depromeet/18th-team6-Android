package com.obrit.obrit.shared.network.response.category

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CategoryApiResponse<T>(
    @SerialName("success") val success: Boolean? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: T? = null,
)
