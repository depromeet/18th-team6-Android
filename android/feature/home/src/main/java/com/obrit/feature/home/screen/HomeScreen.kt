package com.obrit.feature.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.feature.home.screen.section.ConsumableIcon
import com.obrit.feature.home.screen.section.ConsumableStatusSection
import com.obrit.feature.home.screen.section.GlassBallSection

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current

    val icons =
        remember {
            listOf(
                ConsumableIcon(R.drawable.ic_towel, 68.dp, 49.dp),
                ConsumableIcon(R.drawable.ic_toothbrush, 70.dp, 70.dp),
                ConsumableIcon(R.drawable.ic_detergent, 36.dp, 54.dp),
                ConsumableIcon(R.drawable.ic_razor, 58.dp, 79.dp),
            )
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.gray900),
    ) {
        HomeTopBar(
            onSearchClick = onSearchClick,
            onNotificationClick = onNotificationClick,
            onProfileClick = onProfileClick,
            modifier = Modifier.statusBarsPadding(),
        )
        ConsumableStatusSection()
        GlassBallSection(icons = icons)
    }
}
