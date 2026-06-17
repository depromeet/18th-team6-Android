package com.obrit.feature.register.screen.receipt

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.obrit.android.core.designsystem.component.button.OBRitButtonDefaults
import com.obrit.android.core.designsystem.component.button.OBRitLargeFilledButton
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

/**
 * 영수증 분석 실패 시 보여주는 오버레이.
 * 자동 재시도는 하지 않고, 사용자가 "다시 시도"를 누를 때만 재분석한다.
 */
@Composable
internal fun ReceiptAnalyzeErrorOverlay(
    imageUri: Uri,
    onRetry: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                ),
        contentAlignment = Alignment.Center,
    ) {
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
            verticalArrangement = Arrangement.spacedBy(TEXT_TO_BUTTON_GAP.dp),
            modifier = Modifier.padding(horizontal = AtomSpacing.S5.dp),
        ) {
            ErrorTextBlock()
            RetryAndCloseActions(onRetry = onRetry, onClose = onClose)
        }
    }
}

@Composable
private fun ErrorTextBlock() {
    val typography = LocalOBRitTypography.current
    val colors = LocalOBRitColor.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(TITLE_TO_BODY_GAP.dp),
    ) {
        Text(
            text = ERROR_TITLE,
            style = typography.xl5.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            textAlign = TextAlign.Center,
        )
        Text(
            text = ERROR_BODY,
            style = typography.xl.copy(fontWeight = FontWeight.Medium),
            color = colors.gray100,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun RetryAndCloseActions(
    onRetry: () -> Unit,
    onClose: () -> Unit,
) {
    val typography = LocalOBRitTypography.current
    val colors = LocalOBRitColor.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(RETRY_TO_CLOSE_GAP.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        OBRitLargeFilledButton(
            onClick = onRetry,
            colors = OBRitButtonDefaults.positiveButtonColors(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = RETRY_LABEL,
                style = typography.xl.copy(fontWeight = FontWeight.SemiBold),
            )
        }
        Text(
            text = CLOSE_LABEL,
            style =
                typography.base.copy(
                    color = colors.gray100,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline,
                ),
            modifier =
                Modifier.clickable(role = Role.Button, onClick = onClose),
        )
    }
}

private const val BACKGROUND_BLUR = 20
private const val DIM_ALPHA = 0.7f
private const val TEXT_TO_BUTTON_GAP = 32
private const val TITLE_TO_BODY_GAP = 8
private const val RETRY_TO_CLOSE_GAP = 16

private const val ERROR_TITLE = "영수증 인식에 실패했어요"
private const val ERROR_BODY = "잠시 후 다시 시도해주세요"
private const val RETRY_LABEL = "다시 시도"
private const val CLOSE_LABEL = "닫기"
