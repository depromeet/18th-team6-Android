package com.obrit.obrit.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.obrit.obrit.MainActivity
import com.obrit.obrit.shared.data.repository.NotificationRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.android.ext.android.inject
import com.obrit.android.app.R as AppR
import com.obrit.android.core.designsystem.R as DesignSystemR

// lint가 onNewToken() 구현을 요구하지만 그것은 Firebase가 deprecated로 표시한 구버전 콜백이다.
// 공식 권장은 FID를 주는 onRegistered()이고, lint 규칙이 아직 이 전환을 반영하지 못했다.
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class ObritMessagingService : FirebaseMessagingService() {
    private val notificationRepository: NotificationRepository by inject()

    /**
     * onRegistered는 FID가 새로 만들어질 때뿐 아니라 auto-init이 켜진 앱 시작 시에도 호출된다.
     * 그래서 등록에 실패해도 다음 앱 실행 때 다시 시도된다. 별도의 재시도 스케줄링을 두지 않는 이유다.
     *
     * 이 콜백은 이미 백그라운드 스레드에서 실행되고 반환하면 서비스가 종료될 수 있어,
     * 등록이 끝날 때까지 이 스레드에서 기다린다. 다만 이 프로젝트의 HTTP client에는 기본 타임아웃이 없어
     * 응답 없는 서버에 스레드가 무한정 묶일 수 있다. 그래서 호출 전체에 상한을 둔다.
     * registerDevice는 저장된 userId가 없으면 회원 등록까지 하므로 요청이 두 번 나갈 수 있고,
     * 상한은 그 둘을 합친 시간이다.
     */
    override fun onRegistered(installationId: String) {
        Log.d(OBRIT_MESSAGING_TAG, "Firebase Installation ID: $installationId")

        val result =
            runBlocking {
                withTimeoutOrNull(REGISTER_DEVICE_TIMEOUT_MILLIS) {
                    notificationRepository.registerDevice(installationId)
                }
            }

        if (result == null) {
            Log.w(OBRIT_MESSAGING_TAG, "FID 등록 시간 초과. 다음 앱 실행 때 다시 시도된다.")
            return
        }

        result.onFailure { throwable ->
            Log.w(OBRIT_MESSAGING_TAG, "FID 등록 실패. 다음 앱 실행 때 다시 시도된다.", throwable)
        }
    }

    // 앱이 백그라운드일 때 notification 메시지는 시스템이 트레이에 띄우므로 이 콜백을 타지 않는다.
    // 지금 처리하는 것은 notification 메시지의 포그라운드 수신뿐이다.
    // data-only 메시지도 이 콜백으로 오지만, 서버 payload 스펙이 없어 아직 무엇을 표시할지 정할 수 없다.
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
                .setContentIntent(launchAppIntent())
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat
            .from(this)
            .notify(System.currentTimeMillis().toInt(), notification)
    }

    /**
     * 탭하면 앱을 여는 인텐트. 없으면 알림이 눌리지 않고 setAutoCancel도 동작하지 않는다.
     *
     * 백그라운드에서 받은 notification 메시지는 시스템이 알아서 이 인텐트를 붙이지만,
     * 포그라운드 수신은 여기서 알림을 직접 만들기 때문에 직접 넣어야 한다.
     * 알림 종류에 따라 목적지를 나누는 것(FR-NOTI-07)은 서버 payload 스펙이 정해진 뒤의 일이다.
     */
    private fun launchAppIntent(): PendingIntent =
        PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
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
private const val REGISTER_DEVICE_TIMEOUT_MILLIS = 10_000L
