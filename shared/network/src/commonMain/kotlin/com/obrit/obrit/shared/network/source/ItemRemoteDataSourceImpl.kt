package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.client.userIdHeader
import com.obrit.obrit.shared.network.config.UserIdProvider
import com.obrit.obrit.shared.network.request.item.BulkCreateItemRequest
import com.obrit.obrit.shared.network.request.item.CreateItemRequest
import com.obrit.obrit.shared.network.request.item.CreateReplacementRequest
import com.obrit.obrit.shared.network.request.item.PatchItemRequest
import com.obrit.obrit.shared.network.request.item.UpdateSpareCountRequest
import com.obrit.obrit.shared.network.response.ApiResponse
import com.obrit.obrit.shared.network.response.item.ItemResponse
import com.obrit.obrit.shared.network.response.item.ReplacementHistoryResponse
import com.obrit.obrit.shared.network.response.requireData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class ItemRemoteDataSourceImpl(
    private val httpClient: HttpClient,
    private val userIdProvider: UserIdProvider,
) : ItemRemoteDataSource {
    override suspend fun getItems(): List<ItemResponse> {
        val userId = userIdProvider.get()

        return httpClient
            .get(ITEMS_PATH) {
                userIdHeader(userId)
            }.body<ApiResponse<List<ItemResponse>>>()
            .requireData()
    }

    override suspend fun createItem(request: CreateItemRequest): ItemResponse {
        val userId = userIdProvider.get()

        return httpClient
            .post(ITEMS_PATH) {
                userIdHeader(userId)
                setBody(request)
            }.body<ApiResponse<ItemResponse>>()
            .requireData()
    }

    override suspend fun createItems(request: BulkCreateItemRequest): List<ItemResponse> {
        val userId = userIdProvider.get()

        return httpClient
            .post("$ITEMS_PATH/bulk") {
                userIdHeader(userId)
                setBody(request)
            }.body<ApiResponse<List<ItemResponse>>>()
            .requireData()
    }

    override suspend fun patchItem(
        itemId: Long,
        request: PatchItemRequest,
    ): ItemResponse {
        val userId = userIdProvider.get()

        return httpClient
            .patch("$ITEMS_PATH/$itemId") {
                userIdHeader(userId)
                setBody(request)
            }.body<ApiResponse<ItemResponse>>()
            .requireData()
    }

    override suspend fun patchSpareCount(
        itemId: Long,
        request: UpdateSpareCountRequest,
    ): ItemResponse {
        val userId = userIdProvider.get()

        return httpClient
            .patch("$ITEMS_PATH/$itemId/spare-count") {
                userIdHeader(userId)
                setBody(request)
            }.body<ApiResponse<ItemResponse>>()
            .requireData()
    }

    override suspend fun deleteItem(itemId: Long) {
        val userId = userIdProvider.get()

        httpClient.delete("$ITEMS_PATH/$itemId") {
            userIdHeader(userId)
        }
    }

    override suspend fun getReplacementHistories(
        itemId: Long,
        limit: Int?,
    ): List<ReplacementHistoryResponse> {
        val userId = userIdProvider.get()

        return httpClient
            .get("$ITEMS_PATH/$itemId/replacements") {
                userIdHeader(userId)
                limit?.let { value -> parameter("limit", value) }
            }.body<ApiResponse<List<ReplacementHistoryResponse>>>()
            .requireData()
    }

    override suspend fun createReplacement(
        itemId: Long,
        request: CreateReplacementRequest,
    ): ItemResponse {
        val userId = userIdProvider.get()

        return httpClient
            .post("$ITEMS_PATH/$itemId/replacements") {
                userIdHeader(userId)
                setBody(request)
            }.body<ApiResponse<ItemResponse>>()
            .requireData()
    }
}

private const val ITEMS_PATH = "items"
