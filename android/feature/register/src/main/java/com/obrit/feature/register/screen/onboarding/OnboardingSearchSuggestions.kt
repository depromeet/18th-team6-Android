package com.obrit.feature.register.screen.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.obrit.feature.register.screen.category.CategorySearchSuggestionsOverlay
import com.obrit.feature.register.screen.category.rememberCategorySuggestions
import com.obrit.feature.register.viewmodel.OnboardingSelectUiState
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import kotlin.math.roundToInt

// 추천 목록을 검색창 바로 아래에 띄우기 위해 화면 상단과 검색창 하단의 위치를 기억한다.
@Stable
internal class SuggestionAnchor {
    var rootTopPx by mutableFloatStateOf(0f)
    var searchFieldBottomPx by mutableFloatStateOf(0f)

    val topOffsetPx: Int
        get() = (searchFieldBottomPx - rootTopPx).roundToInt()
}

@Composable
internal fun OnboardingSearchSuggestions(
    state: OnboardingSelectUiState,
    topOffsetPx: Int,
    onPick: (String) -> Unit,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val suggestions = rememberCategorySuggestions(state.sections, state.query, state.confirmedQuery)
    if (suggestions.isEmpty()) return
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = AtomSpacing.S5.dp),
    ) {
        CategorySearchSuggestionsOverlay(
            suggestions = suggestions.map { it.name },
            query = state.query,
            onPick = { value ->
                onPick(value)
                keyboard?.hide()
            },
            topOffsetPx = topOffsetPx,
        )
    }
}
