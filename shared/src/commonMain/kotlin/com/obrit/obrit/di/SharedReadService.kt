package com.obrit.obrit.di

import com.obrit.obrit.shared.model.categories.Category
import com.obrit.obrit.shared.model.categories.CategoryIcon
import com.obrit.obrit.shared.model.home.HomeBucketGroup
import com.obrit.obrit.shared.model.home.HomeItemCursorSlice
import com.obrit.obrit.shared.model.home.HomeItemsParams
import com.obrit.obrit.shared.model.home.HomeOverallStatus
import com.obrit.obrit.shared.model.home.MyStatusSummary
import com.obrit.obrit.shared.model.items.Item
import com.obrit.obrit.shared.model.items.ReplacementHistory

/**
 * Swift-facing read facade that keeps Kotlin Result and Koin access out of iOS presentation code.
 */
class SharedReadService(
    private val repositoryProvider: SharedRepositoryProvider = SharedRepositoryProvider(),
) {
    @Throws(Throwable::class)
    suspend fun getCategories(): List<Category> =
        repositoryProvider.categoryRepository().getCategories().getOrThrow()

    @Throws(Throwable::class)
    suspend fun getCategoryIcons(): List<CategoryIcon> =
        repositoryProvider.categoryRepository().getCategoryIcons().getOrThrow()

    @Throws(Throwable::class)
    suspend fun getItems(): List<Item> =
        repositoryProvider.itemRepository().getItems().getOrThrow()

    @Throws(Throwable::class)
    suspend fun getReplacementHistories(
        itemId: Long,
        limit: Int?,
    ): List<ReplacementHistory> =
        repositoryProvider.itemRepository()
            .getReplacementHistories(itemId = itemId, limit = limit)
            .getOrThrow()

    @Throws(Throwable::class)
    suspend fun getOverallStatus(): HomeOverallStatus =
        repositoryProvider.homeRepository().getOverallStatus().getOrThrow()

    @Throws(Throwable::class)
    suspend fun getMyStatusSummary(): MyStatusSummary =
        repositoryProvider.homeRepository().getMyStatusSummary().getOrThrow()

    @Throws(Throwable::class)
    suspend fun getHomeItems(params: HomeItemsParams): HomeItemCursorSlice =
        repositoryProvider.homeRepository().getItems(params = params).getOrThrow()

    @Throws(Throwable::class)
    suspend fun getBuckets(): List<HomeBucketGroup> =
        repositoryProvider.homeRepository().getBuckets().getOrThrow()
}
