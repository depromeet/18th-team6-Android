package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.ReplacementDate
import com.obrit.obrit.shared.model.items.CreateItemParams
import com.obrit.obrit.shared.model.items.Item
import com.obrit.obrit.shared.model.items.ItemDetail
import com.obrit.obrit.shared.model.items.PatchItemParams
import com.obrit.obrit.shared.model.items.ReplacementHistory

interface ItemRepository {
    suspend fun getItems(): Result<List<Item>>

    suspend fun getItem(itemId: Long): Result<ItemDetail>

    suspend fun createItem(params: CreateItemParams): Result<Item>

    suspend fun createItems(
        params: List<CreateItemParams>,
        receiptImageUrl: String? = null,
    ): Result<List<Item>>

    suspend fun patchItem(params: PatchItemParams): Result<Item>

    suspend fun patchSpareCount(
        itemId: Long,
        count: Int,
    ): Result<Item>

    suspend fun deleteItem(itemId: Long): Result<Unit>

    suspend fun getReplacementHistories(
        itemId: Long,
        limit: Int? = null,
    ): Result<List<ReplacementHistory>>

    suspend fun createReplacement(
        itemId: Long,
        replacedDate: ReplacementDate? = null,
    ): Result<Item>
}
