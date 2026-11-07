@file:Suppress("TooManyFunctions")

package com.obrit.android.core.designsystem.component.textfield

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

enum class InputResultState {
    Default,
    Error,
    Success,
}

@Composable
@Suppress("LongParameterList")
fun OBRitOutlinedTextField(
    state: TextFieldState,
    supportingTextEnabled: Boolean,
    modifier: Modifier = Modifier,
    supportingText: String = "",
    placeholder: String = "",
    inputResultState: InputResultState = InputResultState.Default,
    textStyle: TextStyle =
        LocalOBRitTypography.current.xl.copy(
            fontWeight = FontWeight.Medium,
        ),
    maxLength: Int? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    cursorBrush: Brush = SolidColor(LocalOBRitColor.current.common00),
) {
    OBRitOutlinedTextField(
        value = state.text.toString(),
        onValueChange = { changedValue ->
            state.edit {
                replace(0, length, changedValue)
            }
        },
        supportingTextEnabled = supportingTextEnabled,
        supportingText = supportingText,
        modifier = modifier,
        placeholder = placeholder,
        inputResultState = inputResultState,
        textStyle = textStyle,
        maxLength = maxLength,
        enabled = enabled,
        readOnly = readOnly,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        onTextLayout = onTextLayout,
        cursorBrush = cursorBrush,
    )
}

@Composable
@Suppress("CyclomaticComplexMethod", "LongMethod", "LongParameterList")
fun OBRitOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    supportingTextEnabled: Boolean,
    modifier: Modifier = Modifier,
    supportingText: String = "",
    placeholder: String = "",
    inputResultState: InputResultState = InputResultState.Default,
    textStyle: TextStyle =
        LocalOBRitTypography.current.xl.copy(
            fontWeight = FontWeight.Medium,
            color = LocalOBRitColor.current.common00,
        ),
    maxLength: Int? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorBrush: Brush = SolidColor(LocalOBRitColor.current.common00),
) {
    val isFocused by interactionSource.collectIsFocusedAsState()
    val resolvedBorder =
        BorderStroke(
            width = 1.dp,
            color =
                when {
                    !enabled -> LocalOBRitColor.current.gray700
                    inputResultState == InputResultState.Error -> LocalOBRitColor.current.red300
                    inputResultState == InputResultState.Success -> LocalOBRitColor.current.green300
                    isFocused -> LocalOBRitColor.current.common00
                    else -> Color.Transparent
                },
        )

    Column(
        modifier = modifier,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(AtomRadius.Middle.dp))
                    .background(LocalOBRitColor.current.gray800)
                    .border(
                        border = resolvedBorder,
                        shape = RoundedCornerShape(AtomRadius.Middle.dp),
                    ).padding(
                        horizontal = 20.dp,
                        vertical = 16.dp,
                    ),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            visualTransformation = visualTransformation,
            onTextLayout = onTextLayout,
            interactionSource = interactionSource,
            cursorBrush = cursorBrush,
        ) { innerTextField ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            style =
                                textStyle.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = LocalOBRitColor.current.gray700,
                                ),
                            maxLines = if (singleLine) 1 else Int.MAX_VALUE,
                        )
                    }

                    innerTextField()
                }

                if (maxLength != null) {
                    Text(
                        text = "${value.length}/$maxLength",
                        style =
                            LocalOBRitTypography.current.s.copy(
                                fontWeight = FontWeight.Medium,
                                color =
                                    when {
                                        !enabled -> LocalOBRitColor.current.gray700
                                        inputResultState == InputResultState.Error -> LocalOBRitColor.current.red300
                                        inputResultState == InputResultState.Success -> LocalOBRitColor.current.green300
                                        else -> LocalOBRitColor.current.common00
                                    },
                            ),
                        maxLines = 1,
                    )
                }
            }
        }

        if (supportingTextEnabled) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = AtomSpacing.S2.dp),
            ) {
                OBRitOutlinedTextFieldStatusIcon(inputResultState = inputResultState)
                Text(
                    text = supportingText,
                    style =
                        LocalOBRitTypography.current.base.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                    color =
                        when (inputResultState) {
                            InputResultState.Error -> LocalOBRitColor.current.red300
                            InputResultState.Success -> LocalOBRitColor.current.green300
                            else -> Color.Transparent
                        },
                    modifier = Modifier.padding(start = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun OBRitOutlinedTextFieldStatusIcon(inputResultState: InputResultState) {
    val iconRes =
        when (inputResultState) {
            InputResultState.Error -> R.drawable.ic_textfield_exclamation_circle
            InputResultState.Success -> R.drawable.ic_textfield_circle_check
            InputResultState.Default -> return
        }

    Icon(
        imageVector = ImageVector.vectorResource(id = iconRes),
        contentDescription = null,
        modifier = Modifier.size(AtomSpacing.S4.dp),
        tint = Color.Unspecified,
    )
}

@Preview(
    name = "OBRitOutlinedTextField Default Empty",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun OBRitOutlinedTextFieldDefaultEmptyPreview() {
    OBRitOutlinedTextFieldPreviewContainer {
        OBRitOutlinedTextField(
            value = "",
            onValueChange = {},
            supportingTextEnabled = false,
            placeholder = OBRIT_OUTLINED_TEXT_FIELD_PREVIEW_PLACEHOLDER,
            modifier = Modifier.fillMaxWidth(),
            maxLength = OBRIT_OUTLINED_TEXT_FIELD_PREVIEW_MAX_LENGTH,
            singleLine = true,
        )
    }
}

@Preview(
    name = "OBRitOutlinedTextField Default Filled",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun OBRitOutlinedTextFieldDefaultFilledPreview() {
    OBRitOutlinedTextFieldPreviewContainer {
        OBRitOutlinedTextField(
            value = OBRIT_OUTLINED_TEXT_FIELD_PREVIEW_TEXT,
            onValueChange = {},
            supportingTextEnabled = false,
            placeholder = OBRIT_OUTLINED_TEXT_FIELD_PREVIEW_PLACEHOLDER,
            modifier = Modifier.fillMaxWidth(),
            maxLength = OBRIT_OUTLINED_TEXT_FIELD_PREVIEW_MAX_LENGTH,
            singleLine = true,
        )
    }
}

@Preview(
    name = "OBRitOutlinedTextField Disabled",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun OBRitOutlinedTextFieldDisabledPreview() {
    OBRitOutlinedTextFieldPreviewContainer {
        OBRitOutlinedTextField(
            value = "",
            onValueChange = {},
            supportingTextEnabled = false,
            placeholder = OBRIT_OUTLINED_TEXT_FIELD_PREVIEW_PLACEHOLDER,
            modifier = Modifier.fillMaxWidth(),
            maxLength = OBRIT_OUTLINED_TEXT_FIELD_PREVIEW_MAX_LENGTH,
            enabled = false,
            singleLine = true,
        )
    }
}

@Preview(
    name = "OBRitOutlinedTextField ReadOnly",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun OBRitOutlinedTextFieldReadOnlyPreview() {
    OBRitOutlinedTextFieldPreviewContainer {
        OBRitOutlinedTextField(
            value = OBRIT_OUTLINED_TEXT_FIELD_PREVIEW_TEXT,
            onValueChange = {},
            supportingTextEnabled = false,
            placeholder = OBRIT_OUTLINED_TEXT_FIELD_PREVIEW_PLACEHOLDER,
            modifier = Modifier.fillMaxWidth(),
            maxLength = OBRIT_OUTLINED_TEXT_FIELD_PREVIEW_MAX_LENGTH,
            readOnly = true,
            singleLine = true,
        )
    }
}

@Preview(
    name = "OBRitOutlinedTextField Error",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun OBRitOutlinedTextFieldErrorPreview() {
    OBRitOutlinedTextFieldPreviewContainer {
        OBRitOutlinedTextField(
            value = OBRIT_OUTLINED_TEXT_FIELD_PREVIEW_TEXT,
            onValueChange = {},
            supportingTextEnabled = true,
            supportingText = OBRIT_OUTLINED_TEXT_FIELD_ERROR_PREVIEW_TEXT,
            placeholder = OBRIT_OUTLINED_TEXT_FIELD_PREVIEW_PLACEHOLDER,
            inputResultState = InputResultState.Error,
            modifier = Modifier.fillMaxWidth(),
            maxLength = OBRIT_OUTLINED_TEXT_FIELD_PREVIEW_MAX_LENGTH,
            singleLine = true,
        )
    }
}

@Preview(
    name = "OBRitOutlinedTextField Success",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun OBRitOutlinedTextFieldSuccessPreview() {
    OBRitOutlinedTextFieldPreviewContainer {
        OBRitOutlinedTextField(
            value = OBRIT_OUTLINED_TEXT_FIELD_PREVIEW_TEXT,
            onValueChange = {},
            supportingTextEnabled = true,
            supportingText = OBRIT_OUTLINED_TEXT_FIELD_SUCCESS_PREVIEW_TEXT,
            placeholder = OBRIT_OUTLINED_TEXT_FIELD_PREVIEW_PLACEHOLDER,
            inputResultState = InputResultState.Success,
            modifier = Modifier.fillMaxWidth(),
            maxLength = OBRIT_OUTLINED_TEXT_FIELD_PREVIEW_MAX_LENGTH,
            singleLine = true,
        )
    }
}

@Preview(
    name = "OBRitOutlinedTextField Multiline",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun OBRitOutlinedTextFieldMultilinePreview() {
    OBRitOutlinedTextFieldPreviewContainer {
        OBRitOutlinedTextField(
            value = OBRIT_OUTLINED_TEXT_FIELD_MULTILINE_PREVIEW_TEXT,
            onValueChange = {},
            supportingTextEnabled = false,
            placeholder = OBRIT_OUTLINED_TEXT_FIELD_PREVIEW_PLACEHOLDER,
            modifier = Modifier.fillMaxWidth(),
            maxLength = OBRIT_OUTLINED_TEXT_FIELD_MULTILINE_PREVIEW_MAX_LENGTH,
            minLines = 3,
        )
    }
}

@Composable
private fun OBRitOutlinedTextFieldPreviewContainer(content: @Composable () -> Unit) {
    OBRitTheme(dynamicColor = false) {
        Box(
            modifier =
                Modifier
                    .background(LocalOBRitColor.current.gray900)
                    .padding(AtomSpacing.S6.dp),
        ) {
            content()
        }
    }
}

private const val OBRIT_OUTLINED_TEXT_FIELD_PREVIEW_TEXT = "Text"
private const val OBRIT_OUTLINED_TEXT_FIELD_PREVIEW_PLACEHOLDER = "Placeholder"
private const val OBRIT_OUTLINED_TEXT_FIELD_ERROR_PREVIEW_TEXT = "Error message"
private const val OBRIT_OUTLINED_TEXT_FIELD_SUCCESS_PREVIEW_TEXT = "Success message"
private const val OBRIT_OUTLINED_TEXT_FIELD_MULTILINE_PREVIEW_TEXT = "Text\nText\nText"
private const val OBRIT_OUTLINED_TEXT_FIELD_PREVIEW_MAX_LENGTH = 30
private const val OBRIT_OUTLINED_TEXT_FIELD_MULTILINE_PREVIEW_MAX_LENGTH = 120
