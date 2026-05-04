package com.obrit.android.core.designsyetem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsyetem.theme.LocalOBRitColor
import com.obrit.android.core.designsyetem.theme.LocalOBRitTypography
import com.obrit.android.core.designsyetem.theme.OBRitColor
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

enum class FilledButtonColor {
    Green,
    Gray,
    White,
}

internal enum class FilledButtonSize {
    Large,
    Middle,
    Small,
}

@Composable
fun OBRitLargeFilledButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: FilledButtonColor = FilledButtonColor.Green,
    content: @Composable RowScope.(Color) -> Unit,
) {
    OBRitFilledButton(
        onClick = onClick,
        size = FilledButtonSize.Large,
        modifier = modifier,
        enabled = enabled,
        color = color,
        content = content,
    )
}

@Composable
fun OBRitMiddleFilledButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: FilledButtonColor = FilledButtonColor.Green,
    content: @Composable RowScope.(Color) -> Unit,
) {
    OBRitFilledButton(
        onClick = onClick,
        size = FilledButtonSize.Middle,
        modifier = modifier,
        enabled = enabled,
        color = color,
        content = content,
    )
}

@Composable
fun OBRitSmallFilledButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: FilledButtonColor = FilledButtonColor.Green,
    content: @Composable RowScope.(Color) -> Unit,
) {
    OBRitFilledButton(
        onClick = onClick,
        size = FilledButtonSize.Small,
        modifier = modifier,
        enabled = enabled,
        color = color,
        content = content,
    )
}

@Composable
@Suppress("LongParameterList")
internal fun OBRitFilledButton(
    onClick: () -> Unit,
    size: FilledButtonSize,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: FilledButtonColor = FilledButtonColor.Green,
    content: @Composable RowScope.(Color) -> Unit,
) {
    val colors = LocalOBRitColor.current

    Row(
        modifier =
            modifier
                .defaultMinSize(minHeight = filledButtonHeight(size = size))
                .clip(filledButtonShape(size = size))
                .background(filledButtonContainerColor(colors = colors, color = color, enabled = enabled))
                .clickable(
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                ).padding(horizontal = filledButtonHorizontalPadding(size = size)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        content(filledButtonContentColor(colors = colors, color = color, enabled = enabled))
    }
}

internal fun filledButtonContentColor(
    colors: OBRitColor,
    color: FilledButtonColor,
    enabled: Boolean,
): Color =
    when (color) {
        FilledButtonColor.Green -> colors.common100
        FilledButtonColor.Gray ->
            if (enabled) {
                colors.common00
            } else {
                colors.gray700
            }
        FilledButtonColor.White ->
            if (enabled) {
                colors.common100
            } else {
                OBRitFilledButtonDisabledWhiteContentColor
            }
    }

@Composable
internal fun filledButtonTextStyle(size: FilledButtonSize): TextStyle {
    val typography = LocalOBRitTypography.current

    return when (size) {
        FilledButtonSize.Large,
        FilledButtonSize.Middle,
        -> typography.xl
        FilledButtonSize.Small -> typography.base
    }.copy(fontWeight = FontWeight.Bold)
}

private fun filledButtonContainerColor(
    colors: OBRitColor,
    color: FilledButtonColor,
    enabled: Boolean,
): Color =
    when (color) {
        FilledButtonColor.Green ->
            if (enabled) {
                colors.green300
            } else {
                colors.green800
            }
        FilledButtonColor.Gray -> colors.gray800
        FilledButtonColor.White ->
            if (enabled) {
                colors.common00
            } else {
                colors.gray600
            }
    }

private fun filledButtonHeight(size: FilledButtonSize) =
    when (size) {
        FilledButtonSize.Large -> AtomSpacing.S14.dp
        FilledButtonSize.Middle -> OBRitFilledButtonMiddleHeight
        FilledButtonSize.Small -> OBRitFilledButtonSmallHeight
    }

private fun filledButtonHorizontalPadding(size: FilledButtonSize) =
    when (size) {
        FilledButtonSize.Large -> AtomSpacing.S6.dp
        FilledButtonSize.Middle -> OBRitFilledButtonMiddleHorizontalPadding
        FilledButtonSize.Small -> OBRitFilledButtonSmallHorizontalPadding
    }

private fun filledButtonShape(size: FilledButtonSize) =
    when (size) {
        FilledButtonSize.Large -> RoundedCornerShape(AtomRadius.Large.dp)
        FilledButtonSize.Middle -> RoundedCornerShape(AtomRadius.Middle.dp)
        FilledButtonSize.Small -> RoundedCornerShape(AtomRadius.Small.dp)
    }

private val OBRitFilledButtonMiddleHeight = 46.dp
private val OBRitFilledButtonSmallHeight = 38.dp
private val OBRitFilledButtonMiddleHorizontalPadding = 22.dp
private val OBRitFilledButtonSmallHorizontalPadding = 14.dp
private val OBRitFilledButtonDisabledWhiteContentColor = Color(OBRIT_FILLED_BUTTON_DISABLED_WHITE_CONTENT_COLOR)
private const val OBRIT_FILLED_BUTTON_DISABLED_WHITE_CONTENT_COLOR = 0xFF24242AL
