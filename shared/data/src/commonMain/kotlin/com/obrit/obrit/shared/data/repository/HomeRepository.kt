package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.home.HomeBucketGroup
import com.obrit.obrit.shared.model.home.HomeItemCursorSlice
import com.obrit.obrit.shared.model.home.HomeItemsParams
import com.obrit.obrit.shared.model.home.HomeOverallStatus
import com.obrit.obrit.shared.model.home.MyStatusSummary

interface HomeRepository {
    suspend fun getOverallStatus(): Result<HomeOverallStatus>

    suspend fun getMyStatusSummary(): Result<MyStatusSummary>

    suspend fun getItems(params: HomeItemsParams = HomeItemsParams()): Result<HomeItemCursorSlice>

    suspend fun getBuckets(): Result<List<HomeBucketGroup>>
}
