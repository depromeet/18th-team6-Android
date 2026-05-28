package com.obrit.feature.register.screen.manual

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.component.button.OBRitButtonDefaults
import com.obrit.android.core.designsystem.component.button.OBRitLargeFilledButton
import com.obrit.android.core.designsystem.component.title.OBRitTitle
import com.obrit.android.core.designsystem.component.title.OBRitTitleSize
import com.obrit.android.core.designsystem.component.title.OBRitTitleType
import com.obrit.android.core.designsystem.component.topbar.OBRitDepthTopBar
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.register.screen.category.CategorySelectionBottomSheet
import com.obrit.feature.register.viewmodel.ManualRegisterUiState
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import com.obrit.obrit.shared.model.categories.Category
import com.obrit.obrit.shared.model.items.ReplacementPeriod

@Composable
internal fun ManualRegisterScreenContent(
    state: ManualRegisterUiState,
    action: ManualRegisterScreenAction,
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
        OBRitDepthTopBar(title = MANUAL_REGISTER_TITLE, onBackClick = action.onBack)
        ManualRegisterBody(
            state = state,
            fieldActions =
                ManualRegisterFieldActions(
                    onNameChange = action.onNameChange,
                    onLastReplacementPeriodChange = action.onLastReplacementPeriodChange,
                    onCategoryClick = { isCategorySheetOpen = true },
                ),
            onQuantityChange = action.onQuantityChange,
            modifier = Modifier.weight(1f),
        )
        ManualRegisterCta(enabled = state.isSubmitEnabled, onSubmit = action.onSubmit)
    }

    ManualRegisterCategorySheetHost(
        visible = isCategorySheetOpen,
        categories = state.categories,
        initialSelectedId = state.selectedCategoryId,
        onConfirm = action.onCategoryConfirm,
        onDismiss = { isCategorySheetOpen = false },
        onDirectRegister = action.onDirectRegister,
    )
}

@Composable
private fun ManualRegisterCategorySheetHost(
    visible: Boolean,
    categories: List<Category>,
    initialSelectedId: Long?,
    onConfirm: (Category) -> Unit,
    onDismiss: () -> Unit,
    onDirectRegister: () -> Unit,
) {
    if (!visible) return
    CategorySelectionBottomSheet(
        categories = categories,
        initialSelectedId = initialSelectedId,
        onConfirm = { category ->
            onConfirm(category)
            onDismiss()
        },
        onDismissRequest = onDismiss,
        onDirectRegisterClick = {
            // 시트를 먼저 닫아야 Dialog가 새 화면을 가리지 않는다.
            onDismiss()
            onDirectRegister()
        },
    )
}

internal data class ManualRegisterFieldActions(
    val onNameChange: (String) -> Unit,
    val onLastReplacementPeriodChange: (ReplacementPeriod?) -> Unit,
    val onCategoryClick: () -> Unit,
)

@Composable
private fun ManualRegisterBody(
    state: ManualRegisterUiState,
    fieldActions: ManualRegisterFieldActions,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        top = MANUAL_REGISTER_TOP_BAR_TO_BODY_GAP,
                        bottom = MANUAL_REGISTER_SCROLL_FADE_HEIGHT,
                    ),
            verticalArrangement = Arrangement.spacedBy(MANUAL_REGISTER_SECTION_GAP),
        ) {
            ManualRegisterTitleSection()
            ManualRegisterFields(
                state = state,
                fieldActions = fieldActions,
                onQuantityChange = onQuantityChange,
            )
        }
        BottomFadeOverlay(color = colors.gray900)
    }
}

@Composable
private fun ManualRegisterTitleSection() {
    OBRitTitle(
        title = MANUAL_REGISTER_HEADLINE,
        description = MANUAL_REGISTER_DESCRIPTION,
        size = OBRitTitleSize.Large,
        type = OBRitTitleType.Default,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = AtomSpacing.S5.dp, vertical = AtomSpacing.S4.dp),
    )
}

@Composable
private fun ManualRegisterFields(
    state: ManualRegisterUiState,
    fieldActions: ManualRegisterFieldActions,
    onQuantityChange: (Int) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = AtomSpacing.S5.dp),
        verticalArrangement = Arrangement.spacedBy(MANUAL_REGISTER_FIELD_GAP),
    ) {
        CategoryField(value = state.categoryName, onClick = fieldActions.onCategoryClick)
        NameField(value = state.name, onValueChange = fieldActions.onNameChange)
        LastReplaceDateField(
            selected = state.lastReplacementPeriod,
            onChange = fieldActions.onLastReplacementPeriodChange,
        )
        QuantityField(
            title = state.categoryName,
            quantity = state.quantity,
            totalCount = state.totalCount,
            onQuantityChange = onQuantityChange,
        )
    }
}

@Composable
private fun BoxScope.BottomFadeOverlay(color: Color) {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    if (isImeVisible) return
    Box(
        modifier =
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(MANUAL_REGISTER_SCROLL_FADE_HEIGHT)
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, color)),
                ),
    )
}

@Composable
private fun ManualRegisterCta(
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
                    bottom = MANUAL_REGISTER_CTA_BOTTOM_PADDING,
                ),
    ) {
        OBRitLargeFilledButton(
            onClick = onSubmit,
            enabled = enabled,
            colors = OBRitButtonDefaults.positiveButtonColors(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = MANUAL_REGISTER_SUBMIT_LABEL,
                style = typography.xl.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

private const val MANUAL_REGISTER_TITLE = "소모품 등록"
private const val MANUAL_REGISTER_HEADLINE = "소모품의 상세 정보를\n입력해주세요"
private const val MANUAL_REGISTER_DESCRIPTION = "원활한 관리를 위해 구체적인 정보를 입력해주세요"
private const val MANUAL_REGISTER_SUBMIT_LABEL = "소모품 등록하기"

private val MANUAL_REGISTER_TOP_BAR_TO_BODY_GAP = AtomSpacing.S5.dp
private val MANUAL_REGISTER_SECTION_GAP = 28.dp
private val MANUAL_REGISTER_FIELD_GAP = 40.dp
private val MANUAL_REGISTER_CTA_BOTTOM_PADDING = 40.dp
private val MANUAL_REGISTER_SCROLL_FADE_HEIGHT = 52.dp

@Preview(name = "ManualRegisterScreen Empty", showBackground = false)
@Composable
private fun ManualRegisterScreenEmptyPreview() {
    OBRitTheme(dynamicColor = false) {
        ManualRegisterScreenContent(
            state = ManualRegisterUiState(),
            action = previewAction(),
        )
    }
}

@Preview(name = "ManualRegisterScreen Filled", showBackground = false)
@Composable
private fun ManualRegisterScreenFilledPreview() {
    OBRitTheme(dynamicColor = false) {
        ManualRegisterScreenContent(
            state = ManualRegisterUiState(categoryName = "샤워기 필터", name = "샤워 필터"),
            action = previewAction(),
        )
    }
}

private fun previewAction(): ManualRegisterScreenAction =
    ManualRegisterScreenAction(
        onCategoryConfirm = {},
        onNameChange = {},
        onQuantityChange = {},
        onLastReplacementPeriodChange = {},
        onSubmit = {},
        onBack = {},
        onDirectRegister = {},
    )
