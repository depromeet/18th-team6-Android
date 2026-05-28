package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.client.userIdHeader
import com.obrit.obrit.shared.network.config.NETWORK_BASE_URL
import com.obrit.obrit.shared.network.config.UserIdProvider
import com.obrit.obrit.shared.network.request.home.HomeItemsRequest
import com.obrit.obrit.shared.network.response.ApiResponse
import com.obrit.obrit.shared.network.response.home.CursorSliceResponseHomeItemCard
import com.obrit.obrit.shared.network.response.home.HomeBucketsResponse
import com.obrit.obrit.shared.network.response.home.MyStatusSummaryResponse
import com.obrit.obrit.shared.network.response.home.OverallStatusResponse
import com.obrit.obrit.shared.network.response.requireData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class HomeRemoteDataSourceImpl(
    private val httpClient: HttpClient,
    private val userIdProvider: UserIdProvider,
) : HomeRemoteDataSource {
    override suspend fun getOverallStatus(): OverallStatusResponse {
        val userId = userIdProvider.get()
        return httpClient
            .get("$HOME_PATH/overall-status") {
                userIdHeader(userId)
            }.body<ApiResponse<OverallStatusResponse>>()
            .requireData()
    }

    override suspend fun getMyStatusSummary(): MyStatusSummaryResponse {
        val userId = userIdProvider.get()

        return httpClient
            .get("$HOME_PATH/my-summary") {
                userIdHeader(userId)
            }.body<ApiResponse<MyStatusSummaryResponse>>()
            .requireData()
    }

    override suspend fun getItems(request: HomeItemsRequest): CursorSliceResponseHomeItemCard {
        val userId = userIdProvider.get()
        val queryParams =
            buildList {
                request.order?.let { add("order=$it") }
                request.dDay?.let { add("dDay=$it") }
                request.spareQuantity?.let { add("spareQuantity=$it") }
                request.cursor?.let { add("cursor=$it") }
                request.size?.let { add("size=$it") }
            }.joinToString("&")
        println("getItems URL: ${NETWORK_BASE_URL}${HOME_PATH}/items${if (queryParams.isNotEmpty()) "?$queryParams" else ""}")

        return httpClient
            .get("$HOME_PATH/items") {
                userIdHeader(userId)
                request.order?.let { value -> parameter("order", value) }
                request.dDay?.let { value -> parameter("dDay", value) }
                request.spareQuantity?.let { value -> parameter("spareQuantity", value) }
                request.cursor?.let { value -> parameter("cursor", value) }
                request.size?.let { value -> parameter("size", value) }
            }.body<ApiResponse<CursorSliceResponseHomeItemCard>>()
            .requireData()
    }

    override suspend fun getBuckets(): HomeBucketsResponse {
        val userId = userIdProvider.get()
        println("getBuckets URL: ${NETWORK_BASE_URL}${HOME_PATH}/buckets")

        return httpClient
            .get("$HOME_PATH/buckets") {
                userIdHeader(userId)
            }.body<ApiResponse<HomeBucketsResponse>>()
            .requireData()
    }
}

private const val HOME_PATH = "home"
