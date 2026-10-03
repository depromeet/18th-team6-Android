package com.obrit.feature.notification.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun NotificationSettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val isPermissionGranted by rememberNotificationPermissionState()

    // 설정 API가 아직 없어(#162) 화면 안에서만 상태를 들고 있다. 화면을 나가면 초기값으로 돌아간다.
    var state by remember { mutableStateOf(NotificationSettingsUiState()) }

    NotificationSettingsScreenContent(
        state = state,
        isPermissionGranted = isPermissionGranted,
        action =
            NotificationSettingsScreenAction(
                onBackClick = onBackClick,
                onPermissionBannerClick = context::openAppNotificationSettings,
                onReplacementChange = { state = state.copy(isReplacementEnabled = it) },
                onSpareShortageChange = { state = state.copy(isSpareShortageEnabled = it) },
            ),
        modifier = modifier,
    )
}

internal data class NotificationSettingsScreenAction(
    val onBackClick: () -> Unit,
    val onPermissionBannerClick: () -> Unit,
    val onReplacementChange: (Boolean) -> Unit,
    val onSpareShortageChange: (Boolean) -> Unit,
)
