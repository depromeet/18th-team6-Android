package com.obrit.obrit.shared.network.response.category

import com.obrit.obrit.shared.model.categories.CategoryIcon
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryIconResponse(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("iconUrl") val iconUrl: String,
)

fun CategoryIconResponse.toCategoryIcon() =
    CategoryIcon(
        id = id,
        name = name,
        iconUrl = iconUrl,
    )
