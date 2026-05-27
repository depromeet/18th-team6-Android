package com.obrit.obrit.shared.model.home

data class HomeItemCard(
    val id: Long,
    val name: String,
    val iconUrl: String,
    val daysInUse: Int,
    val replacementDday: String,
    val spareQuantity: Int,
)
