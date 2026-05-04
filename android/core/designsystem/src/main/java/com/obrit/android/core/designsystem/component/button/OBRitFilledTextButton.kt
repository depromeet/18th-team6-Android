package com.obrit.android.core.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
fun OBRitLargeFilledTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: FilledButtonColor = FilledButtonColor.Green,
) {
    OBRitFilledButton(
        onClick = onClick,
        size = FilledButtonSize.Large,
        modifier = modifier,
        enabled = enabled,
        color = color,
    ) { contentColor ->
        Text(
            text = text,
            style = filledButtonTextStyle(size = FilledButtonSize.Large).copy(color = contentColor),
            maxLines = 1,
        )
    }
}

@Composable
fun OBRitMiddleFilledTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: FilledButtonColor = FilledButtonColor.Green,
) {
    OBRitFilledTextButton(
        text = text,
        onClick = onClick,
        size = FilledButtonSize.Middle,
        modifier = modifier,
        enabled = enabled,
        color = color,
    )
}

@Composable
fun OBRitSmallFilledTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: FilledButtonColor = FilledButtonColor.Green,
) {
    OBRitFilledTextButton(
        text = text,
        onClick = onClick,
        size = FilledButtonSize.Small,
        modifier = modifier,
        enabled = enabled,
        color = color,
    )
}

@Composable
@Suppress("LongParameterList")
private fun OBRitFilledTextButton(
    text: String,
    onClick: () -> Unit,
    size: FilledButtonSize,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: FilledButtonColor = FilledButtonColor.Green,
) {
    OBRitFilledButton(
        onClick = onClick,
        size = size,
        modifier = modifier,
        enabled = enabled,
        color = color,
    ) { contentColor ->
        Text(
            text = text,
            style = filledButtonTextStyle(size = size).copy(color = contentColor),
            maxLines = 1,
        )
    }
}

@Preview(
    name = "OBRitFilledTextButton Large",
    showBackground = true,
)
@Composable
private fun OBRitFilledTextButtonLargePreview() {
    OBRitFilledButtonPreviewContainer {
        OBRitFilledTextButton(
            text = OBRIT_FILLED_BUTTON_PREVIEW_TEXT,
            onClick = {},
            size = FilledButtonSize.Large,
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            color = FilledButtonColor.Green,
        )
    }
}

@Preview(
    name = "OBRitFilledTextButton Middle",
    showBackground = true,
)
@Composable
private fun OBRitFilledTextButtonMiddlePreview() {
    OBRitFilledButtonPreviewContainer {
        OBRitMiddleFilledTextButton(
            text = OBRIT_FILLED_BUTTON_PREVIEW_TEXT,
            onClick = {},
            color = FilledButtonColor.Gray,
        )
    }
}

@Preview(
    name = "OBRitFilledTextButton Small",
    showBackground = true,
)
@Composable
private fun OBRitFilledTextButtonSmallPreview() {
    OBRitFilledButtonPreviewContainer {
        OBRitSmallFilledTextButton(
            text = OBRIT_FILLED_BUTTON_PREVIEW_TEXT,
            onClick = {},
            color = FilledButtonColor.White,
        )
    }
}

@Preview(
    name = "OBRitFilledTextButton Disabled",
    showBackground = true,
)
@Composable
private fun OBRitFilledTextButtonDisabledPreview() {
    OBRitFilledButtonPreviewContainer {
        Column(
            verticalArrangement = Arrangement.spacedBy(AtomSpacing.S3.dp),
        ) {
            OBRitLargeFilledTextButton(
                text = OBRIT_FILLED_BUTTON_PREVIEW_TEXT,
                onClick = {},
                enabled = false,
            )
            OBRitMiddleFilledTextButton(
                text = OBRIT_FILLED_BUTTON_PREVIEW_TEXT,
                onClick = {},
                enabled = false,
                color = FilledButtonColor.Gray,
            )
            OBRitSmallFilledTextButton(
                text = OBRIT_FILLED_BUTTON_PREVIEW_TEXT,
                onClick = {},
                enabled = false,
                color = FilledButtonColor.White,
            )
        }
    }
}

@Composable
private fun OBRitFilledButtonPreviewContainer(content: @Composable () -> Unit) {
    OBRitTheme(dynamicColor = false) {
        val colors = LocalOBRitColor.current
        Box(
            modifier =
                Modifier
                    .background(colors.gray900)
                    .padding(AtomSpacing.S5.dp),
        ) {
            content()
        }
    }
}

private const val OBRIT_FILLED_BUTTON_PREVIEW_TEXT = "Button"
