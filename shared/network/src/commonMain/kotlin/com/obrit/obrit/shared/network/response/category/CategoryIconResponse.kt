package com.obrit.obrit.shared.network.response.category

import com.obrit.obrit.shared.model.categories.CategoryIcon
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryIconResponse(
    @SerialName("iconId") val id: Long,
    @SerialName("url") val url: String,
)

fun CategoryIconResponse.toCategoryIcon() =
    CategoryIcon(
        id = id,
        url = url,
    )
