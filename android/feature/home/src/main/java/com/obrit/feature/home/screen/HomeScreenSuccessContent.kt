package com.obrit.feature.home.screen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.component.gnb.OBRitGnb
import com.obrit.android.core.designsystem.component.gnb.OBRitGnbTab
import com.obrit.android.core.designsystem.component.topbar.OBRitHomeTopBar
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.feature.home.screen.homeSection.ItemListPreviewSection
import com.obrit.feature.home.screen.homeSection.ItemOrbitSection
import com.obrit.feature.home.screen.homeSection.ItemStatusSection
import com.obrit.feature.home.screen.homeSection.ItemUsageStatusSection
import com.obrit.feature.home.screen.homeSection.MyStatusGraphSection
import com.obrit.feature.home.screen.homeSection.QuickItemSection
import com.obrit.feature.home.viewmodel.ConsumableListSortOrder
import com.obrit.feature.home.viewmodel.HomeUiState
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import kotlinx.coroutines.launch

@Suppress("LongMethod")
@Composable
internal fun HomeScreenSuccessContent(
    state: HomeUiState.Success,
    action: HomeScreenAction,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    var selectedTab by remember { mutableStateOf(OBRitGnbTab.Home) }
    var fabExpanded by remember { mutableStateOf(false) }
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
            if (selectedTab == OBRitGnbTab.Home) {
                HomeContents(
                    state = state,
                    onListSortOrderChange = action.onListSortOrderChange,
                    onMoreClick = { selectedTab = OBRitGnbTab.List },
                    onLoadMoreItems = action.onLoadMoreItems,
                    onItemClick = action.onItemClick,
                )
            } else {
                ItemListScreenContent(
                    items = state.items.content,
                    hasNext = state.items.hasNext,
                    sortOrder = state.listSortOrder,
                    ddayRange = state.ddayRange,
                    ddayFilterMax = state.ddayFilterMax,
                    spareRange = state.spareRange,
                    spareFilterMax = state.spareFilterMax,
                    action =
                        ItemListScreenAction(
                            onBack = {},
                            onSortOrderChange = action.onListSortOrderChange,
                            onDdayFilterChange = action.onDdayFilterChange,
                            onSpareFilterChange = action.onSpareFilterChange,
                            onFilterApply = action.onFilterApply,
                            onLoadMoreItems = action.onLoadMoreItems,
                            onItemClick = action.onItemClick,
                        ),
                    modifier = Modifier.fillMaxSize(),
                    contentBottomPadding = HomeGnbContentBottomPadding,
                )
            }
        }
        if (fabExpanded) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { fabExpanded = false },
                        ),
            )
        }
        HomeGnbBar(
            selectedTab = selectedTab,
            onTabSelect = { selectedTab = it },
            fabExpanded = fabExpanded,
            onFabToggle = { fabExpanded = !fabExpanded },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
        if (fabExpanded) {
            HomeFabMenu(
                onReceiptRegisterClick = {
                    fabExpanded = false
                    action.onReceiptRegisterClick()
                },
                onManualRegisterClick = {
                    fabExpanded = false
                    action.onManualRegisterClick()
                },
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .navigationBarsPadding()
                        .padding(
                            end = HomeGnbHorizontalPadding,
                            bottom = HomeGnbBottomPadding + HomeFabSize + HomeFabMenuGap,
                        ),
            )
        }
    }
}

@Composable
private fun HomeGnbBar(
    selectedTab: OBRitGnbTab,
    onTabSelect: (OBRitGnbTab) -> Unit,
    fabExpanded: Boolean,
    onFabToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(
                    start = HomeGnbHorizontalPadding,
                    end = HomeGnbHorizontalPadding,
                    bottom = HomeGnbBottomPadding,
                ),
    ) {
        OBRitGnb(
            selectedTab = selectedTab,
            onTabSelect = onTabSelect,
            modifier = Modifier.align(Alignment.Center),
        )
        HomeFab(
            expanded = fabExpanded,
            onClick = onFabToggle,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}

@Composable
private fun HomeFab(
    expanded: Boolean,
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
                .background(if (expanded) colors.gray600 else colors.common00)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = if (expanded) R.drawable.ic_topbar_close else R.drawable.ic_gnb_fab),
            contentDescription = null,
            tint = if (expanded) colors.common00 else colors.gray900,
            modifier = Modifier.size(HomeFabIconSize),
        )
    }
}

@Composable
private fun HomeFabMenu(
    onReceiptRegisterClick: () -> Unit,
    onManualRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    Column(
        modifier =
            modifier
                .width(HomeFabMenuWidth)
                .clip(RoundedCornerShape(HomeFabMenuRadius))
                .background(colors.gray50)
                .padding(vertical = AtomSpacing.S2.dp),
    ) {
        HomeFabMenuItem(text = "이미지 등록", onClick = onReceiptRegisterClick)
        HomeFabMenuItem(text = "직접 등록", onClick = onManualRegisterClick)
    }
}

@Composable
private fun HomeFabMenuItem(
    text: String,
    onClick: () -> Unit,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = AtomSpacing.S5.dp, vertical = AtomSpacing.S3.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = typography.xl.copy(fontWeight = FontWeight.Bold),
            color = colors.gray900,
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

@Suppress("LongParameterList")
@Composable
private fun HomeContents(
    state: HomeUiState.Success,
    onListSortOrderChange: (ConsumableListSortOrder) -> Unit,
    onMoreClick: () -> Unit,
    onLoadMoreItems: () -> Unit,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    ScrollToTopOnResumeEffect(scrollState)
    val currentOnLoadMoreItems by rememberUpdatedState(onLoadMoreItems)
    LaunchedEffect(scrollState.value) {
        if (scrollState.maxValue > 0 && scrollState.value >= scrollState.maxValue && state.items.hasNext) {
            currentOnLoadMoreItems()
        }
    }
    if (state.items.content.isEmpty()) {
        HomeContentsEmptyState()
    } else {
        HomeContentsItemList(
            state = state,
            scrollState = scrollState,
            onListSortOrderChange = onListSortOrderChange,
            onMoreClick = onMoreClick,
            onItemClick = onItemClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun HomeContentsEmptyState() {
    val typography = LocalOBRitTypography.current
    val colors = LocalOBRitColor.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
        ) {
            Text(
                text = "아직 등록된 소모품이 없어요.",
                style = typography.xl3.copy(fontWeight = FontWeight.Bold),
                color = colors.common00,
            )
            Text(
                text = "하단 + 버튼으로 소모품을 등록해보세요!",
                style = typography.base.copy(fontWeight = FontWeight.Medium),
                color = colors.gray300,
            )
        }
    }
}

@Suppress("LongParameterList")
@Composable
private fun HomeContentsItemList(
    state: HomeUiState.Success,
    scrollState: ScrollState,
    onListSortOrderChange: (ConsumableListSortOrder) -> Unit,
    onMoreClick: () -> Unit,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalCount = state.myStatusSummary.totalCount.coerceAtLeast(1)
    val negativeRatio = state.myStatusSummary.needReplaceCount.toFloat() / totalCount
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .navigationBarsPadding()
                .padding(bottom = 80.dp),
    ) {
        ItemStatusSection(overallStatus = state.overallStatus)
        ItemOrbitSection(
            items = state.items.content.take(ORBIT_ITEMS_SIZE),
            normalRatio = 1f - negativeRatio,
            negativeRatio = negativeRatio,
        )
        MyStatusGraphSection(myStatusSummary = state.myStatusSummary)
        QuickItemSection(buckets = state.buckets, onItemClick = onItemClick)
        ItemListPreviewSection(
            items = state.items.content,
            sortOrder = state.listSortOrder,
            onSortOrderChange = onListSortOrderChange,
            onMoreClick = onMoreClick,
            onItemClick = onItemClick,
        )
        ItemUsageStatusSection(items = state.items.content, onItemClick = onItemClick)
    }
}

@Composable
private fun ScrollToTopOnResumeEffect(scrollState: ScrollState) {
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    coroutineScope.launch { scrollState.scrollTo(0) }
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

private val HomeFabSize = 56.dp
private val HomeFabIconSize = 24.dp
private val HomeFabShadowBlur = 24.dp
private val HomeFabShadowOffsetY = 16.dp
private val HomeGnbHorizontalPadding = AtomSpacing.S5.dp
private val HomeGnbBottomPadding = AtomSpacing.S5.dp
private val HomeFabMenuGap = AtomSpacing.S2.dp
private val HomeFabMenuWidth = 120.dp
private val HomeFabMenuRadius = 16.dp
private val HomeGnbContentBottomPadding = HomeFabSize + HomeGnbBottomPadding + AtomSpacing.S5.dp
private const val HOME_FAB_SHADOW_ALPHA = 0.24f
private const val ORBIT_ITEMS_SIZE = 10
