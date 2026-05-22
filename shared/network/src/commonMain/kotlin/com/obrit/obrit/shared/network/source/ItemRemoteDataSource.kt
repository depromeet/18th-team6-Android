package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.request.item.CreateItemRequest
import com.obrit.obrit.shared.network.request.item.CreateReplacementRequest
import com.obrit.obrit.shared.network.request.item.PatchItemRequest
import com.obrit.obrit.shared.network.response.item.ItemResponse

interface ItemRemoteDataSource {
    suspend fun getItems(): List<ItemResponse>

    suspend fun createItem(request: CreateItemRequest): ItemResponse

    suspend fun patchItem(
        itemId: Long,
        request: PatchItemRequest,
    ): ItemResponse

    suspend fun deleteItem(itemId: Long)

    suspend fun createReplacement(
        itemId: Long,
        request: CreateReplacementRequest,
    ): ItemResponse
}
