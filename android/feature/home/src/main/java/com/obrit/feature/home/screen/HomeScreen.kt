package com.obrit.feature.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.feature.home.screen.section.ConsumableStatusSection

data class HomeScreenAction(
    val onSearchClick: () -> Unit,
    val onNotificationClick: () -> Unit,
    val onProfileClick: () -> Unit,
)

@Composable
fun HomeScreen(
    action: HomeScreenAction,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.gray900),
    ) {
        HomeTopBar(
            onSearchClick = action.onSearchClick,
            onNotificationClick = action.onNotificationClick,
            onProfileClick = action.onProfileClick,
            modifier = Modifier.statusBarsPadding(),
        )
        ConsumableStatusSection()
    }
}
