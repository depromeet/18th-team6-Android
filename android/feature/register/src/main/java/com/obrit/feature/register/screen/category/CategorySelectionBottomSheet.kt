package com.obrit.feature.register.screen.category

import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.component.bottomsheet.OBRitBottomSheet
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import com.obrit.obrit.shared.model.categories.Category
import kotlin.math.roundToInt

internal data class CategorySheetActions(
    val onConfirm: (Category) -> Unit,
    val onDismiss: () -> Unit,
    val onDirectRegister: () -> Unit,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CategorySelectionBottomSheet(
    categories: List<Category>,
    initialSelectedId: Long?,
    actions: CategorySheetActions,
    modifier: Modifier = Modifier,
) {
    val anchoredState = rememberSheetDragState(actions.onDismiss)
    // 새 API: thresholds/specs는 flingBehavior로 옮김. velocityThreshold는 default 사용 (M3 표준 유사).
    val flingBehavior =
        AnchoredDraggableDefaults.flingBehavior(
            state = anchoredState,
            positionalThreshold = { distance -> distance * POSITIONAL_THRESHOLD_RATIO },
            animationSpec = spring(),
        )

    // M3 ModalBottomSheet의 sheet container가 IME에 자동 반응(CTA 떠오름 + 진동)하는 의도된 동작을
    // disable할 공식 API가 없어, 직접 Dialog로 wrapper를 구성.
    androidx.compose.ui.window.Dialog(
        onDismissRequest = actions.onDismiss,
        properties =
            androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
                dismissOnClickOutside = false,
            ),
    ) {
        CategorySheetScrim(modifier = modifier, onDismiss = actions.onDismiss) {
            CategorySheetContainer(state = anchoredState, flingBehavior = flingBehavior) {
                CategorySelectionBottomSheetContent(
                    categories = categories,
                    initialSelectedId = initialSelectedId,
                    onConfirm = actions.onConfirm,
                    onDirectRegisterClick = actions.onDirectRegister,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun rememberSheetDragState(onDismissRequest: () -> Unit): AnchoredDraggableState<SheetDragValue> =
    remember {
        AnchoredDraggableState(
            initialValue = SheetDragValue.Expanded,
            confirmValueChange = { newValue ->
                if (newValue == SheetDragValue.Hidden) onDismissRequest()
                true
            },
        )
    }

@Composable
private fun CategorySheetScrim(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(CATEGORY_SHEET_SCRIM_COLOR)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                ),
        contentAlignment = Alignment.BottomCenter,
        content = content,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategorySheetContainer(
    state: AnchoredDraggableState<SheetDragValue>,
    flingBehavior: FlingBehavior,
    content: @Composable () -> Unit,
) {
    // 시트 본체: status bar 끝 + 33dp를 기준으로 "TopBar 글씨 직전까지 dim" 의도를
    // 어떤 기기에서도 일관 보장. drag offset/anchors는 onSizeChanged 후 한 번 set.
    Box(
        modifier =
            Modifier
                .statusBarsPadding()
                .padding(top = CATEGORY_SHEET_TOP_GAP_FROM_STATUS_BAR)
                .fillMaxSize()
                .navigationBarsPadding()
                .offset { IntOffset(0, state.requireOffset().roundToInt()) }
                .anchoredDraggable(
                    state = state,
                    orientation = Orientation.Vertical,
                    flingBehavior = flingBehavior,
                ).onSizeChanged { size ->
                    val sheetHeightPx = size.height.toFloat()
                    state.updateAnchors(
                        DraggableAnchors {
                            SheetDragValue.Expanded at 0f
                            SheetDragValue.Hidden at sheetHeightPx
                        },
                    )
                }.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                ),
        content = { content() },
    )
}

internal enum class SheetDragValue { Expanded, Hidden }

@Composable
@Suppress("LongMethod")
private fun CategorySelectionBottomSheetContent(
    categories: List<Category>,
    initialSelectedId: Long?,
    onConfirm: (Category) -> Unit,
    onDirectRegisterClick: () -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var confirmedQuery by rememberSaveable { mutableStateOf("") }
    var selectedId by rememberSaveable(initialSelectedId) { mutableStateOf(initialSelectedId) }
    val keyboard = LocalSoftwareKeyboardController.current
    val displayList = rememberCategoryDisplayList(categories, confirmedQuery)
    val suggestions = rememberCategorySuggestions(categories, query, confirmedQuery)
    val isEmptyResult = confirmedQuery.isNotBlank() && displayList.isEmpty()
    var searchFieldBottomPx by remember { mutableIntStateOf(0) }
    val commitSearch: (String) -> Unit = { v ->
        query = v
        confirmedQuery = v
        keyboard?.hide()
    }
    OBRitBottomSheet(modifier = Modifier.fillMaxHeight()) {
        CategorySheetBody(
            state =
                CategorySheetMainColumnState(
                    query = query,
                    isEmptyResult = isEmptyResult,
                    displayList = displayList,
                    selectedId = selectedId,
                ),
            actions =
                CategorySheetMainColumnActions(
                    onQueryChange = { newValue ->
                        query = newValue
                        if (newValue.isEmpty() && confirmedQuery.isNotEmpty()) confirmedQuery = ""
                    },
                    onSearch = { commitSearch(query) },
                    onCategorySelect = { selectedId = it.id },
                    onDirectRegisterClick = onDirectRegisterClick,
                    onConfirm = {
                        val picked = categories.firstOrNull { it.id == selectedId }
                        if (picked != null) onConfirm(picked)
                    },
                    onSearchFieldSized = { searchFieldBottomPx = it },
                ),
            suggestions = suggestions,
            onSuggestionPick = commitSearch,
            searchFieldBottomPx = searchFieldBottomPx,
        )
    }
}

@Composable
private fun CategorySheetBody(
    state: CategorySheetMainColumnState,
    actions: CategorySheetMainColumnActions,
    suggestions: List<Category>,
    onSuggestionPick: (String) -> Unit,
    searchFieldBottomPx: Int,
) {
    // overlay가 보여도 아래 콘텐츠 위치가 변동되지 않도록 본 콘텐츠 Column과 형제로 분리.
    Box(modifier = Modifier.fillMaxSize()) {
        CategorySheetMainColumn(state = state, actions = actions)
        if (suggestions.isNotEmpty()) {
            CategorySearchSuggestionsOverlay(
                suggestions = suggestions.map { it.name },
                query = state.query,
                onPick = onSuggestionPick,
                topOffsetPx = searchFieldBottomPx,
            )
        }
    }
}

internal data class CategorySheetMainColumnState(
    val query: String,
    val isEmptyResult: Boolean,
    val displayList: List<Category>,
    val selectedId: Long?,
)

internal data class CategorySheetMainColumnActions(
    val onQueryChange: (String) -> Unit,
    val onSearch: () -> Unit,
    val onCategorySelect: (Category) -> Unit,
    val onDirectRegisterClick: () -> Unit,
    val onConfirm: () -> Unit,
    val onSearchFieldSized: (Int) -> Unit,
)

@Composable
private fun CategorySheetMainColumn(
    state: CategorySheetMainColumnState,
    actions: CategorySheetMainColumnActions,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        CategorySearchField(
            query = state.query,
            onQueryChange = actions.onQueryChange,
            onSearch = actions.onSearch,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coords -> actions.onSearchFieldSized(coords.size.height) },
        )
        Spacer(modifier = Modifier.height(CATEGORY_SHEET_SECTION_GAP))
        CategoryCountTitle(
            prefix = if (state.isEmptyResult) CATEGORY_SHEET_EMPTY_RESULT_PREFIX else CATEGORY_SHEET_TITLE_PREFIX,
            count = if (state.isEmptyResult) 0 else state.displayList.size,
        )
        Spacer(modifier = Modifier.height(CATEGORY_SHEET_TITLE_GAP))
        CategoryListArea(
            state =
                CategoryListAreaState(
                    isEmptyResult = state.isEmptyResult,
                    items = state.displayList,
                    selectedId = state.selectedId,
                ),
            onCategorySelect = actions.onCategorySelect,
            onDirectRegisterClick = actions.onDirectRegisterClick,
            modifier = Modifier.weight(1f),
        )
        CategoryConfirmButton(
            isEmptyResult = state.isEmptyResult,
            enabled = !state.isEmptyResult && state.selectedId != null,
            onClick = actions.onConfirm,
        )
    }
}

@Preview(name = "CategorySelectionBottomSheet Empty", showBackground = true, backgroundColor = 0xFF1D1B20)
@Composable
private fun CategorySelectionBottomSheetEmptyPreview() {
    OBRitTheme(dynamicColor = false) {
        CategorySelectionBottomSheetContent(
            categories = emptyList(),
            initialSelectedId = null,
            onConfirm = {},
            onDirectRegisterClick = {},
        )
    }
}

@Preview(name = "CategorySelectionBottomSheet Selected", showBackground = true, backgroundColor = 0xFF1D1B20)
@Composable
private fun CategorySelectionBottomSheetSelectedPreview() {
    OBRitTheme(dynamicColor = false) {
        val sample =
            listOf(
                Category(id = 1, name = "정수기 필터", iconUrl = "", itemCount = 0, totalSpareQuantity = 0),
                Category(id = 2, name = "칫솔", iconUrl = "", itemCount = 0, totalSpareQuantity = 0),
            )
        CategorySelectionBottomSheetContent(
            categories = sample,
            initialSelectedId = 1L,
            onConfirm = {},
            onDirectRegisterClick = {},
        )
    }
}

internal const val CATEGORY_SHEET_COUNT_FORMAT = "%d개"

private const val CATEGORY_SHEET_TITLE_PREFIX = "전체 소모품 "
private const val CATEGORY_SHEET_EMPTY_RESULT_PREFIX = "검색 결과 "
private const val POSITIONAL_THRESHOLD_RATIO = 0.5f
private const val SCRIM_ALPHA = 0.8f

// Figma: 디바이스 최상단부터 시트 상단까지 85dp = status bar(52dp 가정) + 33dp.
// 실제 기기 status bar 다양성에 대응하기 위해 "status bar 끝 + 33dp"로 정의.
private val CATEGORY_SHEET_TOP_GAP_FROM_STATUS_BAR = 33.dp
private val CATEGORY_SHEET_SCRIM_COLOR = Color.Black.copy(alpha = SCRIM_ALPHA)
private val CATEGORY_SHEET_SECTION_GAP = AtomSpacing.S8.dp
private val CATEGORY_SHEET_TITLE_GAP = AtomSpacing.S3.dp
