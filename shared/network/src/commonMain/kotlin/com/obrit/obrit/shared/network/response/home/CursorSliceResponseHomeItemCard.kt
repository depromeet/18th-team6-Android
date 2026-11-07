package com.obrit.obrit.shared.network.response.home

import com.obrit.obrit.shared.model.home.HomeItemCursorSlice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CursorSliceResponseHomeItemCard(
    @SerialName("content") val content: List<HomeItemCardResponse>,
    @SerialName("nextCursor") val nextCursor: Long? = null,
    @SerialName("size") val size: Int,
    @SerialName("hasNext") val hasNext: Boolean,
)

fun CursorSliceResponseHomeItemCard.toHomeItemCursorSlice() =
    HomeItemCursorSlice(
        content = content.map { item -> item.toHomeItemCard() },
        nextCursor = nextCursor,
        size = size,
        hasNext = hasNext,
    )
