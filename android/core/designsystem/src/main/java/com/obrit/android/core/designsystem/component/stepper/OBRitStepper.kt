package com.obrit.android.core.designsystem.component.stepper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
fun OBRitStepper(
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    value: Int = OBRIT_STEPPER_MIN_VALUE,
) {
    val colors = LocalOBRitColor.current
    val isDecrementEnabled = value > OBRIT_STEPPER_MIN_VALUE
    val isIncrementEnabled = value < OBRIT_STEPPER_MAX_VALUE

    Row(
        modifier =
            modifier
                .defaultMinSize(minHeight = OBRitStepperContainerHeight)
                .clip(OBRitStepperShape)
                .background(colors.gray750),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(OBRitStepperSegmentGap),
    ) {
        OBRitStepperAction(
            enabled = isDecrementEnabled,
            onClick = { onValueChange((value - 1).coerceAtLeast(OBRIT_STEPPER_MIN_VALUE)) },
        ) { contentColor ->
            Icon(
                painter = painterResource(id = R.drawable.ic_stepper_minus),
                contentDescription = null,
                tint = contentColor,
            )
        }
        OBRitStepperValueDisplay(value = value)
        OBRitStepperAction(
            enabled = isIncrementEnabled,
            onClick = { onValueChange((value + 1).coerceAtMost(OBRIT_STEPPER_MAX_VALUE)) },
        ) { contentColor ->
            Icon(
                painter = painterResource(id = R.drawable.ic_stepper_plus),
                contentDescription = null,
                tint = contentColor,
            )
        }
    }
}

@Composable
private fun OBRitStepperValueDisplay(value: Int) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Box(
        modifier =
            Modifier
                .clip(OBRitStepperShape)
                .background(colors.gray600)
                .defaultMinSize(
                    minWidth = OBRitStepperValueMinWidth,
                    minHeight = OBRitStepperValueHeight,
                )
                .padding(horizontal = OBRitStepperValueHorizontalPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = value.toString(),
            style =
                typography.base.copy(
                    color = colors.common00,
                    fontWeight = FontWeight.Medium,
                ),
        )
    }
}

@Composable
private fun OBRitStepperAction(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Color) -> Unit,
) {
    val colors = LocalOBRitColor.current
    val contentColor =
        if (enabled) {
            colors.common00
        } else {
            colors.gray700
        }

    Box(
        modifier =
            modifier
                .defaultMinSize(
                    minWidth = OBRitStepperActionWidth,
                    minHeight = OBRitStepperContainerHeight,
                )
                .clickable(
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        content(contentColor)
    }
}

@Preview(
    name = "OBRitStepper Default",
    showBackground = true,
)
@Composable
private fun OBRitStepperDefaultPreview() {
    OBRitStepperPreviewContainer(
        initialValue = 2,
    )
}

@Preview(
    name = "OBRitStepper Min",
    showBackground = true,
)
@Composable
private fun OBRitStepperMinPreview() {
    OBRitStepperPreviewContainer(
        initialValue = 0,
    )
}

@Composable
private fun OBRitStepperPreviewContainer(initialValue: Int) {
    var value by remember { mutableIntStateOf(initialValue) }

    OBRitTheme(dynamicColor = false) {
        Box(
            modifier = Modifier.padding(AtomSpacing.S5.dp),
        ) {
            OBRitStepper(
                value = value,
                onValueChange = { nextValue ->
                    value = nextValue.coerceIn(OBRIT_STEPPER_MIN_VALUE, OBRIT_STEPPER_MAX_VALUE)
                },
            )
        }
    }
}

private val OBRitStepperShape = RoundedCornerShape(AtomRadius.Small.dp)
private val OBRitStepperContainerHeight = AtomSpacing.S11.dp
private val OBRitStepperActionWidth = AtomSpacing.S9.dp
private val OBRitStepperValueMinWidth = AtomSpacing.S9.dp
private val OBRitStepperValueHeight = AtomSpacing.S9.dp
private val OBRitStepperValueHorizontalPadding = AtomSpacing.S1.dp
private val OBRitStepperSegmentGap = AtomSpacing.S1_5.dp
private const val OBRIT_STEPPER_MIN_VALUE = 0
private const val OBRIT_STEPPER_MAX_VALUE = 99
