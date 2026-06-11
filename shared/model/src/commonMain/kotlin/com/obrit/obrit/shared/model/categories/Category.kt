package com.obrit.obrit.shared.model.categories

data class Category(
    val id: Long,
    val name: String,
    val iconUrl: String,
    val itemCount: Int,
    val totalSpareQuantity: Int,
)
