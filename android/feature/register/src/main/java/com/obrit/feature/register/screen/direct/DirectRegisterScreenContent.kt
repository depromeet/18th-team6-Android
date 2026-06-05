package com.obrit.feature.register.screen.direct

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
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.component.button.OBRitButtonDefaults
import com.obrit.android.core.designsystem.component.button.OBRitLargeFilledButton
import com.obrit.android.core.designsystem.component.textfield.InputResultState
import com.obrit.android.core.designsystem.component.textfield.OBRitOutlinedTextField
import com.obrit.android.core.designsystem.component.title.OBRitTitle
import com.obrit.android.core.designsystem.component.title.OBRitTitleSize
import com.obrit.android.core.designsystem.component.title.OBRitTitleType
import com.obrit.android.core.designsystem.component.topbar.OBRitDepthTopBar
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.register.screen.common.FieldSectionHeader
import com.obrit.feature.register.screen.common.rememberFocusBringIntoView
import com.obrit.feature.register.viewmodel.DIRECT_REGISTER_NAME_MAX_LENGTH
import com.obrit.feature.register.viewmodel.DirectRegisterUiState
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
internal fun DirectRegisterScreenContent(
    state: DirectRegisterUiState,
    action: DirectRegisterScreenAction,
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
        OBRitDepthTopBar(title = DIRECT_REGISTER_TITLE, onBackClick = action.onBack)
        DirectRegisterBody(
            state = state,
            onNameChange = action.onNameChange,
            onIconSelect = action.onIconSelect,
            modifier = Modifier.weight(1f),
        )
        DirectRegisterCta(enabled = state.isSubmitEnabled, onSubmit = action.onSubmit)
    }
}

@Composable
private fun DirectRegisterBody(
    state: DirectRegisterUiState,
    onNameChange: (String) -> Unit,
    onIconSelect: (Long) -> Unit,
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
                        top = DIRECT_REGISTER_TOP_BAR_TO_BODY_GAP,
                        bottom = DIRECT_REGISTER_SCROLL_FADE_HEIGHT,
                    ),
            verticalArrangement = Arrangement.spacedBy(DIRECT_REGISTER_SECTION_GAP),
        ) {
            DirectRegisterTitleSection()
            DirectRegisterFieldsColumn(
                state = state,
                onNameChange = onNameChange,
                onIconSelect = onIconSelect,
            )
        }
        BottomFadeOverlay(color = colors.gray900)
    }
}

@Composable
private fun DirectRegisterTitleSection() {
    OBRitTitle(
        title = DIRECT_REGISTER_HEADLINE,
        description = DIRECT_REGISTER_DESCRIPTION,
        size = OBRitTitleSize.Large,
        type = OBRitTitleType.Default,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = AtomSpacing.S5.dp, vertical = AtomSpacing.S4.dp),
    )
}

@Composable
private fun DirectRegisterFieldsColumn(
    state: DirectRegisterUiState,
    onNameChange: (String) -> Unit,
    onIconSelect: (Long) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = AtomSpacing.S5.dp),
        verticalArrangement = Arrangement.spacedBy(DIRECT_REGISTER_FIELD_GAP),
    ) {
        NameField(
            value = state.name,
            isDuplicateName = state.isDuplicateName,
            onValueChange = onNameChange,
        )
        IconGridField(
            label = DIRECT_REGISTER_ICON_LABEL,
            icons = state.icons,
            selectedIconId = state.selectedIconId,
            onSelect = onIconSelect,
            headerSlot = { FieldSectionHeader(label = it) },
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
                .height(DIRECT_REGISTER_SCROLL_FADE_HEIGHT)
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, color)),
                ),
    )
}

@Composable
private fun DirectRegisterCta(
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
                    bottom = DIRECT_REGISTER_CTA_BOTTOM_PADDING,
                ),
    ) {
        OBRitLargeFilledButton(
            onClick = onSubmit,
            enabled = enabled,
            colors = OBRitButtonDefaults.positiveButtonColors(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = DIRECT_REGISTER_SUBMIT_LABEL,
                style = typography.xl.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

@Composable
private fun NameField(
    value: String,
    isDuplicateName: Boolean,
    onValueChange: (String) -> Unit,
) {
    val isOverLimit = value.length > DIRECT_REGISTER_NAME_MAX_LENGTH
    val isError = isOverLimit || isDuplicateName
    val supportingText =
        when {
            isOverLimit -> DIRECT_REGISTER_NAME_OVER_LIMIT_MESSAGE
            isDuplicateName -> DIRECT_REGISTER_NAME_DUPLICATE_MESSAGE
            else -> ""
        }
    val focus = rememberFocusBringIntoView()
    Column(
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S3.dp),
        modifier =
            Modifier
                .bringIntoViewRequester(focus.requester)
                .onSizeChanged(focus.onSize),
    ) {
        FieldSectionHeader(label = DIRECT_REGISTER_NAME_LABEL)
        OBRitOutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            supportingTextEnabled = isError,
            placeholder = DIRECT_REGISTER_NAME_PLACEHOLDER,
            maxLength = DIRECT_REGISTER_NAME_MAX_LENGTH,
            inputResultState = if (isError) InputResultState.Error else InputResultState.Default,
            supportingText = supportingText,
            singleLine = true,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .onFocusEvent { focus.onFocus(it.isFocused) },
        )
    }
}

private const val DIRECT_REGISTER_TITLE = "소모품 직접 등록"
private const val DIRECT_REGISTER_HEADLINE = "소모품 종류 직접 등록하기"
private const val DIRECT_REGISTER_DESCRIPTION = "원하는 소모품 종류가 없다면 직접 추가할 수 있어요."
private const val DIRECT_REGISTER_NAME_LABEL = "소모품 종류 이름"
private const val DIRECT_REGISTER_NAME_PLACEHOLDER = "화장품 퍼프"
private const val DIRECT_REGISTER_NAME_OVER_LIMIT_MESSAGE = "이름은 15자 이내로 입력해주세요"
private const val DIRECT_REGISTER_NAME_DUPLICATE_MESSAGE = "중복된 이름입니다. 다른 이름으로 입력해주세요."
private const val DIRECT_REGISTER_ICON_LABEL = "대표 이미지"
private const val DIRECT_REGISTER_SUBMIT_LABEL = "소모품 종류 등록하기"

private val DIRECT_REGISTER_TOP_BAR_TO_BODY_GAP = AtomSpacing.S5.dp
private val DIRECT_REGISTER_SECTION_GAP = AtomSpacing.S7.dp
private val DIRECT_REGISTER_FIELD_GAP = AtomSpacing.S7.dp
private val DIRECT_REGISTER_CTA_BOTTOM_PADDING = AtomSpacing.S10.dp
private val DIRECT_REGISTER_SCROLL_FADE_HEIGHT = 52.dp

@Preview(name = "DirectRegisterScreen Empty", showBackground = false)
@Composable
private fun DirectRegisterScreenEmptyPreview() {
    OBRitTheme(dynamicColor = false) {
        DirectRegisterScreenContent(
            state = DirectRegisterUiState(),
            action =
                DirectRegisterScreenAction(
                    onNameChange = {},
                    onIconSelect = {},
                    onSubmit = {},
                    onBack = {},
                ),
        )
    }
}
