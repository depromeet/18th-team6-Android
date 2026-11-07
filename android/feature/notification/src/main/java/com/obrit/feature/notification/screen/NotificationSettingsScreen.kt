package com.obrit.feature.notification.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.component.snackbar.OBRitSnackbar
import com.obrit.android.core.designsystem.component.snackbar.SnackbarIcon
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.feature.notification.component.NotificationTimeBottomSheet
import com.obrit.feature.notification.viewmodel.NotificationSettingType
import com.obrit.feature.notification.viewmodel.NotificationSettingsSideEffect
import com.obrit.feature.notification.viewmodel.NotificationSettingsUiState
import com.obrit.feature.notification.viewmodel.NotificationSettingsViewModel
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.time.LocalTime

@Composable
fun NotificationSettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationSettingsViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.collectAsState()
    val isPermissionGranted by rememberNotificationPermissionState()
    var isSendTimeSheetOpen by remember { mutableStateOf(false) }
    var isSaveFailedVisible by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        NotificationSettingsScreenContent(
            state = state,
            isPermissionGranted = isPermissionGranted,
            action =
                NotificationSettingsScreenAction(
                    onBackClick = onBackClick,
                    onRetryClick = viewModel::retryLoad,
                    onPermissionBannerClick = context::openAppNotificationSettings,
                    onTypeChange = viewModel::onTypeChange,
                    onSendTimeClick = { isSendTimeSheetOpen = true },
                ),
        )
        SaveFailedSnackbar(
            isVisible = isSaveFailedVisible,
            onDismiss = { isSaveFailedVisible = false },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    val successState = state as? NotificationSettingsUiState.Success
    if (isSendTimeSheetOpen && successState != null) {
        NotificationSendTimeSheet(
            sendTime = successState.sendTime,
            onTimeChange = viewModel::onSendTimeChange,
            onDismissRequest = { isSendTimeSheetOpen = false },
        )
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is NotificationSettingsSideEffect.SaveFailed -> isSaveFailedVisible = true
        }
    }
}

/** 저장에 실패하면 ViewModel이 서버 값으로 되돌리고 여기서 사유를 알린다(명세서 12.1). */
@Composable
private fun SaveFailedSnackbar(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!isVisible) return

    val currentOnDismiss by rememberUpdatedState(onDismiss)

    LaunchedEffect(Unit) {
        delay(SNACKBAR_DURATION_MILLIS)
        currentOnDismiss()
    }

    OBRitSnackbar(
        message = NOTIFICATION_SETTINGS_SAVE_FAILED_TEXT,
        icon = SnackbarIcon.Error,
        modifier =
            modifier
                .navigationBarsPadding()
                .padding(horizontal = AtomSpacing.S5.dp, vertical = AtomSpacing.S5.dp),
    )
}

/** 디자인에 확인 버튼이 없어서 휠을 멈춘 값이 바로 반영되고, 시트를 닫으면 그대로 확정된다. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationSendTimeSheet(
    sendTime: LocalTime,
    onTimeChange: (LocalTime) -> Unit,
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
        NotificationTimeBottomSheet(
            selectedTime = sendTime,
            onTimeChange = onTimeChange,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
        )
    }
}

private const val NOTIFICATION_SETTINGS_SAVE_FAILED_TEXT = "알림 설정을 저장하지 못했어요"
private const val SNACKBAR_DURATION_MILLIS = 3_000L

internal data class NotificationSettingsScreenAction(
    val onBackClick: () -> Unit,
    val onRetryClick: () -> Unit,
    val onPermissionBannerClick: () -> Unit,
    val onTypeChange: (NotificationSettingType, Boolean) -> Unit,
    val onSendTimeClick: () -> Unit,
)
