package com.obrit.feature.register.screen.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

internal data class CategoryListAreaState(
    val isEmptyResult: Boolean,
    val items: List<String>,
    val selectedName: String,
)

@Composable
internal fun CategoryListArea(
    state: CategoryListAreaState,
    onCategorySelect: (String) -> Unit,
    onDirectRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    Box(modifier = modifier.fillMaxWidth()) {
        if (state.isEmptyResult) {
            CategoryEmptyStateCard(
                onDirectRegisterClick = onDirectRegisterClick,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            CategoryList(
                items = state.items,
                selectedName = state.selectedName,
                onCategorySelect = onCategorySelect,
            )
            CategoryListBottomFade(color = colors.gray900)
        }
    }
}

@Composable
private fun CategoryList(
    items: List<String>,
    selectedName: String,
    onCategorySelect: (String) -> Unit,
) {
    val listState = rememberLazyListState()
    val listFlingGuard = remember(listState) { listFlingGuardConnection(listState) }
    LazyColumn(
        state = listState,
        modifier =
            Modifier
                .fillMaxSize()
                .nestedScroll(listFlingGuard),
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
        contentPadding = PaddingValues(bottom = CATEGORY_LIST_BOTTOM_FADE),
    ) {
        items(items, key = { it }) { name ->
            CategoryListItem(
                name = name,
                addedCount = 0,
                selected = name == selectedName,
                onClick = { onCategorySelect(name) },
            )
        }
    }
}

// M3 ModalBottomSheet의 AnchoredDraggable이 LazyColumn의 잔여 fling 속도를 받아 진동하는 것을 차단.
// LazyColumn이 스크롤 가능한 동안엔 fling을 전부 소비해 sheet의 nestedScroll로 전파하지 않음.
private fun listFlingGuardConnection(listState: LazyListState): NestedScrollConnection =
    object : NestedScrollConnection {
        override fun onPreScroll(
            available: Offset,
            source: NestedScrollSource,
        ): Offset = Offset.Zero

        override suspend fun onPreFling(available: Velocity): Velocity =
            if (listState.canScrollForward || listState.canScrollBackward) available else Velocity.Zero
    }

@Composable
private fun BoxScope.CategoryListBottomFade(color: Color) {
    Box(
        modifier =
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(CATEGORY_LIST_BOTTOM_FADE)
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, color)),
                ),
    )
}

// 리스트 하단 fade gradient 높이
private val CATEGORY_LIST_BOTTOM_FADE = 40.dp
