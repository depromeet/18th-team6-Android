package com.obrit.feature.register.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.obrit.android.core.designsystem.component.button.OBRitButtonDefaults
import com.obrit.android.core.designsystem.component.button.OBRitLargeFilledButton
import com.obrit.android.core.designsystem.component.checkbox.OBRitCheckBox
import com.obrit.android.core.designsystem.component.title.OBRitTitle
import com.obrit.android.core.designsystem.component.title.OBRitTitleSize
import com.obrit.android.core.designsystem.component.title.OBRitTitleType
import com.obrit.android.core.designsystem.component.topbar.OBRitDepthTopBar
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.register.viewmodel.OnboardingSelectUiState
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import com.obrit.obrit.shared.model.categories.Category

@Composable
internal fun OnboardingSelectScreenContent(
    state: OnboardingSelectUiState,
    action: OnboardingSelectScreenAction,
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
        OBRitDepthTopBar(title = ONBOARDING_SELECT_TITLE, onBackClick = action.onBack)
        OnboardingSelectBody(
            state = state,
            onToggle = action.onToggle,
            modifier = Modifier.weight(1f),
        )
        OnboardingSelectCta(enabled = state.isNextEnabled, onNext = action.onNext)
    }
}

@Composable
private fun OnboardingSelectBody(
    state: OnboardingSelectUiState,
    onToggle: (Long) -> Unit,
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
                    bottom = ONBOARDING_SELECT_SCROLL_FADE_HEIGHT,
                ),
        ) {
            item { OnboardingSelectListHeader(selectedCount = state.selectedCount) }
            itemsIndexed(
                items = state.categories,
                key = { _, category -> category.id },
            ) { index, category ->
                Column {
                    if (index > 0) Spacer(Modifier.height(AtomSpacing.S2.dp))
                    OnboardingSelectCard(
                        name = category.name,
                        iconUrl = category.iconUrl,
                        addedCount = 0,
                        selected = category.id in state.selectedIds,
                        onToggle = { onToggle(category.id) },
                    )
                }
            }
        }
        BottomFadeOverlay(color = colors.gray900)
    }
}

@Composable
private fun OnboardingSelectListHeader(selectedCount: Int) {
    Column {
        OnboardingStepIndicator(
            currentStep = 1,
            totalStep = ONBOARDING_SELECT_TOTAL_STEP,
            modifier = Modifier.padding(vertical = AtomSpacing.S4.dp),
        )
        OBRitTitle(
            title = ONBOARDING_SELECT_HEADLINE,
            description = ONBOARDING_SELECT_DESCRIPTION,
            size = OBRitTitleSize.Large,
            type = OBRitTitleType.Default,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = AtomSpacing.S4.dp),
        )
        Spacer(Modifier.height(AtomSpacing.S5.dp))
        OnboardingSelectedCountHeader(
            count = selectedCount,
            modifier = Modifier.padding(bottom = AtomSpacing.S3.dp),
        )
    }
}

@Composable
private fun OnboardingSelectedCountHeader(
    count: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Text(
        text =
            buildAnnotatedString {
                withStyle(SpanStyle(color = colors.common00)) {
                    append(ONBOARDING_SELECT_COUNT_PREFIX)
                }
                withStyle(SpanStyle(color = colors.green300)) {
                    append(ONBOARDING_SELECT_COUNT_FORMAT.format(count))
                }
            },
        style = typography.xl3.copy(fontWeight = FontWeight.Bold),
        modifier = modifier,
    )
}

@Composable
private fun OnboardingSelectCard(
    name: String,
    iconUrl: String,
    addedCount: Int,
    selected: Boolean,
    onToggle: () -> Unit,
) {
    val colors = LocalOBRitColor.current
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AtomRadius.ExtraLarge.dp))
                .background(colors.gray850)
                .clickable(onClick = onToggle)
                .padding(horizontal = AtomSpacing.S5.dp, vertical = AtomSpacing.S4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S4.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(ONBOARDING_SELECT_IMAGE_SIZE)
                    .clip(CircleShape)
                    .background(colors.gray750),
        ) {
            AsyncImage(
                model = iconUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
        }
        OnboardingSelectCardText(name = name, addedCount = addedCount, modifier = Modifier.weight(1f))
        OBRitCheckBox(
            checked = selected,
            onCheckedChange = { onToggle() },
            modifier = Modifier.size(ONBOARDING_SELECT_CHECKBOX_SIZE),
        )
    }
}

@Composable
private fun OnboardingSelectCardText(
    name: String,
    addedCount: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S1.dp),
    ) {
        Text(
            text = name,
            style = typography.xl.copy(color = colors.common00, fontWeight = FontWeight.Bold),
            maxLines = 1,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S0_5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = ONBOARDING_SELECT_ADDED_LABEL,
                style = typography.s.copy(color = colors.gray400, fontWeight = FontWeight.Medium),
            )
            Text(
                text = ONBOARDING_SELECT_ADDED_COUNT_FORMAT.format(addedCount),
                style = typography.s.copy(color = colors.common00, fontWeight = FontWeight.SemiBold),
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
                .height(ONBOARDING_SELECT_SCROLL_FADE_HEIGHT)
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, color)),
                ),
    )
}

@Composable
private fun OnboardingSelectCta(
    enabled: Boolean,
    onNext: () -> Unit,
) {
    val typography = LocalOBRitTypography.current
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    start = AtomSpacing.S5.dp,
                    end = AtomSpacing.S5.dp,
                    bottom = ONBOARDING_SELECT_CTA_BOTTOM_PADDING,
                ),
    ) {
        OBRitLargeFilledButton(
            onClick = onNext,
            enabled = enabled,
            colors = OBRitButtonDefaults.positiveButtonColors(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = ONBOARDING_SELECT_CTA_LABEL,
                style = typography.xl.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

private const val ONBOARDING_SELECT_TITLE = "소모품 등록"
private const val ONBOARDING_SELECT_HEADLINE = "어떤 소모품을 관리해볼까요?"
private const val ONBOARDING_SELECT_DESCRIPTION = "원하는 카테고리가 없다면, 추후 직접 등록도 가능해요."
private const val ONBOARDING_SELECT_COUNT_PREFIX = "선택한 소모품 "
private const val ONBOARDING_SELECT_COUNT_FORMAT = "%d개"
private const val ONBOARDING_SELECT_ADDED_LABEL = "추가한 소모품"
private const val ONBOARDING_SELECT_ADDED_COUNT_FORMAT = "%d개"
private const val ONBOARDING_SELECT_CTA_LABEL = "다음 단계로"
private const val ONBOARDING_SELECT_TOTAL_STEP = 2

private val ONBOARDING_SELECT_IMAGE_SIZE = 52.dp
private val ONBOARDING_SELECT_CHECKBOX_SIZE = AtomSpacing.S7.dp
private val ONBOARDING_SELECT_SCROLL_FADE_HEIGHT = 40.dp
private val ONBOARDING_SELECT_CTA_BOTTOM_PADDING = 40.dp

@Preview(name = "OnboardingSelectScreen", showBackground = false)
@Composable
private fun OnboardingSelectScreenPreview() {
    OBRitTheme(dynamicColor = false) {
        OnboardingSelectScreenContent(
            state =
                OnboardingSelectUiState(
                    categories = previewCategories(),
                    selectedIds = setOf(1L, 2L),
                ),
            action = OnboardingSelectScreenAction(onToggle = {}, onNext = {}, onBack = {}),
        )
    }
}

private fun previewCategories(): List<Category> =
    listOf(
        Category(id = 1L, name = "면도기", iconUrl = "", itemCount = 0, totalSpareQuantity = 0),
        Category(id = 2L, name = "정수기 필터", iconUrl = "", itemCount = 0, totalSpareQuantity = 0),
        Category(id = 3L, name = "칫솔", iconUrl = "", itemCount = 0, totalSpareQuantity = 0),
        Category(id = 4L, name = "세탁 세제", iconUrl = "", itemCount = 0, totalSpareQuantity = 0),
    )
