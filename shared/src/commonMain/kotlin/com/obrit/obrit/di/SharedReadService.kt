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
    suspend fun getCategories(): List<Category> {
        val event = "SharedReadService.getCategories"
        SharedLog.enter(scope = LOG_SCOPE, event = event)
        return try {
            val categories = repositoryProvider.categoryRepository().getCategories().getOrThrow()
            SharedLog.success(scope = LOG_SCOPE, event = event, details = "count=${categories.size}")
            categories
        } catch (throwable: Throwable) {
            SharedLog.failure(scope = LOG_SCOPE, event = event, throwable = throwable)
            throw throwable
        }
    }

    @Throws(Throwable::class)
    suspend fun getCategoryIcons(): List<CategoryIcon> = repositoryProvider.categoryRepository().getCategoryIcons().getOrThrow()

    @Throws(Throwable::class)
    suspend fun getItems(): List<Item> {
        val event = "SharedReadService.getItems"
        SharedLog.enter(scope = LOG_SCOPE, event = event)
        return try {
            val items = repositoryProvider.itemRepository().getItems().getOrThrow()
            SharedLog.success(scope = LOG_SCOPE, event = event, details = "count=${items.size}")
            items
        } catch (throwable: Throwable) {
            SharedLog.failure(scope = LOG_SCOPE, event = event, throwable = throwable)
            throw throwable
        }
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
    suspend fun getOverallStatus(): HomeOverallStatus {
        val event = "SharedReadService.getOverallStatus"
        SharedLog.enter(scope = LOG_SCOPE, event = event)
        return try {
            val status = repositoryProvider.homeRepository().getOverallStatus().getOrThrow()
            SharedLog.success(scope = LOG_SCOPE, event = event, details = "overall=${status.overall}")
            status
        } catch (throwable: Throwable) {
            SharedLog.failure(scope = LOG_SCOPE, event = event, throwable = throwable)
            throw throwable
        }
    }

    @Throws(Throwable::class)
    suspend fun getMyStatusSummary(): MyStatusSummary {
        val event = "SharedReadService.getMyStatusSummary"
        SharedLog.enter(scope = LOG_SCOPE, event = event)
        return try {
            val summary = repositoryProvider.homeRepository().getMyStatusSummary().getOrThrow()
            SharedLog.success(
                scope = LOG_SCOPE,
                event = event,
                details = "totalCount=${summary.totalCount} needReplaceCount=${summary.needReplaceCount}",
            )
            summary
        } catch (throwable: Throwable) {
            SharedLog.failure(scope = LOG_SCOPE, event = event, throwable = throwable)
            throw throwable
        }
    }

    @Throws(Throwable::class)
    suspend fun getHomeItems(params: HomeItemsParams): HomeItemCursorSlice {
        val event = "SharedReadService.getHomeItems"
        val details = "cursor=${params.cursor} size=${params.size} order=${params.order}"
        SharedLog.enter(scope = LOG_SCOPE, event = event, details = details)
        return try {
            val slice = repositoryProvider.homeRepository().getItems(params = params).getOrThrow()
            SharedLog.success(
                scope = LOG_SCOPE,
                event = event,
                details = "$details count=${slice.content.size} hasNext=${slice.hasNext}",
            )
            slice
        } catch (throwable: Throwable) {
            SharedLog.failure(scope = LOG_SCOPE, event = event, throwable = throwable, details = details)
            throw throwable
        }
    }

    @Throws(Throwable::class)
    suspend fun getBuckets(): List<HomeBucketGroup> = repositoryProvider.homeRepository().getBuckets().getOrThrow()

    private companion object {
        const val LOG_SCOPE = "SharedReadService"
    }
}
