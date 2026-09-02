package com.obrit.obrit.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * 알림 정책 7.4에 따라 온보딩 등록 완료 직후 알림 권한을 요청한다.
 *
 * 반환된 람다를 등록 완료 화면의 CTA에 연결한다. 화면 진입 즉시 요청하면 축하 화면을 시스템
 * 다이얼로그가 덮어버려, 정책이 근거로 삼은 "등록했다는 맥락"이 사용자에게 전달되지 않는다.
 * 사용자가 화면을 읽고 스스로 다음으로 넘어가는 시점에 요청한 뒤, 응답을 받고 [onFinish]로 진행한다.
 *
 * 권한을 거부해도 재요청하지 않는다(정책 7.4). 거부한 사용자는 알림 목록 화면의
 * 안내 배너로 OS 설정에 갈 수 있다(FR-NOTI-12).
 */
@Composable
internal fun rememberNotificationPermissionRequest(onFinish: () -> Unit): () -> Unit {
    val context = LocalContext.current
    val currentOnFinish by rememberUpdatedState(onFinish)
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            currentOnFinish()
        }

    return remember(launcher, context) {
        {
            if (context.needsNotificationPermission()) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                currentOnFinish()
            }
        }
    }
}

private fun Context.needsNotificationPermission(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
        PackageManager.PERMISSION_GRANTED
