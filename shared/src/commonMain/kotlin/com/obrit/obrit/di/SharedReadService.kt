package com.obrit.obrit.di

import com.obrit.obrit.shared.model.categories.Category
import com.obrit.obrit.shared.model.categories.CategoryIcon
import com.obrit.obrit.shared.model.home.HomeBucketGroup
import com.obrit.obrit.shared.model.home.HomeItemCursorSlice
import com.obrit.obrit.shared.model.home.HomeItemsParams
import com.obrit.obrit.shared.model.home.HomeOverallStatus
import com.obrit.obrit.shared.model.home.MyStatusSummary
import com.obrit.obrit.shared.model.items.Item
import com.obrit.obrit.shared.model.items.ItemDetail
import com.obrit.obrit.shared.model.items.ReplacementHistory

/**
 * Swift-facing read facade that keeps Kotlin Result and Koin access out of iOS presentation code.
 */
class SharedReadService(
    private val repositoryProvider: SharedRepositoryProvider = SharedRepositoryProvider(),
) {
    @Throws(Throwable::class)
    suspend fun getCategories(): List<Category> =
        logged(
            event = "SharedReadService.getCategories",
        ) {
            val categories = repositoryProvider.categoryRepository().getCategories().getOrThrow()
            categories to "count=${categories.size}"
        }

    @Throws(Throwable::class)
    suspend fun getCategoryIcons(): List<CategoryIcon> = repositoryProvider.categoryRepository().getCategoryIcons().getOrThrow()

    @Throws(Throwable::class)
    suspend fun getItems(): List<Item> =
        logged(
            event = "SharedReadService.getItems",
        ) {
            val items = repositoryProvider.itemRepository().getItems().getOrThrow()
            items to "count=${items.size}"
        }

    @Throws(Throwable::class)
    suspend fun getItem(itemId: Long): ItemDetail =
        logged(
            event = "SharedReadService.getItem",
            enterDetails = "itemId=$itemId",
        ) {
            val item = repositoryProvider.itemRepository().getItem(itemId).getOrThrow()
            item to "itemId=${item.id}"
        }

    @Throws(Throwable::class)
    suspend fun getReplacementHistories(
        itemId: Long,
        limit: Int?,
    ): List<ReplacementHistory> =
        repositoryProvider
            .itemRepository()
            .getReplacementHistories(itemId = itemId, limit = limit)
            .getOrThrow()

    @Throws(Throwable::class)
    suspend fun getOverallStatus(): HomeOverallStatus =
        logged(
            event = "SharedReadService.getOverallStatus",
        ) {
            val status = repositoryProvider.homeRepository().getOverallStatus().getOrThrow()
            status to "overall=${status.overall}"
        }

    @Throws(Throwable::class)
    suspend fun getMyStatusSummary(): MyStatusSummary =
        logged(
            event = "SharedReadService.getMyStatusSummary",
        ) {
            val summary = repositoryProvider.homeRepository().getMyStatusSummary().getOrThrow()
            summary to "totalCount=${summary.totalCount} needReplaceCount=${summary.needReplaceCount}"
        }

    @Throws(Throwable::class)
    suspend fun getHomeItems(params: HomeItemsParams): HomeItemCursorSlice =
        logged(
            event = "SharedReadService.getHomeItems",
            enterDetails = "cursor=${params.cursor} size=${params.size} order=${params.order}",
        ) {
            val slice = repositoryProvider.homeRepository().getItems(params = params).getOrThrow()
            slice to
                "cursor=${params.cursor} size=${params.size} order=${params.order} " +
                "count=${slice.content.size} hasNext=${slice.hasNext}"
        }

    @Throws(Throwable::class)
    suspend fun getBuckets(): List<HomeBucketGroup> = repositoryProvider.homeRepository().getBuckets().getOrThrow()

    private suspend fun <T> logged(
        event: String,
        enterDetails: String? = null,
        block: suspend () -> Pair<T, String>,
    ): T {
        SharedLog.enter(scope = LOG_SCOPE, event = event, details = enterDetails.orEmpty())
        val result = runCatching { block() }

        result.onSuccess { (_, successDetails) ->
            SharedLog.success(scope = LOG_SCOPE, event = event, details = successDetails)
        }
        result.onFailure { throwable ->
            SharedLog.failure(scope = LOG_SCOPE, event = event, throwable = throwable, details = enterDetails.orEmpty())
        }

        return result.getOrThrow().first
    }

    private companion object {
        const val LOG_SCOPE = "SharedReadService"
    }
}
