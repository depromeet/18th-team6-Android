package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.ReplacementDate
import com.obrit.obrit.shared.model.items.CreateItemParams
import com.obrit.obrit.shared.model.items.Item
import com.obrit.obrit.shared.model.items.PatchItemParams
import com.obrit.obrit.shared.model.items.ReplacementHistory
import com.obrit.obrit.shared.model.items.error.CreateItemError
import com.obrit.obrit.shared.network.error.RemoteError
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
        }.recoverCatching { error ->
            if (error is RemoteError && error.statusCode == HTTP_STATUS_CONFLICT) {
                throw CreateItemError.DuplicatedName()
            }
            throw error
        }

    override suspend fun createItems(
        params: List<CreateItemParams>,
        receiptImageUrl: String?,
    ): Result<List<Item>> =
        runCatchingWith {
            params
                .chunked(BULK_CREATE_ITEMS_CHUNK_SIZE)
                .flatMap { chunk ->
                    itemRemoteDataSource.createItems(
                        BulkCreateItemRequest(
                            items = chunk.map { itemParams -> itemParams.toCreateItemRequest() },
                            receiptImageUrl = receiptImageUrl,
                        ),
                    )
                }.map { response -> response.toItem() }
        }.recoverCatching { error ->
            if (error is RemoteError && error.statusCode == HTTP_STATUS_CONFLICT) {
                throw CreateItemError.DuplicatedName()
            }
            throw error
        }

    override suspend fun patchItem(params: PatchItemParams): Result<Item> =
        runCatchingWith {
            itemRemoteDataSource
                .patchItem(
                    itemId = params.itemId,
                    request =
                        PatchItemRequest(
                            categoryId = params.categoryId,
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

private const val BULK_CREATE_ITEMS_CHUNK_SIZE = 20
private const val HTTP_STATUS_CONFLICT = 409

private fun CreateItemParams.toCreateItemRequest() =
    CreateItemRequest(
        categoryId = categoryId,
        name = name,
        spareQuantity = spareQuantity,
        lastReplacementPeriod = lastReplacementPeriod?.name,
        replacementIntervalDays = replacementIntervalDays,
        newCategoryName = newCategoryName,
        newCategoryDefaultReplacementIntervalDays = newCategoryDefaultReplacementIntervalDays,
    )
