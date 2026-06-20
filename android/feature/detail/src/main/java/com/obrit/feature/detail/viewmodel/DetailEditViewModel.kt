@file:Suppress("LongMethod")

package com.obrit.feature.detail.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.obrit.shared.data.repository.CategoryRepository
import com.obrit.obrit.shared.data.repository.ItemRepository
import com.obrit.obrit.shared.model.categories.CategoryIcon
import com.obrit.obrit.shared.model.items.ItemDetail
import com.obrit.obrit.shared.model.items.PatchItemParams
import com.obrit.obrit.shared.model.items.error.GetItemError
import org.orbitmvi.orbit.viewmodel.container
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
                .getItem(consumableId)
                .onSuccess { itemDetail ->
                    if (!isCurrentEditOperation(loadGeneration)) {
                        return@onSuccess
                    }

                    val items = itemRepository.getItems().getOrNull()
                    val iconsResult = categoryRepository.getCategoryIcons()

                    if (!isCurrentEditOperation(loadGeneration)) {
                        return@onSuccess
                    }

                    iconsResult
                        .onSuccess { icons ->
                            reduce {
                                itemDetail.toDetailEditSuccess(
                                    existingNames =
                                        items
                                            .orEmpty()
                                            .filterNot { candidate -> candidate.id == itemDetail.id }
                                            .map { candidate -> candidate.name },
                                    representativeIcons = icons,
                                )
                            }
                        }.onFailure {
                            reduce {
                                DetailEditUiState.LoadFailed.General
                            }
                        }
                }.onFailure { error ->
                    if (!isCurrentEditOperation(loadGeneration)) {
                        return@onFailure
                    }

                    reduce {
                        if (error.isNotFound()) {
                            DetailEditUiState.NotFound
                        } else {
                            DetailEditUiState.LoadFailed.General
                        }
                    }
                }
        }

    fun save(
        consumableId: Long,
        name: String,
        replacementIntervalDays: Int,
        representativeIconId: Long?,
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
                    iconId = representativeIconId,
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
                        representativeIconId = representativeIconId,
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
        val representativeIcons: List<CategoryIcon>,
        val selectedRepresentativeIconId: Long?,
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
        val representativeIconId: Long?,
    ) : DetailEditSideEffect

    data object ShowSaveFailed : DetailEditSideEffect
}

private fun ItemDetail.toDetailEditSuccess(
    existingNames: List<String>,
    representativeIcons: List<CategoryIcon>,
): DetailEditUiState.Success {
    val representativeImageUrl = iconUrl?.takeIf { imageUrl -> imageUrl.isNotBlank() }

    return DetailEditUiState.Success(
        consumableId = id,
        itemName = name,
        categoryName = category.name,
        replacementIntervalDays = recommendedCycleDays.coerceAtLeast(0),
        averageReplacementIntervalDays =
            myAverageCycleDays
                .takeIf { average -> average > 0.0 }
                ?.roundToInt()
                ?: recommendedCycleDays.coerceAtLeast(0),
        representativeImageUrl = representativeImageUrl,
        representativeIcons = representativeIcons,
        selectedRepresentativeIconId =
            representativeIcons
                .firstOrNull { icon -> icon.url == representativeImageUrl }
                ?.id,
        existingNames = existingNames,
    )
}

private fun Throwable.isNotFound(): Boolean = this is GetItemError.NotFound
