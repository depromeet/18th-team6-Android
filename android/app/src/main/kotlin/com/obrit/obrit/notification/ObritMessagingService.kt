package com.obrit.obrit.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.obrit.android.app.R as AppR
import com.obrit.android.core.designsystem.R as DesignSystemR

// lint가 onNewToken() 구현을 요구하지만 그것은 Firebase가 deprecated로 표시한 구버전 콜백이다.
// 공식 권장은 FID를 주는 onRegistered()이고, lint 규칙이 아직 이 전환을 반영하지 못했다.
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class ObritMessagingService : FirebaseMessagingService() {
    override fun onRegistered(installationId: String) {
        Log.d(OBRIT_MESSAGING_TAG, "Firebase Installation ID: $installationId")
        // TODO(#101): FID 등록 API가 확정되면 여기서 서버로 전송한다.
    }

    // 앱이 백그라운드일 때 notification 메시지는 시스템이 트레이에 띄우므로 이 콜백을 타지 않는다.
    // 여기로 오는 것은 포그라운드 수신과 data 메시지다.
    override fun onMessageReceived(message: RemoteMessage) {
        val notification = message.notification ?: return
        showNotification(title = notification.title, body = notification.body)
    }

    private fun showNotification(
        title: String?,
        body: String?,
    ) {
        if (title == null && body == null) return

        // 권한 확인은 lint가 데이터 흐름으로 추적할 수 있도록 이 함수 안에 직접 둔다.
        // 별도 함수로 빼면 NotificationManagerCompat.notify 호출에 MissingPermission 경고가 뜬다.
        val isPermissionGranted =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
        if (!isPermissionGranted) return

        val notification =
            NotificationCompat
                .Builder(this, getString(AppR.string.notification_channel_id))
                .setSmallIcon(DesignSystemR.drawable.ic_topbar_bell)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat
            .from(this)
            .notify(System.currentTimeMillis().toInt(), notification)
    }
}

/**
 * 알림 채널을 만든다. Application에서 앱 시작 시 한 번 호출한다.
 *
 * 채널은 1개로 시작한다. 유형별로 나누면 OS 설정에서 유형별로 꺼져 앱 내 알림 설정(FR-NOTI-09)과 상태가 갈라진다.
 * IMPORTANCE_HIGH는 heads-up 배너를 띄운다. 교체 시기를 놓치지 않는 것이 이 앱의 핵심 가치이므로
 * 상태바에만 쌓이지 않게 한다. 과잉 알림은 정책 7.1의 반복 상한과 7.2의 조용 시간이 통제한다.
 *
 * 중요도와 채널 분리는 생성 후 코드로 바꿀 수 없다. 바꾸려면 채널 ID를 새로 만들거나 앱을 재설치해야 한다.
 */
fun createObritNotificationChannel(context: Context) {
    val channel =
        NotificationChannel(
            context.getString(AppR.string.notification_channel_id),
            context.getString(AppR.string.notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH,
        )

    NotificationManagerCompat.from(context).createNotificationChannel(channel)
}

private const val OBRIT_MESSAGING_TAG = "ObritMessaging"
