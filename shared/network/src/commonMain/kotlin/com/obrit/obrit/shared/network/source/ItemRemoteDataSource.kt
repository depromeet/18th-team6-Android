package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.request.item.BulkCreateItemRequest
import com.obrit.obrit.shared.network.request.item.CreateItemRequest
import com.obrit.obrit.shared.network.request.item.CreateReplacementRequest
import com.obrit.obrit.shared.network.request.item.PatchItemRequest
import com.obrit.obrit.shared.network.request.item.UpdateSpareCountRequest
import com.obrit.obrit.shared.network.response.item.ItemDetailResponse
import com.obrit.obrit.shared.network.response.item.ItemResponse
import com.obrit.obrit.shared.network.response.item.ReplacementHistoryResponse

interface ItemRemoteDataSource {
    suspend fun getItems(): List<ItemResponse>

    suspend fun getItem(itemId: Long): ItemDetailResponse

    suspend fun createItem(request: CreateItemRequest): ItemResponse

    suspend fun createItems(request: BulkCreateItemRequest): List<ItemResponse>

    suspend fun patchItem(
        itemId: Long,
        request: PatchItemRequest,
    ): ItemResponse

    suspend fun patchSpareCount(
        itemId: Long,
        request: UpdateSpareCountRequest,
    ): ItemResponse

    suspend fun deleteItem(itemId: Long)

    suspend fun getReplacementHistories(
        itemId: Long,
        limit: Int? = null,
    ): List<ReplacementHistoryResponse>

    suspend fun createReplacement(
        itemId: Long,
        request: CreateReplacementRequest,
    ): ItemResponse
}
