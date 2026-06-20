@file:Suppress("CyclomaticComplexMethod", "LongMethod", "LongParameterList", "MagicNumber", "TooManyFunctions")

package com.obrit.feature.detail.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.component.snackbar.OBRitSnackbar
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.feature.detail.component.DetailReplacementCompletionDialog
import com.obrit.feature.detail.component.DetailReplacementCompletionDialogKind
import com.obrit.feature.detail.component.DetailReplacementCompletionDialogState
import com.obrit.feature.detail.viewmodel.DetailEditResult
import com.obrit.feature.detail.viewmodel.DetailMenuAction
import com.obrit.feature.detail.viewmodel.DetailReplacementCompletionFeedback
import com.obrit.feature.detail.viewmodel.DetailSideEffect
import com.obrit.feature.detail.viewmodel.DetailUserMessage
import com.obrit.feature.detail.viewmodel.DetailViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Locale

@Composable
fun DetailScreen(
    id: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onEditClick: (Long) -> Unit = {},
    editResult: DetailEditSubmitResult? = null,
    onEditResultConsume: () -> Unit = {},
    viewModel: DetailViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()
    val colors = LocalOBRitColor.current
    val currentOnEditResultConsume by rememberUpdatedState(onEditResultConsume)
    var isMoreMenuExpanded by remember { mutableStateOf(false) }
    var isSpareSheetVisible by remember { mutableStateOf(false) }
    var snackbarEvent by remember { mutableStateOf<DetailSnackbarEvent?>(null) }
    var replacementCompletionDialogState by remember { mutableStateOf<DetailReplacementCompletionDialogState?>(null) }
    val showSnackbar: (String) -> Unit = { message ->
        snackbarEvent =
            DetailSnackbarEvent(
                message = message,
                sequence = snackbarEvent.nextSequence(),
            )
    }

    LaunchedEffect(id) {
        replacementCompletionDialogState = null
        viewModel.loadConsumable(id.toLong())
    }

    LaunchedEffect(editResult) {
        val result = editResult ?: return@LaunchedEffect
        if (result.consumableId == id.toLong()) {
            viewModel.onEditResult(
                DetailEditResult(
                    consumableId = result.consumableId,
                    name = result.name,
                    replacementIntervalDays = result.replacementIntervalDays,
                ),
            )
            currentOnEditResultConsume()
        }
    }

    LaunchedEffect(snackbarEvent) {
        if (snackbarEvent != null) {
            delay(DETAIL_SNACKBAR_VISIBLE_MILLIS)
            snackbarEvent = null
        }
    }

    val action =
        DetailScreenAction(
            onBackClick = viewModel::onBackClick,
            onRetryClick = {
                viewModel.retryLoad(id.toLong())
            },
            onMoreClick = viewModel::onMoreClick,
            onMoreMenuDismiss = {
                isMoreMenuExpanded = false
            },
            onEditClick = {
                viewModel.onMoreMenuAction(DetailMenuAction.EDIT)
            },
            onDeleteClick = {
                viewModel.onMoreMenuAction(DetailMenuAction.DELETE)
            },
            onDeleteConfirmClick = {
                viewModel.onDeleteConfirm()
            },
            onDeleteCancelClick = viewModel::onDeleteCancel,
            onSpareManagementClick = viewModel::onSpareManagementClick,
            onReplaceCompleteClick = viewModel::onReplaceCompleteClick,
            onSpareSheetExitRequest = {
                isSpareSheetVisible = false
            },
            onSpareCountCompleteClick = { count ->
                isSpareSheetVisible = false
                viewModel.onSpareCountCompleteClick(count)
            },
        )

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.gray900),
    ) {
        DetailScreenContent(
            state = state,
            action = action,
            isMoreMenuExpanded = isMoreMenuExpanded,
            isSpareSheetVisible = isSpareSheetVisible,
            modifier = Modifier.fillMaxSize(),
        )

        DetailSnackbarHost(
            message = snackbarEvent?.message,
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        replacementCompletionDialogState?.let { dialogState ->
            DetailReplacementCompletionDialog(
                state = dialogState,
                onConfirmClick = {
                    replacementCompletionDialogState = null
                    viewModel.onReplaceCompletionConfirm()
                },
                onCancelClick = {
                    replacementCompletionDialogState = null
                },
            )
        }
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is DetailSideEffect.NavigateBack -> onBackClick()
            is DetailSideEffect.NavigateAfterDelete -> onBackClick()
            is DetailSideEffect.NavigateToEdit -> {
                onEditClick(sideEffect.consumableId)
            }
            is DetailSideEffect.OpenMoreMenu -> {
                isMoreMenuExpanded = true
            }
            is DetailSideEffect.OpenSpareManagement -> {
                isSpareSheetVisible = true
            }
            is DetailSideEffect.ShowReplacementCompletionDialog -> {
                replacementCompletionDialogState =
                    sideEffect.feedback.toReplacementCompletionDialogState(recordedAt = LocalDateTime.now())
            }
            is DetailSideEffect.ShowSnackbar -> {
                showSnackbar(sideEffect.message.toSnackbarMessage())
            }
            is DetailSideEffect.ShowSpareSheet -> {
                isSpareSheetVisible = true
            }
        }
    }
}

internal data class DetailScreenAction(
    val onBackClick: () -> Unit,
    val onRetryClick: () -> Unit,
    val onMoreClick: () -> Unit,
    val onMoreMenuDismiss: () -> Unit,
    val onEditClick: () -> Unit,
    val onDeleteClick: () -> Unit,
    val onDeleteConfirmClick: () -> Unit,
    val onDeleteCancelClick: () -> Unit,
    val onSpareManagementClick: () -> Unit,
    val onReplaceCompleteClick: () -> Unit,
    val onSpareSheetExitRequest: () -> Unit,
    val onSpareCountCompleteClick: (Int) -> Unit,
)

@Composable
private fun DetailSnackbarHost(
    message: String?,
    modifier: Modifier = Modifier,
) {
    if (message == null) {
        return
    }

    OBRitSnackbar(
        message = message,
        modifier =
            modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(
                    start = DETAIL_HORIZONTAL_PADDING,
                    end = DETAIL_HORIZONTAL_PADDING,
                    bottom = DETAIL_SNACKBAR_BOTTOM_PADDING,
                ),
    )
}

private fun DetailUserMessage.toSnackbarMessage(): String =
    when (this) {
        DetailUserMessage.LOAD_FAILED -> "상세 정보를 다시 불러오지 못했어요."
        DetailUserMessage.REPLACE_FAILED -> "교체 완료 처리에 실패했어요."
        DetailUserMessage.SPARE_UPDATE_SUCCEEDED -> "여분 수량을 저장했어요."
        DetailUserMessage.SPARE_UPDATE_FAILED -> "여분 수량 저장에 실패했어요."
        DetailUserMessage.DELETE_FAILED -> "삭제에 실패했어요."
    }

private fun DetailReplacementCompletionFeedback.toReplacementCompletionDialogState(
    recordedAt: LocalDateTime,
): DetailReplacementCompletionDialogState {
    val displayItemName = itemName.ifBlank { DETAIL_REPLACEMENT_COMPLETION_FALLBACK_ITEM_NAME }

    return if (spareCount <= DETAIL_REPLACEMENT_COMPLETION_LOW_STOCK_THRESHOLD) {
        DetailReplacementCompletionDialogState(
            kind = DetailReplacementCompletionDialogKind.LowStock,
            itemName = displayItemName,
            representativeImageUrl = representativeImageUrl,
            messageLines =
                listOf(
                    "$displayItemName 여분이 얼마 남지 않았어요!",
                    "여분을 확인해주세요",
                ),
            summaryTitle = "남은 여분 갯수",
            summaryValue = "${spareCount.coerceAtLeast(0)} 개",
            recordedAtText = recordedAt.toRecordedAtText(),
        )
    } else {
        DetailReplacementCompletionDialogState(
            kind = DetailReplacementCompletionDialogKind.NextReplacement,
            itemName = displayItemName,
            representativeImageUrl = representativeImageUrl,
            messageLines =
                listOf(
                    daysComparedToPrevious().toReplacementComparisonText(),
                    "교체 시기를 잘 지키고 있어요!",
                ),
            summaryTitle = "다음 교체 예상일",
            summaryValue =
                nextReplacementDate.toNextReplacementLabel(
                    intervalDays = recommendedReplacementIntervalDays,
                ),
            recordedAtText = recordedAt.toRecordedAtText(),
        )
    }
}

private fun DetailReplacementCompletionFeedback.daysComparedToPrevious(): Int {
    val completedRecords =
        replacementRecords
            .filterNot { record -> record.isCurrent }
            .sortedByDescending { record -> record.endedDate ?: record.startedDate ?: LocalDate.MIN }
    val latestUsedDays = completedRecords.firstOrNull()?.usageDays ?: recommendedReplacementIntervalDays
    val previousUsedDays = completedRecords.drop(1).firstOrNull()?.usageDays ?: latestUsedDays

    return latestUsedDays - previousUsedDays
}

private fun Int.toReplacementComparisonText(): String =
    when {
        this < 0 -> "지난번보다 ${-this}일 빠르게 교체했어요."
        this > 0 -> "지난번보다 ${this}일 늦게 교체했어요."
        else -> "지난번과 같은 주기로 교체했어요."
    }

private fun LocalDate?.toNextReplacementLabel(intervalDays: Int): String {
    val replacementDate = this ?: return DETAIL_REPLACEMENT_COMPLETION_UNKNOWN_DATE_TEXT
    val daysLabel = intervalDays.coerceAtLeast(0)

    return "${replacementDate.monthValue}월 ${replacementDate.dayOfMonth}일(${daysLabel}일 후)"
}

private fun LocalDateTime.toRecordedAtText(): String {
    val periodText =
        if (hour < NOON_HOUR) {
            "오전"
        } else {
            "오후"
        }
    val displayHour =
        when (val hourInHalfDay = hour % HALF_DAY_HOURS) {
            0 -> HALF_DAY_HOURS
            else -> hourInHalfDay
        }

    return String.format(
        Locale.KOREAN,
        "%04d. %02d. %02d %s %02d:%02d 기록됨",
        year,
        monthValue,
        dayOfMonth,
        periodText,
        displayHour,
        minute,
    )
}

private data class DetailSnackbarEvent(
    val message: String,
    val sequence: Long,
)

private fun DetailSnackbarEvent?.nextSequence(): Long = (this?.sequence ?: 0L) + 1L

private const val DETAIL_REPLACEMENT_COMPLETION_LOW_STOCK_THRESHOLD = 1
private const val DETAIL_REPLACEMENT_COMPLETION_FALLBACK_ITEM_NAME = "소모품"
private const val DETAIL_REPLACEMENT_COMPLETION_UNKNOWN_DATE_TEXT = "날짜 미정"
private const val HALF_DAY_HOURS = 12
private const val NOON_HOUR = 12
private val DETAIL_HORIZONTAL_PADDING = 20.dp
private val DETAIL_SNACKBAR_BOTTOM_PADDING = 104.dp
private const val DETAIL_SNACKBAR_VISIBLE_MILLIS = 2_500L
