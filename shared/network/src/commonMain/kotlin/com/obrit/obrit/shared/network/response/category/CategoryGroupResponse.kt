package com.obrit.obrit.shared.network.response.category

import com.obrit.obrit.shared.model.categories.CategoryGroup
import com.obrit.obrit.shared.model.categories.CategoryGroupType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryGroupResponse(
    @SerialName("categoryGroupId") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("type") val type: String,
    @SerialName("displayOrder") val displayOrder: Int,
)

fun CategoryGroupResponse.toCategoryGroup() =
    CategoryGroup(
        id = id,
        name = name,
        type =
            when (type) {
                "PRESET" -> CategoryGroupType.PRESET
                "USER" -> CategoryGroupType.USER
                else -> CategoryGroupType.UNKNOWN
            },
        displayOrder = displayOrder,
    )
