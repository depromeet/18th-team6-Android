package com.obrit.obrit.di

import com.obrit.obrit.shared.model.ReplacementDate
import com.obrit.obrit.shared.model.categories.Category
import com.obrit.obrit.shared.model.items.CreateItemParams
import com.obrit.obrit.shared.model.items.Item
import com.obrit.obrit.shared.model.items.LastReplacementPeriod
import com.obrit.obrit.shared.model.items.PatchItemParams

/**
 * Swift-facing write facade that unwraps Kotlin Result before crossing the K/N boundary.
 */
class SharedWriteService(
    private val repositoryProvider: SharedRepositoryProvider = SharedRepositoryProvider(),
) {
    @Throws(Throwable::class)
    suspend fun createCategory(
        name: String,
        iconId: Long,
    ): Category =
        repositoryProvider
            .categoryRepository()
            .createCategory(name = name, iconId = iconId)
            .getOrThrow()

    @Throws(Throwable::class)
    suspend fun createItem(params: CreateItemParams): Item =
        logged(
            event = "SharedWriteService.createItem",
            details =
                "categoryId=${params.categoryId} count=${params.count} " +
                    "lastReplacementPeriod=${params.lastReplacementPeriod?.value} interval=${params.replacementIntervalDays}",
        ) {
            repositoryProvider
                .itemRepository()
                .createItem(params = params)
                .getOrThrow()
        }

    @Throws(Throwable::class)
    suspend fun createItem(
        categoryId: Long,
        name: String,
        count: Int?,
        lastReplacementPeriod: String?,
        replacementIntervalDays: Int?,
    ): Item =
        createItem(
            CreateItemParams(
                categoryId = categoryId,
                name = name,
                count = count,
                lastReplacementPeriod =
                    lastReplacementPeriod?.let { value ->
                        checkNotNull(LastReplacementPeriod.fromValue(value)) {
                            "Unknown lastReplacementPeriod: $value"
                        }
                    },
                replacementIntervalDays = replacementIntervalDays,
            ),
        )

    @Throws(Throwable::class)
    suspend fun patchSpareCount(
        itemId: Long,
        count: Int,
    ): Item =
        logged(
            event = "SharedWriteService.patchSpareCount",
            details = "itemId=$itemId count=$count",
        ) {
            repositoryProvider
                .itemRepository()
                .patchSpareCount(itemId = itemId, count = count)
                .getOrThrow()
        }

    @Throws(Throwable::class)
    suspend fun patchItem(
        itemId: Long,
        name: String?,
        count: Int?,
        lastReplacedDate: String?,
        replacementIntervalDays: Int?,
    ): Item =
        repositoryProvider
            .itemRepository()
            .patchItem(
                PatchItemParams(
                    itemId = itemId,
                    name = name,
                    count = count,
                    lastReplacedDate = lastReplacedDate?.let(::ReplacementDate),
                    replacementIntervalDays = replacementIntervalDays,
                ),
            ).getOrThrow()

    @Throws(Throwable::class)
    suspend fun createReplacement(
        itemId: Long,
        replacedDate: String?,
    ): Item =
        logged(
            event = "SharedWriteService.createReplacement",
            details = "itemId=$itemId replacedDate=$replacedDate",
        ) {
            repositoryProvider
                .itemRepository()
                .createReplacement(
                    itemId = itemId,
                    replacedDate = replacedDate?.let(::ReplacementDate),
                ).getOrThrow()
        }

    @Throws(Throwable::class)
    suspend fun deleteItem(itemId: Long) {
        logged(
            event = "SharedWriteService.deleteItem",
            details = "itemId=$itemId",
        ) {
            repositoryProvider
                .itemRepository()
                .deleteItem(itemId = itemId)
                .getOrThrow()
        }
    }

    private suspend fun <T> logged(
        event: String,
        details: String,
        block: suspend () -> T,
    ): T {
        SharedLog.enter(scope = LOG_SCOPE, event = event, details = details)
        val result = runCatching { block() }

        result.onSuccess {
            SharedLog.success(scope = LOG_SCOPE, event = event, details = details)
        }
        result.onFailure { throwable ->
            SharedLog.failure(scope = LOG_SCOPE, event = event, throwable = throwable, details = details)
        }

        return result.getOrThrow()
    }

    private companion object {
        const val LOG_SCOPE = "SharedWriteService"
    }
}
