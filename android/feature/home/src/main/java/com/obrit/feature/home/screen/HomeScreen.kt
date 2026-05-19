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

    // TODO: ViewModel에서 사용자 소모품 목록을 받아 대체
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
            onSearchClick = action.onSearchClick,
            onNotificationClick = action.onNotificationClick,
            onProfileClick = action.onProfileClick,
            modifier = Modifier.statusBarsPadding(),
        )
        ConsumableStatusSection()
        GlassBallSection(icons = icons)
    }
}
