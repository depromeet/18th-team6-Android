package com.obrit.feature.register.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.component.button.OBRitButtonDefaults
import com.obrit.android.core.designsystem.component.button.OBRitLargeFilledButton
import com.obrit.android.core.designsystem.component.dropdown.OBRitDropdown
import com.obrit.android.core.designsystem.component.dropdown.OBRitDropdownMenu
import com.obrit.android.core.designsystem.component.stepper.OBRitStepper
import com.obrit.android.core.designsystem.component.textfield.InputResultState
import com.obrit.android.core.designsystem.component.textfield.OBRitOutlinedTextField
import com.obrit.android.core.designsystem.component.title.OBRitTitle
import com.obrit.android.core.designsystem.component.title.OBRitTitleSize
import com.obrit.android.core.designsystem.component.title.OBRitTitleType
import com.obrit.android.core.designsystem.component.topbar.OBRitDepthTopBar
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.register.viewmodel.ManualRegisterUiState
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@Composable
internal fun ManualRegisterScreenContent(
    state: ManualRegisterUiState,
    action: ManualRegisterScreenAction,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    var quantity by remember { mutableIntStateOf(ManualRegisterInitialQuantity) }
    var isCategorySheetOpen by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.gray900)
                .statusBarsPadding()
                .navigationBarsPadding(),
    ) {
        OBRitDepthTopBar(
            title = ManualRegisterTitle,
            onBackClick = action.onBack,
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            top = ManualRegisterTopBarToBodyGap,
                            bottom = ManualRegisterScrollFadeHeight,
                        ),
                verticalArrangement = Arrangement.spacedBy(ManualRegisterSectionGap),
            ) {
                OBRitTitle(
                    title = ManualRegisterHeadline,
                    description = ManualRegisterDescription,
                    size = OBRitTitleSize.Large,
                    type = OBRitTitleType.Default,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = AtomSpacing.S5.dp,
                                vertical = AtomSpacing.S4.dp,
                            ),
                )

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AtomSpacing.S5.dp),
                    verticalArrangement = Arrangement.spacedBy(ManualRegisterFieldGap),
                ) {
                    CategoryField(
                        value = state.categoryName,
                        onClick = { isCategorySheetOpen = true },
                    )
                    NameField(value = state.name, onValueChange = action.onNameChange)
                    LastReplaceDateField(
                        selectedOption = state.lastReplaceDate,
                        onOptionChange = action.onLastReplaceDateChange,
                    )
                    QuantityField(
                        title = state.categoryName,
                        quantity = quantity,
                        onQuantityChange = { quantity = it },
                    )
                }
            }

            val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
            if (!isImeVisible) {
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(ManualRegisterScrollFadeHeight)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, colors.gray900),
                                ),
                            ),
                )
            }
        }

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = AtomSpacing.S5.dp,
                        end = AtomSpacing.S5.dp,
                        bottom = ManualRegisterCtaBottomPadding,
                    ),
        ) {
            OBRitLargeFilledButton(
                onClick = action.onSubmit,
                enabled = state.isSubmitEnabled,
                colors = OBRitButtonDefaults.positiveButtonColors(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                val typography = LocalOBRitTypography.current
                Text(
                    text = ManualRegisterSubmitLabel,
                    style = typography.xl.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        }
    }

    if (isCategorySheetOpen) {
        CategorySelectionBottomSheet(
            initialSelected = state.categoryName,
            onConfirm = { name ->
                action.onCategoryChange(name)
                isCategorySheetOpen = false
            },
            onDismissRequest = { isCategorySheetOpen = false },
        )
    }
}

@Composable
private fun CategoryField(
    value: String,
    onClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp)) {
        FieldSectionHeader(label = ManualRegisterCategoryLabel)
        SelectableField(
            placeholder = ManualRegisterCategoryPlaceholder,
            value = value,
            trailingIconRes = R.drawable.ic_topbar_search,
            trailingIconSize = SelectableFieldSearchIconSize,
            onClick = onClick,
        )
    }
}

@Composable
private fun NameField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    val isOverLimit = value.length > ManualRegisterNameMaxLength
    val bringIntoView = remember { BringIntoViewRequester() }
    var isFocused by remember { mutableStateOf(false) }
    var columnSize by remember { mutableStateOf(IntSize.Zero) }
    val imeBottomPx = WindowInsets.ime.getBottom(LocalDensity.current)

    LaunchedEffect(isFocused, imeBottomPx, columnSize) {
        if (isFocused && columnSize.height > 0) {
            bringIntoView.bringIntoView(
                Rect(
                    left = 0f,
                    top = 0f,
                    right = columnSize.width.toFloat(),
                    bottom = columnSize.height + imeBottomPx.toFloat(),
                ),
            )
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
        modifier =
            Modifier
                .bringIntoViewRequester(bringIntoView)
                .onSizeChanged { columnSize = it },
    ) {
        FieldSectionHeader(label = ManualRegisterNameLabel)
        OBRitOutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            supportingTextEnabled = isOverLimit,
            placeholder = ManualRegisterNamePlaceholder,
            maxLength = ManualRegisterNameMaxLength,
            inputResultState = if (isOverLimit) InputResultState.Error else InputResultState.Default,
            supportingText = if (isOverLimit) ManualRegisterNameOverLimitMessage else "",
            singleLine = true,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .onFocusEvent { isFocused = it.isFocused },
        )
    }
}

@Composable
private fun LastReplaceDateField(
    selectedOption: String,
    onOptionChange: (String) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var triggerSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    val menuGapPx = with(density) { LastReplaceDateMenuGap.roundToPx() }
    val positionProvider =
        remember(menuGapPx) { LastReplaceDateMenuPositionProvider(menuGapPx) }

    Column(verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp)) {
        FieldSectionHeader(label = ManualRegisterLastReplaceDateLabel)

        Box {
            OBRitDropdown(
                value = selectedOption,
                onClick = { expanded = !expanded },
                placeholder = ManualRegisterLastReplaceDatePlaceholder,
                expanded = expanded,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .onSizeChanged { triggerSize = it },
            )

            if (expanded) {
                Popup(
                    popupPositionProvider = positionProvider,
                    onDismissRequest = { expanded = false },
                    properties = PopupProperties(focusable = true),
                ) {
                    OBRitDropdownMenu(
                        items = LastReplaceDateOptions,
                        selectedIndex = LastReplaceDateOptions
                            .indexOf(selectedOption)
                            .takeIf { it >= 0 },
                        onItemClick = { index ->
                            onOptionChange(LastReplaceDateOptions[index])
                            expanded = false
                        },
                        modifier = with(density) { Modifier.width(triggerSize.width.toDp()) },
                    )
                }
            }
        }
    }
}

private class LastReplaceDateMenuPositionProvider(
    private val verticalGapPx: Int,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val x = anchorBounds.left
        val belowY = anchorBounds.bottom + verticalGapPx
        val aboveY = anchorBounds.top - popupContentSize.height - verticalGapPx
        val fitsBelow = belowY + popupContentSize.height <= windowSize.height
        val y = if (fitsBelow) belowY else aboveY.coerceAtLeast(0)
        return IntOffset(x, y)
    }
}

@Composable
private fun QuantityField(
    title: String,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp)) {
        FieldSectionHeader(label = ManualRegisterQuantityLabel)
        QuantityCard(
            title = title.ifEmpty { ManualRegisterQuantityTitlePlaceholder },
            totalCount = 0,
            quantity = quantity,
            onQuantityChange = onQuantityChange,
        )
        InfoNote(text = ManualRegisterQuantityHelp)
    }
}

@Composable
private fun FieldSectionHeader(label: String) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style =
                typography.xl2.copy(
                    color = colors.common00,
                    fontWeight = FontWeight.SemiBold,
                ),
        )
        Essential()
    }
}

@Composable
private fun Essential() {
    Icon(
        painter = painterResource(id = R.drawable.ic_essential),
        contentDescription = EssentialContentDescription,
        modifier = Modifier.size(EssentialIconSize),
        tint = Color.Unspecified,
    )
}

@Composable
private fun SelectableField(
    placeholder: String,
    value: String,
    @DrawableRes trailingIconRes: Int,
    trailingIconSize: Dp,
    onClick: () -> Unit,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val isEmpty = value.isEmpty()

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = SelectableFieldMinHeight)
                .clip(SelectableFieldShape)
                .background(colors.gray800)
                .clickable(onClick = onClick)
                .padding(
                    horizontal = AtomSpacing.S5.dp,
                    vertical = AtomSpacing.S4.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
    ) {
        Text(
            text = if (isEmpty) placeholder else value,
            style =
                typography.xl.copy(
                    color = if (isEmpty) colors.gray700 else colors.common00,
                    fontWeight = FontWeight.Medium,
                ),
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )
        Icon(
            painter = painterResource(id = trailingIconRes),
            contentDescription = null,
            modifier = Modifier.size(trailingIconSize),
            tint = Color.Unspecified,
        )
    }
}

@Composable
private fun QuantityCard(
    title: String,
    totalCount: Int,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AtomRadius.ExtraLarge.dp))
                .background(colors.gray850)
                .padding(AtomSpacing.S5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S4.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(QuantityCardImageSize)
                    .clip(CircleShape)
                    .background(colors.gray750),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(AtomSpacing.S1.dp),
        ) {
            Text(
                text = title,
                style =
                    typography.xl.copy(
                        color = colors.common00,
                        fontWeight = FontWeight.Bold,
                    ),
                maxLines = 1,
            )
            Text(
                text = ManualRegisterQuantityTotalFormat.format(totalCount),
                style =
                    typography.s.copy(
                        color = colors.gray500,
                        fontWeight = FontWeight.Medium,
                    ),
                maxLines = 1,
            )
        }
        OBRitStepper(
            value = quantity,
            onValueChange = onQuantityChange,
        )
    }
}

@Composable
private fun InfoNote(text: String) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S1_5.dp),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_info),
            contentDescription = null,
            modifier = Modifier.size(InfoNoteIconSize),
            tint = Color.Unspecified,
        )
        Text(
            text = text,
            style =
                typography.base.copy(
                    color = colors.gray500,
                    fontWeight = FontWeight.SemiBold,
                ),
        )
    }
}

private const val ManualRegisterTitle = "소모품 등록"
private const val ManualRegisterHeadline = "소모품의 상세 정보를\n입력해주세요"
private const val ManualRegisterDescription = "원활한 관리를 위해 구체적인 정보를 입력해주세요"
private const val ManualRegisterCategoryLabel = "소모품 종류"
private const val ManualRegisterCategoryPlaceholder = "소모품 종류를 선택해주세요"
private const val ManualRegisterNameLabel = "소모품 명"
private const val ManualRegisterNamePlaceholder = "구분을 위한 이름을 입력해주세요"
private const val ManualRegisterNameMaxLength = 15
private const val ManualRegisterNameOverLimitMessage = "소모품 명은 15자 이내로 입력해주세요"
private const val ManualRegisterLastReplaceDateLabel = "마지막 교체 일자"
private const val ManualRegisterLastReplaceDatePlaceholder = "마지막 교체 일자를 등록해주세요"
private const val ManualRegisterQuantityLabel = "등록할 수량"
private const val ManualRegisterQuantityTitlePlaceholder = "{title}"
private const val ManualRegisterQuantityTotalFormat = "전체 %d개"
private const val ManualRegisterQuantityHelp = "소모품의 전체 수량은 추후 수정할 수 있어요."
private const val ManualRegisterSubmitLabel = "소모품 등록하기"
private const val EssentialContentDescription = "필수 항목"
private const val ManualRegisterInitialQuantity = 1

private val ManualRegisterTopBarToBodyGap = AtomSpacing.S5.dp
private val ManualRegisterSectionGap = 28.dp
private val ManualRegisterFieldGap = 40.dp
private val ManualRegisterCtaBottomPadding = 40.dp
private val ManualRegisterScrollFadeHeight = 52.dp
private val SelectableFieldMinHeight = AtomSpacing.S14.dp
private val SelectableFieldShape = RoundedCornerShape(AtomRadius.Middle.dp)
private val SelectableFieldSearchIconSize = AtomSpacing.S6.dp
private val EssentialIconSize = AtomSpacing.S6.dp
private val QuantityCardImageSize = 52.dp
private val InfoNoteIconSize = AtomSpacing.S4.dp
private val LastReplaceDateMenuGap = 6.dp

private val LastReplaceDateOptions =
    listOf(
        "1주일 이내",
        "2-4주 전",
        "1-3개월 전",
        "잘 모르겠어요",
    )

@Preview(name = "ManualRegisterScreen Empty", showBackground = false)
@Composable
private fun ManualRegisterScreenEmptyPreview() {
    OBRitTheme(dynamicColor = false) {
        ManualRegisterScreenContent(
            state = ManualRegisterUiState(),
            action =
                ManualRegisterScreenAction(
                    onCategoryChange = {},
                    onNameChange = {},
                    onSpareCountChange = {},
                    onLastReplaceDateChange = {},
                    onSubmit = {},
                    onBack = {},
                ),
        )
    }
}

@Preview(name = "ManualRegisterScreen Filled", showBackground = false)
@Composable
private fun ManualRegisterScreenFilledPreview() {
    OBRitTheme(dynamicColor = false) {
        ManualRegisterScreenContent(
            state = ManualRegisterUiState(categoryName = "샤워기 필터", name = "샤워 필터"),
            action =
                ManualRegisterScreenAction(
                    onCategoryChange = {},
                    onNameChange = {},
                    onSpareCountChange = {},
                    onLastReplaceDateChange = {},
                    onSubmit = {},
                    onBack = {},
                ),
        )
    }
}
