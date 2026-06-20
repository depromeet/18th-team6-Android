@file:Suppress("TooManyFunctions")

package com.obrit.feature.detail.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.obrit.shared.data.repository.AgentRepository
import com.obrit.obrit.shared.data.repository.ItemRepository
import com.obrit.obrit.shared.model.ReplacementDate
import com.obrit.obrit.shared.model.agents.Agent
import com.obrit.obrit.shared.model.items.Item
import com.obrit.obrit.shared.model.items.ItemDetail
import com.obrit.obrit.shared.model.items.ItemDetailReplacement
import com.obrit.obrit.shared.model.items.ItemDetailStatus
import com.obrit.obrit.shared.model.items.ReplacementHistory
import com.obrit.obrit.shared.model.items.error.GetItemError
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class DetailViewModel internal constructor(
    private val agentRepository: AgentRepository,
    private val itemRepository: ItemRepository,
) : BaseContainerHost<DetailUiState, DetailSideEffect>() {
    override val container = container<DetailUiState, DetailSideEffect>(DetailUiState.Loading)

    private var dateProvider: DetailDateProvider = SystemDetailDateProvider
    private var detailLoadGeneration = 0L

    internal fun setDateProviderForTest(dateProvider: DetailDateProvider) {
        this.dateProvider = dateProvider
    }

    fun loadAgent(id: Int) =
        intent {
            val loadGeneration = nextDetailLoadGeneration()
            reduce {
                DetailUiState.Loading
            }

            val result = agentRepository.getAgent(id)
            if (isCurrentDetailLoad(loadGeneration)) {
                result
                    .onSuccess { agent ->
                        reduce {
                            agent.toLegacySuccess()
                        }
                    }.onFailure {
                        reduce {
                            DetailUiState.LoadFailed.General
                        }
                    }
            }
        }

    fun loadConsumable(id: Long) =
        intent {
            val loadGeneration = nextDetailLoadGeneration()
            loadDetailIntoState(
                consumableId = id,
                loadGeneration = loadGeneration,
                showLoading = true,
                keepCurrentOnFailure = false,
            )
        }

    fun retryLoad(consumableId: Long) = loadConsumable(consumableId)

    fun onBackClick() =
        intent {
            postSideEffect(DetailSideEffect.NavigateBack)
        }

    fun onMoreClick() =
        intent {
            val currentState = state as? DetailUiState.ConsumableSuccess ?: return@intent
            if (!currentState.canOpenConsumableSideEffect) {
                return@intent
            }

            postSideEffect(DetailSideEffect.OpenMoreMenu)
        }

    fun onMoreMenuAction(action: DetailMenuAction) =
        intent {
            val currentState = state as? DetailUiState.ConsumableSuccess ?: return@intent

            when (action) {
                DetailMenuAction.EDIT -> {
                    if (!currentState.canOpenConsumableSideEffect) {
                        return@intent
                    }
                    postSideEffect(DetailSideEffect.NavigateToEdit(currentState.consumableId))
                }
                DetailMenuAction.DELETE -> {
                    if (!currentState.canStartConsumableMutation) {
                        return@intent
                    }

                    reduce {
                        currentState
                            .copy(
                                isDeleteConfirmVisible = true,
                                pendingDeleteConsumableId = currentState.consumableId,
                            ).withActionAvailability(dateProvider.today())
                    }
                }
                DetailMenuAction.SPARE_MANAGEMENT -> {
                    if (!currentState.canOpenSpareManagement) {
                        return@intent
                    }
                    postSideEffect(DetailSideEffect.OpenSpareManagement(currentState.consumableId))
                }
            }
        }

    fun onEditClick() =
        intent {
            val currentState = state as? DetailUiState.ConsumableSuccess ?: return@intent
            if (!currentState.canOpenConsumableSideEffect) {
                return@intent
            }

            postSideEffect(DetailSideEffect.NavigateToEdit(currentState.consumableId))
        }

    fun onDeleteRequest() =
        intent {
            val currentState = state as? DetailUiState.ConsumableSuccess ?: return@intent
            if (!currentState.canStartConsumableMutation) {
                return@intent
            }

            reduce {
                currentState
                    .copy(
                        isDeleteConfirmVisible = true,
                        pendingDeleteConsumableId = currentState.consumableId,
                    ).withActionAvailability(dateProvider.today())
            }
        }

    fun onDeleteCancel() =
        intent {
            reduceOn<DetailUiState.ConsumableSuccess> {
                state
                    .copy(
                        isDeleteConfirmVisible = false,
                        pendingDeleteConsumableId = null,
                    ).withActionAvailability(dateProvider.today())
            }
        }

    fun onDeleteConfirm(consumableId: Long? = null) =
        intent {
            val currentState = state as? DetailUiState.ConsumableSuccess ?: return@intent
            if (!currentState.canConfirmDelete(consumableId)) {
                return@intent
            }

            val targetConsumableId = currentState.pendingDeleteConsumableId ?: return@intent
            val mutationGeneration = nextDetailLoadGeneration()
            reduce {
                currentState
                    .copy(
                        isDeleteProcessing = true,
                        isDeleteConfirmVisible = false,
                    ).withActionAvailability(dateProvider.today())
            }

            val deleteResult = itemRepository.deleteItem(targetConsumableId)
            if (!isCurrentConsumableMutation(mutationGeneration, targetConsumableId)) {
                return@intent
            }

            deleteResult
                .onSuccess {
                    postSideEffect(DetailSideEffect.NavigateAfterDelete)
                }.onFailure {
                    val latestState = state as? DetailUiState.ConsumableSuccess ?: return@onFailure
                    reduce {
                        latestState
                            .copy(
                                isDeleteProcessing = false,
                                isDeleteConfirmVisible = false,
                                pendingDeleteConsumableId = null,
                            ).withActionAvailability(dateProvider.today())
                    }
                    postSideEffect(DetailSideEffect.ShowSnackbar(DetailUserMessage.DELETE_FAILED))
                }
        }

    fun onSpareManagementClick() =
        intent {
            val currentState = state as? DetailUiState.ConsumableSuccess ?: return@intent
            if (!currentState.canOpenSpareManagement) {
                return@intent
            }
            postSideEffect(DetailSideEffect.OpenSpareManagement(currentState.consumableId))
        }

    fun onSpareSheetRequest() =
        intent {
            val currentState = state as? DetailUiState.ConsumableSuccess ?: return@intent
            if (!currentState.canOpenSpareManagement) {
                return@intent
            }
            postSideEffect(DetailSideEffect.ShowSpareSheet(currentState.consumableId))
        }

    @Suppress("LongMethod")
    fun onSpareCountCompleteClick(count: Int) =
        intent {
            val currentState = state as? DetailUiState.ConsumableSuccess ?: return@intent
            val today = dateProvider.today()
            if (!currentState.canStartConsumableMutation) {
                return@intent
            }

            val sanitizedCount = count.coerceIn(MIN_SPARE_COUNT, MAX_SPARE_COUNT)
            val mutationGeneration = nextDetailLoadGeneration()
            reduce {
                currentState
                    .copy(isSpareProcessing = true)
                    .withActionAvailability(today)
            }

            val updateResult =
                itemRepository.patchSpareCount(
                    itemId = currentState.consumableId,
                    count = sanitizedCount,
                )
            if (!isCurrentConsumableMutation(mutationGeneration, currentState.consumableId)) {
                return@intent
            }

            updateResult
                .onSuccess { updatedItem ->
                    val refreshedDetail = itemRepository.getItem(currentState.consumableId).getOrNull()
                    if (!isCurrentConsumableMutation(mutationGeneration, currentState.consumableId)) {
                        return@onSuccess
                    }

                    if (refreshedDetail != null) {
                        reduce {
                            refreshedDetail.toDetailSuccess(today)
                        }
                    } else {
                        val latestState = state as? DetailUiState.ConsumableSuccess ?: return@onSuccess
                        reduce {
                            latestState
                                .withSpareCount(updatedItem.count)
                                .withActionAvailability(today)
                        }
                    }
                    postSideEffect(DetailSideEffect.ShowSnackbar(DetailUserMessage.SPARE_UPDATE_SUCCEEDED))
                }.onFailure {
                    if (!isCurrentConsumableMutation(mutationGeneration, currentState.consumableId)) {
                        return@onFailure
                    }
                    val latestState = state as? DetailUiState.ConsumableSuccess ?: return@onFailure
                    reduce {
                        latestState
                            .copy(isSpareProcessing = false)
                            .withActionAvailability(today)
                    }
                    postSideEffect(DetailSideEffect.ShowSnackbar(DetailUserMessage.SPARE_UPDATE_FAILED))
                }
        }

    fun onReplaceCompleteClick() =
        intent {
            val currentState = state as? DetailUiState.ConsumableSuccess ?: return@intent
            val today = dateProvider.today()
            if (!currentState.canStartReplacement(today)) {
                return@intent
            }

            postSideEffect(
                DetailSideEffect.ShowReplacementCompletionDialog(
                    feedback = currentState.toPendingReplacementCompletionFeedback(today),
                ),
            )
        }

    @Suppress("LongMethod")
    fun onReplaceCompletionConfirm() =
        intent {
            val currentState = state as? DetailUiState.ConsumableSuccess ?: return@intent
            val today = dateProvider.today()
            if (!currentState.canStartReplacement(today)) {
                return@intent
            }

            val mutationGeneration = nextDetailLoadGeneration()
            reduce {
                currentState
                    .copy(
                        isReplaceProcessing = true,
                        isReplaceCtaEnabled = false,
                    ).withActionAvailability(today)
            }

            val replacementDate = ReplacementDate(today.toString())
            val replaceResult =
                itemRepository.createReplacement(
                    itemId = currentState.consumableId,
                    replacedDate = replacementDate,
                )
            if (!isCurrentConsumableMutation(mutationGeneration, currentState.consumableId)) {
                return@intent
            }

            replaceResult
                .onSuccess { replacedItem ->
                    val refreshedDetail = itemRepository.getItem(currentState.consumableId).getOrNull()
                    if (!isCurrentConsumableMutation(mutationGeneration, currentState.consumableId)) {
                        return@onSuccess
                    }
                    if (refreshedDetail != null) {
                        val successState = refreshedDetail.toDetailSuccess(today)
                        reduce {
                            successState
                        }
                        return@onSuccess
                    }

                    val effectiveReplacementDate =
                        replacedItem.lastReplacedDate.toLocalDateOrNull() ?: today
                    val effectiveReplacementDateValue =
                        replacedItem.lastReplacedDate ?: ReplacementDate(effectiveReplacementDate.toString())
                    val previousReplacementDate =
                        currentState.lastReplacedDate?.takeUnless { date ->
                            date.isAfter(effectiveReplacementDate)
                        }
                    val histories =
                        itemRepository
                            .getReplacementHistories(itemId = currentState.consumableId)
                            .getOrElse {
                                currentState.toFallbackReplacementHistories(effectiveReplacementDate)
                            }.withReplacementDate(
                                id = PREVIOUS_REPLACEMENT_HISTORY_ID,
                                date = previousReplacementDate,
                            ).withReplacementDate(
                                id = TEMPORARY_HISTORY_ID,
                                date = effectiveReplacementDate,
                            )
                    if (!isCurrentConsumableMutation(mutationGeneration, currentState.consumableId)) {
                        return@onSuccess
                    }
                    val stateItem =
                        replacedItem.copy(
                            count = replacedItem.count.coerceAtLeast(0),
                            lastReplacedDate = effectiveReplacementDateValue,
                        )

                    val successState =
                        stateItem.toDetailSuccess(
                            histories = histories,
                            representativeImageUrl = currentState.representativeImageUrl,
                            today = today,
                        )
                    reduce {
                        successState
                    }
                }.onFailure {
                    if (!isCurrentConsumableMutation(mutationGeneration, currentState.consumableId)) {
                        return@onFailure
                    }
                    val latestState = state as? DetailUiState.ConsumableSuccess ?: return@onFailure
                    reduce {
                        latestState
                            .copy(
                                isReplaceProcessing = false,
                            ).withActionAvailability(today)
                    }
                    postSideEffect(DetailSideEffect.ShowSnackbar(DetailUserMessage.REPLACE_FAILED))
                }
        }

    fun onEditResult(result: DetailEditResult) =
        intent {
            val currentState = state as? DetailUiState.ConsumableSuccess ?: return@intent
            if (
                currentState.hasPendingMutation ||
                result.consumableId != currentState.consumableId
            ) {
                return@intent
            }

            val loadGeneration = nextDetailLoadGeneration()
            loadDetailIntoState(
                consumableId = currentState.consumableId,
                loadGeneration = loadGeneration,
                showLoading = false,
                keepCurrentOnFailure = true,
            )
        }

    fun onDateChanged() =
        intent {
            reduceOn<DetailUiState.ConsumableSuccess> {
                state.recalculateForToday(dateProvider.today())
            }
        }

    fun onScreenReentered() =
        intent {
            val currentState = state as? DetailUiState.ConsumableSuccess ?: return@intent
            if (currentState.hasPendingMutation) {
                return@intent
            }

            val loadGeneration = nextDetailLoadGeneration()
            loadDetailIntoState(
                consumableId = currentState.consumableId,
                loadGeneration = loadGeneration,
                showLoading = false,
                keepCurrentOnFailure = true,
            )
        }

    private suspend fun Syntax<DetailUiState, DetailSideEffect>.loadDetailIntoState(
        consumableId: Long,
        loadGeneration: Long,
        showLoading: Boolean,
        keepCurrentOnFailure: Boolean,
    ) {
        if (showLoading) {
            reduce {
                DetailUiState.Loading
            }
        }

        val result = itemRepository.getItem(consumableId)
        if (isCurrentDetailLoad(loadGeneration)) {
            result
                .onSuccess { itemDetail ->
                    reduce {
                        itemDetail.toDetailSuccess(today = dateProvider.today())
                    }
                }.onFailure { error ->
                    if (error.isNotFound()) {
                        reduce { DetailUiState.NotFound }
                    } else {
                        handleLoadFailure(keepCurrentOnFailure)
                    }
                }
        }
    }

    private suspend fun Syntax<DetailUiState, DetailSideEffect>.handleLoadFailure(keepCurrent: Boolean) {
        if (keepCurrent) {
            postSideEffect(DetailSideEffect.ShowSnackbar(DetailUserMessage.LOAD_FAILED))
        } else {
            reduce {
                DetailUiState.LoadFailed.General
            }
        }
    }

    private fun nextDetailLoadGeneration(): Long {
        detailLoadGeneration += 1
        return detailLoadGeneration
    }

    private fun isCurrentDetailLoad(loadGeneration: Long): Boolean = loadGeneration == detailLoadGeneration

    private fun Syntax<DetailUiState, DetailSideEffect>.isCurrentConsumableMutation(
        mutationGeneration: Long,
        consumableId: Long,
    ): Boolean =
        isCurrentDetailLoad(mutationGeneration) &&
            (state as? DetailUiState.ConsumableSuccess)?.consumableId == consumableId
}

sealed interface DetailUiState {
    sealed interface Success : DetailUiState {
        val agent: DetailLegacyAgentPreview
    }

    @Immutable
    data class ConsumableSuccess(
        val consumableId: Long,
        val categoryName: String,
        val itemName: String,
        val representativeImageUrl: String?,
        val lastReplacedDate: LocalDate?,
        val nextReplacementDate: LocalDate?,
        val recommendedReplacementIntervalDays: Int,
        val currentUsageDays: Int,
        val spareCount: Int,
        val replacementRecords: List<DetailReplacementRecordUiState>,
        val averageReplacementIntervalDays: Double,
        val dDayValue: Int?,
        val dDayLabel: String,
        val dDayDirection: DetailDDayDirection,
        val progressRawRatio: Double,
        val progressDisplayRatio: Double,
        val statusGrade: DetailStatusGrade,
        val colorTone: DetailColorTone,
        val spareStatus: DetailSpareStatus,
        val isSpareManagementEnabled: Boolean,
        val isReplaceCtaEnabled: Boolean,
        val isReplaceProcessing: Boolean,
        val isSpareProcessing: Boolean,
        val isDeleteProcessing: Boolean,
        val isDeleteConfirmVisible: Boolean,
        val pendingDeleteConsumableId: Long?,
    ) : Success {
        val headerTitle: String
            get() = itemName

        val spareAreaItemName: String
            get() = itemName

        override val agent: DetailLegacyAgentPreview
            get() =
                DetailLegacyAgentPreview(
                    id = consumableId,
                    name = itemName,
                    type = DetailLegacyAgentType(categoryName),
                    description = recommendedReplacementIntervalDays.toString(),
                    timestamp = lastReplacedDate?.toString().orEmpty(),
                )
    }

    @Immutable
    data class LegacyAgentSuccess(
        override val agent: DetailLegacyAgentPreview,
    ) : Success

    data object Loading : DetailUiState

    sealed interface LoadFailed : DetailUiState {
        data object General : LoadFailed
    }

    data object NotFound : LoadFailed
}

@Immutable
data class DetailReplacementRecordUiState(
    val id: Long?,
    val startedDate: LocalDate?,
    val endedDate: LocalDate?,
    val usageDays: Int,
    val usageDaysLabel: String,
    val dateLabel: String,
    val progressDisplayRatio: Double,
    val isCurrent: Boolean,
)

@Immutable
data class DetailReplacementCompletionFeedback(
    val itemName: String,
    val representativeImageUrl: String?,
    val spareCount: Int,
    val recommendedReplacementIntervalDays: Int,
    val nextReplacementDate: LocalDate?,
    val replacementRecords: List<DetailReplacementRecordUiState>,
)

@Immutable
data class DetailSpareStatus(
    val count: Int,
    val hasSpare: Boolean,
    val grade: DetailStatusGrade,
    val colorTone: DetailColorTone,
)

@Immutable
data class DetailEditResult(
    val consumableId: Long,
    val name: String? = null,
    val lastReplacedDate: String? = null,
    val isLastReplacedDateCleared: Boolean = false,
    val replacementIntervalDays: Int? = null,
    val spareCount: Int? = null,
)

@Immutable
data class DetailLegacyAgentPreview(
    val id: Long,
    val name: String,
    val type: DetailLegacyAgentType,
    val description: String,
    val timestamp: String,
)

@Immutable
data class DetailLegacyAgentType(
    val name: String,
)

enum class DetailStatusGrade {
    PERFECT,
    GOOD,
    WARNING,
    DANGER,
}

enum class DetailColorTone {
    BRAND,
    WARNING,
}

enum class DetailDDayDirection {
    UPCOMING,
    TODAY,
    OVERDUE,
    UNKNOWN,
}

enum class DetailMenuAction {
    EDIT,
    DELETE,
    SPARE_MANAGEMENT,
}

enum class DetailUserMessage {
    LOAD_FAILED,
    REPLACE_FAILED,
    SPARE_UPDATE_SUCCEEDED,
    SPARE_UPDATE_FAILED,
    DELETE_FAILED,
}

sealed interface DetailSideEffect {
    data object NavigateBack : DetailSideEffect

    data object OpenMoreMenu : DetailSideEffect

    data class NavigateToEdit(
        val consumableId: Long,
    ) : DetailSideEffect

    data class OpenSpareManagement(
        val consumableId: Long,
    ) : DetailSideEffect

    data class ShowSpareSheet(
        val consumableId: Long,
    ) : DetailSideEffect

    data object NavigateAfterDelete : DetailSideEffect

    data class ShowReplacementCompletionDialog(
        val feedback: DetailReplacementCompletionFeedback,
    ) : DetailSideEffect

    data class ShowSnackbar(
        val message: DetailUserMessage,
    ) : DetailSideEffect
}

private fun DetailUiState.ConsumableSuccess.toPendingReplacementCompletionFeedback(today: LocalDate): DetailReplacementCompletionFeedback {
    val completedRecords = replacementRecords.filterNot { record -> record.isCurrent }
    val completedCurrentRecord =
        DetailReplacementRecordUiState(
            id = null,
            startedDate = lastReplacedDate,
            endedDate = today,
            usageDays = currentUsageDays,
            usageDaysLabel = currentUsageDays.toString(),
            dateLabel = today.toMonthDayLabel(),
            progressDisplayRatio = progressDisplayRatio,
            isCurrent = false,
        )

    return DetailReplacementCompletionFeedback(
        itemName = itemName,
        representativeImageUrl = representativeImageUrl,
        spareCount = (spareCount - 1).coerceAtLeast(0),
        recommendedReplacementIntervalDays = recommendedReplacementIntervalDays,
        nextReplacementDate =
            computedNextReplacementDate(
                lastReplacementDate = today,
                replacementIntervalDays = recommendedReplacementIntervalDays,
                fallbackNextReplacementDate = null,
            ),
        replacementRecords = completedRecords + completedCurrentRecord,
    )
}

@Suppress("LongMethod")
internal fun Item.toDetailSuccess(
    histories: List<ReplacementHistory>,
    representativeImageUrl: String?,
    today: LocalDate,
): DetailUiState.ConsumableSuccess {
    val sanitizedSpareCount = count.coerceAtLeast(0)
    val sanitizedReplacementIntervalDays = replacementIntervalDays.coerceAtLeast(0)
    val itemLastReplacedDate = lastReplacedDate.toLocalDateOrNull()
    val historyDates =
        histories
            .toHistoryDates()
            .withDate(
                id = ITEM_LAST_REPLACED_HISTORY_ID,
                date = itemLastReplacedDate,
            )
    val lastReplacementDate =
        latestReplacementDate(
            itemLastReplacedDate = itemLastReplacedDate,
            historyDates = historyDates,
        )
    val currentUsageDays = lastReplacementDate?.daysUntil(today)?.coerceAtLeast(0) ?: 0
    val calculatedNextReplacementDate =
        computedNextReplacementDate(
            lastReplacementDate = lastReplacementDate,
            replacementIntervalDays = sanitizedReplacementIntervalDays,
            fallbackNextReplacementDate = nextReplacementDate.toLocalDateOrNull(),
        )
    val dDay = calculatedNextReplacementDate.toDDay(today)
    val progressRawRatio = currentUsageDays.toProgressRawRatio(sanitizedReplacementIntervalDays)
    val statusGrade = progressRawRatio.toStatusGrade(sanitizedReplacementIntervalDays)
    val completedCycles = historyDates.toCompletedCycles()
    val replacementRecords =
        completedCycles
            .takeLast(MAX_COMPLETED_REPLACEMENT_RECORDS)
            .map { cycle ->
                cycle.toRecord(sanitizedReplacementIntervalDays)
            } +
            currentReplacementRecord(
                startedDate = lastReplacementDate,
                currentUsageDays = currentUsageDays,
                replacementIntervalDays = sanitizedReplacementIntervalDays,
            )
    val spareStatus = sanitizedSpareCount.toSpareStatus()

    return DetailUiState.ConsumableSuccess(
        consumableId = id,
        categoryName = categoryName,
        itemName = name,
        representativeImageUrl = representativeImageUrl,
        lastReplacedDate = lastReplacementDate,
        nextReplacementDate = calculatedNextReplacementDate,
        recommendedReplacementIntervalDays = sanitizedReplacementIntervalDays,
        currentUsageDays = currentUsageDays,
        spareCount = sanitizedSpareCount,
        replacementRecords = replacementRecords.takeLast(MAX_RECENT_REPLACEMENT_RECORDS),
        averageReplacementIntervalDays =
            averageReplacementIntervalDays(
                completedCycles = completedCycles,
                replacementIntervalDays = sanitizedReplacementIntervalDays,
            ),
        dDayValue = dDay.value,
        dDayLabel = dDay.label,
        dDayDirection = dDay.direction,
        progressRawRatio = progressRawRatio,
        progressDisplayRatio = progressRawRatio.coerceIn(MIN_PROGRESS_RATIO, MAX_PROGRESS_RATIO),
        statusGrade = statusGrade,
        colorTone = statusGrade.toColorTone(),
        spareStatus = spareStatus,
        isSpareManagementEnabled = true,
        isReplaceCtaEnabled = lastReplacementDate?.isBefore(today) != false,
        isReplaceProcessing = false,
        isSpareProcessing = false,
        isDeleteProcessing = false,
        isDeleteConfirmVisible = false,
        pendingDeleteConsumableId = null,
    )
}

private fun Agent.toLegacySuccess(): DetailUiState.LegacyAgentSuccess =
    DetailUiState.LegacyAgentSuccess(
        agent =
            DetailLegacyAgentPreview(
                id = id.toLong(),
                name = name,
                type = DetailLegacyAgentType(type.name),
                description = description,
                timestamp = timestamp.toString(),
            ),
    )

@Suppress("LongMethod")
internal fun ItemDetail.toDetailSuccess(today: LocalDate): DetailUiState.ConsumableSuccess {
    val sanitizedSpareCount = spareQuantity.coerceAtLeast(0)
    val sanitizedReplacementIntervalDays = recommendedCycleDays.coerceAtLeast(0)
    val lastReplacementDate = lastReplacedDate.toLocalDateOrNull()
    val resolvedNextReplacementDate =
        nextReplacementDate.toLocalDateOrNull()
            ?: computedNextReplacementDate(
                lastReplacementDate = lastReplacementDate,
                replacementIntervalDays = sanitizedReplacementIntervalDays,
                fallbackNextReplacementDate = null,
            )
    val resolvedDDay = toDetailDDay(fallbackDate = resolvedNextReplacementDate, today = today)
    val progressRawRatio = progressPercentage.coerceAtLeast(0.0) / PERCENT_SCALE
    val statusGrade =
        status.toDetailStatusGrade(
            progressRawRatio = progressRawRatio,
            replacementIntervalDays = sanitizedReplacementIntervalDays,
        )
    val replacementRecords =
        recentReplacements
            .toDetailReplacementRecords(sanitizedReplacementIntervalDays)
            .ifEmpty {
                listOf(
                    currentReplacementRecord(
                        startedDate = lastReplacementDate,
                        currentUsageDays = usedDays.coerceAtLeast(0),
                        replacementIntervalDays = sanitizedReplacementIntervalDays,
                    ),
                )
            }
    val spareStatus = sanitizedSpareCount.toSpareStatus()

    return DetailUiState.ConsumableSuccess(
        consumableId = id,
        categoryName = category.name,
        itemName = name,
        representativeImageUrl = iconUrl?.takeIf { value -> value.isNotBlank() },
        lastReplacedDate = lastReplacementDate,
        nextReplacementDate = resolvedNextReplacementDate,
        recommendedReplacementIntervalDays = sanitizedReplacementIntervalDays,
        currentUsageDays = usedDays.coerceAtLeast(0),
        spareCount = sanitizedSpareCount,
        replacementRecords = replacementRecords.takeLast(MAX_RECENT_REPLACEMENT_RECORDS),
        averageReplacementIntervalDays =
            myAverageCycleDays
                .takeIf { average -> average > 0.0 }
                ?: sanitizedReplacementIntervalDays.toDouble(),
        dDayValue = resolvedDDay.value,
        dDayLabel = resolvedDDay.label,
        dDayDirection = resolvedDDay.direction,
        progressRawRatio = progressRawRatio,
        progressDisplayRatio = progressRawRatio.coerceIn(MIN_PROGRESS_RATIO, MAX_PROGRESS_RATIO),
        statusGrade = statusGrade,
        colorTone = statusGrade.toColorTone(),
        spareStatus = spareStatus,
        isSpareManagementEnabled = true,
        isReplaceCtaEnabled = lastReplacementDate?.isBefore(today) != false,
        isReplaceProcessing = false,
        isSpareProcessing = false,
        isDeleteProcessing = false,
        isDeleteConfirmVisible = false,
        pendingDeleteConsumableId = null,
    )
}

@Suppress("LongMethod")
private fun DetailUiState.ConsumableSuccess.recalculateForToday(today: LocalDate): DetailUiState.ConsumableSuccess {
    val currentUsageDays = lastReplacedDate?.daysUntil(today)?.coerceAtLeast(0) ?: 0
    val calculatedNextReplacementDate =
        computedNextReplacementDate(
            lastReplacementDate = lastReplacedDate,
            replacementIntervalDays = recommendedReplacementIntervalDays,
            fallbackNextReplacementDate = nextReplacementDate,
        )
    val dDay = calculatedNextReplacementDate.toDDay(today)
    val progressRawRatio = currentUsageDays.toProgressRawRatio(recommendedReplacementIntervalDays)
    val statusGrade = progressRawRatio.toStatusGrade(recommendedReplacementIntervalDays)
    val spareStatus = spareCount.toSpareStatus()
    val updatedRecords =
        replacementRecords.map { record ->
            if (record.isCurrent) {
                currentReplacementRecord(
                    startedDate = lastReplacedDate,
                    currentUsageDays = currentUsageDays,
                    replacementIntervalDays = recommendedReplacementIntervalDays,
                )
            } else {
                record.withProgressDisplayRatio(recommendedReplacementIntervalDays)
            }
        }
    val completedRecords = updatedRecords.filterNot { record -> record.isCurrent }
    return copy(
        averageReplacementIntervalDays =
            if (completedRecords.isEmpty()) {
                recommendedReplacementIntervalDays.coerceAtLeast(0).toDouble()
            } else {
                averageReplacementIntervalDays
            },
        nextReplacementDate = calculatedNextReplacementDate,
        currentUsageDays = currentUsageDays,
        replacementRecords = updatedRecords,
        dDayValue = dDay.value,
        dDayLabel = dDay.label,
        dDayDirection = dDay.direction,
        progressRawRatio = progressRawRatio,
        progressDisplayRatio = progressRawRatio.coerceIn(MIN_PROGRESS_RATIO, MAX_PROGRESS_RATIO),
        statusGrade = statusGrade,
        colorTone = statusGrade.toColorTone(),
        spareStatus = spareStatus,
    ).withActionAvailability(today)
}

private fun DetailUiState.ConsumableSuccess.withSpareCount(count: Int): DetailUiState.ConsumableSuccess {
    val sanitizedCount = count.coerceAtLeast(0)

    return copy(
        spareCount = sanitizedCount,
        spareStatus = sanitizedCount.toSpareStatus(),
        isSpareProcessing = false,
    )
}

private fun DetailReplacementRecordUiState.withProgressDisplayRatio(replacementIntervalDays: Int): DetailReplacementRecordUiState =
    copy(
        progressDisplayRatio =
            usageDays
                .toProgressRawRatio(replacementIntervalDays)
                .coerceIn(MIN_PROGRESS_RATIO, MAX_PROGRESS_RATIO),
    )

private fun DetailUiState.ConsumableSuccess.withActionAvailability(today: LocalDate? = null): DetailUiState.ConsumableSuccess {
    val isActionEnabled = !hasPendingMutation
    val isReplaceEnabled =
        isActionEnabled &&
            (today?.let { currentDate -> lastReplacedDate?.isBefore(currentDate) != false } ?: true)

    return copy(
        isSpareManagementEnabled = isActionEnabled,
        isReplaceCtaEnabled = isReplaceEnabled,
    )
}

private val DetailUiState.ConsumableSuccess.hasPendingMutation: Boolean
    get() = isDeleteProcessing || isDeleteConfirmVisible || isReplaceProcessing || isSpareProcessing

private val DetailUiState.ConsumableSuccess.canStartConsumableMutation: Boolean
    get() = !hasPendingMutation

private fun DetailUiState.ConsumableSuccess.canStartReplacement(today: LocalDate): Boolean =
    canStartConsumableMutation && lastReplacedDate?.isBefore(today) != false

private fun DetailUiState.ConsumableSuccess.canConfirmDelete(consumableId: Long?): Boolean {
    val pendingConsumableId = pendingDeleteConsumableId ?: return false
    val isRequestedItem = consumableId == null || consumableId == pendingConsumableId

    return isDeleteConfirmVisible &&
        !isDeleteProcessing &&
        !isReplaceProcessing &&
        !isSpareProcessing &&
        pendingConsumableId == this.consumableId &&
        isRequestedItem
}

private val DetailUiState.ConsumableSuccess.canOpenConsumableSideEffect: Boolean
    get() = !hasPendingMutation

private val DetailUiState.ConsumableSuccess.canOpenSpareManagement: Boolean
    get() = canOpenConsumableSideEffect && isSpareManagementEnabled

private fun List<ReplacementHistory>.toHistoryDates(): List<DetailHistoryDate> =
    mapNotNull { history ->
        history.replacedDate.toLocalDateOrNull()?.let { date ->
            DetailHistoryDate(
                id = history.id,
                date = date,
            )
        }
    }.sortedWith(
        compareBy<DetailHistoryDate> { historyDate -> historyDate.date }
            .thenBy { historyDate -> historyDate.id },
    )

private fun List<DetailHistoryDate>.withDate(
    id: Long,
    date: LocalDate?,
): List<DetailHistoryDate> {
    if (date == null || any { historyDate -> historyDate.date == date }) {
        return this
    }

    return (this + DetailHistoryDate(id = id, date = date))
        .sortedWith(
            compareBy<DetailHistoryDate> { historyDate -> historyDate.date }
                .thenBy { historyDate -> historyDate.id },
        )
}

private fun List<ReplacementHistory>.withReplacementDate(
    id: Long,
    date: LocalDate?,
): List<ReplacementHistory> =
    if (date == null || any { history -> history.replacedDate.toLocalDateOrNull() == date }) {
        this
    } else {
        this +
            ReplacementHistory(
                id = id,
                replacedDate = ReplacementDate(date.toString()),
            )
    }

private fun DetailUiState.ConsumableSuccess.toFallbackReplacementHistories(newReplacementDate: LocalDate): List<ReplacementHistory> =
    (
        replacementRecords.flatMap { record ->
            listOfNotNull(record.startedDate, record.endedDate)
        } +
            listOfNotNull(lastReplacedDate, newReplacementDate)
    ).distinct()
        .filterNot { date -> date.isAfter(newReplacementDate) }
        .sorted()
        .mapIndexed { index, date ->
            ReplacementHistory(
                id = TEMPORARY_HISTORY_ID - index.toLong(),
                replacedDate = ReplacementDate(date.toString()),
            )
        }

private fun latestReplacementDate(
    itemLastReplacedDate: LocalDate?,
    historyDates: List<DetailHistoryDate>,
): LocalDate? =
    listOfNotNull(
        itemLastReplacedDate,
        historyDates.lastOrNull()?.date,
    ).maxOrNull()

private fun computedNextReplacementDate(
    lastReplacementDate: LocalDate?,
    replacementIntervalDays: Int,
    fallbackNextReplacementDate: LocalDate?,
): LocalDate? =
    if (replacementIntervalDays > 0) {
        lastReplacementDate?.plusDays(replacementIntervalDays.toLong())
            ?: fallbackNextReplacementDate
    } else {
        null
    }

private fun List<DetailHistoryDate>.toCompletedCycles(): List<DetailCompletedCycle> =
    zipWithNext { started, ended ->
        DetailCompletedCycle(
            id = ended.id,
            startedDate = started.date,
            endedDate = ended.date,
            usageDays = started.date.daysUntil(ended.date).coerceAtLeast(0),
        )
    }

private fun DetailCompletedCycle.toRecord(replacementIntervalDays: Int): DetailReplacementRecordUiState =
    DetailReplacementRecordUiState(
        id = id,
        startedDate = startedDate,
        endedDate = endedDate,
        usageDays = usageDays,
        usageDaysLabel = usageDays.toString(),
        dateLabel = endedDate.toMonthDayLabel(),
        progressDisplayRatio =
            usageDays
                .toProgressRawRatio(replacementIntervalDays)
                .coerceIn(MIN_PROGRESS_RATIO, MAX_PROGRESS_RATIO),
        isCurrent = false,
    )

private fun currentReplacementRecord(
    startedDate: LocalDate?,
    currentUsageDays: Int,
    replacementIntervalDays: Int,
): DetailReplacementRecordUiState =
    DetailReplacementRecordUiState(
        id = null,
        startedDate = startedDate,
        endedDate = null,
        usageDays = currentUsageDays,
        usageDaysLabel = currentUsageDays.toString(),
        dateLabel = startedDate?.toMonthDayLabel().orEmpty(),
        progressDisplayRatio =
            currentUsageDays
                .toProgressRawRatio(replacementIntervalDays)
                .coerceIn(MIN_PROGRESS_RATIO, MAX_PROGRESS_RATIO),
        isCurrent = true,
    )

private fun ItemDetail.toDetailDDay(
    fallbackDate: LocalDate?,
    today: LocalDate,
): DetailDDay {
    val fallback = fallbackDate.toDDay(today)

    return DetailDDay(
        value = dDay ?: fallback.value,
        label = dDayLabel.ifBlank { fallback.label },
        direction = dDay.toDDayDirection(fallback.direction),
    )
}

private fun Int?.toDDayDirection(fallback: DetailDDayDirection): DetailDDayDirection =
    when {
        this == null -> fallback
        this > 0 -> DetailDDayDirection.UPCOMING
        this == 0 -> DetailDDayDirection.TODAY
        else -> DetailDDayDirection.OVERDUE
    }

private fun List<ItemDetailReplacement>.toDetailReplacementRecords(replacementIntervalDays: Int): List<DetailReplacementRecordUiState> =
    mapNotNull { replacement ->
        replacement.toDetailReplacementRecord(replacementIntervalDays)
    }

private fun ItemDetailReplacement.toDetailReplacementRecord(replacementIntervalDays: Int): DetailReplacementRecordUiState? {
    val replacementDate = date.toLocalDateOrNull() ?: return null
    val sanitizedUsageDays = cycleDays.coerceAtLeast(0)
    val startedDate =
        if (current) {
            replacementDate
        } else {
            replacementDate.minusDays(sanitizedUsageDays.toLong())
        }

    return DetailReplacementRecordUiState(
        id = id,
        startedDate = startedDate,
        endedDate = if (current) null else replacementDate,
        usageDays = sanitizedUsageDays,
        usageDaysLabel = sanitizedUsageDays.toString(),
        dateLabel = replacementDate.toMonthDayLabel(),
        progressDisplayRatio =
            sanitizedUsageDays
                .toProgressRawRatio(replacementIntervalDays)
                .coerceIn(MIN_PROGRESS_RATIO, MAX_PROGRESS_RATIO),
        isCurrent = current,
    )
}

private fun averageReplacementIntervalDays(
    completedCycles: List<DetailCompletedCycle>,
    replacementIntervalDays: Int,
): Double =
    averageReplacementIntervalDaysFromUsageDays(
        completedUsageDays = completedCycles.map { cycle -> cycle.usageDays },
        replacementIntervalDays = replacementIntervalDays,
    )

private fun averageReplacementIntervalDaysFromUsageDays(
    completedUsageDays: List<Int>,
    replacementIntervalDays: Int,
): Double =
    completedUsageDays
        .takeIf { usageDays -> usageDays.isNotEmpty() }
        ?.average()
        ?: replacementIntervalDays.coerceAtLeast(0).toDouble()

private fun Int.toProgressRawRatio(replacementIntervalDays: Int): Double =
    if (replacementIntervalDays > 0) {
        toDouble() / replacementIntervalDays.toDouble()
    } else {
        MIN_PROGRESS_RATIO
    }

// UI policy until the domain defines detail status thresholds.
private fun Double.toStatusGrade(replacementIntervalDays: Int): DetailStatusGrade =
    when {
        replacementIntervalDays <= 0 -> DetailStatusGrade.DANGER
        this < PERFECT_PROGRESS_THRESHOLD -> DetailStatusGrade.PERFECT
        this < GOOD_PROGRESS_THRESHOLD -> DetailStatusGrade.GOOD
        this <= MAX_PROGRESS_RATIO -> DetailStatusGrade.WARNING
        else -> DetailStatusGrade.DANGER
    }

private fun DetailStatusGrade.toColorTone(): DetailColorTone =
    when (this) {
        DetailStatusGrade.PERFECT,
        DetailStatusGrade.GOOD,
        -> DetailColorTone.BRAND
        DetailStatusGrade.WARNING,
        DetailStatusGrade.DANGER,
        -> DetailColorTone.WARNING
    }

private fun ItemDetailStatus.toDetailStatusGrade(
    progressRawRatio: Double,
    replacementIntervalDays: Int,
): DetailStatusGrade =
    when (this) {
        ItemDetailStatus.GOOD -> DetailStatusGrade.GOOD
        ItemDetailStatus.WARNING -> DetailStatusGrade.WARNING
        ItemDetailStatus.DANGER -> DetailStatusGrade.DANGER
        ItemDetailStatus.LOW_STOCK -> DetailStatusGrade.WARNING
        ItemDetailStatus.UNKNOWN -> progressRawRatio.toStatusGrade(replacementIntervalDays)
    }

private fun Int.toSpareStatus(): DetailSpareStatus {
    val sanitizedCount = coerceAtLeast(0)
    val hasSpare = sanitizedCount > 0

    return DetailSpareStatus(
        count = sanitizedCount,
        hasSpare = hasSpare,
        grade = if (hasSpare) DetailStatusGrade.GOOD else DetailStatusGrade.DANGER,
        colorTone = if (hasSpare) DetailColorTone.BRAND else DetailColorTone.WARNING,
    )
}

private fun LocalDate.daysUntil(targetDate: LocalDate): Int =
    ChronoUnit
        .DAYS
        .between(this, targetDate)
        .coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong())
        .toInt()

private fun LocalDate.toMonthDayLabel(): String = "$monthValue/$dayOfMonth"

private fun ReplacementDate?.toLocalDateOrNull(): LocalDate? = this?.value?.toLocalDateOrNull()

private fun Throwable.isNotFound(): Boolean = this is GetItemError.NotFound

private fun String.toLocalDateOrNull(): LocalDate? {
    val dateString =
        trim()
            .takeIf { it.isNotEmpty() }
            ?.let { value ->
                if (value.length >= ISO_LOCAL_DATE_LENGTH) {
                    value.take(ISO_LOCAL_DATE_LENGTH)
                } else {
                    value
                }
            } ?: return null

    return runCatching {
        LocalDate.parse(dateString)
    }.getOrNull()
}

private fun LocalDate?.toDDay(today: LocalDate): DetailDDay {
    if (this == null) {
        return DetailDDay(
            value = null,
            label = "",
            direction = DetailDDayDirection.UNKNOWN,
        )
    }

    val daysUntil = today.daysUntil(this)
    return when {
        daysUntil > 0 ->
            DetailDDay(
                value = daysUntil,
                label = "D-$daysUntil",
                direction = DetailDDayDirection.UPCOMING,
            )
        daysUntil == 0 ->
            DetailDDay(
                value = daysUntil,
                label = "D-Day",
                direction = DetailDDayDirection.TODAY,
            )
        else ->
            DetailDDay(
                value = daysUntil,
                label = "D+${-daysUntil}",
                direction = DetailDDayDirection.OVERDUE,
            )
    }
}

internal fun interface DetailDateProvider {
    fun today(): LocalDate
}

private object SystemDetailDateProvider : DetailDateProvider {
    override fun today(): LocalDate = LocalDate.now()
}

private data class DetailHistoryDate(
    val id: Long,
    val date: LocalDate,
)

private data class DetailCompletedCycle(
    val id: Long,
    val startedDate: LocalDate,
    val endedDate: LocalDate,
    val usageDays: Int,
)

private data class DetailDDay(
    val value: Int?,
    val label: String,
    val direction: DetailDDayDirection,
)

private const val MAX_RECENT_REPLACEMENT_RECORDS = 5
private const val MAX_COMPLETED_REPLACEMENT_RECORDS = 4
private const val TEMPORARY_HISTORY_ID = 0L
private const val PREVIOUS_REPLACEMENT_HISTORY_ID = -1L
private const val ITEM_LAST_REPLACED_HISTORY_ID = Long.MIN_VALUE
private const val MIN_PROGRESS_RATIO = 0.0
private const val MAX_PROGRESS_RATIO = 1.0
private const val MIN_SPARE_COUNT = 0
private const val MAX_SPARE_COUNT = 99
private const val PERCENT_SCALE = 100.0
private const val PERFECT_PROGRESS_THRESHOLD = 0.5
private const val GOOD_PROGRESS_THRESHOLD = 0.8
private const val ISO_LOCAL_DATE_LENGTH = 10
