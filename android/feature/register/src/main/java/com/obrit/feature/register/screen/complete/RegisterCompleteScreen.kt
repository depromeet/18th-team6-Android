package com.obrit.feature.register.screen.complete

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.component.button.OBRitButtonDefaults
import com.obrit.android.core.designsystem.component.button.OBRitLargeFilledButton
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
fun RegisterCompleteScreen(
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onExit)

    val colors = LocalOBRitColor.current
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.gray900)
                .statusBarsPadding()
                .navigationBarsPadding(),
    ) {
        RegisterCompleteHero()
        RegisterCompleteCta(onExit = onExit)
    }
}

@Composable
private fun BoxScope.RegisterCompleteHero() {
    Column(
        modifier =
            Modifier
                .align(Alignment.Center)
                .offset(y = REGISTER_COMPLETE_CENTER_OFFSET_Y),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(REGISTER_COMPLETE_HERO_TO_TEXT_GAP),
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_register_complete),
            contentDescription = null,
            modifier =
                Modifier
                    .width(REGISTER_COMPLETE_HERO_WIDTH)
                    .height(REGISTER_COMPLETE_HERO_HEIGHT),
        )
        RegisterCompleteHeroText()
    }
}

@Composable
private fun RegisterCompleteHeroText() {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(REGISTER_COMPLETE_TITLE_TO_DESCRIPTION_GAP),
    ) {
        Text(
            text = REGISTER_COMPLETE_HEADLINE,
            style = typography.xl6.copy(color = colors.common00, fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
        )
        Text(
            text = REGISTER_COMPLETE_DESCRIPTION,
            style = typography.xl.copy(color = colors.gray300, fontWeight = FontWeight.Medium),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun BoxScope.RegisterCompleteCta(onExit: () -> Unit) {
    val typography = LocalOBRitTypography.current
    Column(
        modifier =
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    start = AtomSpacing.S5.dp,
                    end = AtomSpacing.S5.dp,
                    bottom = REGISTER_COMPLETE_CTA_BOTTOM_PADDING,
                ),
    ) {
        OBRitLargeFilledButton(
            onClick = onExit,
            colors = OBRitButtonDefaults.positiveButtonColors(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = REGISTER_COMPLETE_CTA_LABEL,
                style = typography.xl.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

private const val REGISTER_COMPLETE_HEADLINE = "소모품이 등록되었어요!"
private const val REGISTER_COMPLETE_DESCRIPTION = "이제부터 OBRIT이 간편하게\n소모품을 관리해드릴게요"
private const val REGISTER_COMPLETE_CTA_LABEL = "홈 화면으로 돌아가기"

// Hero를 화면 정중앙보다 살짝 위로 띄워 CTA와 시각적 균형을 맞춘다.
private const val REGISTER_COMPLETE_CENTER_OFFSET_DP = -60
private val REGISTER_COMPLETE_CENTER_OFFSET_Y = REGISTER_COMPLETE_CENTER_OFFSET_DP.dp
private val REGISTER_COMPLETE_HERO_WIDTH = 227.4.dp
private val REGISTER_COMPLETE_HERO_HEIGHT = 215.4.dp
private val REGISTER_COMPLETE_HERO_TO_TEXT_GAP = 36.dp
private val REGISTER_COMPLETE_TITLE_TO_DESCRIPTION_GAP = 16.dp
private val REGISTER_COMPLETE_CTA_BOTTOM_PADDING = 40.dp

@Preview(name = "RegisterCompleteScreen", showBackground = false)
@Composable
private fun RegisterCompleteScreenPreview() {
    OBRitTheme(dynamicColor = false) {
        RegisterCompleteScreen(onExit = {})
    }
}
