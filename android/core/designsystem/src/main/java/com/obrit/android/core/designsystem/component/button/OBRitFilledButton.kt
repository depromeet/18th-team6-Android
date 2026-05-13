@file:Suppress("TooManyFunctions")

package com.obrit.android.core.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
fun OBRitSmallFilledButton(
    text: String,
    onClick: () -> Unit,
    colors: ButtonColors,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OBRitFilledButton(
        text = text,
        onClick = onClick,
        textStyle =
            LocalOBRitTypography.current.base.copy(
                fontWeight = FontWeight.SemiBold,
            ),
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(AtomRadius.Small.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
        colors = colors,
    )
}

@Composable
fun OBRitSmallFilledButton(
    onClick: () -> Unit,
    colors: ButtonColors,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    OBRitFilledButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(AtomRadius.Small.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
        colors = colors,
        content = content,
    )
}

@Composable
fun OBRitMiddleFilledButton(
    text: String,
    onClick: () -> Unit,
    colors: ButtonColors,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OBRitFilledButton(
        text = text,
        onClick = onClick,
        textStyle =
            LocalOBRitTypography.current.xl.copy(
                fontWeight = FontWeight.SemiBold,
            ),
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(AtomRadius.Middle.dp),
        contentPadding = PaddingValues(vertical = 11.dp, horizontal = 20.dp),
        colors = colors,
    )
}

@Composable
fun OBRitMiddleFilledButton(
    onClick: () -> Unit,
    colors: ButtonColors,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    OBRitFilledButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(AtomRadius.Middle.dp),
        contentPadding = PaddingValues(vertical = 11.dp, horizontal = 20.dp),
        colors = colors,
        content = content,
    )
}

@Composable
fun OBRitLargeFilledButton(
    text: String,
    onClick: () -> Unit,
    colors: ButtonColors,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OBRitFilledButton(
        text = text,
        onClick = onClick,
        textStyle =
            LocalOBRitTypography.current.xl.copy(
                fontWeight = FontWeight.SemiBold,
            ),
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(AtomRadius.Large.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        colors = colors,
    )
}

@Composable
fun OBRitLargeFilledButton(
    onClick: () -> Unit,
    colors: ButtonColors,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    OBRitFilledButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(AtomRadius.Large.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        colors = colors,
        content = content,
    )
}

@Composable
@Suppress("LongParameterList")
internal fun OBRitFilledButton(
    onClick: () -> Unit,
    colors: ButtonColors,
    shape: Shape,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        contentPadding = contentPadding,
        colors = colors,
        content = content,
    )
}

@Composable
@Suppress("LongParameterList")
internal fun OBRitFilledButton(
    text: String,
    onClick: () -> Unit,
    textStyle: TextStyle,
    colors: ButtonColors,
    shape: Shape,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        contentPadding = contentPadding,
        colors = colors,
    ) {
        Text(
            text = text,
            style = textStyle,
        )
    }
}

@Preview(name = "OBRitSmallFilledButton", showBackground = true)
@Composable
private fun OBRitSmallFilledButtonPreview() {
    OBRitFilledButtonPreview(size = OBRitFilledButtonPreviewSize.Small)
}

@Preview(name = "OBRitMiddleFilledButton", showBackground = true)
@Composable
private fun OBRitMiddleFilledButtonPreview() {
    OBRitFilledButtonPreview(size = OBRitFilledButtonPreviewSize.Middle)
}

@Preview(name = "OBRitLargeFilledButton", showBackground = true)
@Composable
private fun OBRitLargeFilledButtonPreview() {
    OBRitFilledButtonPreview(size = OBRitFilledButtonPreviewSize.Large)
}

@Composable
private fun OBRitFilledButtonPreview(size: OBRitFilledButtonPreviewSize) {
    OBRitTheme(dynamicColor = false) {
        val colors = LocalOBRitColor.current

        Column(
            modifier =
                Modifier
                    .width(OBRitFilledButtonPreviewWidth)
                    .background(colors.gray900)
                    .padding(AtomSpacing.S5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AtomSpacing.S3.dp),
        ) {
            OBRitFilledButtonPreviewItems(
                size = size,
                text = OBRIT_FILLED_BUTTON_POSITIVE_PREVIEW_TEXT,
                colors = OBRitButtonDefaults.positiveButtonColors(),
            )
            OBRitFilledButtonPreviewItems(
                size = size,
                text = OBRIT_FILLED_BUTTON_DEFAULT_PREVIEW_TEXT,
                colors = OBRitButtonDefaults.defaultButtonColors(),
            )
            OBRitFilledButtonPreviewItems(
                size = size,
                text = OBRIT_FILLED_BUTTON_COMMON_PREVIEW_TEXT,
                colors = OBRitButtonDefaults.commonButtonColors(),
            )
        }
    }
}

@Composable
private fun OBRitFilledButtonPreviewItems(
    size: OBRitFilledButtonPreviewSize,
    text: String,
    colors: ButtonColors,
) {
    listOf(true, false).forEach { enabled ->
        val buttonText = if (enabled) text else "$text Disabled"

        when (size) {
            OBRitFilledButtonPreviewSize.Small ->
                OBRitSmallFilledButton(
                    text = buttonText,
                    onClick = {},
                    colors = colors,
                    enabled = enabled,
                )
            OBRitFilledButtonPreviewSize.Middle ->
                OBRitMiddleFilledButton(
                    text = buttonText,
                    onClick = {},
                    colors = colors,
                    enabled = enabled,
                )
            OBRitFilledButtonPreviewSize.Large ->
                OBRitLargeFilledButton(
                    text = buttonText,
                    onClick = {},
                    colors = colors,
                    enabled = enabled,
                )
        }
    }
}

private enum class OBRitFilledButtonPreviewSize {
    Small,
    Middle,
    Large,
}

private val OBRitFilledButtonPreviewWidth = 320.dp
private const val OBRIT_FILLED_BUTTON_POSITIVE_PREVIEW_TEXT = "Positive"
private const val OBRIT_FILLED_BUTTON_DEFAULT_PREVIEW_TEXT = "Default"
private const val OBRIT_FILLED_BUTTON_COMMON_PREVIEW_TEXT = "Common"
