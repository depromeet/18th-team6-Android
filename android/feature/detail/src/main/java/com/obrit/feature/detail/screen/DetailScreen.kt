@file:Suppress("CyclomaticComplexMethod", "LongMethod", "LongParameterList")

package com.obrit.feature.detail.screen

import androidx.activity.compose.BackHandler
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
import com.obrit.feature.detail.component.DetailBackExitConfirmDialog
import com.obrit.feature.detail.viewmodel.DetailEditResult
import com.obrit.feature.detail.viewmodel.DetailMenuAction
import com.obrit.feature.detail.viewmodel.DetailSideEffect
import com.obrit.feature.detail.viewmodel.DetailUserMessage
import com.obrit.feature.detail.viewmodel.DetailViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

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
    var backExitConfirmTarget by remember { mutableStateOf<DetailBackExitConfirmTarget?>(null) }
    var snackbarEvent by remember { mutableStateOf<DetailSnackbarEvent?>(null) }
    val isBackExitConfirmVisible = backExitConfirmTarget != null
    val showSnackbar: (String) -> Unit = { message ->
        snackbarEvent =
            DetailSnackbarEvent(
                message = message,
                sequence = snackbarEvent.nextSequence(),
            )
    }
    val requestBackExitConfirm: () -> Unit = {
        isMoreMenuExpanded = false
        backExitConfirmTarget =
            if (isSpareSheetVisible) {
                DetailBackExitConfirmTarget.SpareSheet
            } else {
                DetailBackExitConfirmTarget.Navigation
            }
    }
    val requestSpareSheetExitConfirm = {
        backExitConfirmTarget = DetailBackExitConfirmTarget.SpareSheet
    }
    val dismissBackExitConfirm = {
        backExitConfirmTarget = null
    }
    val confirmBackExit: () -> Unit = {
        val target = backExitConfirmTarget
        backExitConfirmTarget = null
        when (target) {
            DetailBackExitConfirmTarget.SpareSheet -> {
                isSpareSheetVisible = false
            }
            DetailBackExitConfirmTarget.Navigation -> {
                isSpareSheetVisible = false
                viewModel.onBackClick()
            }
            null -> Unit
        }
    }

    BackHandler(enabled = !isBackExitConfirmVisible) {
        requestBackExitConfirm()
    }

    LaunchedEffect(id) {
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
            onBackClick = requestBackExitConfirm,
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
            onSpareSheetDismiss = {
                isSpareSheetVisible = false
            },
            onSpareSheetExitRequest = requestSpareSheetExitConfirm,
            onSpareCountCompleteClick = {
                // 해야할일: DetailViewModel에 여분 수량 수정 action이 추가되면 저장 처리 연결.
                isSpareSheetVisible = false
                showSnackbar("여분 수량 저장 연결이 필요해요.")
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

        if (isBackExitConfirmVisible) {
            DetailBackExitConfirmDialog(
                onCancelClick = dismissBackExitConfirm,
                onExitClick = confirmBackExit,
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
            is DetailSideEffect.ReplaceCompletedFeedback -> {
                showSnackbar("교체 완료했어요.")
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
    val onSpareSheetDismiss: () -> Unit,
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
        DetailUserMessage.DELETE_FAILED -> "삭제에 실패했어요."
    }

private data class DetailSnackbarEvent(
    val message: String,
    val sequence: Long,
)

private enum class DetailBackExitConfirmTarget {
    Navigation,
    SpareSheet,
}

private fun DetailSnackbarEvent?.nextSequence(): Long = (this?.sequence ?: 0L) + 1L

private val DETAIL_HORIZONTAL_PADDING = 20.dp
private val DETAIL_SNACKBAR_BOTTOM_PADDING = 104.dp
private const val DETAIL_SNACKBAR_VISIBLE_MILLIS = 2_500L
