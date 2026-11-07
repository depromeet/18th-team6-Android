@file:Suppress("LongMethod", "LongParameterList", "MagicNumber", "ScreenActionContract", "TooManyFunctions")

package com.obrit.feature.detail.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.component.button.OBRitLargeFilledButton
import com.obrit.android.core.designsystem.component.textfield.InputResultState
import com.obrit.android.core.designsystem.component.textfield.OBRitOutlinedTextField
import com.obrit.android.core.designsystem.component.topbar.OBRitCloseTopBar
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.detail.component.DetailRemoteImage
import com.obrit.feature.detail.viewmodel.DetailEditUiState
import com.obrit.obrit.shared.model.categories.CategoryIcon

@Composable
internal fun DetailEditScreenContent(
    state: DetailEditUiState,
    action: DetailEditScreenAction,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is DetailEditUiState.Success -> {
            DetailEditSuccessContent(
                state = state,
                action = action,
                modifier = modifier,
            )
        }
        is DetailEditUiState.Loading -> {
            DetailEditMessageContent(
                title = DETAIL_EDIT_LOADING_TEXT,
                action = action,
                showRetry = false,
                modifier = modifier,
            )
        }
        is DetailEditUiState.LoadFailed -> {
            DetailEditMessageContent(
                title = DETAIL_EDIT_LOAD_FAILED_TEXT,
                action = action,
                showRetry = true,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun DetailEditSuccessContent(
    state: DetailEditUiState.Success,
    action: DetailEditScreenAction,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    var itemName by remember(state.consumableId) { mutableStateOf(state.itemName) }
    var replacementCycleText by remember(state.consumableId) {
        mutableStateOf(
            state.replacementIntervalDays
                .takeIf { days -> days > 0 }
                ?.toString()
                .orEmpty(),
        )
    }
    var selectedRepresentativeIconId by remember(
        state.consumableId,
        state.selectedRepresentativeIconId,
    ) {
        mutableStateOf(state.selectedRepresentativeIconId)
    }
    val nameValidation =
        validateDetailEditName(
            inputName = itemName,
            originalName = state.itemName,
            existingNames = state.existingNames,
        )
    val replacementCycleDays = replacementCycleText.toIntOrNull()
    val isNameChanged = !itemName.trim().equals(state.itemName.trim(), ignoreCase = true)
    val isReplacementCycleChanged = replacementCycleDays != state.replacementIntervalDays
    val isRepresentativeIconChanged = selectedRepresentativeIconId != state.selectedRepresentativeIconId
    val hasPersistableChanges = isNameChanged || isReplacementCycleChanged || isRepresentativeIconChanged
    val canSubmit =
        itemName.trim().isNotEmpty() &&
            nameValidation != DetailEditNameValidation.Duplicate &&
            replacementCycleDays != null &&
            replacementCycleDays > 0 &&
            !state.isSaveProcessing &&
            hasPersistableChanges
    val submittedItemName =
        if (itemName.trim().equals(state.itemName.trim(), ignoreCase = true)) {
            state.itemName
        } else {
            itemName.trim()
        }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.gray900),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            OBRitCloseTopBar(
                title = DETAIL_EDIT_TITLE,
                onCloseClick = action.onCloseClick,
                modifier = Modifier.statusBarsPadding(),
            )

            Box(modifier = Modifier.weight(1f)) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = DETAIL_EDIT_HORIZONTAL_PADDING),
                ) {
                    Spacer(modifier = Modifier.height(DETAIL_EDIT_FIRST_SECTION_TOP_PADDING))
                    DetailEditNameSection(
                        itemName = itemName,
                        onItemNameChange = { changedName ->
                            itemName = changedName.take(DETAIL_EDIT_NAME_MAX_LENGTH)
                        },
                        validation = nameValidation,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(DETAIL_EDIT_SECTION_GAP))
                    DetailEditReplacementCycleSection(
                        title = state.categoryName,
                        replacementCycleText = replacementCycleText,
                        onReplacementCycleChange = { changedValue ->
                            replacementCycleText = changedValue.filter { char -> char.isDigit() }
                        },
                        recommendedReplacementIntervalDays = replacementCycleDays ?: state.replacementIntervalDays,
                        averageReplacementIntervalDays = state.averageReplacementIntervalDays,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(DETAIL_EDIT_SECTION_GAP))
                    DetailEditRepresentativeImageSection(
                        itemName = itemName.trim().ifEmpty { state.itemName },
                        representativeIcons = state.representativeIcons,
                        selectedRepresentativeIconId = selectedRepresentativeIconId,
                        onRepresentativeIconClick = { iconId ->
                            selectedRepresentativeIconId = iconId
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(DETAIL_EDIT_SCROLL_BOTTOM_SPACER_HEIGHT))
                }

                DetailEditBottomButton(
                    enabled = canSubmit,
                    onClick = submitClick@{
                        val submittedReplacementCycleDays = replacementCycleDays ?: return@submitClick
                        if (!canSubmit) {
                            return@submitClick
                        }

                        action.onSubmitClick(
                            DetailEditSubmitResult(
                                consumableId = state.consumableId,
                                name = submittedItemName,
                                replacementIntervalDays = submittedReplacementCycleDays,
                                representativeIconId =
                                    selectedRepresentativeIconId.takeIf {
                                        isRepresentativeIconChanged
                                    },
                            ),
                        )
                    },
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}

@Composable
private fun DetailEditMessageContent(
    title: String,
    action: DetailEditScreenAction,
    showRetry: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.gray900),
    ) {
        OBRitCloseTopBar(
            title = DETAIL_EDIT_TITLE,
            onCloseClick = action.onCloseClick,
            modifier = Modifier.statusBarsPadding(),
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = typography.xl.copy(fontWeight = FontWeight.Bold),
                    color = colors.gray300,
                    textAlign = TextAlign.Center,
                )
                if (showRetry) {
                    TextButton(onClick = action.onRetryClick) {
                        Text(
                            text = DETAIL_EDIT_RETRY_TEXT,
                            style = typography.base.copy(fontWeight = FontWeight.Bold),
                            color = colors.green300,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailEditNameSection(
    itemName: String,
    onItemNameChange: (String) -> Unit,
    validation: DetailEditNameValidation,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        DetailEditSectionTitle(text = DETAIL_EDIT_NAME_SECTION_TITLE)
        Spacer(modifier = Modifier.height(DETAIL_EDIT_TITLE_FIELD_GAP))
        OBRitOutlinedTextField(
            value = itemName,
            onValueChange = onItemNameChange,
            supportingTextEnabled = false,
            inputResultState = validation.toInputResultState(),
            modifier = Modifier.fillMaxWidth(),
            maxLength = DETAIL_EDIT_NAME_MAX_LENGTH,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        Spacer(modifier = Modifier.height(DETAIL_EDIT_HELPER_TOP_PADDING))
        DetailEditValidationMessage(validation = validation)
    }
}

@Composable
private fun DetailEditReplacementCycleSection(
    title: String,
    replacementCycleText: String,
    onReplacementCycleChange: (String) -> Unit,
    recommendedReplacementIntervalDays: Int,
    averageReplacementIntervalDays: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        DetailEditSectionTitle(text = DETAIL_EDIT_REPLACEMENT_CYCLE_SECTION_TITLE)
        Spacer(modifier = Modifier.height(DETAIL_EDIT_TITLE_FIELD_GAP))
        DetailEditCycleTextField(
            value = replacementCycleText,
            onValueChange = onReplacementCycleChange,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(DETAIL_EDIT_CYCLE_GUIDE_TOP_PADDING))
        DetailEditCycleGuideRow(
            title = title,
            emphasizedText = "권장 교체 주기는 ${recommendedReplacementIntervalDays.toReplacementDaysLabel()}",
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(DETAIL_EDIT_CYCLE_GUIDE_GAP))
        DetailEditCycleGuideRow(
            title = title,
            emphasizedText = "나의 평균 교체 주기는 ${averageReplacementIntervalDays.toReplacementDaysLabel()}",
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun DetailEditRepresentativeImageSection(
    itemName: String,
    representativeIcons: List<CategoryIcon>,
    selectedRepresentativeIconId: Long?,
    onRepresentativeIconClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        DetailEditSectionTitle(text = DETAIL_EDIT_REPRESENTATIVE_IMAGE_SECTION_TITLE)
        Spacer(modifier = Modifier.height(DETAIL_EDIT_IMAGE_GRID_TOP_PADDING))
        DetailEditRepresentativeIconGrid(
            itemName = itemName,
            representativeIcons = representativeIcons,
            selectedRepresentativeIconId = selectedRepresentativeIconId,
            onRepresentativeIconClick = onRepresentativeIconClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun DetailEditRepresentativeIconGrid(
    itemName: String,
    representativeIcons: List<CategoryIcon>,
    selectedRepresentativeIconId: Long?,
    onRepresentativeIconClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(DETAIL_EDIT_IMAGE_ICON_GRID_ROW_GAP),
    ) {
        representativeIcons
            .chunked(DETAIL_EDIT_IMAGE_ICON_GRID_COLUMNS)
            .forEach { rowIcons ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    rowIcons.forEach { icon ->
                        DetailEditRepresentativeImageItem(
                            itemName = itemName,
                            representativeImageUrl = icon.url,
                            selected = icon.id == selectedRepresentativeIconId,
                            onClick = { onRepresentativeIconClick(icon.id) },
                        )
                    }
                    repeat(DETAIL_EDIT_IMAGE_ICON_GRID_COLUMNS - rowIcons.size) {
                        Spacer(modifier = Modifier.size(DETAIL_EDIT_IMAGE_ITEM_OUTER_SIZE))
                    }
                }
            }
    }
}

@Composable
private fun DetailEditSectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Text(
        text = text,
        style = typography.xl2.copy(fontWeight = FontWeight.Bold),
        color = colors.common00,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}

@Composable
private fun DetailEditValidationMessage(
    validation: DetailEditNameValidation,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val messageColor =
        when (validation) {
            DetailEditNameValidation.Duplicate -> colors.red300
            DetailEditNameValidation.Valid -> colors.green300
            DetailEditNameValidation.Default -> colors.gray400
        }
    val message =
        when (validation) {
            DetailEditNameValidation.Duplicate -> DETAIL_EDIT_NAME_DUPLICATE_MESSAGE
            DetailEditNameValidation.Valid,
            DetailEditNameValidation.Default,
            -> DETAIL_EDIT_NAME_DEFAULT_MESSAGE
        }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DetailEditStatusIcon(
            color = messageColor,
            isError = validation == DetailEditNameValidation.Duplicate,
        )
        Text(
            text = message,
            style = typography.s.copy(fontWeight = FontWeight.Medium),
            color = messageColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = DETAIL_EDIT_HELPER_ICON_TEXT_GAP),
        )
    }
}

@Composable
private fun DetailEditCycleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderColor = if (isFocused) colors.common00 else Color.Transparent
    val textStyle =
        typography.xl.copy(
            fontWeight = FontWeight.Medium,
            color = colors.common00,
        )

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier =
            modifier
                .clip(DETAIL_EDIT_FIELD_SHAPE)
                .background(colors.gray800)
                .border(
                    border = BorderStroke(width = DETAIL_EDIT_FIELD_BORDER_WIDTH, color = borderColor),
                    shape = DETAIL_EDIT_FIELD_SHAPE,
                ).padding(
                    horizontal = DETAIL_EDIT_FIELD_HORIZONTAL_PADDING,
                    vertical = DETAIL_EDIT_FIELD_VERTICAL_PADDING,
                ),
        textStyle = textStyle,
        singleLine = true,
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
        interactionSource = interactionSource,
        cursorBrush = SolidColor(colors.common00),
    ) { innerTextField ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = DETAIL_EDIT_REPLACEMENT_CYCLE_PLACEHOLDER,
                        style = textStyle,
                        color = colors.common00,
                        maxLines = 1,
                    )
                }
                innerTextField()
            }
            Text(
                text = DETAIL_EDIT_REPLACEMENT_CYCLE_SUFFIX,
                style = typography.s.copy(fontWeight = FontWeight.Medium),
                color = colors.common00,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun DetailEditCycleGuideRow(
    title: String,
    emphasizedText: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val textColor = colors.gray300

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DetailEditStatusIcon(color = colors.gray300)
        Text(
            text =
                buildAnnotatedString {
                    append(title)
                    append("의 ")
                    withStyle(SpanStyle(color = colors.green300)) {
                        append(emphasizedText)
                    }
                    append("이에요")
                },
            style = typography.s.copy(fontWeight = FontWeight.Medium),
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = DETAIL_EDIT_HELPER_ICON_TEXT_GAP),
        )
    }
}

@Composable
private fun DetailEditRepresentativeImageItem(
    itemName: String,
    representativeImageUrl: String?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val borderModifier =
        if (selected) {
            Modifier.border(
                width = DETAIL_EDIT_IMAGE_SELECTED_BORDER_WIDTH,
                color = colors.green300,
                shape = CircleShape,
            )
        } else {
            Modifier
        }
    val imageDescription =
        if (representativeImageUrl.isNullOrBlank()) {
            "${itemName.ifBlank { "소모품" }} 대표 이미지 자리"
        } else {
            "${itemName.ifBlank { "소모품" }} 대표 이미지"
        }

    Box(
        modifier =
            modifier
                .size(DETAIL_EDIT_IMAGE_ITEM_OUTER_SIZE)
                .then(borderModifier)
                .clip(CircleShape)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(DETAIL_EDIT_IMAGE_ITEM_INNER_SIZE)
                    .clip(CircleShape)
                    .background(colors.gray800),
            contentAlignment = Alignment.Center,
        ) {
            DetailRemoteImage(
                imageUrl = representativeImageUrl,
                contentDescription = imageDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun DetailEditBottomButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(colors.gray900)
                .navigationBarsPadding()
                .imePadding()
                .padding(
                    horizontal = DETAIL_EDIT_HORIZONTAL_PADDING,
                    vertical = DETAIL_EDIT_BUTTON_VERTICAL_PADDING,
                ),
    ) {
        OBRitLargeFilledButton(
            text = DETAIL_EDIT_COMPLETE_BUTTON_TEXT,
            onClick = onClick,
            enabled = enabled,
            modifier =
                Modifier
                    .fillMaxWidth(),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = colors.green300,
                    contentColor = colors.common1000,
                    disabledContainerColor = colors.gray800,
                    disabledContentColor = colors.gray700,
                ),
        )
    }
}

@Composable
private fun DetailEditStatusIcon(
    color: Color,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    val markColor = LocalOBRitColor.current.gray900

    Canvas(modifier = modifier.size(DETAIL_EDIT_STATUS_ICON_SIZE)) {
        drawCircle(color = color)
        if (isError) {
            drawLine(
                color = markColor,
                start = Offset(size.width * 0.5f, size.height * 0.25f),
                end = Offset(size.width * 0.5f, size.height * 0.58f),
                strokeWidth = size.width * 0.12f,
            )
            drawCircle(
                color = markColor,
                radius = size.width * 0.06f,
                center = Offset(size.width * 0.5f, size.height * 0.74f),
            )
        } else {
            drawLine(
                color = markColor,
                start = Offset(size.width * 0.30f, size.height * 0.53f),
                end = Offset(size.width * 0.44f, size.height * 0.67f),
                strokeWidth = size.width * 0.11f,
            )
            drawLine(
                color = markColor,
                start = Offset(size.width * 0.44f, size.height * 0.67f),
                end = Offset(size.width * 0.72f, size.height * 0.36f),
                strokeWidth = size.width * 0.11f,
            )
        }
    }
}

private fun validateDetailEditName(
    inputName: String,
    originalName: String,
    existingNames: List<String>,
): DetailEditNameValidation {
    val normalizedInputName = inputName.trim()
    val normalizedOriginalName = originalName.trim()

    return when {
        normalizedInputName.isEmpty() -> DetailEditNameValidation.Default
        normalizedInputName.equals(normalizedOriginalName, ignoreCase = true) -> DetailEditNameValidation.Default
        existingNames.any { name -> name.trim().equals(normalizedInputName, ignoreCase = true) } ->
            DetailEditNameValidation.Duplicate
        else -> DetailEditNameValidation.Valid
    }
}

private fun DetailEditNameValidation.toInputResultState(): InputResultState =
    when (this) {
        DetailEditNameValidation.Duplicate -> InputResultState.Error
        DetailEditNameValidation.Valid -> InputResultState.Success
        DetailEditNameValidation.Default -> InputResultState.Default
    }

private fun Int.toReplacementDaysLabel(): String = "${coerceAtLeast(0)}일"

private enum class DetailEditNameValidation {
    Default,
    Duplicate,
    Valid,
}

@Preview(
    name = "DetailEditScreenContent",
    showBackground = true,
    widthDp = 393,
    heightDp = 852,
)
@Composable
private fun DetailEditScreenContentPreview() {
    OBRitTheme(dynamicColor = false) {
        DetailEditScreenContent(
            state =
                DetailEditUiState.Success(
                    consumableId = 1L,
                    itemName = "칫솔",
                    categoryName = "욕실",
                    replacementIntervalDays = 30,
                    averageReplacementIntervalDays = 28,
                    representativeImageUrl = "https://example.com/icon-1.png",
                    representativeIcons =
                        listOf(
                            CategoryIcon(id = 1L, url = "https://example.com/icon-1.png"),
                            CategoryIcon(id = 2L, url = "https://example.com/icon-2.png"),
                            CategoryIcon(id = 3L, url = "https://example.com/icon-3.png"),
                            CategoryIcon(id = 4L, url = "https://example.com/icon-4.png"),
                            CategoryIcon(id = 5L, url = "https://example.com/icon-5.png"),
                        ),
                    selectedRepresentativeIconId = 1L,
                    existingNames = listOf("수건", "샤워필터"),
                    isSaveProcessing = false,
                ),
            action =
                DetailEditScreenAction(
                    onCloseClick = {},
                    onRetryClick = {},
                    onSubmitClick = {},
                ),
            modifier = Modifier.fillMaxSize(),
        )
    }
}

private val DETAIL_EDIT_HORIZONTAL_PADDING = 20.dp
private val DETAIL_EDIT_FIRST_SECTION_TOP_PADDING = 28.dp
private val DETAIL_EDIT_SECTION_GAP = 36.dp
private val DETAIL_EDIT_TITLE_FIELD_GAP = 16.dp
private val DETAIL_EDIT_HELPER_TOP_PADDING = 10.dp
private val DETAIL_EDIT_CYCLE_GUIDE_TOP_PADDING = 10.dp
private val DETAIL_EDIT_CYCLE_GUIDE_GAP = 6.dp
private val DETAIL_EDIT_IMAGE_GRID_TOP_PADDING = 24.dp
private val DETAIL_EDIT_IMAGE_ICON_GRID_ROW_GAP = 12.dp
private val DETAIL_EDIT_SCROLL_BOTTOM_SPACER_HEIGHT = 116.dp
private val DETAIL_EDIT_BUTTON_VERTICAL_PADDING = 16.dp
private val DETAIL_EDIT_FIELD_HORIZONTAL_PADDING = 20.dp
private val DETAIL_EDIT_FIELD_VERTICAL_PADDING = 17.dp
private val DETAIL_EDIT_FIELD_BORDER_WIDTH = 1.dp
private val DETAIL_EDIT_IMAGE_ITEM_OUTER_SIZE = 70.dp
private val DETAIL_EDIT_IMAGE_ITEM_INNER_SIZE = 62.dp
private val DETAIL_EDIT_IMAGE_SELECTED_BORDER_WIDTH = 2.dp
private val DETAIL_EDIT_STATUS_ICON_SIZE = 16.dp
private val DETAIL_EDIT_HELPER_ICON_TEXT_GAP = 8.dp
private val DETAIL_EDIT_FIELD_SHAPE = RoundedCornerShape(10.dp)
private const val DETAIL_EDIT_IMAGE_ICON_GRID_COLUMNS = 5
private const val DETAIL_EDIT_NAME_MAX_LENGTH = 15
private const val DETAIL_EDIT_TITLE = "편집하기"
private const val DETAIL_EDIT_LOADING_TEXT = "편집 정보를 불러오고 있어요."
private const val DETAIL_EDIT_LOAD_FAILED_TEXT = "편집 정보를 다시 불러오지 못했어요."
private const val DETAIL_EDIT_RETRY_TEXT = "다시 시도"
private const val DETAIL_EDIT_NAME_SECTION_TITLE = "소모품명"
private const val DETAIL_EDIT_REPLACEMENT_CYCLE_SECTION_TITLE = "교체 주기"
private const val DETAIL_EDIT_REPRESENTATIVE_IMAGE_SECTION_TITLE = "대표 이미지"
private const val DETAIL_EDIT_NAME_DEFAULT_MESSAGE = "다른 이름과 중복되지 않게 입력해주세요"
private const val DETAIL_EDIT_NAME_DUPLICATE_MESSAGE = "이미 사용 중인 이름이에요"
private const val DETAIL_EDIT_REPLACEMENT_CYCLE_PLACEHOLDER = "NN"
private const val DETAIL_EDIT_REPLACEMENT_CYCLE_SUFFIX = "일"
private const val DETAIL_EDIT_COMPLETE_BUTTON_TEXT = "편집 완료"
