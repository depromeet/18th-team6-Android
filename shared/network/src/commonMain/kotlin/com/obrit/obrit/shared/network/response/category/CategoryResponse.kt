package com.obrit.obrit.shared.network.response.category

import com.obrit.obrit.shared.model.categories.Category
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    @SerialName("categoryId") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("iconUrl") val iconUrl: String,
    @SerialName("itemCount") val itemCount: Int,
    @SerialName("totalSpareQuantity") val totalSpareQuantity: Int,
)

fun CategoryResponse.toCategory() =
    Category(
        id = id,
        name = name,
        iconUrl = iconUrl,
        itemCount = itemCount,
        totalSpareQuantity = totalSpareQuantity,
    )
