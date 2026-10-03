package com.obrit.feature.notification.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.feature.notification.component.NotificationDaysBottomSheet
import com.obrit.feature.notification.component.NotificationTimeBottomSheet

@Composable
fun NotificationSettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val isPermissionGranted by rememberNotificationPermissionState()

    // 설정 API가 아직 없어(#162) 화면 안에서만 상태를 들고 있다. 화면을 나가면 초기값으로 돌아간다.
    var state by remember { mutableStateOf(NotificationSettingsUiState()) }
    var openSheet by remember { mutableStateOf<NotificationSettingsSheet?>(null) }

    NotificationSettingsScreenContent(
        state = state,
        isPermissionGranted = isPermissionGranted,
        action =
            NotificationSettingsScreenAction(
                onBackClick = onBackClick,
                onPermissionBannerClick = context::openAppNotificationSettings,
                onReplacementChange = { state = state.copy(isReplacementEnabled = it) },
                onSpareShortageChange = { state = state.copy(isSpareShortageEnabled = it) },
                onSheetRequest = { openSheet = it },
            ),
        modifier = modifier,
    )

    openSheet?.let { sheet ->
        NotificationSettingsSheetHost(
            sheet = sheet,
            state = state,
            onStateChange = { state = it },
            onDismissRequest = { openSheet = null },
        )
    }
}

/** 디자인에 확인 버튼이 없어서 휠을 멈춘 값이 바로 반영되고, 시트를 닫으면 그대로 확정된다. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationSettingsSheetHost(
    sheet: NotificationSettingsSheet,
    state: NotificationSettingsUiState,
    onStateChange: (NotificationSettingsUiState) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val colors = LocalOBRitColor.current

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(0.dp),
        containerColor = Color.Transparent,
        contentColor = colors.common00,
        scrimColor = colors.backgroundDefaultDimDefault,
        dragHandle = null,
    ) {
        NotificationSettingsSheetContent(
            sheet = sheet,
            state = state,
            onStateChange = onStateChange,
        )
    }
}

@Composable
private fun NotificationSettingsSheetContent(
    sheet: NotificationSettingsSheet,
    state: NotificationSettingsUiState,
    onStateChange: (NotificationSettingsUiState) -> Unit,
) {
    val sheetModifier =
        Modifier
            .fillMaxWidth()
            .navigationBarsPadding()

    when (sheet) {
        NotificationSettingsSheet.LeadDays ->
            NotificationDaysBottomSheet(
                selectedDays = state.leadDays,
                daySuffix = "전",
                onDaysChange = { onStateChange(state.copy(leadDays = it)) },
                modifier = sheetModifier,
            )

        NotificationSettingsSheet.OverdueDays ->
            NotificationDaysBottomSheet(
                selectedDays = state.overdueDays,
                daySuffix = "후",
                onDaysChange = { onStateChange(state.copy(overdueDays = it)) },
                modifier = sheetModifier,
            )

        NotificationSettingsSheet.SendTime ->
            NotificationTimeBottomSheet(
                selectedTime = state.sendTime,
                onTimeChange = { onStateChange(state.copy(sendTime = it)) },
                modifier = sheetModifier,
            )
    }
}

internal data class NotificationSettingsScreenAction(
    val onBackClick: () -> Unit,
    val onPermissionBannerClick: () -> Unit,
    val onReplacementChange: (Boolean) -> Unit,
    val onSpareShortageChange: (Boolean) -> Unit,
    val onSheetRequest: (NotificationSettingsSheet) -> Unit,
)
