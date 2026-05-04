package com.obrit.android.core.designsyetem.component.radiobutton

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsyetem.R
import com.obrit.android.core.designsyetem.theme.LocalOBRitColor
import com.obrit.android.core.designsyetem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
fun OBRitRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val selectableModifier =
        if (onClick != null) {
            Modifier.selectable(
                selected = selected,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = onClick,
            )
        } else {
            Modifier
        }

    Icon(
        painter = painterResource(id = radioButtonIconRes(selected = selected, enabled = enabled)),
        contentDescription = null,
        modifier =
            modifier
                .size(OBRitRadioButtonSize)
                .then(selectableModifier),
        tint = Color.Unspecified,
    )
}

@DrawableRes
private fun radioButtonIconRes(
    selected: Boolean,
    enabled: Boolean,
): Int =
    when {
        selected && enabled -> R.drawable.ic_radiobutton_selected
        selected -> R.drawable.ic_radiobutton_selected_disabled
        enabled -> R.drawable.ic_radiobutton_unselected
        else -> R.drawable.ic_radiobutton_unselected_disabled
    }

private val OBRitRadioButtonSize = AtomSpacing.S6.dp

@Preview(
    name = "OBRitRadioButton Selected",
    showBackground = true,
)
@Composable
private fun OBRitRadioButtonSelectedPreview() {
    OBRitRadioButtonPreviewContainer {
        OBRitRadioButton(
            selected = true,
            onClick = null,
        )
    }
}

@Preview(
    name = "OBRitRadioButton Unselected",
    showBackground = true,
)
@Composable
private fun OBRitRadioButtonUnselectedPreview() {
    OBRitRadioButtonPreviewContainer {
        OBRitRadioButton(
            selected = false,
            onClick = null,
        )
    }
}

@Preview(
    name = "OBRitRadioButton Selected Disabled",
    showBackground = true,
)
@Composable
private fun OBRitRadioButtonSelectedDisabledPreview() {
    OBRitRadioButtonPreviewContainer {
        OBRitRadioButton(
            selected = true,
            onClick = null,
            enabled = false,
        )
    }
}

@Preview(
    name = "OBRitRadioButton Unselected Disabled",
    showBackground = true,
)
@Composable
private fun OBRitRadioButtonUnselectedDisabledPreview() {
    OBRitRadioButtonPreviewContainer {
        OBRitRadioButton(
            selected = false,
            onClick = null,
            enabled = false,
        )
    }
}

@Composable
private fun OBRitRadioButtonPreviewContainer(content: @Composable () -> Unit) {
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
