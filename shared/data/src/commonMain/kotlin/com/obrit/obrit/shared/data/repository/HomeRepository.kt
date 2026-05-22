package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.home.HomeBucketGroup
import com.obrit.obrit.shared.model.home.HomeOverallStatus
import com.obrit.obrit.shared.model.home.MyStatusSummary

interface HomeRepository {
    suspend fun getOverallStatus(): Result<HomeOverallStatus>

    suspend fun getMyStatusSummary(): Result<MyStatusSummary>

    suspend fun getBuckets(): Result<List<HomeBucketGroup>>
}
