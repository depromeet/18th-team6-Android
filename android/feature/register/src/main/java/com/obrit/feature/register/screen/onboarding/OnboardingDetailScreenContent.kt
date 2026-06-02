package com.obrit.feature.register.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.component.button.OBRitButtonDefaults
import com.obrit.android.core.designsystem.component.button.OBRitLargeFilledButton
import com.obrit.android.core.designsystem.component.title.OBRitTitle
import com.obrit.android.core.designsystem.component.title.OBRitTitleSize
import com.obrit.android.core.designsystem.component.title.OBRitTitleType
import com.obrit.android.core.designsystem.component.topbar.OBRitDepthTopBar
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.register.screen.common.ReplacementPeriodDropdown
import com.obrit.feature.register.viewmodel.OnboardingDetailRow
import com.obrit.feature.register.viewmodel.OnboardingDetailUiState
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import com.obrit.obrit.shared.model.categories.Category
import com.obrit.obrit.shared.model.items.ReplacementPeriod

@Composable
internal fun OnboardingDetailScreenContent(
    state: OnboardingDetailUiState,
    action: OnboardingDetailScreenAction,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.gray900)
                .statusBarsPadding()
                .navigationBarsPadding(),
    ) {
        OBRitDepthTopBar(title = ONBOARDING_DETAIL_TITLE, onBackClick = action.onBack)
        OnboardingDetailBody(
            rows = state.rows,
            onPeriodChange = action.onPeriodChange,
            modifier = Modifier.weight(1f),
        )
        OnboardingDetailCta(enabled = state.isSubmitEnabled, onSubmit = action.onSubmit)
    }
}

@Composable
private fun OnboardingDetailBody(
    rows: List<OnboardingDetailRow>,
    onPeriodChange: (categoryId: Long, period: ReplacementPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    Box(modifier = modifier.fillMaxWidth()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    start = AtomSpacing.S5.dp,
                    end = AtomSpacing.S5.dp,
                    bottom = ONBOARDING_DETAIL_SCROLL_FADE_HEIGHT,
                ),
        ) {
            item { OnboardingDetailListHeader() }
            itemsIndexed(
                items = rows,
                key = { _, row -> row.category.id },
            ) { index, row ->
                Column {
                    if (index > 0) Spacer(Modifier.height(AtomSpacing.S2.dp))
                    OnboardingDetailCard(
                        row = row,
                        onPeriodChange = { period -> onPeriodChange(row.category.id, period) },
                    )
                }
            }
        }
        BottomFadeOverlay(color = colors.gray900)
    }
}

@Composable
private fun OnboardingDetailListHeader() {
    Column {
        OnboardingStepIndicator(
            currentStep = 2,
            totalStep = ONBOARDING_DETAIL_TOTAL_STEP,
            modifier = Modifier.padding(vertical = AtomSpacing.S4.dp),
        )
        OBRitTitle(
            title = ONBOARDING_DETAIL_HEADLINE,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = AtomSpacing.S4.dp),
            size = OBRitTitleSize.Large,
            type = OBRitTitleType.Default,
            description = ONBOARDING_DETAIL_DESCRIPTION,
        )
    }
}

@Composable
private fun OnboardingDetailCard(
    row: OnboardingDetailRow,
    onPeriodChange: (ReplacementPeriod) -> Unit,
) {
    val colors = LocalOBRitColor.current
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AtomRadius.ExtraLarge.dp))
                .background(colors.gray850)
                .padding(AtomSpacing.S5.dp),
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
    ) {
        OnboardingDetailCardHeader(category = row.category)
        ReplacementPeriodDropdown(
            selected = row.period,
            onChange = onPeriodChange,
        )
    }
}

@Composable
private fun OnboardingDetailCardHeader(category: Category) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(ONBOARDING_DETAIL_ICON_SIZE)
                    .clip(CircleShape)
                    .background(colors.gray750),
        ) {
            AsyncImage(
                model = category.iconUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = category.name,
                style =
                    typography.xl2.copy(
                        color = colors.common00,
                        fontWeight = FontWeight.SemiBold,
                    ),
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_essential),
                contentDescription = null,
                modifier = Modifier.size(ONBOARDING_DETAIL_ESSENTIAL_SIZE),
                tint = Color.Unspecified,
            )
        }
    }
}

@Composable
private fun BoxScope.BottomFadeOverlay(color: Color) {
    Box(
        modifier =
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(ONBOARDING_DETAIL_SCROLL_FADE_HEIGHT)
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, color)),
                ),
    )
}

@Composable
private fun OnboardingDetailCta(
    enabled: Boolean,
    onSubmit: () -> Unit,
) {
    val typography = LocalOBRitTypography.current
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    start = AtomSpacing.S5.dp,
                    end = AtomSpacing.S5.dp,
                    bottom = ONBOARDING_DETAIL_CTA_BOTTOM_PADDING,
                ),
    ) {
        OBRitLargeFilledButton(
            onClick = onSubmit,
            enabled = enabled,
            colors = OBRitButtonDefaults.positiveButtonColors(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = ONBOARDING_DETAIL_CTA_LABEL,
                style = typography.xl.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

private const val ONBOARDING_DETAIL_TITLE = "소모품 등록"
private const val ONBOARDING_DETAIL_HEADLINE = "마지막 교체일을 알려주세요"
private const val ONBOARDING_DETAIL_DESCRIPTION = "소모품 관리의 시작! 마지막 교체일을 입력해주세요."
private const val ONBOARDING_DETAIL_CTA_LABEL = "다음 단계로"
private const val ONBOARDING_DETAIL_TOTAL_STEP = 2

private val ONBOARDING_DETAIL_ICON_SIZE = 28.dp
private val ONBOARDING_DETAIL_ESSENTIAL_SIZE = AtomSpacing.S5.dp
private val ONBOARDING_DETAIL_SCROLL_FADE_HEIGHT = 20.dp
private val ONBOARDING_DETAIL_CTA_BOTTOM_PADDING = 20.dp

@Suppress("LongMethod")
@Preview(name = "OnboardingDetailScreen", showBackground = false)
@Composable
private fun OnboardingDetailScreenPreview() {
    OBRitTheme(dynamicColor = false) {
        OnboardingDetailScreenContent(
            state =
                OnboardingDetailUiState(
                    rows =
                        listOf(
                            OnboardingDetailRow(
                                category =
                                    Category(
                                        id = 1L,
                                        name = "면도기",
                                        iconUrl = "",
                                        itemCount = 0,
                                        totalSpareQuantity = 0,
                                    ),
                                period = ReplacementPeriod.WITHIN_MONTH,
                            ),
                            OnboardingDetailRow(
                                category =
                                    Category(
                                        id = 2L,
                                        name = "정수기 필터",
                                        iconUrl = "",
                                        itemCount = 0,
                                        totalSpareQuantity = 0,
                                    ),
                                period = null,
                            ),
                        ),
                ),
            action =
                OnboardingDetailScreenAction(
                    onPeriodChange = { _, _ -> },
                    onSubmit = {},
                    onBack = {},
                ),
        )
    }
}
