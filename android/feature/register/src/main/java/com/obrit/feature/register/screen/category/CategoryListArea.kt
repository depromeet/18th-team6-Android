package com.obrit.feature.register.screen.category

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.collectIsDraggedAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.feature.register.viewmodel.CategorySection
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import com.obrit.obrit.shared.model.categories.Category
import kotlinx.coroutines.launch

internal data class CategoryBrowserState(
    val sections: List<CategorySection>,
    val searchResults: List<Category>?,
    val selectedId: Long?,
)

internal data class CategoryBrowserActions(
    val onCategorySelect: (Category) -> Unit,
    val onDirectRegisterClick: () -> Unit,
    val onRailClick: () -> Unit,
)

@Composable
internal fun CategoryBrowser(
    state: CategoryBrowserState,
    actions: CategoryBrowserActions,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val headerIndices = remember(state.sections) { categorySectionHeaderIndices(state.sections) }
    val selection = rememberCategoryRailSelection(listState, state, headerIndices)
    val isSearching = state.searchResults != null
    Row(modifier = modifier.fillMaxWidth()) {
        CategoryRail(
            sections = state.sections,
            selectedIndex = selection.index.takeUnless { isSearching },
            onSelect = { index ->
                actions.onRailClick()
                selection.pinnedIndex = index
                scope.launch {
                    // 검색 중이면 전체 목록이 다시 그려진 뒤에 스크롤해야 한다.
                    if (isSearching) withFrameNanos {}
                    listState.animateScrollToItem(headerIndices[index])
                }
            },
        )
        Spacer(modifier = Modifier.width(AtomSpacing.S4.dp))
        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            if (state.searchResults == null) {
                CategorySectionList(listState = listState, state = state, actions = actions)
            } else {
                CategorySearchResultList(
                    results = state.searchResults,
                    selectedId = state.selectedId,
                    actions = actions,
                )
            }
        }
    }
}

// 마지막 섹션은 맨 위까지 올라오지 못하므로, 칩으로 이동한 동안에는 누른 칩을 그대로 선택해 둔다.
@Stable
private class CategoryRailSelection(
    private val visibleIndex: State<Int>,
) {
    var pinnedIndex by mutableStateOf<Int?>(null)

    val index: Int
        get() = pinnedIndex ?: visibleIndex.value
}

@Composable
private fun rememberCategoryRailSelection(
    listState: LazyListState,
    state: CategoryBrowserState,
    headerIndices: List<Int>,
): CategoryRailSelection {
    val visibleIndex =
        remember(listState, headerIndices) {
            derivedStateOf { listState.visibleSectionIndex(headerIndices) }
        }
    val selection = remember(visibleIndex) { CategoryRailSelection(visibleIndex) }
    val isDragged by listState.interactionSource.collectIsDraggedAsState()
    LaunchedEffect(isDragged) {
        if (isDragged) selection.pinnedIndex = null
    }
    LaunchedEffect(headerIndices) {
        val index = state.sections.indexOfFirst { section -> section.categories.any { it.id == state.selectedId } }
        if (index >= 0) {
            selection.pinnedIndex = index
            listState.scrollToItem(headerIndices[index])
        }
    }
    return selection
}

@Composable
private fun CategoryRail(
    sections: List<CategorySection>,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit,
) {
    val colors = LocalOBRitColor.current
    Column(
        modifier =
            Modifier
                .width(CATEGORY_RAIL_WIDTH)
                .fillMaxHeight()
                .drawBehind {
                    drawLine(
                        color = colors.gray850,
                        start = Offset(size.width, 0f),
                        end = Offset(size.width, size.height),
                        strokeWidth = CATEGORY_RAIL_DIVIDER_WIDTH.toPx(),
                    )
                }.padding(end = AtomSpacing.S3.dp),
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S1.dp),
    ) {
        sections.forEachIndexed { index, section ->
            CategoryRailItem(
                name = section.name,
                selected = index == selectedIndex,
                unselectedColor = colors.gray500,
                onClick = { onSelect(index) },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategorySectionList(
    listState: LazyListState,
    state: CategoryBrowserState,
    actions: CategoryBrowserActions,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = CATEGORY_LIST_BOTTOM_PADDING),
    ) {
        state.sections.forEachIndexed { index, section ->
            stickyHeader(key = "header-${section.id}") {
                CategorySectionHeader(
                    title = section.name,
                    count = section.categories.size,
                    onAddClick = actions.onDirectRegisterClick.takeIf { section.isUserSection },
                )
            }
            categoryGridRows(
                categories = section.categories,
                selectedId = state.selectedId,
                onCategorySelect = actions.onCategorySelect,
            )
            if (index != state.sections.lastIndex) {
                item(key = "gap-${section.id}") {
                    Spacer(modifier = Modifier.height(CATEGORY_SECTION_GAP))
                }
            }
        }
    }
}

@Composable
private fun CategorySearchResultList(
    results: List<Category>,
    selectedId: Long?,
    actions: CategoryBrowserActions,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = CATEGORY_LIST_BOTTOM_PADDING),
    ) {
        item(key = "search-header") {
            CategorySectionHeader(title = CATEGORY_SEARCH_RESULT_TITLE, count = results.size)
        }
        if (results.isEmpty()) {
            item(key = "search-empty") {
                CategoryEmptyStateCard(
                    onDirectRegisterClick = actions.onDirectRegisterClick,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        } else {
            categoryGridRows(
                categories = results,
                selectedId = selectedId,
                onCategorySelect = actions.onCategorySelect,
            )
        }
    }
}

private fun LazyListScope.categoryGridRows(
    categories: List<Category>,
    selectedId: Long?,
    onCategorySelect: (Category) -> Unit,
) {
    items(
        items = categories.chunked(CATEGORY_GRID_COLUMNS),
        key = { row -> "row-${row.first().id}" },
    ) { row ->
        val typography = LocalOBRitTypography.current
        CategoryGridRow(
            categories = row,
            isSelected = { category -> category.id == selectedId },
            onClick = onCategorySelect,
            style =
                CategoryCellStyle(
                    imageSize = CATEGORY_CELL_IMAGE_SIZE,
                    selectedBorderWidth = CATEGORY_CELL_SELECTED_BORDER_WIDTH,
                    nameStyle = typography.base.copy(fontWeight = FontWeight.Medium),
                ),
            modifier = Modifier.padding(bottom = AtomSpacing.S6.dp),
        )
    }
}

private fun LazyListState.visibleSectionIndex(headerIndices: List<Int>): Int =
    if (!canScrollForward && canScrollBackward) {
        headerIndices.lastIndex
    } else {
        headerIndices.indexOfLast { index -> index <= firstVisibleItemIndex }.coerceAtLeast(0)
    }

// LazyColumn 항목 순서: 섹션마다 헤더, 소모품 행들, 마지막을 뺀 섹션 간격.
private fun categorySectionHeaderIndices(sections: List<CategorySection>): List<Int> =
    sections
        .runningFold(0) { headerIndex, section ->
            val rowCount = section.categories.chunked(CATEGORY_GRID_COLUMNS).size
            headerIndex + CATEGORY_HEADER_ITEM_COUNT + rowCount + CATEGORY_GAP_ITEM_COUNT
        }.dropLast(1)

private const val CATEGORY_SEARCH_RESULT_TITLE = "검색 결과"
private const val CATEGORY_HEADER_ITEM_COUNT = 1
private const val CATEGORY_GAP_ITEM_COUNT = 1

private val CATEGORY_RAIL_WIDTH = 104.dp
private val CATEGORY_RAIL_DIVIDER_WIDTH = 1.dp
private val CATEGORY_SECTION_GAP = 60.dp
private val CATEGORY_CELL_IMAGE_SIZE = 64.dp
private val CATEGORY_CELL_SELECTED_BORDER_WIDTH = 1.4.dp

// 하단 CTA에 가려지지 않도록 마지막 행 아래에 남기는 여백.
private val CATEGORY_LIST_BOTTOM_PADDING = 100.dp
