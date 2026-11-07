package com.obrit.obrit.shared.network.request.home

data class HomeItemsRequest(
    val order: String? = null,
    val dDay: Int? = null,
    val spareQuantity: Int? = null,
    val cursor: Long? = null,
    val size: Int? = null,
)
