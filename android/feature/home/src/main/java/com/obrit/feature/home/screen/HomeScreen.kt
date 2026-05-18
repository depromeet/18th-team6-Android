package com.obrit.feature.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.feature.home.screen.section.ConsumableStatusSection

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val colors = LocalOBRitColor.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.gray900),
    ) {
        ConsumableStatusSection()
    }
}
