package com.obrit.obrit.shared.network.response.category

import com.obrit.obrit.shared.model.categories.Category
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("iconUrl") val iconUrl: String,
    @SerialName("userId") val userId: Long? = null,
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("defaultReplacementIntervalDays") val defaultReplacementIntervalDays: Int,
)

fun CategoryResponse.toCategory() =
    Category(
        id = id,
        name = name,
        iconUrl = iconUrl,
        userId = userId,
        createdAt = createdAt,
        defaultReplacementIntervalDays = defaultReplacementIntervalDays,
    )
