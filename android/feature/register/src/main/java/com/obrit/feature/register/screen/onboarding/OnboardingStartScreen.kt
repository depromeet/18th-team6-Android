package com.obrit.feature.register.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.component.button.OBRitButtonDefaults
import com.obrit.android.core.designsystem.component.button.OBRitLargeFilledButton
import com.obrit.android.core.designsystem.component.title.OBRitTitle
import com.obrit.android.core.designsystem.component.title.OBRitTitleSize
import com.obrit.android.core.designsystem.component.title.OBRitTitleType
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
fun OnboardingStartScreen(
    onStartOnboarding: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.gray900)
                .statusBarsPadding()
                .navigationBarsPadding(),
    ) {
        OnboardingStartTitle()
        OnboardingStartCta(onClick = onStartOnboarding)
    }
}

@Composable
private fun BoxScope.OnboardingStartTitle() {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Column(
        modifier =
            Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(top = ONBOARDING_START_TITLE_TOP_PADDING)
                .padding(horizontal = AtomSpacing.S5.dp, vertical = AtomSpacing.S4.dp),
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S1.dp),
    ) {
        Text(
            text = ONBOARDING_START_LABEL,
            style =
                typography.xl.copy(
                    fontWeight = FontWeight.Medium,
                    color = colors.green300,
                ),
        )
        OBRitTitle(
            title = ONBOARDING_START_HEADLINE,
            description = ONBOARDING_START_DESCRIPTION,
            size = OBRitTitleSize.Large,
            type = OBRitTitleType.Default,
        )
    }
}

@Composable
private fun BoxScope.OnboardingStartCta(onClick: () -> Unit) {
    val typography = LocalOBRitTypography.current
    Column(
        modifier =
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    start = AtomSpacing.S5.dp,
                    end = AtomSpacing.S5.dp,
                    bottom = ONBOARDING_START_CTA_BOTTOM_PADDING,
                ),
    ) {
        OBRitLargeFilledButton(
            onClick = onClick,
            colors = OBRitButtonDefaults.positiveButtonColors(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = ONBOARDING_START_CTA_LABEL,
                style = typography.xl.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

private const val ONBOARDING_START_LABEL = "소모품 등록"
private const val ONBOARDING_START_HEADLINE = "환영합니다!\nOBRIT과 함께 소모품 관리해요"
private const val ONBOARDING_START_DESCRIPTION = "소모품 관리, 온보딩으로 간편하게 시작해요!"
private const val ONBOARDING_START_CTA_LABEL = "온보딩 시작하기"

private val ONBOARDING_START_TITLE_TOP_PADDING = 100.dp
private val ONBOARDING_START_CTA_BOTTOM_PADDING = 40.dp

@Preview(name = "OnboardingStartScreen", showBackground = false)
@Composable
private fun OnboardingStartScreenPreview() {
    OBRitTheme(dynamicColor = false) {
        OnboardingStartScreen(onStartOnboarding = {})
    }
}
