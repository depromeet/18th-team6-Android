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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.component.bottomsheet.OBRitBottomSheet
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.register.screen.direct.DirectRegisterSheetPage
import com.obrit.feature.register.viewmodel.CategorySection
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import com.obrit.obrit.shared.model.categories.Category
import kotlinx.coroutines.flow.first
import kotlin.math.roundToInt

internal data class CategorySheetActions(
    val onConfirm: (Category) -> Unit,
    val onDismiss: () -> Unit,
    val onDirectRegistered: (Category) -> Unit,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CategorySelectionBottomSheet(
    sections: List<CategorySection>,
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
                    sections = sections,
                    initialSelectedId = initialSelectedId,
                    onConfirm = actions.onConfirm,
                    onDirectRegister = actions.onDirectRegistered,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun rememberSheetDragState(onDismissRequest: () -> Unit): AnchoredDraggableState<SheetDragValue> {
    val state = remember { AnchoredDraggableState(initialValue = SheetDragValue.Expanded) }
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)
    LaunchedEffect(state) {
        snapshotFlow { state.settledValue }.first { it == SheetDragValue.Hidden }
        currentOnDismissRequest()
    }
    return state
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
    // 시트 본체: status bar 끝 + 81dp를 기준으로 TopBar가 dim 너머로 보이는 높이를
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
private fun CategorySelectionBottomSheetContent(
    sections: List<CategorySection>,
    initialSelectedId: Long?,
    onConfirm: (Category) -> Unit,
    onDirectRegister: (Category) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    // 입력 중에는 추천만 보여주고, 검색을 누르거나 추천을 골라야 목록을 거른다.
    var confirmedQuery by rememberSaveable { mutableStateOf("") }
    var selectedId by rememberSaveable(initialSelectedId) { mutableStateOf(initialSelectedId) }
    // null이면 소모품 목록, 값이 있으면 그 이름이 채워진 직접 등록 화면을 보여준다.
    var directRegisterName by rememberSaveable { mutableStateOf<String?>(null) }
    OBRitBottomSheet(modifier = Modifier.fillMaxHeight()) {
        val initialName = directRegisterName
        if (initialName != null) {
            DirectRegisterSheetPage(
                initialName = initialName,
                onBack = { directRegisterName = null },
                onRegister = onDirectRegister,
            )
        } else {
            CategoryListPage(
                state = CategoryListPageState(sections, query, confirmedQuery, selectedId),
                actions =
                    CategoryListPageActions(
                        onQueryChange = { value ->
                            query = value
                            if (value.isEmpty()) confirmedQuery = ""
                        },
                        onSearch = { value ->
                            query = value
                            confirmedQuery = value
                        },
                        onCategorySelect = { selectedId = it.id },
                        onDirectRegisterClick = { directRegisterName = confirmedQuery.trim() },
                        onConfirm = onConfirm,
                    ),
            )
        }
    }
}

private data class CategoryListPageState(
    val sections: List<CategorySection>,
    val query: String,
    val confirmedQuery: String,
    val selectedId: Long?,
) {
    val selectedCategory: Category?
        get() = sections.flatMap { it.categories }.firstOrNull { it.id == selectedId }
}

private data class CategoryListPageActions(
    val onQueryChange: (String) -> Unit,
    val onSearch: (String) -> Unit,
    val onCategorySelect: (Category) -> Unit,
    val onDirectRegisterClick: () -> Unit,
    val onConfirm: (Category) -> Unit,
)

@Composable
private fun CategoryListPage(
    state: CategoryListPageState,
    actions: CategoryListPageActions,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val suggestions = rememberCategorySuggestions(state.sections, state.query, state.confirmedQuery)
    var searchFieldBottomPx by remember { mutableIntStateOf(0) }
    val commitSearch: (String) -> Unit = { value ->
        actions.onSearch(value)
        keyboard?.hide()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        CategoryListColumn(
            state = state,
            actions = actions,
            onSearch = { commitSearch(state.query) },
            onSearchFieldBottomChange = { searchFieldBottomPx = it },
        )
        CategoryConfirmButton(
            enabled = state.selectedId != null,
            onClick = { state.selectedCategory?.let(actions.onConfirm) },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
        if (suggestions.isNotEmpty()) {
            CategorySearchSuggestionsOverlay(
                suggestions = suggestions.map { it.name },
                query = state.query,
                onPick = commitSearch,
                topOffsetPx = searchFieldBottomPx,
            )
        }
    }
}

@Composable
private fun CategoryListColumn(
    state: CategoryListPageState,
    actions: CategoryListPageActions,
    onSearch: () -> Unit,
    onSearchFieldBottomChange: (Int) -> Unit,
) {
    val searchResults = rememberCategorySearchResults(state.sections, state.confirmedQuery)
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(AtomSpacing.S2_5.dp))
        CategorySearchField(
            query = state.query,
            onQueryChange = actions.onQueryChange,
            onSearch = onSearch,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coords ->
                        onSearchFieldBottomChange((coords.positionInParent().y + coords.size.height).roundToInt())
                    },
        )
        Spacer(modifier = Modifier.height(AtomSpacing.S6.dp))
        CategoryBrowser(
            state =
                CategoryBrowserState(
                    sections = state.sections,
                    searchResults = searchResults.takeIf { state.confirmedQuery.isNotBlank() },
                    selectedId = state.selectedId,
                ),
            actions =
                CategoryBrowserActions(
                    onCategorySelect = actions.onCategorySelect,
                    onDirectRegisterClick = actions.onDirectRegisterClick,
                    onRailClick = { actions.onQueryChange("") },
                ),
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview(name = "CategorySelectionBottomSheet Selected", showBackground = true, backgroundColor = 0xFF1D1B20)
@Composable
private fun CategorySelectionBottomSheetSelectedPreview() {
    OBRitTheme(dynamicColor = false) {
        val bathroom =
            listOf(
                Category(id = 1, name = "칫솔", iconUrl = "", itemCount = 0, totalSpareQuantity = 0),
                Category(id = 2, name = "샤워기 필터", iconUrl = "", itemCount = 0, totalSpareQuantity = 0),
            )
        CategorySelectionBottomSheetContent(
            sections =
                listOf(
                    CategorySection(id = 2, name = "욕실", categories = bathroom, isUserSection = false),
                    CategorySection(id = 1, name = "직접 등록", categories = emptyList(), isUserSection = true),
                ),
            initialSelectedId = 1L,
            onConfirm = {},
            onDirectRegister = {},
        )
    }
}

private const val POSITIONAL_THRESHOLD_RATIO = 0.5f
private const val SCRIM_ALPHA = 0.8f

// Figma: 디바이스 최상단부터 시트 상단까지 133dp = status bar(52dp 가정) + 81dp.
// 실제 기기 status bar 다양성에 대응하기 위해 "status bar 끝 + 81dp"로 정의.
private val CATEGORY_SHEET_TOP_GAP_FROM_STATUS_BAR = 81.dp
private val CATEGORY_SHEET_SCRIM_COLOR = Color.Black.copy(alpha = SCRIM_ALPHA)
