package com.obrit.feature.register.screen

import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.component.bottomsheet.OBRitBottomSheet
import com.obrit.android.core.designsystem.component.button.OBRitButtonDefaults
import com.obrit.android.core.designsystem.component.button.OBRitLargeFilledButton
import com.obrit.android.core.designsystem.component.radiobutton.OBRitRadioButton
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CategorySelectionBottomSheet(
    initialSelected: String,
    onConfirm: (String) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // AnchoredDraggable: 2개 anchor (Expanded ↔ Hidden). M3 ModalBottomSheet과 같은 swipe-dismiss UX
    // 를 자체 구현하되, IME insets와 무관하게 anchor를 직접 정의해 진동 위험 없음.
    val anchoredState =
        remember {
            AnchoredDraggableState(
                initialValue = SheetDragValue.Expanded,
                confirmValueChange = { newValue ->
                    if (newValue == SheetDragValue.Hidden) {
                        onDismissRequest()
                    }
                    true
                },
            )
        }
    // 새 API: thresholds/specs는 flingBehavior로 옮김. velocityThreshold는 default 사용 (M3 표준 유사).
    val flingBehavior =
        AnchoredDraggableDefaults.flingBehavior(
            state = anchoredState,
            positionalThreshold = { distance -> distance * 0.5f },
            animationSpec = spring(),
        )

    // M3 ModalBottomSheet의 sheet container가 IME에 자동 반응(CTA 떠오름 + 진동)하는 의도된 동작을
    // disable할 공식 API가 없어, 직접 Dialog로 wrapper를 구성.
    Dialog(
        onDismissRequest = onDismissRequest,
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
                dismissOnClickOutside = false,
            ),
    ) {
        Box(
            modifier =
                modifier
                    .fillMaxSize()
                    .background(CategorySheetScrimColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismissRequest,
                    ),
            contentAlignment = Alignment.BottomCenter,
        ) {
            // 시트 본체.
            // - statusBarsPadding() + padding(top=33.dp): 디자인 의도 "TopBar 글씨 직전까지 dim"을
            //   기기 status bar 다양성에 무관하게 일관 보장.
            //   (Figma 기준 status bar 52dp + TopBar 33dp = 화면 최상단 85dp dim. 실제 기기 status bar가
            //   다르면 절대값 85dp는 어긋나지만, status bar 끝 + 33dp는 어디서나 동일.)
            // - fillMaxSize + navigationBarsPadding: 그 아래 영역을 차지하되 기기 하단바 위에서 끝.
            // - offset { ... }: swipe-down 시 시트가 손가락 따라 내려가는 시각 효과.
            // - anchoredDraggable: drag gesture 처리. confirmValueChange에서 Hidden 도달 시 dismiss.
            // - onSizeChanged: 시트 height 측정 후 anchors 정의 (한 번 set 후 거의 안 바뀜).
            // - clickable no-op: scrim 탭 propagate 차단.
            Box(
                modifier =
                    Modifier
                        .statusBarsPadding()
                        .padding(top = CategorySheetTopGapFromStatusBar)
                        .fillMaxSize()
                        .navigationBarsPadding()
                        .offset {
                            IntOffset(0, anchoredState.requireOffset().roundToInt())
                        }
                        .anchoredDraggable(
                            state = anchoredState,
                            orientation = Orientation.Vertical,
                            flingBehavior = flingBehavior,
                        )
                        .onSizeChanged { size ->
                            val sheetHeightPx = size.height.toFloat()
                            anchoredState.updateAnchors(
                                DraggableAnchors {
                                    SheetDragValue.Expanded at 0f
                                    SheetDragValue.Hidden at sheetHeightPx
                                },
                            )
                        }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {},
                        ),
            ) {
                CategorySelectionBottomSheetContent(
                    initialSelected = initialSelected,
                    onConfirm = onConfirm,
                )
            }
        }
    }
}

private enum class SheetDragValue { Expanded, Hidden }

// Figma: 디바이스 최상단부터 시트 상단까지 85dp = status bar(52dp 가정) + 33dp.
// 실제 기기 status bar 다양성에 대응하기 위해 "status bar 끝 + 33dp"로 정의해
// "TopBar 글씨 직전까지 dim" 의도를 어떤 기기에서도 일관 보장.
private val CategorySheetTopGapFromStatusBar = 33.dp

// Figma DimBackground: rgba(0,0,0,0.8)
private val CategorySheetScrimColor = Color.Black.copy(alpha = 0.8f)

@Composable
private fun CategorySelectionBottomSheetContent(
    initialSelected: String,
    onConfirm: (String) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var confirmedQuery by rememberSaveable { mutableStateOf("") }
    var selectedName by rememberSaveable(initialSelected) { mutableStateOf(initialSelected) }
    val keyboard = LocalSoftwareKeyboardController.current

    val displayList =
        remember(confirmedQuery) {
            if (confirmedQuery.isBlank()) {
                MockCategories
            } else {
                MockCategories.filter { it.contains(confirmedQuery, ignoreCase = true) }
            }
        }
    val suggestions =
        remember(query, confirmedQuery) {
            if (query.isBlank() || query == confirmedQuery) {
                emptyList()
            } else {
                MockCategories
                    .filter { it.contains(query, ignoreCase = true) }
                    .take(MaxSuggestions)
            }
        }
    val isEmptyResult = confirmedQuery.isNotBlank() && displayList.isEmpty()

    fun commitSearch(value: String) {
        query = value
        confirmedQuery = value
        keyboard?.hide()
    }

    val colors = LocalOBRitColor.current
    val density = LocalDensity.current

    // 검색창 height(px) — suggestions overlay 위치 계산용
    var searchFieldBottomPx by remember { mutableIntStateOf(0) }
    val suggestionsGapPx = with(density) { CategorySuggestionsGap.roundToPx() }

    val listState = rememberLazyListState()
    // M3 ModalBottomSheet의 AnchoredDraggable이 LazyColumn의 잔여 fling 속도를 받아 진동하는 것을 차단.
    // LazyColumn이 스크롤 가능한 동안엔 fling을 전부 소비해 sheet의 nestedScroll로 전파하지 않음.
    val listFlingGuard =
        remember(listState) {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset = Offset.Zero

                override suspend fun onPreFling(available: Velocity): Velocity =
                    if (listState.canScrollForward || listState.canScrollBackward) {
                        available
                    } else {
                        Velocity.Zero
                    }
            }
        }

    OBRitBottomSheet(
        modifier = Modifier.fillMaxHeight(),
    ) {
        // Box wrap: 본 콘텐츠 Column과 suggestions overlay를 형제로 분리.
        // overlay가 보여도 아래 콘텐츠("전체 소모품" 타이틀, 리스트, CTA) 위치가 변동되지 않음.
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                CategorySearchField(
                    query = query,
                    onQueryChange = { newValue ->
                        query = newValue
                        // 검색어를 완전히 비우면 자동으로 전체 리스트 복귀
                        if (newValue.isEmpty() && confirmedQuery.isNotEmpty()) {
                            confirmedQuery = ""
                        }
                    },
                    onSearch = { commitSearch(query) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coords ->
                                searchFieldBottomPx = coords.size.height
                            },
                )

                Spacer(modifier = Modifier.height(CategorySheetSectionGap))

            CategoryCountTitle(
                prefix = if (isEmptyResult) CategorySheetEmptyResultPrefix else CategorySheetTitlePrefix,
                count = if (isEmptyResult) 0 else displayList.size,
            )

            Spacer(modifier = Modifier.height(CategorySheetTitleGap))

            // 리스트 영역 — CTA 바로 위까지만, CTA 아래에는 리스트가 침범하지 않음
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
            ) {
                if (isEmptyResult) {
                    CategoryEmptyStateCard(
                        // 후속 PR에서 직접 등록 화면 연결
                        onDirectRegisterClick = {},
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .nestedScroll(listFlingGuard),
                        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
                        contentPadding = PaddingValues(bottom = CategoryListBottomFade),
                    ) {
                        items(displayList, key = { it }) { name ->
                            CategoryListItem(
                                name = name,
                                addedCount = 0,
                                selected = name == selectedName,
                                onClick = { selectedName = name },
                            )
                        }
                    }

                    // 리스트 하단 fade — 스크롤 중 아이템이 CTA 직전에서 부드럽게 사라지는 효과
                    Box(
                        modifier =
                            Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(CategoryListBottomFade)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, colors.gray900),
                                    ),
                                ),
                    )
                }
            }

            OBRitLargeFilledButton(
                onClick = { onConfirm(selectedName) },
                enabled = !isEmptyResult && selectedName.isNotBlank(),
                colors = OBRitButtonDefaults.positiveButtonColors(),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = CategoryCtaBottomPadding),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S1.dp),
                ) {
                    if (isEmptyResult) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_button_exclamation_circle),
                            contentDescription = null,
                            tint = LocalOBRitColor.current.common1000,
                            modifier = Modifier.size(CategoryCtaIconSize),
                        )
                    }
                    Text(
                        text = CategorySheetCtaLabel,
                        style =
                            LocalOBRitTypography.current.xl.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                    )
                }
            }
            }

            // Suggestions overlay — 본 콘텐츠 Column 위에 떠서 일부를 가림.
            // 검색창 height(searchFieldBottomPx) + gap 만큼 아래로 offset.
            // .offset { ... } 람다 형태는 placement-only이므로 measure에 영향 없음.
            if (suggestions.isNotEmpty()) {
                CategorySearchSuggestions(
                    suggestions = suggestions,
                    query = query,
                    onPick = { commitSearch(it) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopStart)
                            .offset { IntOffset(0, searchFieldBottomPx + suggestionsGapPx) },
                )
            }
        }
    }
}

@Composable
private fun CategoryCountTitle(
    prefix: String,
    count: Int,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val text =
        buildAnnotatedString {
            withStyle(SpanStyle(color = colors.common00)) {
                append(prefix)
            }
            withStyle(SpanStyle(color = colors.green300)) {
                append(CategorySheetCountFormat.format(count))
            }
        }
    Text(
        text = text,
        style = typography.xl3.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun CategoryListItem(
    name: String,
    addedCount: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AtomRadius.ExtraLarge.dp))
                .background(colors.gray850)
                .clickable(onClick = onClick)
                .padding(
                    horizontal = AtomSpacing.S5.dp,
                    vertical = AtomSpacing.S4.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S4.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(CategoryItemImageSize)
                    .clip(CircleShape)
                    .background(colors.gray750),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(AtomSpacing.S1.dp),
        ) {
            Text(
                text = name,
                style =
                    typography.xl.copy(
                        color = colors.common00,
                        fontWeight = FontWeight.Bold,
                    ),
                maxLines = 1,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S0_5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = CategorySheetAddedCountLabel,
                    style =
                        typography.s.copy(
                            color = colors.gray400,
                            fontWeight = FontWeight.Medium,
                        ),
                )
                Text(
                    text = CategorySheetCountFormat.format(addedCount),
                    style =
                        typography.s.copy(
                            color = colors.common00,
                            fontWeight = FontWeight.SemiBold,
                        ),
                )
            }
        }
        OBRitRadioButton(
            selected = selected,
            onClick = onClick,
        )
    }
}

// OBRitTopBar.kt의 TopBarSearchInput과 동일 디자인 (디테일 변경 시 양쪽 함께 수정).
// Figma SSOT에 정식 컴포넌트 등록되면 designsystem으로 추출 예정.
@Composable
private fun CategorySearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val textStyle =
        typography.xl.copy(
            fontWeight = FontWeight.Medium,
            color = colors.common00,
        )
    val placeholderStyle = textStyle.copy(color = colors.gray700)

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier =
            modifier
                .clip(RoundedCornerShape(AtomRadius.Middle.dp))
                .border(
                    width = CategorySearchBorderWidth,
                    color = colors.gray300,
                    shape = RoundedCornerShape(AtomRadius.Middle.dp),
                ).padding(
                    horizontal = AtomSpacing.S5.dp,
                    vertical = AtomSpacing.S4.dp,
                ),
        singleLine = true,
        textStyle = textStyle,
        cursorBrush = SolidColor(colors.common00),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
    ) { innerTextField ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = CategorySheetSearchPlaceholder,
                        style = placeholderStyle,
                        maxLines = 1,
                    )
                }
                innerTextField()
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_topbar_search),
                contentDescription = null,
                tint = colors.common00,
                modifier =
                    Modifier
                        .size(AtomSpacing.S6.dp)
                        .clickable(onClick = onSearch),
            )
        }
    }
}

@Composable
private fun CategorySearchSuggestions(
    suggestions: List<String>,
    query: String,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val itemTextStyle =
        typography.xl.copy(
            fontWeight = FontWeight.Medium,
            color = colors.common00,
        )

    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(AtomRadius.Small.dp))
                .border(
                    width = CategorySuggestionsBorderWidth,
                    color = colors.gray700,
                    shape = RoundedCornerShape(AtomRadius.Small.dp),
                ).background(colors.gray800),
    ) {
        suggestions.forEach { item ->
            Text(
                text =
                    buildHighlightedSuggestion(
                        text = item,
                        query = query,
                        highlightColor = colors.green300,
                        defaultColor = colors.common00,
                    ),
                style = itemTextStyle,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { onPick(item) }
                        .padding(
                            horizontal = AtomSpacing.S5.dp,
                            vertical = AtomSpacing.S4.dp,
                        ),
                maxLines = 1,
            )
        }
    }
}

private fun buildHighlightedSuggestion(
    text: String,
    query: String,
    highlightColor: Color,
    defaultColor: Color,
): AnnotatedString =
    buildAnnotatedString {
        val startIndex =
            if (query.isBlank()) -1 else text.lowercase().indexOf(query.lowercase())
        if (startIndex < 0) {
            withStyle(SpanStyle(color = defaultColor)) { append(text) }
            return@buildAnnotatedString
        }
        val endIndex = startIndex + query.length
        if (startIndex > 0) {
            withStyle(SpanStyle(color = defaultColor)) {
                append(text.substring(0, startIndex))
            }
        }
        withStyle(SpanStyle(color = highlightColor)) {
            append(text.substring(startIndex, endIndex))
        }
        if (endIndex < text.length) {
            withStyle(SpanStyle(color = defaultColor)) {
                append(text.substring(endIndex))
            }
        }
    }

@Composable
private fun CategoryEmptyStateCard(
    onDirectRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(AtomRadius.ExtraLarge.dp))
                .background(colors.gray850)
                .padding(
                    horizontal = AtomSpacing.S5.dp,
                    vertical = AtomSpacing.S20.dp,
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S5.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
        ) {
            Text(
                text = CategorySheetEmptyTitle,
                style =
                    typography.xl3.copy(
                        fontWeight = FontWeight.Bold,
                        color = colors.common00,
                    ),
                textAlign = TextAlign.Center,
            )
            Text(
                text = CategorySheetEmptyDescription,
                style =
                    typography.base.copy(
                        fontWeight = FontWeight.Medium,
                        color = colors.gray300.copy(alpha = CategoryEmptyDescriptionAlpha),
                    ),
                textAlign = TextAlign.Center,
            )
        }
        Row(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(AtomRadius.Small.dp))
                    .background(colors.common00)
                    .clickable(onClick = onDirectRegisterClick)
                    .padding(
                        horizontal = AtomSpacing.S3.dp,
                        vertical = AtomSpacing.S2.dp,
                    ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S0_5.dp),
        ) {
            Text(
                text = CategorySheetEmptyButtonLabel,
                style =
                    typography.base.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = colors.common1000,
                    ),
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = colors.common1000,
                modifier = Modifier.size(CategoryEmptyButtonIconSize),
            )
        }
    }
}

private const val CategorySheetSearchPlaceholder = "원하시는 소모품을 검색해보세요"
private const val CategorySheetTitlePrefix = "전체 소모품 "
private const val CategorySheetEmptyResultPrefix = "검색 결과 "
private const val CategorySheetCountFormat = "%d개"
private const val CategorySheetAddedCountLabel = "추가된 소모품"
private const val CategorySheetCtaLabel = "소모품 종류 선택하기"
private const val CategorySheetEmptyTitle = "소모품 검색 결과가 없어요!"
private const val CategorySheetEmptyDescription = "하단 버튼을 통해 직접 소모품 종류를 등록할 수 있어요."
private const val CategorySheetEmptyButtonLabel = "직접 등록하기"

private val CategorySheetSectionGap = AtomSpacing.S8.dp
private val CategorySheetTitleGap = AtomSpacing.S3.dp
private val CategorySuggestionsGap = AtomSpacing.S1_5.dp
private val CategoryItemImageSize = 52.dp
private val CategoryCtaIconSize = AtomSpacing.S6.dp
private val CategoryEmptyButtonIconSize = AtomSpacing.S4.dp

// 리스트 하단 fade gradient 높이
private val CategoryListBottomFade = 40.dp

// CTA 아래 ~ 시트 바닥 총 40dp. OBRitBottomSheet 자체가 pb=20을 가지므로 여기서 20dp만 추가.
private val CategoryCtaBottomPadding = AtomSpacing.S5.dp

private val CategorySearchBorderWidth = 1.4f.dp
private val CategorySuggestionsBorderWidth = 1.dp

private const val CategoryEmptyDescriptionAlpha = 0.64f
private const val MaxSuggestions = 3

private val MockCategories =
    listOf(
        "면도기",
        "정수기 필터",
        "칫솔",
        "치약",
        "세탁 세제",
        "수건",
        "샤워기 필터",
        "수세미",
        "주방세제",
        "휴지",
    )

@Preview(name = "CategorySelectionBottomSheet Empty", showBackground = true, backgroundColor = 0xFF1D1B20)
@Composable
private fun CategorySelectionBottomSheetEmptyPreview() {
    OBRitTheme(dynamicColor = false) {
        CategorySelectionBottomSheetContent(
            initialSelected = "",
            onConfirm = {},
        )
    }
}

@Preview(name = "CategorySelectionBottomSheet Selected", showBackground = true, backgroundColor = 0xFF1D1B20)
@Composable
private fun CategorySelectionBottomSheetSelectedPreview() {
    OBRitTheme(dynamicColor = false) {
        CategorySelectionBottomSheetContent(
            initialSelected = "정수기 필터",
            onConfirm = {},
        )
    }
}
