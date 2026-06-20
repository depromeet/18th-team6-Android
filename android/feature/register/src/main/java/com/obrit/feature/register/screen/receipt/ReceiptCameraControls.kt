package com.obrit.feature.register.screen.receipt

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R

/**
 * 하단 컨트롤: 좌 갤러리 · 중앙 셔터 · 우 전/후면 전환.
 * 표시 전용 — 동작은 상위(화면)에서 콜백으로 주입한다.
 *
 * 원형 버튼 배경은 Figma의 backdrop-blur(6px) 대신 반투명 검정으로 처리한다.
 * Compose에서 실제 backdrop blur는 별도 라이브러리가 필요해 over-engineering을 피한다.
 */
@Composable
internal fun ReceiptCameraControls(
    onGalleryClick: () -> Unit,
    onShutterClick: () -> Unit,
    onFlipClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(BUTTON_GAP.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleIconButton(
            iconRes = R.drawable.ic_camera_gallery,
            contentDescription = GALLERY_DESCRIPTION,
            onClick = onGalleryClick,
        )
        ShutterButton(onClick = onShutterClick)
        CircleIconButton(
            iconRes = R.drawable.ic_camera_flip,
            contentDescription = FLIP_DESCRIPTION,
            onClick = onFlipClick,
        )
    }
}

@Composable
private fun CircleIconButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(SIDE_BUTTON_SIZE.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = SIDE_BUTTON_ALPHA))
                .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(SIDE_ICON_SIZE.dp),
        )
    }
}

@Composable
private fun ShutterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(SHUTTER_SIZE.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(SHUTTER_SIZE.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val ringStroke = SHUTTER_RING_STROKE.dp.toPx()
            // 외곽 링
            drawCircle(
                color = Color.White,
                radius = size.minDimension / 2f - ringStroke / 2f,
                center = center,
                style = Stroke(width = ringStroke),
            )
            // 내부 채워진 원
            drawCircle(
                color = Color.White,
                radius = SHUTTER_INNER_RADIUS.dp.toPx(),
                center = center,
            )
        }
    }
}

private const val BUTTON_GAP = 48
private const val SIDE_BUTTON_SIZE = 60
private const val SIDE_ICON_SIZE = 28
private const val SIDE_BUTTON_ALPHA = 0.4f
private const val SHUTTER_SIZE = 68
private const val SHUTTER_RING_STROKE = 4
private const val SHUTTER_INNER_RADIUS = 26

private const val GALLERY_DESCRIPTION = "갤러리에서 선택"
private const val FLIP_DESCRIPTION = "전후면 전환"
