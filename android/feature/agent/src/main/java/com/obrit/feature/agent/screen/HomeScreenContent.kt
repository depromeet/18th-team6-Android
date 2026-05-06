@file:Suppress("LongMethod", "MagicNumber", "TooManyFunctions")

package com.obrit.feature.agent.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.feature.agent.viewmodel.HomeConsumableUiModel
import com.obrit.feature.agent.viewmodel.HomePreviewSort
import com.obrit.feature.agent.viewmodel.HomeStatusFilter
import com.obrit.feature.agent.viewmodel.HomeUiState
import com.obrit.feature.agent.viewmodel.HomeUsageUiModel

@Composable
internal fun HomeScreenContent(
    state: HomeUiState,
    action: HomeScreenAction,
    modifier: Modifier = Modifier,
) {
    DeviceTiltReporter(onTiltChange = action.onDeviceTiltChanged)

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(HomeBackground),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .padding(horizontal = HomeHorizontalPadding)
                    .padding(top = 24.dp, bottom = 116.dp),
        ) {
            HomeHeader(
                onSearchClick = action.onSearchClick,
                onNotificationClick = action.onNotificationClick,
                onProfileClick = action.onProfileClick,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(28.dp))
            HomeTitle(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(28.dp))
            HomeHealthSummary(
                state = state,
                onOrbDragged = action.onOrbDragged,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(18.dp))
            HomeStatusMeter(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(30.dp))
            HomeStatusFilters(
                selectedFilter = state.selectedStatusFilter,
                onFilterClick = action.onStatusFilterClick,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(14.dp))
            HomeUrgentCards(
                consumables = state.filteredUrgentConsumables(),
                onConsumableClick = action.onConsumableClick,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(32.dp))
            HomePreviewHeader(
                sort = state.previewSort,
                onSortClick = action.onPreviewSortClick,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(14.dp))
            HomePreviewList(
                consumables = state.visiblePreviewConsumables(),
                onConsumableClick = action.onConsumableClick,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(14.dp))
            HomeMoreButton(
                isExpanded = state.isPreviewExpanded,
                onClick = action.onPreviewMoreClick,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(36.dp))
            HomeUsageSection(
                usageItems = state.usageItems,
                onUsageClick = action.onUsageClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        HomeBottomBar(
            onHomeClick = action.onHomeTabClick,
            onListClick = action.onListTabClick,
            onAddClick = action.onAddClick,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = HomeHorizontalPadding)
                    .padding(bottom = 12.dp),
        )
    }
}

@Composable
private fun HomeHeader(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "OBRit",
            style =
                typography.xl2.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                ),
            maxLines = 1,
        )
        Spacer(modifier = Modifier.weight(1f))
        HeaderIconButton(
            onClick = onSearchClick,
            modifier = Modifier.size(34.dp),
        ) {
            SearchIcon(
                color = Color.White,
                modifier = Modifier.size(20.dp),
            )
        }
        HeaderIconButton(
            onClick = onNotificationClick,
            modifier = Modifier.size(34.dp),
        ) {
            BellIcon(
                color = Color.White,
                modifier = Modifier.size(20.dp),
            )
        }
        HeaderIconButton(
            onClick = onProfileClick,
            modifier = Modifier.size(34.dp),
        ) {
            UserIcon(
                color = Color.White,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun HeaderIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier =
            modifier
                .clip(CircleShape)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun HomeTitle(modifier: Modifier = Modifier) {
    val typography = LocalOBRitTypography.current

    Column(modifier = modifier) {
        Text(
            text =
                buildAnnotatedString {
                    append("오늘의 소모품 관리\n상태는 ")
                    withStyle(SpanStyle(color = WarningOrange)) {
                        append("경고")
                    }
                    append("예요")
                },
            style =
                typography.xl.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                ),
            maxLines = 2,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
            SmallLegendText(text = "교체 관리 경고")
            SmallLegendText(text = "여분 관리 경고")
        }
    }
}

@Composable
private fun SmallLegendText(
    text: String,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current

    Text(
        text = text,
        modifier = modifier,
        style =
            typography.xs.copy(
                color = MutedText,
                fontWeight = FontWeight.Medium,
            ),
        maxLines = 1,
    )
}

@Composable
private fun HomeHealthSummary(
    state: HomeUiState,
    onOrbDragged: (Float, Float, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .height(224.dp),
    ) {
        HealthPercentLabel(
            percent = "${state.normalPercent}%",
            label = "양호",
            color = HealthyMint,
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 22.dp, bottom = 42.dp),
        )
        GlassConsumableOrb(
            orb = state.orb,
            ratios = GlassOrbRatios(normal = state.normalRatio, warning = state.warningRatio),
            colors =
                GlassOrbColors(
                    positive = HealthyMint,
                    warning = WarningOrange,
                    shadow = CardBlack,
                    glass = Color.White,
                ),
            onOrbDragged = onOrbDragged,
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .size(224.dp),
        )
        HealthPercentLabel(
            percent = "${state.warningPercent}%",
            label = "경고",
            color = WarningOrange,
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 22.dp, bottom = 42.dp),
        )
    }
}

@Composable
private fun HealthPercentLabel(
    percent: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = percent,
            style =
                typography.lg.copy(
                    color = color,
                    fontWeight = FontWeight.Bold,
                ),
            maxLines = 1,
        )
        Text(
            text = label,
            style =
                typography.s.copy(
                    color = color,
                    fontWeight = FontWeight.Bold,
                ),
            maxLines = 1,
        )
    }
}

@Composable
private fun HomeStatusMeter(modifier: Modifier = Modifier) {
    val typography = LocalOBRitTypography.current

    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(12.dp))
                .background(CardBlack)
                .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            MeterValueLine(
                label = "내 소모품",
                value = "16",
                color = Color.White,
            )
            MeterValueLine(
                label = "교체 위험",
                value = "4",
                color = WarningOrange,
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "내 상태",
                style =
                    typography.xs.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    ),
                maxLines = 1,
            )
            HomeMeterTicks(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(22.dp),
            )
            Text(
                text = "평균",
                style =
                    typography.xs.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    ),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun MeterValueLine(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style =
                typography.xs.copy(
                    color = color,
                    fontWeight = FontWeight.Bold,
                ),
            maxLines = 1,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style =
                typography.xs.copy(
                    color = color,
                    fontWeight = FontWeight.Bold,
                ),
            maxLines = 1,
        )
    }
}

@Composable
private fun HomeMeterTicks(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val tickCount = 56
        val tickWidth = 1.6.dp.toPx()
        val gap = (size.width - tickWidth * tickCount) / (tickCount - 1)

        repeat(tickCount) { index ->
            val progress = index / (tickCount - 1).toFloat()
            val tickColor =
                when {
                    index < 10 -> WarningOrange
                    index < 20 -> WarmGold
                    index < 36 -> Color(0xFFC4C4AA)
                    else -> HealthyMint
                }
            val height = if (index == 21) 19.dp.toPx() else 15.dp.toPx()
            val color = if (index == 21) Color.White else tickColor.copy(alpha = 0.25f + progress * 0.75f)
            val x = index * (tickWidth + gap)

            drawRoundRect(
                color = color,
                topLeft = Offset(x, (size.height - height) / 2f),
                size = Size(tickWidth, height),
                cornerRadius = CornerRadius(tickWidth, tickWidth),
            )
        }
    }
}

@Composable
private fun HomeStatusFilters(
    selectedFilter: HomeStatusFilter,
    onFilterClick: (HomeStatusFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        HomeFilterChip(
            text = "교체 위험  4",
            selected = selectedFilter == HomeStatusFilter.ReplacementDanger,
            onClick = { onFilterClick(HomeStatusFilter.ReplacementDanger) },
        )
        HomeFilterChip(
            text = "여분 부족  3",
            selected = selectedFilter == HomeStatusFilter.SpareShortage,
            onClick = { onFilterClick(HomeStatusFilter.SpareShortage) },
        )
        HomeFilterChip(
            text = "교체 경고  4",
            selected = selectedFilter == HomeStatusFilter.ReplacementWarning,
            onClick = { onFilterClick(HomeStatusFilter.ReplacementWarning) },
        )
    }
}

@Composable
private fun HomeFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current
    val background = if (selected) Color.White else ChipGray
    val contentColor = if (selected) CardBlack else Color.White

    Box(
        modifier =
            modifier
                .height(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(background)
                .clickable(onClick = onClick)
                .padding(horizontal = 17.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style =
                typography.s.copy(
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                ),
            maxLines = 1,
        )
    }
}

@Composable
private fun HomeUrgentCards(
    consumables: List<HomeConsumableUiModel>,
    onConsumableClick: (HomeConsumableUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        consumables.forEachIndexed { index, consumable ->
            HomeUrgentCard(
                consumable = consumable,
                emphasized = index == 0,
                onClick = { onConsumableClick(consumable) },
                modifier = Modifier.width(134.dp),
            )
        }
    }
}

@Composable
private fun HomeUrgentCard(
    consumable: HomeConsumableUiModel,
    emphasized: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current
    val background = if (emphasized) WarningOrange else BrownCard

    Column(
        modifier =
            modifier
                .height(134.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(background)
                .clickable(onClick = onClick)
                .padding(14.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(DotGray),
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = consumable.title,
            style =
                typography.s.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = consumable.remainLabel,
            style =
                typography.xs.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(10.dp))
        HomeBadge(
            text = consumable.replacementLabel,
            background = Color.White,
            contentColor = WarningOrange,
            modifier = Modifier.height(24.dp),
        )
    }
}

@Composable
private fun HomePreviewHeader(
    sort: HomePreviewSort,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier =
                Modifier
                    .height(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.44f), RoundedCornerShape(6.dp))
                    .clickable(onClick = onSortClick)
                    .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = sort.label(),
                style =
                    typography.xs.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                    ),
                maxLines = 1,
            )
            Spacer(modifier = Modifier.width(4.dp))
            ChevronDownIcon(
                color = Color.White,
                modifier = Modifier.size(12.dp),
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "미리보기",
            style =
                typography.s.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                ),
            maxLines = 1,
        )
    }
}

@Composable
private fun HomePreviewList(
    consumables: List<HomeConsumableUiModel>,
    onConsumableClick: (HomeConsumableUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        consumables.forEachIndexed { index, consumable ->
            HomePreviewCard(
                consumable = consumable,
                emphasized = index == 0,
                onClick = { onConsumableClick(consumable) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun HomePreviewCard(
    consumable: HomeConsumableUiModel,
    emphasized: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current
    val background = if (emphasized) WarningOrange else BrownCard

    Row(
        modifier =
            modifier
                .height(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(background)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(DotGray),
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = consumable.title,
                style =
                    typography.s.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = consumable.remainLabel,
                style =
                    typography.xs.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            HomeBadge(
                text = consumable.replacementLabel,
                background = if (emphasized) Color.White.copy(alpha = 0.28f) else Color.White,
                contentColor = if (emphasized) Color.White else WarningOrange,
            )
            HomeBadge(
                text = consumable.spareLabel,
                background = if (emphasized) Color.White.copy(alpha = 0.28f) else BadgeDark,
                contentColor = Color.White,
            )
        }
    }
}

@Composable
private fun HomeBadge(
    text: String,
    background: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current

    Box(
        modifier =
            modifier
                .height(24.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(background)
                .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style =
                typography.xs.copy(
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                ),
            maxLines = 1,
        )
    }
}

@Composable
private fun HomeMoreButton(
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current

    Box(
        modifier =
            modifier
                .height(44.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(ButtonGray)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (isExpanded) "접기" else "더보기",
            style =
                typography.s.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                ),
            maxLines = 1,
        )
    }
}

@Composable
private fun HomeUsageSection(
    usageItems: List<HomeUsageUiModel>,
    onUsageClick: (HomeUsageUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current

    Column(modifier = modifier) {
        Text(
            text = "사용 현황",
            style =
                typography.s.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                ),
            maxLines = 1,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            usageItems.forEach { usage ->
                HomeUsageRow(
                    usage = usage,
                    onClick = { onUsageClick(usage) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun HomeUsageRow(
    usage: HomeUsageUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current

    Row(
        modifier =
            modifier
                .height(34.dp)
                .clip(RoundedCornerShape(6.dp))
                .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(DarkDot),
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = usage.title,
            modifier = Modifier.weight(1f),
            style =
                typography.s.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = "${usage.daysInUse}일째 사용중",
            style =
                typography.s.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                ),
            maxLines = 1,
        )
        Spacer(modifier = Modifier.width(12.dp))
        ChevronRightIcon(
            color = Color.White,
            modifier = Modifier.size(14.dp),
        )
    }
}

@Composable
private fun HomeBottomBar(
    onHomeClick: () -> Unit,
    onListClick: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier =
                Modifier
                    .height(54.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(BottomBarGray)
                    .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable(onClick = onHomeClick),
                contentAlignment = Alignment.Center,
            ) {
                HomeIcon(
                    color = CardBlack,
                    modifier = Modifier.size(22.dp),
                )
            }
            Box(
                modifier =
                    Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onListClick),
                contentAlignment = Alignment.Center,
            ) {
                ListIcon(
                    color = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
        Spacer(modifier = Modifier.weight(0.72f))
        Box(
            modifier =
                Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(onClick = onAddClick),
            contentAlignment = Alignment.Center,
        ) {
            PlusIcon(
                color = CardBlack,
                modifier = Modifier.size(26.dp),
            )
        }
    }
}

@Composable
private fun SearchIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        drawCircle(
            color = color,
            radius = size.minDimension * 0.28f,
            center = Offset(size.width * 0.42f, size.height * 0.42f),
            style = Stroke(width = 2.dp.toPx()),
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.62f, size.height * 0.62f),
            end = Offset(size.width * 0.84f, size.height * 0.84f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun BellIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val stroke = 1.8.dp.toPx()
        drawLine(
            color = color,
            start = Offset(size.width * 0.28f, size.height * 0.66f),
            end = Offset(size.width * 0.72f, size.height * 0.66f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawArc(
            color = color,
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(size.width * 0.25f, size.height * 0.18f),
            size = Size(size.width * 0.5f, size.height * 0.62f),
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.36f, size.height * 0.42f),
            end = Offset(size.width * 0.22f, size.height * 0.66f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.64f, size.height * 0.42f),
            end = Offset(size.width * 0.78f, size.height * 0.66f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawCircle(
            color = color,
            radius = size.minDimension * 0.06f,
            center = Offset(size.width * 0.5f, size.height * 0.8f),
        )
    }
}

@Composable
private fun UserIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val stroke = 1.8.dp.toPx()
        drawCircle(
            color = color,
            radius = size.minDimension * 0.18f,
            center = Offset(size.width * 0.5f, size.height * 0.32f),
            style = Stroke(width = stroke),
        )
        drawArc(
            color = color,
            startAngle = 205f,
            sweepAngle = 130f,
            useCenter = false,
            topLeft = Offset(size.width * 0.22f, size.height * 0.48f),
            size = Size(size.width * 0.56f, size.height * 0.46f),
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
    }
}

@Composable
private fun ChevronDownIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        drawLine(
            color = color,
            start = Offset(size.width * 0.22f, size.height * 0.38f),
            end = Offset(size.width * 0.5f, size.height * 0.66f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.5f, size.height * 0.66f),
            end = Offset(size.width * 0.78f, size.height * 0.38f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun ChevronRightIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        drawLine(
            color = color,
            start = Offset(size.width * 0.36f, size.height * 0.22f),
            end = Offset(size.width * 0.64f, size.height * 0.5f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.64f, size.height * 0.5f),
            end = Offset(size.width * 0.36f, size.height * 0.78f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun HomeIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val stroke = 2.2.dp.toPx()
        drawLine(
            color = color,
            start = Offset(size.width * 0.22f, size.height * 0.48f),
            end = Offset(size.width * 0.5f, size.height * 0.24f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.5f, size.height * 0.24f),
            end = Offset(size.width * 0.78f, size.height * 0.48f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.3f, size.height * 0.46f),
            size = Size(size.width * 0.4f, size.height * 0.34f),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
    }
}

@Composable
private fun ListIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val stroke = 2.dp.toPx()
        listOf(0.3f, 0.5f, 0.7f).forEach { y ->
            drawCircle(
                color = color,
                radius = size.minDimension * 0.045f,
                center = Offset(size.width * 0.26f, size.height * y),
            )
            drawLine(
                color = color,
                start = Offset(size.width * 0.42f, size.height * y),
                end = Offset(size.width * 0.78f, size.height * y),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun PlusIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val stroke = 2.2.dp.toPx()
        drawLine(
            color = color,
            start = Offset(size.width * 0.5f, size.height * 0.24f),
            end = Offset(size.width * 0.5f, size.height * 0.76f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.24f, size.height * 0.5f),
            end = Offset(size.width * 0.76f, size.height * 0.5f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
    }
}

private fun HomeUiState.filteredUrgentConsumables(): List<HomeConsumableUiModel> =
    urgentConsumables
        .filter { it.statusFilter == selectedStatusFilter }
        .ifEmpty { urgentConsumables }

private fun HomeUiState.visiblePreviewConsumables(): List<HomeConsumableUiModel> {
    val sortedConsumables =
        when (previewSort) {
            HomePreviewSort.NearReplacement -> previewConsumables
            HomePreviewSort.LowSpare -> previewConsumables.sortedByDescending { it.spareLabel.contains("0") }
            HomePreviewSort.LongUse -> previewConsumables.reversed()
        }

    return if (isPreviewExpanded) {
        sortedConsumables
    } else {
        sortedConsumables.take(3)
    }
}

private fun HomePreviewSort.label(): String =
    when (this) {
        HomePreviewSort.NearReplacement -> "교체 임박 순"
        HomePreviewSort.LowSpare -> "여분 부족 순"
        HomePreviewSort.LongUse -> "오래 사용 순"
    }

private val HomeHorizontalPadding = 16.dp
private val HomeBackground = Color(0xFF1D1B20)
private val CardBlack = Color(0xFF08090C)
private val ChipGray = Color(0xFF2D2E33)
private val ButtonGray = Color(0xFF303136)
private val BottomBarGray = Color(0xFF6E6E72)
private val BrownCard = Color(0xFF3A201F)
private val BadgeDark = Color(0xFF3C3E42)
private val DotGray = Color(0xFF30343C)
private val DarkDot = Color(0xFF22232A)
private val MutedText = Color(0xFFB2B3B4)
private val WarningOrange = Color(0xFFFF5922)
private val HealthyMint = Color(0xFF25EFCD)
private val WarmGold = Color(0xFFB99C63)
