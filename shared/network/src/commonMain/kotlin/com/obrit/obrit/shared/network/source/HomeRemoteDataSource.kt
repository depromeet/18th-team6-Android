package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.request.home.HomeItemsRequest
import com.obrit.obrit.shared.network.response.home.CursorSliceResponseHomeItemCard
import com.obrit.obrit.shared.network.response.home.HomeBucketsResponse
import com.obrit.obrit.shared.network.response.home.MyStatusSummaryResponse
import com.obrit.obrit.shared.network.response.home.OverallStatusResponse

interface HomeRemoteDataSource {
    suspend fun getOverallStatus(): OverallStatusResponse

    suspend fun getMyStatusSummary(): MyStatusSummaryResponse

    suspend fun getItems(request: HomeItemsRequest): CursorSliceResponseHomeItemCard

    suspend fun getBuckets(): HomeBucketsResponse
}
