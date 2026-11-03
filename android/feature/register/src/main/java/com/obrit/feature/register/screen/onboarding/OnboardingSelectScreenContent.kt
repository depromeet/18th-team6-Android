package com.obrit.feature.register.screen.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.obrit.feature.register.screen.category.CATEGORY_GRID_COLUMNS
import com.obrit.feature.register.screen.category.CategoryCellStyle
import com.obrit.feature.register.screen.category.CategoryEmptyStateCard
import com.obrit.feature.register.screen.category.CategoryGridRow
import com.obrit.feature.register.screen.category.CategoryRailItem
import com.obrit.feature.register.screen.category.CategorySearchField
import com.obrit.feature.register.screen.category.CategorySectionHeader
import com.obrit.feature.register.screen.common.toMessage
import com.obrit.feature.register.viewmodel.CategorySection
import com.obrit.feature.register.viewmodel.OnboardingSelectUiState
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import com.obrit.obrit.shared.model.categories.Category

@Composable
internal fun OnboardingSelectScreenContent(
    state: OnboardingSelectUiState,
    action: OnboardingSelectScreenAction,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val anchor = remember { SuggestionAnchor() }
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.gray900)
                .statusBarsPadding()
                .navigationBarsPadding()
                .onGloballyPositioned { anchor.rootTopPx = it.positionInRoot().y },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            OBRitDepthTopBar(title = ONBOARDING_SELECT_TITLE, onBackClick = action.onBack)
            OnboardingSelectHeader(
                query = state.query,
                onQueryChange = action.onQueryChange,
                onSearch = { action.onSearch(state.query) },
                onSearchFieldBottomChange = { anchor.searchFieldBottomPx = it },
            )
            OnboardingSelectBody(
                state = state,
                onSectionSelect = action.onSectionSelect,
                onToggle = action.onToggle,
                onNewItemAdd = action.onNewItemAdd,
                modifier = Modifier.weight(1f),
            )
            OnboardingSelectCta(state = state, onSubmit = action.onSubmit)
        }
        OnboardingSearchSuggestions(state = state, topOffsetPx = anchor.topOffsetPx, onPick = action.onSearch)
    }
}

@Composable
private fun OnboardingSelectHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onSearchFieldBottomChange: (Float) -> Unit,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    Column {
        OBRitTitle(
            title = ONBOARDING_SELECT_HEADLINE,
            description = ONBOARDING_SELECT_DESCRIPTION,
            size = OBRitTitleSize.Large,
            type = OBRitTitleType.Default,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = AtomSpacing.S5.dp)
                    .padding(horizontal = AtomSpacing.S5.dp, vertical = AtomSpacing.S4.dp),
        )
        CategorySearchField(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = {
                onSearch()
                keyboard?.hide()
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AtomSpacing.S5.dp)
                    .onGloballyPositioned { coords ->
                        onSearchFieldBottomChange(coords.positionInRoot().y + coords.size.height)
                    },
        )
        Spacer(modifier = Modifier.height(AtomSpacing.S6.dp))
    }
}

@Composable
private fun OnboardingSelectBody(
    state: OnboardingSelectUiState,
    onSectionSelect: (Long) -> Unit,
    onToggle: (Long) -> Unit,
    onNewItemAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(start = AtomSpacing.S5.dp, end = AtomSpacing.S6.dp),
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S6.dp),
    ) {
        OnboardingSelectRail(
            state = state,
            onSectionSelect = onSectionSelect,
        )
        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            OnboardingSelectList(state = state, onToggle = onToggle, onNewItemAdd = onNewItemAdd)
            OnboardingSelectBottomFade(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
private fun OnboardingSelectRail(
    state: OnboardingSelectUiState,
    onSectionSelect: (Long) -> Unit,
) {
    val colors = LocalOBRitColor.current
    Column(
        modifier = Modifier.width(ONBOARDING_SELECT_RAIL_WIDTH),
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S1.dp),
    ) {
        state.railSections.forEach { section ->
            CategoryRailItem(
                name = section.name,
                selected = !state.isSearching && section.id == state.activeSection?.id,
                unselectedColor = colors.gray400,
                hasSelection = section.categories.any(state::isSelected),
                onClick = { onSectionSelect(section.id) },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingSelectList(
    state: OnboardingSelectUiState,
    onToggle: (Long) -> Unit,
    onNewItemAdd: () -> Unit,
) {
    val cellStyle = onboardingCellStyle()
    val sections = state.visibleSections
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = ONBOARDING_SELECT_SCROLL_FADE_HEIGHT),
    ) {
        if (state.isSearching && sections.isEmpty()) {
            item(key = "search-empty") {
                CategoryEmptyStateCard(
                    onDirectRegisterClick = onNewItemAdd.takeIf { state.newItemError == null },
                    errorMessage = state.newItemError?.toMessage(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        sections.forEach { section ->
            stickyHeader(key = "header-${section.id}") {
                CategorySectionHeader(title = section.name, count = section.categories.size)
            }
            items(
                items = section.categories.chunked(CATEGORY_GRID_COLUMNS),
                key = { row -> "row-${row.first().id}" },
            ) { row ->
                CategoryGridRow(
                    categories = row,
                    isSelected = state::isSelected,
                    onClick = { category -> onToggle(category.id) },
                    style = cellStyle,
                    modifier = Modifier.padding(bottom = AtomSpacing.S6.dp),
                )
            }
        }
    }
}

@Composable
private fun onboardingCellStyle(): CategoryCellStyle =
    CategoryCellStyle(
        imageSize = ONBOARDING_SELECT_IMAGE_SIZE,
        selectedBorderWidth = ONBOARDING_SELECT_BORDER_WIDTH,
        nameStyle = LocalOBRitTypography.current.lg.copy(fontWeight = FontWeight.Medium),
    )

@Composable
private fun OnboardingSelectBottomFade(modifier: Modifier = Modifier) {
    val colors = LocalOBRitColor.current
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(ONBOARDING_SELECT_SCROLL_FADE_HEIGHT)
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, colors.gray900)),
                ),
    )
}

@Composable
private fun OnboardingSelectCta(
    state: OnboardingSelectUiState,
    onSubmit: () -> Unit,
) {
    val typography = LocalOBRitTypography.current
    val label =
        when {
            state.isSubmitting -> ONBOARDING_SELECT_SUBMITTING_LABEL
            state.selectedCount > 0 -> ONBOARDING_SELECT_CTA_COUNT_FORMAT.format(state.selectedCount)
            else -> ONBOARDING_SELECT_CTA_LABEL
        }
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    start = AtomSpacing.S5.dp,
                    end = AtomSpacing.S5.dp,
                    bottom = ONBOARDING_SELECT_CTA_BOTTOM_PADDING,
                ),
    ) {
        OBRitLargeFilledButton(
            onClick = onSubmit,
            enabled = state.isSubmitEnabled,
            colors = OBRitButtonDefaults.positiveButtonColors(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = label,
                style = typography.xl.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

private const val ONBOARDING_SELECT_TITLE = "소모품 등록"
private const val ONBOARDING_SELECT_HEADLINE = "관리할 소모품을 골라주세요"
private const val ONBOARDING_SELECT_DESCRIPTION = "골라서 한 번에 등록해요"
private const val ONBOARDING_SELECT_CTA_LABEL = "소모품 등록하기"
private const val ONBOARDING_SELECT_CTA_COUNT_FORMAT = "%d개 등록하기"
private const val ONBOARDING_SELECT_SUBMITTING_LABEL = "등록 중..."

private val ONBOARDING_SELECT_RAIL_WIDTH = 92.dp
private val ONBOARDING_SELECT_IMAGE_SIZE = 68.dp
private val ONBOARDING_SELECT_BORDER_WIDTH = 2.dp
private val ONBOARDING_SELECT_SCROLL_FADE_HEIGHT = 40.dp
private val ONBOARDING_SELECT_CTA_BOTTOM_PADDING = 40.dp

@Preview(name = "OnboardingSelectScreen", showBackground = false)
@Composable
private fun OnboardingSelectScreenPreview() {
    OBRitTheme(dynamicColor = false) {
        OnboardingSelectScreenContent(
            state =
                OnboardingSelectUiState(
                    sections = previewSections(),
                    selectedIds = setOf(1L, 2L),
                ),
            action =
                OnboardingSelectScreenAction(
                    onToggle = {},
                    onNewItemAdd = {},
                    onSectionSelect = {},
                    onQueryChange = {},
                    onSearch = {},
                    onSubmit = {},
                    onBack = {},
                ),
        )
    }
}

private fun previewSections(): List<CategorySection> =
    listOf(
        CategorySection(
            id = 2L,
            name = "욕실",
            categories =
                listOf(
                    Category(id = 1L, name = "칫솔", iconUrl = "", itemCount = 0, totalSpareQuantity = 0),
                    Category(id = 2L, name = "샤워기 필터", iconUrl = "", itemCount = 0, totalSpareQuantity = 0),
                ),
            isUserSection = false,
        ),
        CategorySection(
            id = 3L,
            name = "주방",
            categories =
                listOf(
                    Category(id = 3L, name = "수세미", iconUrl = "", itemCount = 0, totalSpareQuantity = 0),
                ),
            isUserSection = false,
        ),
    )
