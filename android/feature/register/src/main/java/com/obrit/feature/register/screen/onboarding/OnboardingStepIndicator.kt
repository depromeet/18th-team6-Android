package com.obrit.feature.register.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
internal fun OnboardingStepIndicator(
    currentStep: Int,
    totalStep: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S1.dp),
    ) {
        for (step in 1..totalStep) {
            OnboardingStepCircle(number = step, active = step == currentStep)
            if (step < totalStep) OnboardingStepConnector()
        }
    }
}

@Composable
private fun OnboardingStepCircle(
    number: Int,
    active: Boolean,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Box(
        modifier =
            Modifier
                .size(ONBOARDING_STEP_CIRCLE_SIZE)
                .clip(CircleShape)
                .background(if (active) colors.common00 else colors.gray750),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = number.toString(),
            style =
                typography.base.copy(
                    color = if (active) colors.common1000 else colors.common00,
                    fontWeight = FontWeight.SemiBold,
                ),
        )
    }
}

@Composable
private fun OnboardingStepConnector() {
    val colors = LocalOBRitColor.current
    Box(
        modifier =
            Modifier
                .width(ONBOARDING_STEP_CONNECTOR_WIDTH)
                .height(ONBOARDING_STEP_CONNECTOR_HEIGHT)
                .background(colors.gray750),
    )
}

internal val ONBOARDING_STEP_CIRCLE_SIZE = 28.dp
private val ONBOARDING_STEP_CONNECTOR_WIDTH = 28.dp
private val ONBOARDING_STEP_CONNECTOR_HEIGHT = 2.dp
