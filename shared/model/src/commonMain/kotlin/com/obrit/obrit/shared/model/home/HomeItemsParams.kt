package com.obrit.obrit.shared.model.home

data class HomeItemsParams(
    val order: HomeItemOrder? = null,
    val dDay: Int? = null,
    val spareQuantity: Int? = null,
    val cursor: Long? = null,
    val size: Int? = null,
)
