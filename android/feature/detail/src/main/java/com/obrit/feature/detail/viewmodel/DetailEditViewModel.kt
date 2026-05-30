@file:Suppress("LongMethod")

package com.obrit.feature.detail.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.obrit.shared.data.repository.CategoryRepository
import com.obrit.obrit.shared.data.repository.ItemRepository
import com.obrit.obrit.shared.model.items.Item
import com.obrit.obrit.shared.model.items.PatchItemParams
import com.obrit.obrit.shared.model.items.ReplacementHistory
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

class DetailEditViewModel internal constructor(
    private val itemRepository: ItemRepository,
    private val categoryRepository: CategoryRepository,
) : BaseContainerHost<DetailEditUiState, DetailEditSideEffect>() {
    override val container = container<DetailEditUiState, DetailEditSideEffect>(DetailEditUiState.Loading)

    private var editOperationGeneration = 0L

    fun load(consumableId: Long) =
        intent {
            val loadGeneration = nextEditOperationGeneration()
            reduce {
                DetailEditUiState.Loading
            }

            itemRepository
                .getItems()
                .onSuccess { items ->
                    if (!isCurrentEditOperation(loadGeneration)) {
                        return@onSuccess
                    }

                    val item = items.firstOrNull { candidate -> candidate.id == consumableId }

                    if (item == null) {
                        reduce {
                            DetailEditUiState.NotFound
                        }
                        return@onSuccess
                    }

                    val replacementHistories =
                        itemRepository
                            .getReplacementHistories(itemId = consumableId)
                            .getOrElse { emptyList() }
                    val representativeImageUrl =
                        categoryRepository
                            .getCategories()
                            .getOrNull()
                            ?.firstOrNull { category -> category.id == item.categoryId }
                            ?.iconUrl
                            ?.takeIf { imageUrl -> imageUrl.isNotBlank() }

                    if (!isCurrentEditOperation(loadGeneration)) {
                        return@onSuccess
                    }

                    reduce {
                        item.toDetailEditSuccess(
                            existingNames =
                                items
                                    .filterNot { candidate -> candidate.id == item.id }
                                    .map { candidate -> candidate.name },
                            replacementHistories = replacementHistories,
                            representativeImageUrl = representativeImageUrl,
                        )
                    }
                }.onFailure {
                    if (!isCurrentEditOperation(loadGeneration)) {
                        return@onFailure
                    }

                    reduce {
                        DetailEditUiState.LoadFailed.General
                    }
                }
        }

    fun save(
        consumableId: Long,
        name: String,
        replacementIntervalDays: Int,
    ) = intent {
        val currentState = state as? DetailEditUiState.Success ?: return@intent
        if (currentState.consumableId != consumableId || currentState.isSaveProcessing) {
            return@intent
        }

        val saveGeneration = nextEditOperationGeneration()
        reduce {
            currentState.copy(isSaveProcessing = true)
        }

        val saveResult =
            itemRepository.patchItem(
                PatchItemParams(
                    itemId = consumableId,
                    name = name,
                    replacementIntervalDays = replacementIntervalDays,
                ),
            )
        if (
            !isCurrentEditOperation(saveGeneration) ||
            (state as? DetailEditUiState.Success)?.consumableId != consumableId
        ) {
            return@intent
        }

        saveResult
            .onSuccess {
                postSideEffect(
                    DetailEditSideEffect.EditCompleted(
                        consumableId = consumableId,
                        name = name,
                        replacementIntervalDays = replacementIntervalDays,
                    ),
                )
            }.onFailure {
                val latestState = state as? DetailEditUiState.Success ?: return@onFailure
                reduce {
                    latestState.copy(isSaveProcessing = false)
                }
                postSideEffect(DetailEditSideEffect.ShowSaveFailed)
            }
    }

    private fun nextEditOperationGeneration(): Long {
        editOperationGeneration += 1
        return editOperationGeneration
    }

    private fun isCurrentEditOperation(operationGeneration: Long): Boolean = operationGeneration == editOperationGeneration
}

sealed interface DetailEditUiState {
    @Immutable
    data class Success(
        val consumableId: Long,
        val itemName: String,
        val categoryName: String,
        val replacementIntervalDays: Int,
        val averageReplacementIntervalDays: Int,
        val representativeImageUrl: String?,
        val existingNames: List<String>,
        val isSaveProcessing: Boolean = false,
    ) : DetailEditUiState

    data object Loading : DetailEditUiState

    sealed interface LoadFailed : DetailEditUiState {
        data object General : LoadFailed
    }

    data object NotFound : LoadFailed
}

sealed interface DetailEditSideEffect {
    data class EditCompleted(
        val consumableId: Long,
        val name: String,
        val replacementIntervalDays: Int,
    ) : DetailEditSideEffect

    data object ShowSaveFailed : DetailEditSideEffect
}

private fun Item.toDetailEditSuccess(
    existingNames: List<String>,
    replacementHistories: List<ReplacementHistory>,
    representativeImageUrl: String?,
): DetailEditUiState.Success =
    DetailEditUiState.Success(
        consumableId = id,
        itemName = name,
        categoryName = categoryName,
        replacementIntervalDays = replacementIntervalDays.coerceAtLeast(0),
        averageReplacementIntervalDays =
            replacementHistories
                .toAverageReplacementIntervalDays()
                ?: replacementIntervalDays.coerceAtLeast(0),
        representativeImageUrl = representativeImageUrl,
        existingNames = existingNames,
    )

private fun List<ReplacementHistory>.toAverageReplacementIntervalDays(): Int? {
    val completedUsageDays =
        mapNotNull { history -> history.replacedDate.value.toLocalDateOrNull() }
            .distinct()
            .sorted()
            .zipWithNext { startDate, endDate ->
                ChronoUnit
                    .DAYS
                    .between(startDate, endDate)
                    .coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong())
                    .toInt()
                    .coerceAtLeast(0)
            }

    return completedUsageDays
        .takeIf { usageDays -> usageDays.isNotEmpty() }
        ?.average()
        ?.roundToInt()
}

private fun String.toLocalDateOrNull(): LocalDate? =
    runCatching {
        LocalDate.parse(take(ISO_LOCAL_DATE_LENGTH))
    }.getOrNull()

private const val ISO_LOCAL_DATE_LENGTH = 10
