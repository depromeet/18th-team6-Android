package com.obrit.feature.notification.screen

import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.obrit.feature.notification.viewmodel.NotificationItem
import com.obrit.feature.notification.viewmodel.NotificationViewModel
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun NotificationScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.collectAsState()
    var isNotificationEnabled by remember {
        mutableStateOf(NotificationManagerCompat.from(context).areNotificationsEnabled())
    }

    // 사용자가 시스템 알림 설정에서 알림을 켜고 돌아오면 안내 배너가 사라져야 한다.
    NotificationResumeEffect {
        isNotificationEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    NotificationScreenContent(
        state = state,
        isNotificationEnabled = isNotificationEnabled,
        action =
            NotificationScreenAction(
                onBackClick = onBackClick,
                onRetryClick = viewModel::retryLoad,
                onPermissionBannerClick = {
                    context.startActivity(appNotificationSettingsIntent(context.packageName))
                },
                onItemClick = viewModel::onItemClick,
            ),
        modifier = modifier,
    )
}

internal data class NotificationScreenAction(
    val onBackClick: () -> Unit,
    val onRetryClick: () -> Unit,
    val onPermissionBannerClick: () -> Unit,
    val onItemClick: (NotificationItem) -> Unit,
)

@Composable
private fun NotificationResumeEffect(onResume: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnResume by rememberUpdatedState(onResume)

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    currentOnResume()
                }
            }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

private fun appNotificationSettingsIntent(packageName: String): Intent =
    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
