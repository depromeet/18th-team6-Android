package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.request.item.CreateItemRequest
import com.obrit.obrit.shared.network.request.item.CreateReplacementRequest
import com.obrit.obrit.shared.network.request.item.PatchItemRequest
import com.obrit.obrit.shared.network.response.item.ItemResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class ItemRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : ItemRemoteDataSource {
    override suspend fun getItems(): List<ItemResponse> =
        httpClient
            .get(ITEMS_PATH)
            .body()

    override suspend fun createItem(request: CreateItemRequest): ItemResponse =
        httpClient
            .post(ITEMS_PATH) {
                setBody(request)
            }.body()

    override suspend fun patchItem(
        itemId: Long,
        request: PatchItemRequest,
    ): ItemResponse =
        httpClient
            .patch("$ITEMS_PATH/$itemId") {
                setBody(request)
            }.body()

    override suspend fun deleteItem(itemId: Long) {
        httpClient.delete("$ITEMS_PATH/$itemId")
    }

    override suspend fun createReplacement(
        itemId: Long,
        request: CreateReplacementRequest,
    ): ItemResponse =
        httpClient
            .post("$ITEMS_PATH/$itemId/replacements") {
                setBody(request)
            }.body()
}

private const val ITEMS_PATH = "items"
