package com.obrit.feature.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.feature.home.screen.section.ConsumableAlertSection
import com.obrit.feature.home.screen.section.ConsumableIcon
import com.obrit.feature.home.screen.section.ConsumableListPreviewSection
import com.obrit.feature.home.screen.section.ConsumableOrbit
import com.obrit.feature.home.screen.section.ConsumableStatusSection
import com.obrit.feature.home.screen.section.ConsumableUsageStatusSection
import com.obrit.feature.home.screen.section.HomeGraphSection
import com.obrit.feature.home.viewmodel.ConsumableListSortOrder
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
        HomeContents(
            state = state,
            icons = icons,
            onListSortOrderChange = action.onListSortOrderChange,
            onMoreClick = action.onMoreClick,
        )
    }
}

@Composable
private fun HomeContents(
    state: HomeUiState.Success,
    icons: List<ConsumableIcon>,
    onListSortOrderChange: (ConsumableListSortOrder) -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
    ) {
        ConsumableStatusSection(
            title = state.status.message.title,
            highlightWord = state.status.message.highlightWord,
            replacementStatus = state.status.message.replacementStatus,
            stockStatus = state.status.message.stockStatus,
        )
        ConsumableOrbit(
            icons = icons,
//        positiveRatio = state.status.ratio.goodPercentage / 100f,
//        positiveScore = state.status.ratio.goodPercentage,
//        negativeScore = state.status.ratio.warningPercentage,
        )
        HomeGraphSection(
            totalCount = state.status.graph.totalCount,
            needReplaceCount = state.status.graph.needReplaceCount,
            score = state.status.graph.score,
            averageScore = state.status.graph.averageScore,
        )
        ConsumableAlertSection(buckets = state.status.buckets)
        ConsumableListPreviewSection(
            buckets = state.status.buckets,
            sortOrder = state.listSortOrder,
            onSortOrderChange = onListSortOrderChange,
            onMoreClick = onMoreClick,
        )
        ConsumableUsageStatusSection(buckets = state.status.buckets)
    }
}
