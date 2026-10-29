package com.obrit.feature.notification.screen

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * OS 알림 권한 허용 여부. 앱은 이 값을 읽기만 할 수 있고 켜는 것은 OS 설정에서만 가능하다.
 *
 * 사용자가 OS 설정에서 알림을 켜고 돌아오면 화면이 그 즉시 반영되어야 하므로 `ON_RESUME`마다 다시 읽는다.
 * 알림 목록과 알림 설정 두 화면이 같은 규칙을 쓴다.
 */
@Composable
internal fun rememberNotificationPermissionState(): State<Boolean> {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val isGranted = remember { mutableStateOf(context.areNotificationsEnabled()) }

    DisposableEffect(lifecycleOwner, context) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    isGranted.value = context.areNotificationsEnabled()
                }
            }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    return isGranted
}

internal fun Context.openAppNotificationSettings() {
    val intent =
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)

    startActivity(intent)
}

private fun Context.areNotificationsEnabled(): Boolean = NotificationManagerCompat.from(this).areNotificationsEnabled()
