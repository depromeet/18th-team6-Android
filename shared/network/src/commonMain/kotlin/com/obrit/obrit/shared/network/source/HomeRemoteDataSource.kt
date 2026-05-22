package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.response.home.HomeBucketsResponse
import com.obrit.obrit.shared.network.response.home.MyStatusSummaryResponse
import com.obrit.obrit.shared.network.response.home.OverallStatusResponse

interface HomeRemoteDataSource {
    suspend fun getOverallStatus(): OverallStatusResponse

    suspend fun getMyStatusSummary(): MyStatusSummaryResponse

    suspend fun getBuckets(): HomeBucketsResponse
}
