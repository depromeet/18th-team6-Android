package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.ReplacementDate
import com.obrit.obrit.shared.model.items.CreateItemParams
import com.obrit.obrit.shared.model.items.Item
import com.obrit.obrit.shared.model.items.PatchItemParams

interface ItemRepository {
    suspend fun getItems(): Result<List<Item>>

    suspend fun createItem(params: CreateItemParams): Result<Item>

    suspend fun patchItem(params: PatchItemParams): Result<Item>

    suspend fun deleteItem(itemId: Long): Result<Unit>

    suspend fun createReplacement(
        itemId: Long,
        replacedDate: ReplacementDate? = null,
    ): Result<Item>
}
