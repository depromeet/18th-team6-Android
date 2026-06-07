package com.obrit.feature.register.screen.receipt

import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography

/**
 * 서버가 영수증 이미지를 분석하는 동안 보여주는 로딩 오버레이.
 * 카메라 촬영·갤러리 선택 등 이미지 출처와 무관하게, 이미지가 확정되면 표시된다.
 *
 * 화면을 전부 덮어 분석 중 조작을 차단한다(디자인에 상단 바·하단 컨트롤 없음).
 */
@Composable
internal fun ReceiptAnalyzingOverlay(
    imageUri: Uri,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current
    val colors = LocalOBRitColor.current

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        // 확정된 이미지를 흐리게 깔아준다. blur는 API31+에서만 적용되고
        // minSdk28의 28~30에서는 무시되어 dim만 남는다(자연스러운 degradation).
        AsyncImage(
            model = imageUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .fillMaxSize()
                    .blur(BACKGROUND_BLUR.dp),
        )
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = DIM_ALPHA)),
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SPINNER_TEXT_GAP.dp),
        ) {
            LoadingSpinner()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(TEXT_GAP.dp),
            ) {
                Text(
                    text = WAIT_TEXT,
                    style = typography.xl.copy(fontWeight = FontWeight.Medium),
                    color = colors.gray100,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = ANALYZING_TEXT,
                    style = typography.xl5.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun LoadingSpinner(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "receipt-loading")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = ROTATION_PERIOD, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "receipt-loading-angle",
    )
    Image(
        painter = painterResource(id = R.drawable.ic_receipt_loading),
        contentDescription = null,
        modifier =
            modifier
                .size(SPINNER_SIZE.dp)
                .graphicsLayer { rotationZ = angle },
    )
}

private const val BACKGROUND_BLUR = 20
private const val DIM_ALPHA = 0.7f
private const val SPINNER_SIZE = 80
private const val SPINNER_TEXT_GAP = 22
private const val TEXT_GAP = 4
private const val ROTATION_PERIOD = 1100

private const val WAIT_TEXT = "잠시만 기다려주세요..."
private const val ANALYZING_TEXT = "이미지를 분석하고 있어요"
