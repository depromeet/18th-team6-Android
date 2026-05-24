package com.obrit.obrit.shared.model.home

data class HomeItemCursorSlice(
    val content: List<HomeItemCard>,
    val nextCursor: Long?,
    val size: Int,
    val hasNext: Boolean,
)
