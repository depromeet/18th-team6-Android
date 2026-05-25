package com.obrit.obrit.shared.network.request.category

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateCategoryRequest(
    @SerialName("name") val name: String,
    @SerialName("iconId") val iconId: Long,
)
