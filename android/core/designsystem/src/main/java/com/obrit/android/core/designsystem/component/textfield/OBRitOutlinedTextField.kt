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
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitColor
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
    modifier: Modifier = Modifier,
    placeholder: String = "",
    inputResultState: InputResultState = InputResultState.Default,
    containerColor: Color = LocalOBRitColor.current.gray800,
    textStyle: TextStyle =
        LocalOBRitTypography.current.xl.copy(
            fontWeight = FontWeight.Medium,
        ),
    placeholderTextStyle: TextStyle = textStyle,
    maxLength: Int? = null,
    supportingText: String = "",
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource? = null,
    cursorBrush: Brush = SolidColor(LocalOBRitColor.current.common00),
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit =
        @Composable { innerTextField -> innerTextField() },
) {
    OBRitOutlinedTextField(
        value = state.text.toString(),
        onValueChange = { changedValue ->
            state.edit {
                replace(0, length, changedValue)
            }
        },
        modifier = modifier,
        placeholder = placeholder,
        inputResultState = inputResultState,
        containerColor = containerColor,
        textStyle = textStyle,
        placeholderTextStyle = placeholderTextStyle,
        maxLength = maxLength,
        supportingText = supportingText,
        enabled = enabled,
        readOnly = readOnly,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        onTextLayout = onTextLayout,
        interactionSource = interactionSource,
        cursorBrush = cursorBrush,
        decorationBox = decorationBox,
    )
}

@Composable
@Suppress("LongMethod", "LongParameterList")
fun OBRitOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    inputResultState: InputResultState = InputResultState.Default,
    containerColor: Color = LocalOBRitColor.current.gray800,
    textStyle: TextStyle =
        LocalOBRitTypography.current.xl.copy(
            fontWeight = FontWeight.Medium,
        ),
    placeholderTextStyle: TextStyle = textStyle,
    maxLength: Int? = null,
    supportingText: String = "",
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource? = null,
    cursorBrush: Brush = SolidColor(LocalOBRitColor.current.common00),
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit =
        @Composable { innerTextField -> innerTextField() },
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val actualInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isFocused by actualInteractionSource.collectIsFocusedAsState()
    val borderColor =
        outlinedTextFieldBorderColor(
            colors = colors,
            enabled = enabled,
            inputResultState = inputResultState,
            isFocused = isFocused,
        )
    val contentColor =
        if (enabled) {
            colors.common00
        } else {
            colors.gray700
        }
    val placeholderColor = colors.gray700
    val counterColor =
        outlinedTextFieldCounterColor(
            colors = colors,
            enabled = enabled,
            inputResultState = inputResultState,
        )
    val statusColor =
        when (inputResultState) {
            InputResultState.Error -> colors.red300
            InputResultState.Success -> colors.green300
            InputResultState.Default -> contentColor
        }
    val resolvedTextStyle = textStyle.copy(color = contentColor)
    val resolvedPlaceholderStyle =
        placeholderTextStyle.copy(color = placeholderColor)
    val resolvedCounterStyle =
        typography.s.copy(
            color = counterColor,
            fontWeight = FontWeight.Medium,
        )
    val resolvedCounterText = maxLength?.let { "${value.length.coerceAtMost(it)}/$it" }
    val resolvedStatusTextStyle =
        typography.base.copy(
            color = statusColor,
            fontWeight = FontWeight.SemiBold,
        )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(OBRitOutlinedTextFieldSupportingGap),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = OBRitOutlinedTextFieldMinHeight)
                    .clip(OBRitOutlinedTextFieldShape)
                    .background(containerColor)
                    .then(
                        Modifier.border(
                            border =
                                BorderStroke(
                                    width = OBRitOutlinedTextFieldBorderWidth,
                                    color = borderColor,
                                ),
                            shape = OBRitOutlinedTextFieldShape,
                        ),
                    ).padding(
                        horizontal = OBRitOutlinedTextFieldHorizontalPadding,
                        vertical = OBRitOutlinedTextFieldVerticalPadding,
                    ),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = resolvedTextStyle,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            visualTransformation = visualTransformation,
            onTextLayout = onTextLayout,
            interactionSource = actualInteractionSource,
            cursorBrush = cursorBrush,
        ) { innerTextField ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(OBRitOutlinedTextFieldContentGap),
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            style = resolvedPlaceholderStyle,
                            maxLines = if (singleLine) 1 else Int.MAX_VALUE,
                        )
                    }

                    decorationBox(innerTextField)
                }

                if (resolvedCounterText != null) {
                    Text(
                        text = resolvedCounterText,
                        style = resolvedCounterStyle,
                        maxLines = 1,
                    )
                }
            }
        }

        if (inputResultState != InputResultState.Default && supportingText.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(OBRitOutlinedTextFieldStatusGap),
            ) {
                OBRitOutlinedTextFieldStatusIcon(inputResultState = inputResultState)
                Text(
                    text = supportingText,
                    style = resolvedStatusTextStyle,
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
        painter = painterResource(id = iconRes),
        contentDescription = null,
        modifier = Modifier.size(OBRitOutlinedTextFieldStatusIconSize),
        tint = Color.Unspecified,
    )
}

private fun outlinedTextFieldCounterColor(
    colors: OBRitColor,
    enabled: Boolean,
    inputResultState: InputResultState,
): Color =
    when {
        !enabled -> colors.gray600
        inputResultState == InputResultState.Error -> colors.red300
        inputResultState == InputResultState.Success -> colors.green300
        else -> colors.common00
    }

private fun outlinedTextFieldBorderColor(
    colors: OBRitColor,
    enabled: Boolean,
    inputResultState: InputResultState,
    isFocused: Boolean,
): Color =
    when {
        !enabled -> colors.gray600
        inputResultState == InputResultState.Error -> colors.red300
        inputResultState == InputResultState.Success -> colors.green300
        isFocused -> colors.common00
        else -> colors.gray300
    }

private val OBRitOutlinedTextFieldMinHeight = AtomSpacing.S14.dp
private val OBRitOutlinedTextFieldBorderWidth = 1.4f.dp
private val OBRitOutlinedTextFieldHorizontalPadding = AtomSpacing.S5.dp
private val OBRitOutlinedTextFieldVerticalPadding = AtomSpacing.S4.dp
private val OBRitOutlinedTextFieldContentGap = AtomSpacing.S2.dp
private val OBRitOutlinedTextFieldSupportingGap = AtomSpacing.S2.dp
private val OBRitOutlinedTextFieldStatusGap = AtomSpacing.S1_5.dp
private val OBRitOutlinedTextFieldStatusIconSize = AtomSpacing.S4.dp
private val OBRitOutlinedTextFieldShape = RoundedCornerShape(AtomRadius.Middle.dp)

@Preview(
    name = "OBRitOutlinedTextField Default Empty",
    showBackground = true,
)
@Composable
private fun OBRitOutlinedTextFieldDefaultEmptyPreview() {
    OBRitOutlinedTextFieldPreviewContainer {
        OBRitOutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = "TEXT",
            modifier = Modifier.fillMaxWidth(),
            maxLength = 30,
            singleLine = true,
        )
    }
}

@Preview(
    name = "OBRitOutlinedTextField Default Filled",
    showBackground = true,
)
@Composable
private fun OBRitOutlinedTextFieldDefaultFilledPreview() {
    OBRitOutlinedTextFieldPreviewContainer {
        OBRitOutlinedTextField(
            value = "TEXT",
            onValueChange = {},
            placeholder = "TEXT",
            modifier = Modifier.fillMaxWidth(),
            maxLength = 30,
            singleLine = true,
        )
    }
}

@Preview(
    name = "OBRitOutlinedTextField Disabled",
    showBackground = true,
)
@Composable
private fun OBRitOutlinedTextFieldDisabledPreview() {
    OBRitOutlinedTextFieldPreviewContainer {
        OBRitOutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = "TEXT",
            modifier = Modifier.fillMaxWidth(),
            maxLength = 30,
            enabled = false,
            singleLine = true,
        )
    }
}

@Preview(
    name = "OBRitOutlinedTextField Error",
    showBackground = true,
)
@Composable
private fun OBRitOutlinedTextFieldErrorPreview() {
    OBRitOutlinedTextFieldPreviewContainer {
        OBRitOutlinedTextField(
            value = "TEXT",
            onValueChange = {},
            placeholder = "TEXT",
            modifier = Modifier.fillMaxWidth(),
            inputResultState = InputResultState.Error,
            maxLength = 30,
            supportingText = "에러 텍스트를 입력해주세요",
            singleLine = true,
        )
    }
}

@Preview(
    name = "OBRitOutlinedTextField Success",
    showBackground = true,
)
@Composable
private fun OBRitOutlinedTextFieldSuccessPreview() {
    OBRitOutlinedTextFieldPreviewContainer {
        OBRitOutlinedTextField(
            value = "TEXT",
            onValueChange = {},
            placeholder = "TEXT",
            modifier = Modifier.fillMaxWidth(),
            inputResultState = InputResultState.Success,
            maxLength = 30,
            supportingText = "성공 텍스트를 입력해주세요",
            singleLine = true,
        )
    }
}

@Composable
private fun OBRitOutlinedTextFieldPreviewContainer(content: @Composable () -> Unit) {
    OBRitTheme(dynamicColor = false) {
        val colors = LocalOBRitColor.current
        Box(
            modifier =
                Modifier
                    .background(colors.gray900)
                    .padding(AtomSpacing.S6.dp),
        ) {
            content()
        }
    }
}
