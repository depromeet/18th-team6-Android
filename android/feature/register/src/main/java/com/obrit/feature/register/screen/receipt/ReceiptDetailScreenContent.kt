package com.obrit.feature.register.screen.receipt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.component.button.OBRitButtonDefaults
import com.obrit.android.core.designsystem.component.button.OBRitLargeFilledButton
import com.obrit.android.core.designsystem.component.stepper.OBRitStepper
import com.obrit.android.core.designsystem.component.textfield.InputResultState
import com.obrit.android.core.designsystem.component.textfield.OBRitOutlinedTextField
import com.obrit.android.core.designsystem.component.title.OBRitTitle
import com.obrit.android.core.designsystem.component.title.OBRitTitleSize
import com.obrit.android.core.designsystem.component.title.OBRitTitleType
import com.obrit.android.core.designsystem.component.topbar.OBRitDepthTopBar
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.feature.register.screen.common.ReplacementPeriodDropdown
import com.obrit.feature.register.screen.common.rememberFocusBringIntoView
import com.obrit.feature.register.screen.onboarding.OnboardingStepIndicator
import com.obrit.feature.register.viewmodel.RECEIPT_DETAIL_NAME_MAX_LENGTH
import com.obrit.feature.register.viewmodel.ReceiptDetailForm
import com.obrit.feature.register.viewmodel.ReceiptDetailUiState
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import com.obrit.obrit.shared.model.items.ReplacementPeriod
import kotlin.math.absoluteValue

@Composable
internal fun ReceiptDetailScreenContent(
    state: ReceiptDetailUiState,
    action: ReceiptDetailScreenAction,
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
        OBRitDepthTopBar(title = RECEIPT_DETAIL_TITLE, onBackClick = action.onBack)
        ReceiptDetailBody(
            forms = state.forms,
            onNameChange = action.onNameChange,
            onPeriodChange = action.onPeriodChange,
            onQuantityChange = action.onQuantityChange,
            modifier = Modifier.weight(1f),
        )
        ReceiptDetailCta(enabled = state.isSubmitEnabled, onSubmit = action.onSubmit)
    }
}

@Composable
private fun ReceiptDetailBody(
    forms: List<ReceiptDetailForm>,
    onNameChange: (Long, String) -> Unit,
    onPeriodChange: (Long, ReplacementPeriod) -> Unit,
    onQuantityChange: (Long, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
    ) {
        OnboardingStepIndicator(
            currentStep = RECEIPT_DETAIL_CURRENT_STEP,
            totalStep = RECEIPT_DETAIL_TOTAL_STEP,
            modifier = Modifier.padding(horizontal = AtomSpacing.S5.dp, vertical = AtomSpacing.S4.dp),
        )
        OBRitTitle(
            title = RECEIPT_DETAIL_HEADLINE,
            description = RECEIPT_DETAIL_DESCRIPTION,
            size = OBRitTitleSize.Large,
            type = OBRitTitleType.Default,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AtomSpacing.S5.dp, vertical = AtomSpacing.S4.dp),
        )
        Spacer(Modifier.height(RECEIPT_DETAIL_SECTION_GAP))
        ReceiptDetailPagerSection(
            forms = forms,
            onNameChange = onNameChange,
            onPeriodChange = onPeriodChange,
            onQuantityChange = onQuantityChange,
        )
        // 화면이 짧아 스크롤될 때도 indicator가 CTA에 붙지 않도록 Figma 기준 간격(약 40dp)을 보장한다.
        Spacer(Modifier.height(RECEIPT_DETAIL_INDICATOR_TO_CTA_GAP))
    }
}

@Composable
private fun ReceiptDetailPagerSection(
    forms: List<ReceiptDetailForm>,
    onNameChange: (Long, String) -> Unit,
    onPeriodChange: (Long, ReplacementPeriod) -> Unit,
    onQuantityChange: (Long, Int) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { forms.size })
    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = RECEIPT_DETAIL_PAGER_CONTENT_PADDING),
        pageSpacing = RECEIPT_DETAIL_PAGE_SPACING,
        beyondViewportPageCount = 1,
        modifier = Modifier.fillMaxWidth(),
    ) { page ->
        val form = forms.getOrNull(page) ?: return@HorizontalPager
        ReceiptDetailCard(
            form = form,
            onNameChange = { value -> onNameChange(form.id, value) },
            onPeriodChange = { period -> onPeriodChange(form.id, period) },
            onQuantityChange = { value -> onQuantityChange(form.id, value) },
            modifier = Modifier.pageTransition(pagerState = pagerState, page = page),
        )
    }
    Spacer(Modifier.height(AtomSpacing.S2.dp))
    ReceiptDetailPageIndicator(
        pageCount = forms.size,
        currentPage = pagerState.currentPage,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = RECEIPT_DETAIL_INDICATOR_VERTICAL_PADDING),
    )
}

// Figma: 중앙 카드 기준 양옆 카드는 90% 축소 + 30% 투명도. 스와이프 진행률에 따라 보간한다.
private fun Modifier.pageTransition(
    pagerState: PagerState,
    page: Int,
): Modifier =
    graphicsLayer {
        val pageOffset =
            ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                .absoluteValue
                .coerceIn(0f, 1f)
        val scale = lerp(1f, RECEIPT_DETAIL_PAGE_MIN_SCALE, pageOffset)
        scaleX = scale
        scaleY = scale
        alpha = lerp(1f, RECEIPT_DETAIL_PAGE_MIN_ALPHA, pageOffset)
    }

@Composable
private fun ReceiptDetailPageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    Row(
        modifier = modifier,
        horizontalArrangement =
            Arrangement.spacedBy(RECEIPT_DETAIL_INDICATOR_GAP, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier =
                    Modifier
                        .size(RECEIPT_DETAIL_INDICATOR_DOT_SIZE)
                        .clip(CircleShape)
                        .background(if (index == currentPage) colors.green300 else colors.gray700),
            )
        }
    }
}

@Composable
private fun ReceiptDetailCard(
    form: ReceiptDetailForm,
    onNameChange: (String) -> Unit,
    onPeriodChange: (ReplacementPeriod) -> Unit,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AtomRadius.ExtraLarge.dp))
                .background(colors.gray850)
                .padding(AtomSpacing.S5.dp),
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S5.dp),
    ) {
        ReceiptDetailNameField(value = form.name, onValueChange = onNameChange)
        ReceiptDetailPeriodField(selected = form.lastReplacementPeriod, onChange = onPeriodChange)
        ReceiptDetailQuantityField(quantity = form.quantity, onQuantityChange = onQuantityChange)
    }
}

@Composable
private fun ReceiptDetailNameField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    val isOverLimit = value.length > RECEIPT_DETAIL_NAME_MAX_LENGTH
    val focus = rememberFocusBringIntoView()
    Column(
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
        modifier =
            Modifier
                .bringIntoViewRequester(focus.requester)
                .onSizeChanged(focus.onSize),
    ) {
        ReceiptDetailFieldLabel(label = RECEIPT_DETAIL_NAME_LABEL, essential = true)
        OBRitOutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            supportingTextEnabled = isOverLimit,
            placeholder = RECEIPT_DETAIL_NAME_PLACEHOLDER,
            maxLength = RECEIPT_DETAIL_NAME_MAX_LENGTH,
            inputResultState = if (isOverLimit) InputResultState.Error else InputResultState.Default,
            supportingText = if (isOverLimit) RECEIPT_DETAIL_NAME_OVER_LIMIT_MESSAGE else "",
            singleLine = true,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .onFocusEvent { focus.onFocus(it.isFocused) },
        )
    }
}

@Composable
private fun ReceiptDetailPeriodField(
    selected: ReplacementPeriod?,
    onChange: (ReplacementPeriod) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp)) {
        ReceiptDetailFieldLabel(label = RECEIPT_DETAIL_LAST_REPLACE_DATE_LABEL, essential = true)
        ReplacementPeriodDropdown(
            selected = selected,
            onChange = onChange,
        )
    }
}

@Composable
private fun ReceiptDetailQuantityField(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Column(verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp)) {
        ReceiptDetailFieldLabel(label = RECEIPT_DETAIL_QUANTITY_LABEL, essential = false)
        OBRitStepper(value = quantity, onValueChange = onQuantityChange)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S1.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_info),
                contentDescription = null,
                modifier = Modifier.size(RECEIPT_DETAIL_INFO_ICON_SIZE),
                tint = Color.Unspecified,
            )
            Text(
                text = RECEIPT_DETAIL_QUANTITY_HELP,
                style = typography.s.copy(color = colors.gray500, fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

@Composable
private fun ReceiptDetailFieldLabel(
    label: String,
    essential: Boolean,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = typography.xl.copy(color = colors.common00, fontWeight = FontWeight.SemiBold),
        )
        if (essential) {
            Icon(
                painter = painterResource(id = R.drawable.ic_essential),
                contentDescription = RECEIPT_DETAIL_ESSENTIAL_DESCRIPTION,
                modifier = Modifier.size(RECEIPT_DETAIL_ESSENTIAL_ICON_SIZE),
                tint = Color.Unspecified,
            )
        }
    }
}

@Composable
private fun ReceiptDetailCta(
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
                    bottom = RECEIPT_DETAIL_CTA_BOTTOM_PADDING,
                ),
    ) {
        OBRitLargeFilledButton(
            onClick = onSubmit,
            enabled = enabled,
            colors = OBRitButtonDefaults.positiveButtonColors(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = RECEIPT_DETAIL_CTA_LABEL,
                style = typography.xl.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

private const val RECEIPT_DETAIL_TITLE = "소모품 등록"
private const val RECEIPT_DETAIL_HEADLINE = "소모품의 상세 정보를\n입력해주세요"
private const val RECEIPT_DETAIL_DESCRIPTION = "원활한 관리를 위해 구체적인 정보를 입력해주세요"
private const val RECEIPT_DETAIL_NAME_LABEL = "소모품 명"
private const val RECEIPT_DETAIL_NAME_PLACEHOLDER = "구분을 위한 이름을 입력해주세요"
private const val RECEIPT_DETAIL_NAME_OVER_LIMIT_MESSAGE = "소모품 명은 15자 이내로 입력해주세요"
private const val RECEIPT_DETAIL_LAST_REPLACE_DATE_LABEL = "마지막 교체 일자"
private const val RECEIPT_DETAIL_QUANTITY_LABEL = "등록할 수량"
private const val RECEIPT_DETAIL_QUANTITY_HELP = "소모품의 전체 수량은 추후에도 등록할 수 있어요."
private const val RECEIPT_DETAIL_CTA_LABEL = "소모품 등록하기"
private const val RECEIPT_DETAIL_ESSENTIAL_DESCRIPTION = "필수 항목"
private const val RECEIPT_DETAIL_CURRENT_STEP = 2
private const val RECEIPT_DETAIL_TOTAL_STEP = 2
private const val RECEIPT_DETAIL_PAGE_MIN_SCALE = 0.9f
private const val RECEIPT_DETAIL_PAGE_MIN_ALPHA = 0.3f

private val RECEIPT_DETAIL_SECTION_GAP = AtomSpacing.S10.dp
private val RECEIPT_DETAIL_INDICATOR_TO_CTA_GAP = AtomSpacing.S10.dp
private val RECEIPT_DETAIL_PAGER_CONTENT_PADDING = AtomSpacing.S9.dp
private val RECEIPT_DETAIL_PAGE_SPACING = AtomSpacing.S3.dp
private val RECEIPT_DETAIL_INDICATOR_VERTICAL_PADDING = AtomSpacing.S3.dp
private val RECEIPT_DETAIL_INDICATOR_GAP = AtomSpacing.S2_5.dp
private val RECEIPT_DETAIL_INDICATOR_DOT_SIZE = AtomSpacing.S2.dp
private val RECEIPT_DETAIL_ESSENTIAL_ICON_SIZE = AtomSpacing.S5.dp
private val RECEIPT_DETAIL_INFO_ICON_SIZE = AtomSpacing.S4.dp
private val RECEIPT_DETAIL_CTA_BOTTOM_PADDING = AtomSpacing.S10.dp

