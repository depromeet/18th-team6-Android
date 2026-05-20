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
import com.obrit.feature.home.screen.section.ConsumableOrbit
import com.obrit.feature.home.screen.section.ConsumableStatusSection
import com.obrit.feature.home.screen.section.HomeGraphSection
import com.obrit.feature.home.viewmodel.HomeUiState

@Composable
internal fun HomeScreenSuccessContent(
    state: HomeUiState.Success,
    action: HomeScreenAction,
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
    Column(modifier = modifier.fillMaxSize().background(colors.gray900)) {
        HomeTopBar(
            onSearchClick = action.onSearchClick,
            onNotificationClick = action.onNotificationClick,
            onProfileClick = action.onProfileClick,
            modifier = Modifier.statusBarsPadding(),
        )
        HomeContents(state = state, icons = icons)
    }
}

@Composable
private fun HomeContents(
    state: HomeUiState.Success,
    icons: List<ConsumableIcon>,
    modifier: Modifier = Modifier,
) {
    ConsumableStatusSection(
        title = state.status.message.title,
        highlightWord = state.status.message.highlightWord,
        replacementStatus = state.status.message.replacementStatus,
        stockStatus = state.status.message.stockStatus,
        modifier = modifier,
    )
    ConsumableOrbit(icons = icons)
    HomeGraphSection(
        totalCount = state.status.graph.totalCount,
        needReplaceCount = state.status.graph.needReplaceCount,
        score = state.status.graph.score,
        averageScore = state.status.graph.averageScore,
    )
}
