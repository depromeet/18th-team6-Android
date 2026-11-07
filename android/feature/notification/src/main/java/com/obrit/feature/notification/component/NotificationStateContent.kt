package com.obrit.feature.notification.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography

/** 알림함과 알림 설정이 같이 쓰는 로딩·실패 상태. 두 화면 모두 조회 실패에 재시도를 준다(명세서 12.1). */
@Composable
internal fun NotificationLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
internal fun NotificationLoadFailed(
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
    message: String = NOTIFICATION_LOAD_FAILED_TEXT,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            style = typography.base,
            color = colors.gray500,
            textAlign = TextAlign.Center,
        )
        TextButton(onClick = onRetryClick) {
            Text(
                text = NOTIFICATION_RETRY_TEXT,
                style = typography.base,
                color = colors.green300,
            )
        }
    }
}

private const val NOTIFICATION_LOAD_FAILED_TEXT = "알림을 불러오지 못했어요"
private const val NOTIFICATION_RETRY_TEXT = "다시 시도"
