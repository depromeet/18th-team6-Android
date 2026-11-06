package com.obrit.feature.notification.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.obrit.feature.notification.NotificationReceivedSignal
import com.obrit.feature.notification.viewmodel.NotificationItem
import com.obrit.feature.notification.viewmodel.NotificationSideEffect
import com.obrit.feature.notification.viewmodel.NotificationViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun NotificationScreen(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onDeepLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val receivedSignal = koinInject<NotificationReceivedSignal>()
    val state by viewModel.collectAsState()
    val isPermissionGranted by rememberNotificationPermissionState()

    // 알림함에 들어올 때와 앱이 다시 앞으로 나올 때 새로 불러온다. 그 사이 도착한 알림을 놓치지 않기 위해서다.
    LifecycleResumeEffect(viewModel) {
        viewModel.refresh()
        onPauseOrDispose {}
    }
    // 알림함을 보고 있는 중에 푸시가 오면 바로 목록에 넣는다.
    LaunchedEffect(receivedSignal) {
        receivedSignal.events.collect { viewModel.refresh() }
    }

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

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is NotificationSideEffect.OpenDeepLink -> onDeepLinkClick(sideEffect.deepLink)
        }
    }
}

internal data class NotificationScreenAction(
    val onBackClick: () -> Unit,
    val onSettingsClick: () -> Unit,
    val onRetryClick: () -> Unit,
    val onPermissionBannerClick: () -> Unit,
    val onItemClick: (NotificationItem) -> Unit,
)
