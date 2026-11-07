package com.obrit.feature.detail.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.obrit.android.core.designsystem.component.dim.OBRitDim
import com.obrit.android.core.designsystem.component.modal.OBRitModal
import com.obrit.android.core.designsystem.component.modal.OBRitModalAppearance
import com.obrit.android.core.designsystem.theme.OBRitTheme

@Composable
internal fun DetailDeleteConfirmDialog(
    onCancelClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onCancelClick,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        DetailDeleteConfirmContent(
            onCancelClick = onCancelClick,
            onDeleteClick = onDeleteClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun DetailDeleteConfirmContent(
    onCancelClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OBRitModal(
        title = DETAIL_DELETE_CONFIRM_TITLE,
        description = DETAIL_DELETE_CONFIRM_DESCRIPTION,
        primaryButtonText = DETAIL_DELETE_CONFIRM_BUTTON_TEXT,
        onPrimaryButtonClick = onDeleteClick,
        appearance = OBRitModalAppearance.Dark,
        showImage = false,
        secondaryButtonText = DETAIL_DELETE_CANCEL_BUTTON_TEXT,
        onSecondaryButtonClick = onCancelClick,
        modifier = modifier,
    )
}

@Preview(name = "DetailDeleteConfirmDialog", showBackground = true)
@Composable
private fun DetailDeleteConfirmDialogPreview() {
    OBRitTheme(dynamicColor = false) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            OBRitDim()
            DetailDeleteConfirmContent(
                onCancelClick = {},
                onDeleteClick = {},
            )
        }
    }
}

private const val DETAIL_DELETE_CONFIRM_TITLE = "해당 소모품을 삭제하시겠습니까?"
private const val DETAIL_DELETE_CONFIRM_DESCRIPTION = ""
private const val DETAIL_DELETE_CONFIRM_BUTTON_TEXT = "삭제"
private const val DETAIL_DELETE_CANCEL_BUTTON_TEXT = "아니요"
