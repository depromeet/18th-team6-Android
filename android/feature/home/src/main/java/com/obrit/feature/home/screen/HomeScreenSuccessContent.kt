package com.obrit.feature.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.component.gnb.OBRitGnb
import com.obrit.android.core.designsystem.component.gnb.OBRitGnbTab
import com.obrit.android.core.designsystem.component.topbar.OBRitHomeTopBar
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
    var selectedTab by remember { mutableStateOf(OBRitGnbTab.Home) }
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.gray900),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            OBRitHomeTopBar(
                onSearchClick = action.onSearchClick,
                onNotificationClick = action.onNotificationClick,
                onProfileClick = action.onProfileClick,
                modifier = Modifier.statusBarsPadding(),
            )
            HomeContents(
                state = state,
                onListSortOrderChange = action.onListSortOrderChange,
                onMoreClick = action.onMoreClick,
            )
        }
        HomeGnbBar(
            selectedTab = selectedTab,
            onTabSelect = { selectedTab = it },
            onRegisterClick = action.onRegisterClick,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun HomeGnbBar(
    selectedTab: OBRitGnbTab,
    onTabSelect: (OBRitGnbTab) -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 24.dp, start = 24.dp, end = 24.dp),
    ) {
        OBRitGnb(
            selectedTab = selectedTab,
            onTabSelect = onTabSelect,
            modifier = Modifier.align(Alignment.Center),
        )
        HomeFab(
            onClick = onRegisterClick,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}

@Composable
private fun HomeFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    Box(
        modifier =
            modifier
                .homeFabShadow()
                .size(HomeFabSize)
                .clip(CircleShape)
                .background(colors.common00)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_gnb_fab),
            contentDescription = null,
            tint = colors.gray900,
            modifier = Modifier.size(HomeFabIconSize),
        )
    }
}

private fun Modifier.homeFabShadow(): Modifier =
    drawBehind {
        drawIntoCanvas { canvas ->
            val paint =
                Paint().apply {
                    asFrameworkPaint().apply {
                        isAntiAlias = true
                        color = android.graphics.Color.TRANSPARENT
                        setShadowLayer(
                            HomeFabShadowBlur.toPx(),
                            0f,
                            HomeFabShadowOffsetY.toPx(),
                            Color.Black.copy(alpha = HOME_FAB_SHADOW_ALPHA).toArgb(),
                        )
                    }
                }
            canvas.drawRoundRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
                radiusX = size.width / 2f,
                radiusY = size.height / 2f,
                paint = paint,
            )
        }
    }

@Composable
private fun HomeContents(
    state: HomeUiState.Success,
    onListSortOrderChange: (ConsumableListSortOrder) -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(bottom = 80.dp),
    ) {
        ConsumableStatusSection(
            title = state.status.message.title,
            highlightWord = state.status.message.highlightWord,
            replacementStatus = state.status.message.replacementStatus,
            stockStatus = state.status.message.stockStatus,
        )
        ConsumableOrbit(
            icons = homeConsumableIcons,
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

private val homeConsumableIcons =
    listOf(
        ConsumableIcon(R.drawable.ic_towel, 68.dp, 49.dp),
        ConsumableIcon(R.drawable.ic_toothbrush, 70.dp, 70.dp),
        ConsumableIcon(R.drawable.ic_detergent, 36.dp, 54.dp),
        ConsumableIcon(R.drawable.ic_razor, 58.dp, 79.dp),
    )
private val HomeFabSize = 56.dp
private val HomeFabIconSize = 30.dp
private val HomeFabShadowBlur = 24.dp
private val HomeFabShadowOffsetY = 16.dp
private const val HOME_FAB_SHADOW_ALPHA = 0.24f
