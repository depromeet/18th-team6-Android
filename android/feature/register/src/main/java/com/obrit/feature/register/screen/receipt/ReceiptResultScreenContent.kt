package com.obrit.feature.register.screen.receipt

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
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
import com.obrit.feature.register.screen.category.CategorySelectionBottomSheet
import com.obrit.feature.register.screen.category.CategorySheetActions
import com.obrit.feature.register.screen.onboarding.OnboardingStepIndicator
import com.obrit.feature.register.viewmodel.ReceiptResultItem
import com.obrit.feature.register.viewmodel.ReceiptResultUiState
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import com.obrit.obrit.shared.model.categories.Category

@Composable
internal fun ReceiptResultScreenContent(
    state: ReceiptResultUiState,
    action: ReceiptResultScreenAction,
    modifier: Modifier = Modifier,
) {
    var isCategorySheetOpen by rememberSaveable { mutableStateOf(false) }
    val colors = LocalOBRitColor.current

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.gray900)
                .statusBarsPadding()
                .navigationBarsPadding(),
    ) {
        OBRitDepthTopBar(title = RECEIPT_RESULT_TITLE, onBackClick = action.onBack)
        ReceiptResultBody(
            state = state,
            onDeleteItem = action.onDeleteItem,
            modifier = Modifier.weight(1f),
        )
        ReceiptResultCta(
            enabled = state.isNextEnabled,
            onNext = action.onNext,
            onDirectRegisterClick = { isCategorySheetOpen = true },
        )
    }

    ReceiptResultCategorySheetHost(
        visible = isCategorySheetOpen,
        categories = state.categories,
        actions =
            CategorySheetActions(
                onConfirm = action.onCategoryConfirm,
                onDismiss = { isCategorySheetOpen = false },
                onDirectRegister = action.onDirectRegister,
            ),
    )
}

@Composable
private fun ReceiptResultCategorySheetHost(
    visible: Boolean,
    categories: List<Category>,
    actions: CategorySheetActions,
) {
    if (!visible) return
    CategorySelectionBottomSheet(
        categories = categories,
        initialSelectedId = null,
        actions =
            CategorySheetActions(
                onConfirm = { category ->
                    actions.onConfirm(category)
                    actions.onDismiss()
                },
                onDismiss = actions.onDismiss,
                // 시트를 먼저 닫아야 Dialog가 새 화면을 가리지 않는다.
                onDirectRegister = {
                    actions.onDismiss()
                    actions.onDirectRegister()
                },
            ),
    )
}

@Composable
private fun ReceiptResultBody(
    state: ReceiptResultUiState,
    onDeleteItem: (Long) -> Unit,
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
                    bottom = RECEIPT_RESULT_SCROLL_FADE_HEIGHT,
                ),
        ) {
            item {
                ReceiptResultListHeader(
                    purchaseDate = state.purchaseDate,
                    itemCount = state.items.size,
                )
            }
            itemsIndexed(
                items = state.items,
                key = { _, item -> item.id },
            ) { index, item ->
                Column {
                    if (index > 0) Spacer(Modifier.height(AtomSpacing.S2.dp))
                    ReceiptResultCard(
                        item = item,
                        onDeleteClick = { onDeleteItem(item.id) },
                    )
                }
            }
        }
        BottomFadeOverlay(color = colors.gray900)
    }
}

@Composable
private fun ReceiptResultListHeader(
    purchaseDate: String,
    itemCount: Int,
) {
    Column {
        OnboardingStepIndicator(
            currentStep = RECEIPT_RESULT_CURRENT_STEP,
            totalStep = RECEIPT_RESULT_TOTAL_STEP,
            modifier = Modifier.padding(vertical = AtomSpacing.S4.dp),
        )
        OBRitTitle(
            title = RECEIPT_RESULT_HEADLINE,
            description = RECEIPT_RESULT_PURCHASE_DATE_FORMAT.format(purchaseDate),
            tagText = RECEIPT_RESULT_TAG,
            size = OBRitTitleSize.Large,
            type = OBRitTitleType.WithTag,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = AtomSpacing.S4.dp),
        )
        Spacer(Modifier.height(AtomSpacing.S5.dp))
        ReceiptResultCountHeader(
            count = itemCount,
            modifier = Modifier.padding(bottom = AtomSpacing.S3.dp),
        )
    }
}

@Composable
private fun ReceiptResultCountHeader(
    count: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Text(
        text =
            buildAnnotatedString {
                withStyle(SpanStyle(color = colors.common00)) {
                    append(RECEIPT_RESULT_COUNT_PREFIX)
                }
                withStyle(SpanStyle(color = colors.green300)) {
                    append(RECEIPT_RESULT_COUNT_FORMAT.format(count))
                }
            },
        style = typography.xl3.copy(fontWeight = FontWeight.Bold),
        modifier = modifier,
    )
}

@Composable
private fun ReceiptResultCard(
    item: ReceiptResultItem,
    onDeleteClick: () -> Unit,
) {
    val colors = LocalOBRitColor.current
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AtomRadius.ExtraLarge.dp))
                .background(colors.gray850)
                .padding(horizontal = AtomSpacing.S5.dp, vertical = AtomSpacing.S4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S4.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(RECEIPT_RESULT_IMAGE_SIZE)
                    .clip(CircleShape)
                    .background(colors.gray750),
        ) {
            AsyncImage(
                model = item.iconUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
        }
        ReceiptResultCardText(
            name = item.name,
            recognizedCount = item.recognizedCount,
            modifier = Modifier.weight(1f),
        )
        ReceiptResultDeleteButton(onClick = onDeleteClick)
    }
}

@Composable
private fun ReceiptResultCardText(
    name: String,
    recognizedCount: Int,
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
                text = RECEIPT_RESULT_CARD_LABEL,
                style =
                    typography.s.copy(
                        color = colors.common00.copy(alpha = RECEIPT_RESULT_CARD_LABEL_ALPHA),
                        fontWeight = FontWeight.Medium,
                    ),
            )
            Text(
                text = RECEIPT_RESULT_COUNT_FORMAT.format(recognizedCount),
                style = typography.s.copy(color = colors.common00, fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

@Composable
private fun ReceiptResultDeleteButton(onClick: () -> Unit) {
    val colors = LocalOBRitColor.current
    Box(
        modifier =
            Modifier
                .size(RECEIPT_RESULT_DELETE_BUTTON_SIZE)
                .clip(RoundedCornerShape(AtomRadius.Small.dp))
                .background(colors.gray750)
                .border(
                    width = RECEIPT_RESULT_DELETE_BUTTON_BORDER,
                    color = colors.gray700,
                    shape = RoundedCornerShape(AtomRadius.Small.dp),
                ).clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_trash),
            contentDescription = RECEIPT_RESULT_DELETE_DESCRIPTION,
            tint = colors.common00,
            modifier = Modifier.size(RECEIPT_RESULT_DELETE_ICON_SIZE),
        )
    }
}

@Composable
private fun BoxScope.BottomFadeOverlay(color: Color) {
    Box(
        modifier =
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(RECEIPT_RESULT_SCROLL_FADE_HEIGHT)
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, color)),
                ),
    )
}

@Composable
private fun ReceiptResultCta(
    enabled: Boolean,
    onNext: () -> Unit,
    onDirectRegisterClick: () -> Unit,
) {
    val typography = LocalOBRitTypography.current
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    start = AtomSpacing.S5.dp,
                    end = AtomSpacing.S5.dp,
                    bottom = RECEIPT_RESULT_CTA_BOTTOM_PADDING,
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OBRitLargeFilledButton(
            onClick = onNext,
            enabled = enabled,
            colors = OBRitButtonDefaults.positiveButtonColors(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = RECEIPT_RESULT_CTA_LABEL,
                style = typography.xl.copy(fontWeight = FontWeight.SemiBold),
            )
        }
        Spacer(Modifier.height(RECEIPT_RESULT_CTA_TO_DIRECT_GAP))
        ReceiptResultDirectRegisterRow(onClick = onDirectRegisterClick)
    }
}

@Composable
private fun ReceiptResultDirectRegisterRow(onClick: () -> Unit) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
    ) {
        Text(
            text = RECEIPT_RESULT_DIRECT_QUESTION,
            style = typography.base.copy(color = colors.gray200, fontWeight = FontWeight.Medium),
        )
        Text(
            text = RECEIPT_RESULT_DIRECT_ACTION,
            style =
                typography.base.copy(
                    color = colors.common00,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline,
                ),
            modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
        )
    }
}

private const val RECEIPT_RESULT_TITLE = "소모품 등록"
private const val RECEIPT_RESULT_HEADLINE = "영수증 분석이 완료되었어요"
private const val RECEIPT_RESULT_TAG = "영수증"
private const val RECEIPT_RESULT_PURCHASE_DATE_FORMAT = "%s 구매"
private const val RECEIPT_RESULT_COUNT_PREFIX = "인식한 소모품 "
private const val RECEIPT_RESULT_COUNT_FORMAT = "%d개"
private const val RECEIPT_RESULT_CARD_LABEL = "인식한 소모품"
private const val RECEIPT_RESULT_CTA_LABEL = "다음 단계로"
private const val RECEIPT_RESULT_DIRECT_QUESTION = "인식되지 않은 소모품이 있나요?"
private const val RECEIPT_RESULT_DIRECT_ACTION = "직접 등록하기"
private const val RECEIPT_RESULT_DELETE_DESCRIPTION = "삭제"
private const val RECEIPT_RESULT_CURRENT_STEP = 1
private const val RECEIPT_RESULT_TOTAL_STEP = 2
private const val RECEIPT_RESULT_CARD_LABEL_ALPHA = 0.64f

private val RECEIPT_RESULT_IMAGE_SIZE = 52.dp
private val RECEIPT_RESULT_DELETE_BUTTON_SIZE = AtomSpacing.S12.dp
private val RECEIPT_RESULT_DELETE_BUTTON_BORDER = AtomSpacing.Px.dp
private val RECEIPT_RESULT_DELETE_ICON_SIZE = AtomSpacing.S5.dp
private val RECEIPT_RESULT_SCROLL_FADE_HEIGHT = AtomSpacing.S10.dp
private val RECEIPT_RESULT_CTA_BOTTOM_PADDING = AtomSpacing.S5.dp
private val RECEIPT_RESULT_CTA_TO_DIRECT_GAP = AtomSpacing.S4.dp

