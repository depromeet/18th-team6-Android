package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.home.HomeBucketGroup
import com.obrit.obrit.shared.model.home.HomeItemCursorSlice
import com.obrit.obrit.shared.model.home.HomeItemsParams
import com.obrit.obrit.shared.model.home.HomeOverallStatus
import com.obrit.obrit.shared.model.home.MyStatusSummary
import com.obrit.obrit.shared.network.error.runCatchingWith
import com.obrit.obrit.shared.network.request.home.HomeItemsRequest
import com.obrit.obrit.shared.network.response.home.toHomeBucketGroups
import com.obrit.obrit.shared.network.response.home.toHomeItemCursorSlice
import com.obrit.obrit.shared.network.response.home.toHomeOverallStatus
import com.obrit.obrit.shared.network.response.home.toMyStatusSummary
import com.obrit.obrit.shared.network.source.HomeRemoteDataSource

internal class HomeRepositoryImpl(
    private val homeRemoteDataSource: HomeRemoteDataSource,
) : HomeRepository {
    override suspend fun getOverallStatus(): Result<HomeOverallStatus> =
        runCatchingWith {
            homeRemoteDataSource.getOverallStatus().toHomeOverallStatus()
        }

    override suspend fun getMyStatusSummary(): Result<MyStatusSummary> =
        runCatchingWith {
            homeRemoteDataSource.getMyStatusSummary().toMyStatusSummary()
        }

    override suspend fun getItems(params: HomeItemsParams): Result<HomeItemCursorSlice> =
        runCatchingWith {
            homeRemoteDataSource
                .getItems(
                    HomeItemsRequest(
                        order = params.order?.name,
                        dDay = params.dDay,
                        spareQuantity = params.spareQuantity,
                        cursor = params.cursor,
                        size = params.size,
                    ),
                ).toHomeItemCursorSlice()
        }

    override suspend fun getBuckets(): Result<List<HomeBucketGroup>> =
        runCatchingWith {
            homeRemoteDataSource.getBuckets().toHomeBucketGroups()
        }
}
