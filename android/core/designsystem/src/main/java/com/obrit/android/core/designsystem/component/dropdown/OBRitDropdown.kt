package com.obrit.android.core.designsystem.component.dropdown

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

enum class DropdownInputState {
    Default,
    Error,
}

@Composable
@Suppress("LongParameterList")
fun OBRitDropdownMenu(
    items: List<String>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    selectedIndex: Int? = null,
    enabled: Boolean = true,
    itemTextStyle: TextStyle =
        LocalOBRitTypography.current.xl.copy(
            fontWeight = FontWeight.Medium,
        ),
) {
    val colors = LocalOBRitColor.current
    val shape = RoundedCornerShape(AtomRadius.Small.dp)

    Column(
        modifier =
            modifier
                .width(IntrinsicSize.Max)
                .clip(shape)
                .border(
                    border =
                        BorderStroke(
                            width = 1.dp,
                            color = colors.gray700,
                        ),
                    shape = shape,
                ),
    ) {
        items.forEachIndexed { index, item ->
            OBRitDropdownItem(
                text = item,
                selected = selectedIndex == index,
                enabled = enabled,
                textStyle = itemTextStyle,
                onClick = { onItemClick(index) },
            )
        }
    }
}

@Composable
@Suppress("LongMethod", "LongParameterList")
fun OBRitDropdown(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    inputState: DropdownInputState = DropdownInputState.Default,
    supportingText: String = "",
    enabled: Boolean = true,
    expanded: Boolean = false,
    containerColor: Color = LocalOBRitColor.current.gray800,
    textStyle: TextStyle =
        LocalOBRitTypography.current.xl.copy(
            fontWeight = FontWeight.Medium,
        ),
    placeholderTextStyle: TextStyle = textStyle,
) {
    val colors = LocalOBRitColor.current
    val shape = RoundedCornerShape(AtomRadius.Middle.dp)
    val borderColor =
        when {
            inputState == DropdownInputState.Error -> colors.red300
            expanded -> colors.gray700
            else -> Color.Transparent
        }
    val isPlaceholder = value.isEmpty()

    Column(modifier = modifier) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(containerColor)
                    .border(
                        border =
                            BorderStroke(
                                width = 1.dp,
                                color = borderColor,
                            ),
                        shape = shape,
                    ).clickable(
                        enabled = enabled,
                        onClick = onClick,
                    ).padding(
                        start = 20.dp,
                        top = 16.dp,
                        end = 24.dp,
                        bottom = 16.dp,
                    ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = value.ifEmpty { placeholder },
                modifier = Modifier.weight(1f),
                style =
                    if (isPlaceholder) {
                        placeholderTextStyle
                    } else {
                        textStyle
                    }.copy(
                        color =
                            if (isPlaceholder || !enabled) {
                                colors.gray700
                            } else {
                                colors.common00
                            },
                    ),
                maxLines = 1,
            )

            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_dropdown_chevron_down),
                contentDescription = null,
                modifier =
                    Modifier
                        .padding(start = AtomSpacing.S2.dp)
                        .size(AtomSpacing.S4.dp),
                tint = Color.Unspecified,
            )
        }

        if (inputState == DropdownInputState.Error && supportingText.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = AtomSpacing.S3.dp),
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_textfield_exclamation_circle),
                    contentDescription = null,
                    modifier = Modifier.size(AtomSpacing.S4.dp),
                    tint = Color.Unspecified,
                )
                Text(
                    text = supportingText,
                    style =
                        LocalOBRitTypography.current.base.copy(
                            color = colors.red300,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    modifier = Modifier.padding(start = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun OBRitDropdownItem(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    textStyle: TextStyle,
    onClick: () -> Unit,
) {
    val colors = LocalOBRitColor.current
    val backgroundColor =
        if (selected) {
            colors.gray750
        } else {
            colors.gray800
        }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .clickable(
                    enabled = enabled,
                    onClick = onClick,
                ).padding(
                    horizontal = 20.dp,
                    vertical = 16.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = textStyle.copy(color = colors.common00),
            maxLines = 1,
        )
    }
}

private const val OBRIT_DROPDOWN_MENU_PREVIEW_ITEM_COUNT = 6

@Preview(
    name = "OBRitDropdown Disabled Empty",
    showBackground = true,
)
@Composable
private fun OBRitDropdownDisabledEmptyPreview() {
    OBRitDropdownPreviewContainer {
        OBRitDropdown(
            value = "",
            onClick = {},
            placeholder = "TEXT",
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
        )
    }
}

@Preview(
    name = "OBRitDropdown Default Filled",
    showBackground = true,
)
@Composable
private fun OBRitDropdownDefaultFilledPreview() {
    OBRitDropdownPreviewContainer {
        OBRitDropdown(
            value = "TEXT",
            onClick = {},
            placeholder = "TEXT",
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(
    name = "OBRitDropdown Expanded Empty",
    showBackground = true,
)
@Composable
private fun OBRitDropdownExpandedEmptyPreview() {
    OBRitDropdownPreviewContainer {
        OBRitDropdown(
            value = "",
            onClick = {},
            placeholder = "TEXT",
            modifier = Modifier.fillMaxWidth(),
            expanded = true,
        )
    }
}

@Preview(
    name = "OBRitDropdown Error Filled",
    showBackground = true,
)
@Composable
private fun OBRitDropdownErrorFilledPreview() {
    OBRitDropdownPreviewContainer {
        OBRitDropdown(
            value = "TEXT",
            onClick = {},
            placeholder = "TEXT",
            modifier = Modifier.fillMaxWidth(),
            inputState = DropdownInputState.Error,
            supportingText = "에러 텍스트를 입력해주세요",
        )
    }
}

@Preview(
    name = "OBRitDropdown Menu",
    showBackground = true,
)
@Composable
private fun OBRitDropdownMenuPreview() {
    OBRitDropdownPreviewContainer {
        OBRitDropdownMenu(
            items = List(OBRIT_DROPDOWN_MENU_PREVIEW_ITEM_COUNT) { "TEXT" },
            selectedIndex = 1,
            onItemClick = {},
        )
    }
}

@Composable
private fun OBRitDropdownPreviewContainer(content: @Composable () -> Unit) {
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
