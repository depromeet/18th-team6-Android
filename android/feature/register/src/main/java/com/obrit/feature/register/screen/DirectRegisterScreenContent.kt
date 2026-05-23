package com.obrit.feature.register.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
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
import com.obrit.feature.register.viewmodel.DirectRegisterIconPlaceholderCount
import com.obrit.feature.register.viewmodel.DirectRegisterNameMaxLength
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
        OBRitDepthTopBar(
            title = DirectRegisterTitle,
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
                            top = DirectRegisterTopBarToBodyGap,
                            bottom = DirectRegisterScrollFadeHeight,
                        ),
                verticalArrangement = Arrangement.spacedBy(DirectRegisterSectionGap),
            ) {
                OBRitTitle(
                    title = DirectRegisterHeadline,
                    description = DirectRegisterDescription,
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
                    verticalArrangement = Arrangement.spacedBy(DirectRegisterFieldGap),
                ) {
                    NameField(value = state.name, onValueChange = action.onNameChange)
                    IconGridField(
                        totalCount = DirectRegisterIconPlaceholderCount,
                        selectedIndex = state.selectedIconIndex,
                        onSelect = action.onIconSelect,
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
                            .height(DirectRegisterScrollFadeHeight)
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
                        bottom = DirectRegisterCtaBottomPadding,
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
                    text = DirectRegisterSubmitLabel,
                    style = typography.xl.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        }
    }
}

@Composable
private fun NameField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    val isOverLimit = value.length > DirectRegisterNameMaxLength
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
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S3.dp),
        modifier =
            Modifier
                .bringIntoViewRequester(bringIntoView)
                .onSizeChanged { columnSize = it },
    ) {
        FieldSectionHeader(label = DirectRegisterNameLabel)
        OBRitOutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            supportingTextEnabled = isOverLimit,
            placeholder = DirectRegisterNamePlaceholder,
            maxLength = DirectRegisterNameMaxLength,
            inputResultState = if (isOverLimit) InputResultState.Error else InputResultState.Default,
            supportingText = if (isOverLimit) DirectRegisterNameOverLimitMessage else "",
            singleLine = true,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .onFocusEvent { isFocused = it.isFocused },
        )
    }
}

@Composable
private fun IconGridField(
    totalCount: Int,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AtomSpacing.S3.dp)) {
        FieldSectionHeader(label = DirectRegisterIconLabel)
        IconGrid(
            totalCount = totalCount,
            selectedIndex = selectedIndex,
            onSelect = onSelect,
        )
    }
}

@Composable
private fun IconGrid(
    totalCount: Int,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit,
) {
    val rowCount = (totalCount + IconGridColumns - 1) / IconGridColumns
    Column(verticalArrangement = Arrangement.spacedBy(IconGridRowGap)) {
        repeat(rowCount) { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                repeat(IconGridColumns) { columnIndex ->
                    val itemIndex = rowIndex * IconGridColumns + columnIndex
                    if (itemIndex < totalCount) {
                        IconCircle(
                            selected = itemIndex == selectedIndex,
                            onClick = { onSelect(itemIndex) },
                        )
                    } else {
                        Spacer(modifier = Modifier.size(IconCircleSize))
                    }
                }
            }
        }
    }
}

@Composable
private fun IconCircle(
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalOBRitColor.current
    val borderModifier =
        if (selected) {
            Modifier.border(
                width = IconCircleSelectedBorderWidth,
                color = colors.green300,
                shape = CircleShape,
            )
        } else {
            Modifier
        }
    Box(
        modifier =
            Modifier
                .size(IconCircleSize)
                .clip(CircleShape)
                .background(colors.gray750)
                .then(borderModifier)
                .clickable(onClick = onClick),
        // TODO: GET /categories/icons 연동 시 Image / AsyncImage 추가
    )
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

internal data class DirectRegisterScreenAction(
    val onNameChange: (String) -> Unit,
    val onIconSelect: (Int) -> Unit,
    val onSubmit: () -> Unit,
    val onBack: () -> Unit,
)

private const val DirectRegisterTitle = "소모품 직접 등록"
private const val DirectRegisterHeadline = "소모품 종류 직접 등록하기"
private const val DirectRegisterDescription = "원하는 소모품 종류가 없다면 직접 추가할 수 있어요."
private const val DirectRegisterNameLabel = "소모품 종류 이름"
private const val DirectRegisterNamePlaceholder = "화장품 퍼프"
private const val DirectRegisterNameOverLimitMessage = "이름은 15자 이내로 입력해주세요"
private const val DirectRegisterIconLabel = "대표 이미지"
private const val DirectRegisterSubmitLabel = "소모품 종류 등록하기"
private const val EssentialContentDescription = "필수 항목"

private val DirectRegisterTopBarToBodyGap = AtomSpacing.S5.dp
private val DirectRegisterSectionGap = AtomSpacing.S7.dp
private val DirectRegisterFieldGap = AtomSpacing.S7.dp
private val DirectRegisterCtaBottomPadding = AtomSpacing.S10.dp
private val DirectRegisterScrollFadeHeight = 52.dp
private val EssentialIconSize = AtomSpacing.S6.dp
private val IconCircleSize = 60.dp
private val IconCircleSelectedBorderWidth = 2.dp
private val IconGridRowGap = AtomSpacing.S3.dp
private const val IconGridColumns = 5

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

@Preview(name = "DirectRegisterScreen Filled", showBackground = false)
@Composable
private fun DirectRegisterScreenFilledPreview() {
    OBRitTheme(dynamicColor = false) {
        DirectRegisterScreenContent(
            state = DirectRegisterUiState(name = "화장품 퍼프", selectedIconIndex = 0),
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
