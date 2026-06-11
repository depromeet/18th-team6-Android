package com.obrit.obrit.shared.model.home

data class HomeItemCard(
    val itemId: Long,
    val name: String,
    val daysInUse: Int,
    val replacementDday: String,
    val spareQuantity: Int,
    val iconUrl: String,
    val itemBucket: ItemBucket,
)
