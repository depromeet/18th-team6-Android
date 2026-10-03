package com.obrit.feature.notification.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.obrit.feature.notification.viewmodel.NotificationItem
import com.obrit.feature.notification.viewmodel.NotificationViewModel
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun NotificationScreen(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.collectAsState()
    val isPermissionGranted by rememberNotificationPermissionState()

    NotificationScreenContent(
        state = state,
        isNotificationEnabled = isPermissionGranted,
        action =
            NotificationScreenAction(
                onBackClick = onBackClick,
                onSettingsClick = onSettingsClick,
                onRetryClick = viewModel::retryLoad,
                onPermissionBannerClick = context::openAppNotificationSettings,
                onItemClick = viewModel::onItemClick,
            ),
        modifier = modifier,
    )
}

internal data class NotificationScreenAction(
    val onBackClick: () -> Unit,
    val onSettingsClick: () -> Unit,
    val onRetryClick: () -> Unit,
    val onPermissionBannerClick: () -> Unit,
    val onItemClick: (NotificationItem) -> Unit,
)
