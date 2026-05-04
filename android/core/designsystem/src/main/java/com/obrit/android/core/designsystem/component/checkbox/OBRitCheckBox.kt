package com.obrit.android.core.designsystem.component.checkbox

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
fun OBRitCheckBox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val toggleableModifier =
        if (onCheckedChange != null) {
            Modifier.toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Checkbox,
                onValueChange = onCheckedChange,
            )
        } else {
            Modifier
        }

    Icon(
        painter = painterResource(id = checkBoxIconRes(checked = checked, enabled = enabled)),
        contentDescription = null,
        modifier =
            modifier
                .size(OBRitCheckBoxSize)
                .then(toggleableModifier),
        tint = Color.Unspecified,
    )
}

@DrawableRes
private fun checkBoxIconRes(
    checked: Boolean,
    enabled: Boolean,
): Int =
    when {
        checked && enabled -> R.drawable.ic_checkbox_checked
        checked -> R.drawable.ic_checkbox_checked_disabled
        enabled -> R.drawable.ic_checkbox_unchecked
        else -> R.drawable.ic_checkbox_unchecked_disabled
    }

private val OBRitCheckBoxSize = AtomSpacing.S6.dp

@Preview(
    name = "OBRitCheckBox Checked",
    showBackground = true,
)
@Composable
private fun OBRitCheckBoxCheckedPreview() {
    OBRitCheckBoxPreviewContainer {
        OBRitCheckBox(
            checked = true,
            onCheckedChange = null,
        )
    }
}

@Preview(
    name = "OBRitCheckBox Unchecked",
    showBackground = true,
)
@Composable
private fun OBRitCheckBoxUncheckedPreview() {
    OBRitCheckBoxPreviewContainer {
        OBRitCheckBox(
            checked = false,
            onCheckedChange = null,
        )
    }
}

@Preview(
    name = "OBRitCheckBox Checked Disabled",
    showBackground = true,
)
@Composable
private fun OBRitCheckBoxCheckedDisabledPreview() {
    OBRitCheckBoxPreviewContainer {
        OBRitCheckBox(
            checked = true,
            onCheckedChange = null,
            enabled = false,
        )
    }
}

@Preview(
    name = "OBRitCheckBox Unchecked Disabled",
    showBackground = true,
)
@Composable
private fun OBRitCheckBoxUncheckedDisabledPreview() {
    OBRitCheckBoxPreviewContainer {
        OBRitCheckBox(
            checked = false,
            onCheckedChange = null,
            enabled = false,
        )
    }
}

@Composable
private fun OBRitCheckBoxPreviewContainer(content: @Composable () -> Unit) {
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
