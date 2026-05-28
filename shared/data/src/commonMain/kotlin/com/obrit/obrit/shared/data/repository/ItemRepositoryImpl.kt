package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.ReplacementDate
import com.obrit.obrit.shared.model.items.CreateItemParams
import com.obrit.obrit.shared.model.items.Item
import com.obrit.obrit.shared.model.items.PatchItemParams
import com.obrit.obrit.shared.model.items.ReplacementHistory
import com.obrit.obrit.shared.network.error.runCatchingWith
import com.obrit.obrit.shared.network.request.item.BulkCreateItemRequest
import com.obrit.obrit.shared.network.request.item.CreateItemRequest
import com.obrit.obrit.shared.network.request.item.CreateReplacementRequest
import com.obrit.obrit.shared.network.request.item.PatchItemRequest
import com.obrit.obrit.shared.network.request.item.UpdateSpareCountRequest
import com.obrit.obrit.shared.network.response.item.toItem
import com.obrit.obrit.shared.network.response.item.toReplacementHistory
import com.obrit.obrit.shared.network.source.ItemRemoteDataSource

internal class ItemRepositoryImpl(
    private val itemRemoteDataSource: ItemRemoteDataSource,
) : ItemRepository {
    override suspend fun getItems(): Result<List<Item>> =
        runCatchingWith {
            itemRemoteDataSource.getItems().map { response -> response.toItem() }
        }

    override suspend fun createItem(params: CreateItemParams): Result<Item> =
        runCatchingWith {
            itemRemoteDataSource
                .createItem(params.toCreateItemRequest())
                .toItem()
        }

    override suspend fun createItems(params: List<CreateItemParams>): Result<List<Item>> =
        runCatchingWith {
            itemRemoteDataSource
                .createItems(
                    BulkCreateItemRequest(
                        items = params.map { itemParams -> itemParams.toCreateItemRequest() },
                    ),
                ).map { response -> response.toItem() }
        }

    override suspend fun patchItem(params: PatchItemParams): Result<Item> =
        runCatchingWith {
            itemRemoteDataSource
                .patchItem(
                    itemId = params.itemId,
                    request =
                        PatchItemRequest(
                            name = params.name,
                            count = params.count,
                            lastReplacedDate = params.lastReplacedDate?.value,
                            replacementIntervalDays = params.replacementIntervalDays,
                        ),
                ).toItem()
        }

    override suspend fun patchSpareCount(
        itemId: Long,
        count: Int,
    ): Result<Item> =
        runCatchingWith {
            itemRemoteDataSource
                .patchSpareCount(
                    itemId = itemId,
                    request = UpdateSpareCountRequest(count = count),
                ).toItem()
        }

    override suspend fun deleteItem(itemId: Long): Result<Unit> =
        runCatchingWith {
            itemRemoteDataSource.deleteItem(itemId)
        }

    override suspend fun getReplacementHistories(
        itemId: Long,
        limit: Int?,
    ): Result<List<ReplacementHistory>> =
        runCatchingWith {
            itemRemoteDataSource
                .getReplacementHistories(
                    itemId = itemId,
                    limit = limit,
                ).map { response -> response.toReplacementHistory() }
        }

    override suspend fun createReplacement(
        itemId: Long,
        replacedDate: ReplacementDate?,
    ): Result<Item> =
        runCatchingWith {
            itemRemoteDataSource
                .createReplacement(
                    itemId = itemId,
                    request =
                        CreateReplacementRequest(
                            replacedDate = replacedDate?.value,
                        ),
                ).toItem()
        }
}

private fun CreateItemParams.toCreateItemRequest() =
    CreateItemRequest(
        categoryId = categoryId,
        name = name,
        count = count,
        lastReplacementPeriod = lastReplacementPeriod?.value,
        replacementIntervalDays = replacementIntervalDays,
    )
