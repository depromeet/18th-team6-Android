@file:Suppress("LongMethod", "LongParameterList", "MagicNumber", "TooManyFunctions")

package com.obrit.feature.agent.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.feature.agent.R
import com.obrit.feature.agent.viewmodel.ConsumableDetailStatus
import com.obrit.feature.agent.viewmodel.ConsumableDetailUiModel
import com.obrit.feature.agent.viewmodel.ConsumableDetailUiState
import com.obrit.feature.agent.viewmodel.ReplacementHistoryUiModel

@Composable
internal fun ConsumableDetailScreenContent(
    state: ConsumableDetailUiState,
    action: ConsumableDetailScreenAction,
    modifier: Modifier = Modifier,
) {
    val detail = state.detail
    val accentColor = detail.accentColor()

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(DetailBackground),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .padding(horizontal = DetailHorizontalPadding)
                    .padding(top = 18.dp, bottom = 132.dp),
        ) {
            DetailTopBar(
                title = detail.title,
                onBackClick = action.onBackClick,
                onMoreClick = action.onMoreClick,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(28.dp))
            DetailHero(
                detail = detail,
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(230.dp),
            )
            Spacer(modifier = Modifier.height(38.dp))
            DetailReplacementSummary(
                detail = detail,
                accentColor = accentColor,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(28.dp))
            DetailSpareCard(
                detail = detail,
                accentColor = accentColor,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(28.dp))
            DetailCycleCard(
                detail = detail,
                accentColor = accentColor,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(28.dp))
            DetailHistoryCard(
                detail = detail,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        DetailBottomActions(
            accentColor = accentColor,
            onSpareManageClick = action.onSpareManageClick,
            onReplacementCompleteClick = action.onReplacementCompleteClick,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = DetailHorizontalPadding)
                    .padding(bottom = 22.dp),
        )
    }
}

@Composable
private fun DetailTopBar(
    title: String,
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current

    Box(
        modifier =
            modifier
                .height(40.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center,
        ) {
            ChevronLeftIcon(
                color = Color.White,
                modifier = Modifier.size(22.dp),
            )
        }
        Text(
            text = title,
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 52.dp),
            style =
                typography.lg.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Box(
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onMoreClick),
            contentAlignment = Alignment.Center,
        ) {
            MoreVerticalIcon(
                color = Color.White,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun DetailHero(
    detail: ConsumableDetailUiModel,
    modifier: Modifier = Modifier,
) {
    val accentColor = detail.accentColor()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val fillColor =
                if (detail.status == ConsumableDetailStatus.Warning) {
                    WarningCircle
                } else {
                    HealthyCircle
                }
            val ringInset = 8.dp.toPx()
            val ringSize = size.width - ringInset * 2
            val stroke = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)

            drawCircle(
                color = fillColor,
                radius = size.minDimension / 2f - 10.dp.toPx(),
                center = center,
            )
            drawArc(
                color = accentColor.copy(alpha = 0.24f),
                startAngle = 210f,
                sweepAngle = 278f,
                useCenter = false,
                topLeft = Offset(ringInset, ringInset),
                size = Size(ringSize, ringSize),
                style = stroke,
            )
            drawArc(
                brush =
                    Brush.sweepGradient(
                        colorStops =
                            arrayOf(
                                0f to accentColor,
                                0.55f to accentColor,
                                1f to accentColor.copy(alpha = 0.55f),
                            ),
                        center = center,
                    ),
                startAngle = -92f,
                sweepAngle =
                    if (detail.status == ConsumableDetailStatus.Warning) {
                        270f
                    } else {
                        226f
                    },
                useCenter = false,
                topLeft = Offset(ringInset, ringInset),
                size = Size(ringSize, ringSize),
                style = stroke,
            )
        }
        Image(
            painter = painterResource(id = detail.imageResId()),
            contentDescription = null,
            modifier = Modifier.size(154.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun DetailReplacementSummary(
    detail: ConsumableDetailUiModel,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .height(81.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SummaryCard)
                .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DetailSummaryColumn(
            label = "최근 교체일",
            value = detail.recentReplacementDate,
            valueColor = Color.White,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier =
                Modifier
                    .width(1.dp)
                    .height(52.dp)
                    .background(DividerColor),
        )
        DetailSummaryColumn(
            label = "다음 교체 예정일",
            value = detail.nextReplacementDate,
            valueColor = accentColor,
            badge = detail.replacementBadge,
            badgeColor = accentColor,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DetailSummaryColumn(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
    badge: String? = null,
    badgeColor: Color = valueColor,
) {
    val typography = LocalOBRitTypography.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = label,
            style =
                typography.s.copy(
                    color = MutedText,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
            maxLines = 1,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = value,
                style =
                    typography.xl.copy(
                        color = valueColor,
                        fontWeight = FontWeight.Black,
                    ),
                maxLines = 1,
            )
            if (badge != null) {
                Spacer(modifier = Modifier.width(8.dp))
                DetailBadge(
                    text = badge,
                    background = badgeColor.copy(alpha = 0.28f),
                    contentColor = badgeColor,
                )
            }
        }
    }
}

@Composable
private fun DetailSpareCard(
    detail: ConsumableDetailUiModel,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current

    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(16.dp))
                .background(PanelCard)
                .border(1.dp, PanelStroke, RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 26.dp),
    ) {
        Text(
            text = "여분 수량",
            style =
                typography.lg.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                ),
            maxLines = 1,
        )
        Spacer(modifier = Modifier.height(22.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(IconCircle),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = detail.imageResId()),
                    contentDescription = null,
                    modifier = Modifier.size(34.dp),
                    contentScale = ContentScale.Fit,
                )
            }
            Spacer(modifier = Modifier.width(18.dp))
            Text(
                text = detail.title,
                modifier = Modifier.weight(1f),
                style =
                    typography.lg.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${detail.spareCount}개",
                style =
                    typography.lg.copy(
                        color = accentColor,
                        fontWeight = FontWeight.Black,
                    ),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun DetailCycleCard(
    detail: ConsumableDetailUiModel,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current

    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(16.dp))
                .background(PanelCard)
                .border(1.dp, PanelStroke, RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 26.dp),
    ) {
        Text(
            text = "교체 주기",
            style =
                typography.lg.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                ),
            maxLines = 1,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "전체적인 상태를 빠르게 확인해보세요!",
            style =
                typography.s.copy(
                    color = MutedText,
                    fontWeight = FontWeight.Bold,
                ),
            maxLines = 1,
        )
        Spacer(modifier = Modifier.height(24.dp))
        DetailMetricRow(
            label = "나의 평균 교체 주기",
            value = "${detail.averageCycleDays}일",
        )
        DetailDivider()
        DetailMetricRow(
            label = "권장 교체 주기",
            value = "${detail.recommendedCycleDays}일",
        )
        DetailDivider()
        DetailMetricRow(
            label = "현재 사용 상태",
            value = "${detail.currentUsageDays}일째",
            badge = if (detail.status == ConsumableDetailStatus.Warning) detail.replacementBadge else null,
            badgeColor = accentColor,
        )
        DetailDivider()
    }
}

@Composable
private fun DetailMetricRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    badge: String? = null,
    badgeColor: Color = WarningOrange,
) {
    val typography = LocalOBRitTypography.current

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(58.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style =
                typography.base.copy(
                    color = MutedText,
                    fontWeight = FontWeight.Bold,
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (badge != null) {
            DetailBadge(
                text = badge,
                background = badgeColor.copy(alpha = 0.28f),
                contentColor = badgeColor,
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = value,
            style =
                typography.lg.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                ),
            maxLines = 1,
        )
    }
}

@Composable
private fun DetailHistoryCard(
    detail: ConsumableDetailUiModel,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current

    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(16.dp))
                .background(PanelCard)
                .border(1.dp, PanelStroke, RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 26.dp),
    ) {
        Text(
            text = "교체 주기 기록",
            style =
                typography.lg.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                ),
            maxLines = 1,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text =
                buildAnnotatedString {
                    append("교체 기록은 ")
                    withStyle(SpanStyle(color = HealthyMint)) {
                        append("최근 5회")
                    }
                    append("까지 제공해요")
                },
            style =
                typography.s.copy(
                    color = MutedText,
                    fontWeight = FontWeight.Bold,
                ),
            maxLines = 1,
        )
        Spacer(modifier = Modifier.height(28.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            detail.history.takeLast(5).forEach { history ->
                DetailHistoryBar(
                    history = history,
                    maxDays = detail.history.maxOfOrNull { it.days }.orZero(),
                    modifier = Modifier.width(58.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(26.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DividerColor.copy(alpha = 0.7f)),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "평균 교체 주기  ${detail.averageCycleDays}.8일",
            modifier = Modifier.fillMaxWidth(),
            style =
                typography.base.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
            maxLines = 1,
        )
    }
}

@Composable
private fun DetailHistoryBar(
    history: ReplacementHistoryUiModel,
    maxDays: Int,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current
    val active = history.isCurrent
    val barColor = if (active) HealthyMint else HistoryFill
    val trackColor = if (active) HealthyMint else HistoryTrack

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "${history.days}일",
            style =
                typography.s.copy(
                    color = if (active) HealthyMint else MutedText,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
            maxLines = 1,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier =
                Modifier
                    .size(width = 58.dp, height = 86.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(trackColor),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(history.heightFraction(maxDays))
                        .background(barColor),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = history.dateLabel,
            style =
                typography.xs.copy(
                    color = if (active) HealthyMint else MutedText,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
            maxLines = 1,
        )
    }
}

@Composable
private fun DetailBottomActions(
    accentColor: Color,
    onSpareManageClick: () -> Unit,
    onReplacementCompleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DetailActionButton(
            text = "여분 관리",
            background = ButtonGray,
            contentColor = Color.White,
            onClick = onSpareManageClick,
            modifier = Modifier.weight(1f),
        )
        DetailActionButton(
            text = "교체 완료",
            background = accentColor,
            contentColor = DetailBackground,
            onClick = onReplacementCompleteClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DetailActionButton(
    text: String,
    background: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current

    Box(
        modifier =
            modifier
                .height(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(background)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style =
                typography.base.copy(
                    color = contentColor,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                ),
            maxLines = 1,
        )
    }
}

@Composable
private fun DetailBadge(
    text: String,
    background: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current

    Box(
        modifier =
            modifier
                .height(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(background)
                .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style =
                typography.s.copy(
                    color = contentColor,
                    fontWeight = FontWeight.Black,
                ),
            maxLines = 1,
        )
    }
}

@Composable
private fun DetailDivider(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DividerColor.copy(alpha = 0.72f)),
    )
}

@Composable
private fun ChevronLeftIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        drawLine(
            color = color,
            start = Offset(size.width * 0.64f, size.height * 0.2f),
            end = Offset(size.width * 0.36f, size.height * 0.5f),
            strokeWidth = 2.2.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.36f, size.height * 0.5f),
            end = Offset(size.width * 0.64f, size.height * 0.8f),
            strokeWidth = 2.2.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun MoreVerticalIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        listOf(0.2f, 0.5f, 0.8f).forEach { y ->
            drawCircle(
                color = color,
                radius = size.minDimension * 0.07f,
                center = Offset(size.width * 0.5f, size.height * y),
            )
        }
    }
}

private fun ConsumableDetailUiModel.accentColor(): Color =
    when (status) {
        ConsumableDetailStatus.Warning -> WarningOrange
        ConsumableDetailStatus.Healthy -> HealthyMint
    }

private fun ConsumableDetailUiModel.imageResId(): Int =
    when (status) {
        ConsumableDetailStatus.Warning -> R.drawable.img_detail_consumable_warning
        ConsumableDetailStatus.Healthy -> R.drawable.img_detail_consumable_healthy
    }

private fun ReplacementHistoryUiModel.heightFraction(maxDays: Int): Float {
    if (isCurrent) {
        return 1f
    }

    return if (maxDays <= 0) {
        0.55f
    } else {
        (days / maxDays.toFloat()).coerceIn(0.45f, 0.92f)
    }
}

private fun Int?.orZero(): Int = this ?: 0

private val DetailHorizontalPadding = 20.dp
private val DetailBackground = Color(0xFF1D1B20)
private val SummaryCard = Color(0xFF24242A)
private val PanelCard = Color(0xFF222228)
private val PanelStroke = Color(0xFF4E565E).copy(alpha = 0.5f)
private val ButtonGray = Color(0xFF2E2F33)
private val IconCircle = Color(0xFF393A3D)
private val MutedText = Color(0xFF76777A)
private val DividerColor = Color(0xFF454449)
private val WarningOrange = Color(0xFFFF5922)
private val HealthyMint = Color(0xFF25EFCD)
private val WarningCircle = Color(0xFF55291F)
private val HealthyCircle = Color(0xFF1F3B3A)
private val HistoryTrack = Color(0xFF1F3B3A)
private val HistoryFill = Color(0xFF1F504B)
