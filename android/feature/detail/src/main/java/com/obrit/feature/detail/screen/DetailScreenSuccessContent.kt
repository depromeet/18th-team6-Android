@file:Suppress("LongMethod", "MagicNumber", "ScreenActionContract", "TooManyFunctions")

package com.obrit.feature.detail.screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.component.topbar.OBRitDepthTopBar
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.detail.component.DetailCtaSection
import com.obrit.feature.detail.component.DetailDateInfoSection
import com.obrit.feature.detail.component.DetailHeaderSection
import com.obrit.feature.detail.component.DetailHeaderSectionAction
import com.obrit.feature.detail.component.DetailHeroStateSection
import com.obrit.feature.detail.component.DetailReplacementCycleSection
import com.obrit.feature.detail.component.DetailReplacementHistorySection
import com.obrit.feature.detail.component.DetailSpareCountEditBottomSheet
import com.obrit.feature.detail.component.DetailSpareCountSection
import com.obrit.feature.detail.viewmodel.DetailColorTone
import com.obrit.feature.detail.viewmodel.DetailDDayDirection
import com.obrit.feature.detail.viewmodel.DetailReplacementRecordUiState
import com.obrit.feature.detail.viewmodel.DetailSpareStatus
import com.obrit.feature.detail.viewmodel.DetailStatusGrade
import com.obrit.feature.detail.viewmodel.DetailUiState
import java.time.LocalDate

@Composable
internal fun DetailScreenSuccessContent(
    state: DetailUiState.Success,
    action: DetailScreenAction,
    isMoreMenuExpanded: Boolean,
    isSpareSheetVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is DetailUiState.ConsumableSuccess ->
            DetailConsumableSuccessContent(
                state = state,
                action = action,
                isMoreMenuExpanded = isMoreMenuExpanded,
                isSpareSheetVisible = isSpareSheetVisible,
                modifier = modifier,
            )
        is DetailUiState.LegacyAgentSuccess ->
            DetailLegacyAgentSuccessContent(
                state = state,
                action = action,
                modifier = modifier,
            )
    }
}

@Composable
private fun DetailConsumableSuccessContent(
    state: DetailUiState.ConsumableSuccess,
    action: DetailScreenAction,
    isMoreMenuExpanded: Boolean,
    isSpareSheetVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.gray900),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailHeaderSection(
                title = state.headerTitle,
                isMoreMenuExpanded = isMoreMenuExpanded,
                isDeleteConfirmVisible = state.isDeleteConfirmVisible,
                action =
                    DetailHeaderSectionAction(
                        onBackClick = action.onBackClick,
                        onMoreClick = action.onMoreClick,
                        onMoreMenuDismiss = action.onMoreMenuDismiss,
                        onEditClick = action.onEditClick,
                        onDeleteClick = action.onDeleteClick,
                        onDeleteConfirmClick = action.onDeleteConfirmClick,
                        onDeleteCancelClick = action.onDeleteCancelClick,
                    ),
                modifier = Modifier.statusBarsPadding(),
            )

            Box(modifier = Modifier.weight(1f)) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = DETAIL_HORIZONTAL_PADDING),
                    verticalArrangement = Arrangement.spacedBy(DETAIL_SECTION_GAP),
                ) {
                    DetailHeroStateSection(
                        state = state,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = DETAIL_HERO_TOP_PADDING),
                    )
                    DetailDateInfoSection(
                        state = state,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    DetailSpareCountSection(
                        state = state,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    DetailReplacementCycleSection(
                        averageReplacementIntervalDays = state.averageReplacementIntervalDays,
                        recommendedReplacementIntervalDays = state.recommendedReplacementIntervalDays,
                        currentUsageDays = state.currentUsageDays,
                        dDayLabel = state.dDayLabel,
                        dDayDirection = state.dDayDirection,
                        colorTone = state.colorTone,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    DetailReplacementHistorySection(
                        replacementRecords = state.replacementRecords,
                        averageReplacementIntervalDays = state.averageReplacementIntervalDays,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(DETAIL_SCROLL_BOTTOM_SPACER_HEIGHT))
                }

                DetailBottomCtaSection(
                    state = state,
                    action = action,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }

        if (isSpareSheetVisible) {
            DetailSpareCountEditSheetOverlay(
                title = state.spareAreaItemName,
                initialCount = state.spareStatus.count,
                onCompleteClick = action.onSpareCountCompleteClick,
                onDismissRequest = action.onSpareSheetExitRequest,
            )
        }
    }
}

@Composable
private fun DetailBottomCtaSection(
    state: DetailUiState.ConsumableSuccess,
    action: DetailScreenAction,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(colors.gray900)
                .navigationBarsPadding()
                .padding(
                    horizontal = DETAIL_HORIZONTAL_PADDING,
                    vertical = DETAIL_CTA_VERTICAL_PADDING,
                ),
    ) {
        DetailCtaSection(
            onSpareManagementRequest = action.onSpareManagementClick,
            onReplaceCompleteClick = action.onReplaceCompleteClick,
            isSpareManagementEnabled = state.isSpareManagementEnabled,
            isReplaceCtaEnabled = state.isReplaceCtaEnabled,
            isReplaceProcessing = state.isReplaceProcessing,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailSpareCountEditSheetOverlay(
    title: String,
    initialCount: Int,
    onCompleteClick: (Int) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val colors = LocalOBRitColor.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(0.dp),
        containerColor = Color.Transparent,
        contentColor = colors.common00,
        scrimColor = colors.backgroundDefaultDimDefault,
        dragHandle = null,
    ) {
        DetailSpareCountEditBottomSheet(
            title = title,
            initialCount = initialCount,
            onCompleteClick = onCompleteClick,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
        )
    }
}

@Composable
private fun DetailLegacyAgentSuccessContent(
    state: DetailUiState.LegacyAgentSuccess,
    action: DetailScreenAction,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val agent = state.agent

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.gray900),
    ) {
        OBRitDepthTopBar(
            title = agent.name,
            onBackClick = action.onBackClick,
            modifier = Modifier.statusBarsPadding(),
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(DETAIL_HORIZONTAL_PADDING),
            verticalArrangement = Arrangement.spacedBy(DETAIL_LEGACY_SECTION_GAP),
        ) {
            Text(
                text = agent.name,
                style = typography.xl4.copy(fontWeight = FontWeight.Bold),
                color = colors.common00,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = agent.description,
                style = typography.xl.copy(fontWeight = FontWeight.Medium),
                color = colors.gray300,
            )
            DetailLegacyInfoRow(
                label = "유형",
                value = agent.type.name,
            )
            DetailLegacyInfoRow(
                label = "ID",
                value = agent.id.toString(),
            )
            DetailLegacyInfoRow(
                label = "생성일",
                value = agent.timestamp,
            )
        }
    }
}

@Composable
private fun DetailLegacyInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = typography.lg.copy(fontWeight = FontWeight.Bold),
            color = colors.gray500,
            maxLines = 1,
        )
        Text(
            text = value,
            modifier = Modifier.weight(2f),
            style = typography.lg.copy(fontWeight = FontWeight.Bold),
            color = colors.common00,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private val DETAIL_HORIZONTAL_PADDING = 20.dp
private val DETAIL_SECTION_GAP = 20.dp
private val DETAIL_HERO_TOP_PADDING = 16.dp
private val DETAIL_CTA_VERTICAL_PADDING = 16.dp
private val DETAIL_SCROLL_BOTTOM_SPACER_HEIGHT = 116.dp
private val DETAIL_LEGACY_SECTION_GAP = 16.dp

@Preview(
    name = "DetailScreenSuccessContent Brand",
    showBackground = true,
    widthDp = 393,
    heightDp = 852,
)
@Composable
private fun DetailScreenSuccessContentBrandPreview() {
    DetailScreenSuccessContentPreviewContainer {
        DetailScreenSuccessContent(
            state = detailSuccessPreviewState(colorTone = DetailColorTone.BRAND),
            action = detailSuccessPreviewAction(),
            isMoreMenuExpanded = false,
            isSpareSheetVisible = false,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(
    name = "DetailScreenSuccessContent Warning",
    showBackground = true,
    widthDp = 393,
    heightDp = 852,
)
@Composable
private fun DetailScreenSuccessContentWarningPreview() {
    DetailScreenSuccessContentPreviewContainer {
        DetailScreenSuccessContent(
            state = detailSuccessPreviewState(colorTone = DetailColorTone.WARNING),
            action = detailSuccessPreviewAction(),
            isMoreMenuExpanded = false,
            isSpareSheetVisible = false,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(
    name = "DetailScreenSuccessContent Spare Sheet",
    showBackground = true,
    widthDp = 393,
    heightDp = 852,
)
@Composable
private fun DetailScreenSuccessContentSpareSheetPreview() {
    DetailScreenSuccessContentPreviewContainer {
        DetailScreenSuccessContent(
            state = detailSuccessPreviewState(colorTone = DetailColorTone.BRAND),
            action = detailSuccessPreviewAction(),
            isMoreMenuExpanded = false,
            isSpareSheetVisible = true,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun DetailScreenSuccessContentPreviewContainer(content: @Composable () -> Unit) {
    OBRitTheme(dynamicColor = false) {
        content()
    }
}

private fun detailSuccessPreviewAction(): DetailScreenAction =
    DetailScreenAction(
        onBackClick = {},
        onRetryClick = {},
        onMoreClick = {},
        onMoreMenuDismiss = {},
        onEditClick = {},
        onDeleteClick = {},
        onDeleteConfirmClick = {},
        onDeleteCancelClick = {},
        onSpareManagementClick = {},
        onReplaceCompleteClick = {},
        onSpareSheetDismiss = {},
        onSpareSheetExitRequest = {},
        onSpareCountCompleteClick = {},
    )

private fun detailSuccessPreviewState(colorTone: DetailColorTone): DetailUiState.ConsumableSuccess {
    val isWarning = colorTone == DetailColorTone.WARNING
    val today = LocalDate.of(2026, 5, 26)
    val lastReplacedDate = LocalDate.of(2026, 4, 19)
    val replacementIntervalDays = 35
    val currentUsageDays = 37

    return DetailUiState.ConsumableSuccess(
        consumableId = 1L,
        categoryName = "칫솔",
        itemName = "회사용 칫솔",
        representativeImageUrl = null,
        lastReplacedDate = lastReplacedDate,
        nextReplacementDate = today.minusDays(2),
        recommendedReplacementIntervalDays = replacementIntervalDays,
        currentUsageDays = if (isWarning) currentUsageDays else 18,
        spareCount = if (isWarning) 0 else 3,
        replacementRecords = detailPreviewReplacementRecords(),
        averageReplacementIntervalDays = 33.8,
        dDayValue = if (isWarning) -2 else 17,
        dDayLabel = if (isWarning) "D+2" else "D-17",
        dDayDirection = if (isWarning) DetailDDayDirection.OVERDUE else DetailDDayDirection.UPCOMING,
        progressRawRatio = if (isWarning) 1.06 else 0.51,
        progressDisplayRatio = if (isWarning) 1.0 else 0.51,
        statusGrade = if (isWarning) DetailStatusGrade.DANGER else DetailStatusGrade.GOOD,
        colorTone = colorTone,
        spareStatus =
            DetailSpareStatus(
                count = if (isWarning) 0 else 3,
                hasSpare = !isWarning,
                grade = if (isWarning) DetailStatusGrade.DANGER else DetailStatusGrade.GOOD,
                colorTone = colorTone,
            ),
        isSpareManagementEnabled = true,
        isReplaceCtaEnabled = true,
        isReplaceProcessing = false,
        isDeleteProcessing = false,
        isDeleteConfirmVisible = false,
        pendingDeleteConsumableId = null,
    )
}

private fun detailPreviewReplacementRecords(): List<DetailReplacementRecordUiState> =
    listOf(
        DetailReplacementRecordUiState(
            id = 1L,
            startedDate = LocalDate.of(2026, 1, 1),
            endedDate = LocalDate.of(2026, 2, 3),
            usageDays = 33,
            usageDaysLabel = "33",
            dateLabel = "02/03",
            progressDisplayRatio = 0.94,
            isCurrent = false,
        ),
        DetailReplacementRecordUiState(
            id = 2L,
            startedDate = LocalDate.of(2026, 2, 3),
            endedDate = LocalDate.of(2026, 3, 10),
            usageDays = 35,
            usageDaysLabel = "35",
            dateLabel = "03/10",
            progressDisplayRatio = 1.0,
            isCurrent = false,
        ),
        DetailReplacementRecordUiState(
            id = 3L,
            startedDate = LocalDate.of(2026, 3, 10),
            endedDate = LocalDate.of(2026, 4, 19),
            usageDays = 40,
            usageDaysLabel = "50",
            dateLabel = "04/19",
            progressDisplayRatio = 1.0,
            isCurrent = false,
        ),
        DetailReplacementRecordUiState(
            id = null,
            startedDate = LocalDate.of(2026, 4, 19),
            endedDate = null,
            usageDays = 37,
            usageDaysLabel = "37",
            dateLabel = "현재",
            progressDisplayRatio = 1.0,
            isCurrent = true,
        ),
    )
