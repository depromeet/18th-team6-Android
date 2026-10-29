package com.obrit.obrit.shared.model.categories

data class CategoryGroup(
    val id: Long,
    val name: String,
    val type: CategoryGroupType,
    val displayOrder: Int,
)
