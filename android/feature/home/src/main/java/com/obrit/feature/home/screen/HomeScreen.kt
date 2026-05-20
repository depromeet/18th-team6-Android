package com.obrit.feature.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.feature.home.screen.section.ConsumableIcon
import com.obrit.feature.home.screen.section.ConsumableStatusSection
import com.obrit.feature.home.screen.section.GlassBallSection
import com.obrit.feature.home.screen.section.HomeGraphSection
import com.obrit.feature.home.viewmodel.HomeUiState
import com.obrit.feature.home.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.collectAsState()
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
        modifier = modifier.fillMaxSize().background(colors.gray900),
    ) {
        HomeTopBar(
            onSearchClick = onSearchClick,
            onNotificationClick = onNotificationClick,
            onProfileClick = onProfileClick,
            modifier = Modifier.statusBarsPadding(),
        )
        HomeContents(state = state, icons = icons)
    }
}

@Composable
private fun HomeContents(
    state: HomeUiState,
    icons: List<ConsumableIcon>,
) {
    if (state is HomeUiState.Success) {
        ConsumableStatusSection(
            title = state.status.message.title,
            highlightWord = state.status.message.highlightWord,
            replacementStatus = state.status.message.replacementStatus,
            stockStatus = state.status.message.stockStatus,
        )
    }
    GlassBallSection(icons = icons)
    if (state is HomeUiState.Success) {
        HomeGraphSection(
            totalCount = state.status.graph.totalCount,
            needReplaceCount = state.status.graph.needReplaceCount,
            score = state.status.graph.score,
            averageScore = state.status.graph.averageScore,
        )
    }
}
