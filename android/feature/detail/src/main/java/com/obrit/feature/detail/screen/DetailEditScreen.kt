@file:Suppress("LongMethod")

package com.obrit.feature.detail.screen

import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.component.snackbar.OBRitSnackbar
import com.obrit.feature.detail.component.DetailBackExitConfirmDialog
import com.obrit.feature.detail.viewmodel.DetailEditSideEffect
import com.obrit.feature.detail.viewmodel.DetailEditViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun DetailEditScreen(
    consumableId: Long,
    onCloseClick: () -> Unit,
    onCompleteClick: (DetailEditSubmitResult) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailEditViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()
    var saveFailureMessage by remember { mutableStateOf<String?>(null) }
    var isExitConfirmVisible by remember { mutableStateOf(false) }

    val requestCloseConfirm = {
        isExitConfirmVisible = true
    }
    val dismissCloseConfirm = {
        isExitConfirmVisible = false
    }
    val confirmClose = {
        isExitConfirmVisible = false
        onCloseClick()
    }

    BackHandler(enabled = !isExitConfirmVisible) {
        requestCloseConfirm()
    }

    LaunchedEffect(consumableId) {
        viewModel.load(consumableId)
    }

    LaunchedEffect(saveFailureMessage) {
        if (saveFailureMessage != null) {
            delay(DETAIL_EDIT_SNACKBAR_VISIBLE_MILLIS)
            saveFailureMessage = null
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        DetailEditScreenContent(
            state = state,
            action =
                DetailEditScreenAction(
                    onCloseClick = requestCloseConfirm,
                    onRetryClick = {
                        viewModel.load(consumableId)
                    },
                    onSubmitClick = { result ->
                        viewModel.save(
                            consumableId = result.consumableId,
                            name = result.name,
                            replacementIntervalDays = result.replacementIntervalDays,
                            representativeIconId = result.representativeIconId,
                        )
                    },
                ),
            modifier = Modifier.fillMaxSize(),
        )

        DetailEditSnackbarHost(
            message = saveFailureMessage,
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        if (isExitConfirmVisible) {
            DetailBackExitConfirmDialog(
                onCancelClick = dismissCloseConfirm,
                onExitClick = confirmClose,
            )
        }
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is DetailEditSideEffect.EditCompleted -> {
                onCompleteClick(
                    DetailEditSubmitResult(
                        consumableId = sideEffect.consumableId,
                        name = sideEffect.name,
                        replacementIntervalDays = sideEffect.replacementIntervalDays,
                        representativeIconId = sideEffect.representativeIconId,
                    ),
                )
            }
            is DetailEditSideEffect.ShowSaveFailed -> {
                saveFailureMessage = DETAIL_EDIT_SAVE_FAILURE_MESSAGE
            }
        }
    }
}

data class DetailEditSubmitResult(
    val consumableId: Long,
    val name: String,
    val replacementIntervalDays: Int,
    val representativeIconId: Long?,
)

internal data class DetailEditScreenAction(
    val onCloseClick: () -> Unit,
    val onRetryClick: () -> Unit,
    val onSubmitClick: (DetailEditSubmitResult) -> Unit,
)

@Composable
private fun DetailEditSnackbarHost(
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
                    start = DETAIL_EDIT_SNACKBAR_HORIZONTAL_PADDING,
                    end = DETAIL_EDIT_SNACKBAR_HORIZONTAL_PADDING,
                    bottom = DETAIL_EDIT_SNACKBAR_BOTTOM_PADDING,
                ),
    )
}

private val DETAIL_EDIT_SNACKBAR_HORIZONTAL_PADDING = 20.dp
private val DETAIL_EDIT_SNACKBAR_BOTTOM_PADDING = 104.dp
private const val DETAIL_EDIT_SNACKBAR_VISIBLE_MILLIS = 2_500L
private const val DETAIL_EDIT_SAVE_FAILURE_MESSAGE = "편집 내용을 저장하지 못했어요."
