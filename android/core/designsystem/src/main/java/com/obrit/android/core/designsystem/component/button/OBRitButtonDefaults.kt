package com.obrit.android.core.designsystem.component.button

import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import com.obrit.android.core.designsystem.theme.LocalOBRitColor

object OBRitButtonDefaults {
    @Composable
    fun positiveButtonColors() =
        ButtonColors(
            containerColor = LocalOBRitColor.current.green300,
            contentColor = LocalOBRitColor.current.common1000,
            disabledContainerColor = LocalOBRitColor.current.green800,
            disabledContentColor = LocalOBRitColor.current.common1000,
        )

    @Composable
    fun defaultButtonColors() =
        ButtonColors(
            containerColor = LocalOBRitColor.current.gray800,
            contentColor = LocalOBRitColor.current.common00,
            disabledContainerColor = LocalOBRitColor.current.gray800,
            disabledContentColor = LocalOBRitColor.current.gray700,
        )

    @Composable
    fun commonButtonColors() =
        ButtonColors(
            containerColor = LocalOBRitColor.current.common00,
            contentColor = LocalOBRitColor.current.common1000,
            disabledContainerColor = LocalOBRitColor.current.gray600,
            disabledContentColor = LocalOBRitColor.current.gray850,
        )
}
