package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.response.home.HomeBucketsResponse
import com.obrit.obrit.shared.network.response.home.MyStatusSummaryResponse
import com.obrit.obrit.shared.network.response.home.OverallStatusResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class HomeRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : HomeRemoteDataSource {
    override suspend fun getOverallStatus(): OverallStatusResponse =
        httpClient
            .get("$HOME_PATH/overall-status")
            .body()

    override suspend fun getMyStatusSummary(): MyStatusSummaryResponse =
        httpClient
            .get("$HOME_PATH/my-summary")
            .body()

    override suspend fun getBuckets(): HomeBucketsResponse =
        httpClient
            .get("$HOME_PATH/buckets")
            .body()
}

private const val HOME_PATH = "home"
