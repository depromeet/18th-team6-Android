package com.obrit.android.core.designsystem.component.dim

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.color.AtomColors

@Composable
fun OBRitDim(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(OBRitDimColor),
    )
}

@Preview(
    name = "OBRitDim",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
)
@Composable
private fun OBRitDimPreview() {
    OBRitTheme(dynamicColor = false) {
        OBRitDim()
    }
}

private val OBRitDimColor = Color(AtomColors.CommonOpacity.CommonBlack.S00_80)
