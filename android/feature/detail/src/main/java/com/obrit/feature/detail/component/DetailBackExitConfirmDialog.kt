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
internal fun DetailBackExitConfirmDialog(
    onCancelClick: () -> Unit,
    onExitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onCancelClick,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        DetailBackExitConfirmContent(
            onCancelClick = onCancelClick,
            onExitClick = onExitClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun DetailBackExitConfirmContent(
    onCancelClick: () -> Unit,
    onExitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OBRitModal(
        title = DETAIL_BACK_EXIT_TITLE,
        description = DETAIL_BACK_EXIT_DESCRIPTION,
        primaryButtonText = DETAIL_BACK_EXIT_PRIMARY_TEXT,
        onPrimaryButtonClick = onExitClick,
        appearance = OBRitModalAppearance.Dark,
        showImage = false,
        secondaryButtonText = DETAIL_BACK_EXIT_CANCEL_TEXT,
        onSecondaryButtonClick = onCancelClick,
        modifier = modifier,
    )
}

@Preview(name = "DetailBackExitConfirmDialog", showBackground = true)
@Composable
private fun DetailBackExitConfirmDialogPreview() {
    OBRitTheme(dynamicColor = false) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            OBRitDim()
            DetailBackExitConfirmContent(
                onCancelClick = {},
                onExitClick = {},
            )
        }
    }
}

private const val DETAIL_BACK_EXIT_TITLE = "편집하기를 종료하시겠습니까?"
private const val DETAIL_BACK_EXIT_DESCRIPTION = "지금까지의 내용은 저장되지 않습니다."
private const val DETAIL_BACK_EXIT_CANCEL_TEXT = "아니요"
private const val DETAIL_BACK_EXIT_PRIMARY_TEXT = "종료하기"
