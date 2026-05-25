package com.obrit.feature.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.component.topbar.OBRitSearchTopBar
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.home.viewmodel.SearchUiState
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import com.obrit.android.core.designsystem.R as DesignSystemR

@Composable
internal fun SearchScreenContent(
    state: SearchUiState,
    action: SearchScreenAction,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.gray900),
    ) {
        OBRitSearchTopBar(
            query = state.query,
            onQueryChange = action.onQueryChange,
            onBackClick = action.onBackClick,
            focusRequester = focusRequester,
        )
        when {
            state.query.isNotEmpty() ->
                SearchSuggestionContent(
                    suggestions = state.suggestions,
                    query = state.query,
                    onKeywordClick = action.onKeywordClick,
                    modifier = Modifier.weight(1f),
                )
            state.recentKeywords.isNotEmpty() ->
                SearchRecentContent(
                    keywords = state.recentKeywords,
                    onKeywordClick = action.onKeywordClick,
                    onRemoveKeyword = action.onRemoveKeyword,
                    modifier = Modifier.weight(1f),
                )
            else -> SearchEmptyContent(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun SearchSuggestionContent(
    suggestions: List<String>,
    query: String,
    onKeywordClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(items = suggestions, key = { it }) { keyword ->
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { onKeywordClick(keyword) }
                        .padding(
                            horizontal = AtomSpacing.S5.dp,
                            vertical = AtomSpacing.S4.dp,
                        ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = buildHighlightedText(keyword, query, colors.green300),
                    style = typography.xl.copy(fontWeight = FontWeight.Bold),
                    color = colors.common00,
                )
            }
        }
    }
}

private fun buildHighlightedText(
    text: String,
    query: String,
    highlightColor: Color,
): AnnotatedString {
    val matchIndex = text.indexOf(query, ignoreCase = true)
    if (matchIndex == -1) return AnnotatedString(text)
    return buildAnnotatedString {
        append(text.substring(0, matchIndex))
        withStyle(SpanStyle(color = highlightColor)) {
            append(text.substring(matchIndex, matchIndex + query.length))
        }
        append(text.substring(matchIndex + query.length))
    }
}

@Composable
private fun SearchRecentContent(
    keywords: List<String>,
    onKeywordClick: (String) -> Unit,
    onRemoveKeyword: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = AtomSpacing.S4.dp)
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = AtomSpacing.S5.dp,
                        vertical = AtomSpacing.S3.dp,
                    ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "최근 검색어",
                style = typography.base.copy(fontWeight = FontWeight.Bold),
                color = colors.gray500,
            )
        }
        LazyColumn {
            items(items = keywords, key = { it }) { keyword ->
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { onKeywordClick(keyword) }
                            .padding(
                                horizontal = AtomSpacing.S5.dp,
                                vertical = AtomSpacing.S4.dp,
                            ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = keyword,
                        style = typography.xl.copy(fontWeight = FontWeight.Bold),
                        color = colors.common00,
                    )
                    Icon(
                        painter = painterResource(id = DesignSystemR.drawable.ic_topbar_close),
                        contentDescription = "삭제",
                        tint = colors.common00,
                        modifier =
                            Modifier
                                .size(AtomSpacing.S6.dp)
                                .clickable { onRemoveKeyword(keyword) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchEmptyContent(modifier: Modifier = Modifier) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize(),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "어떤 소모품을 찾아드릴까요?",
                style = typography.xl2.copy(fontWeight = FontWeight.Bold),
                color = colors.common00,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(AtomSpacing.S2.dp))
            Text(
                text = "지금 떠오르는 소모품을 검색해 보세요",
                style = typography.base,
                color = colors.gray500,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenContentEmptyPreview() {
    OBRitTheme(dynamicColor = false) {
        SearchScreenContent(
            state = SearchUiState(),
            action =
                SearchScreenAction(
                    onQueryChange = {},
                    onKeywordClick = {},
                    onRemoveKeyword = {},
                    onBackClick = {},
                ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenContentRecentPreview() {
    OBRitTheme(dynamicColor = false) {
        SearchScreenContent(
            state = SearchUiState(recentKeywords = listOf("칫솔", "치약", "샴푸")),
            action =
                SearchScreenAction(
                    onQueryChange = {},
                    onKeywordClick = {},
                    onRemoveKeyword = {},
                    onBackClick = {},
                ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenContentSuggestionPreview() {
    OBRitTheme(dynamicColor = false) {
        SearchScreenContent(
            state =
                SearchUiState(
                    query = "필터",
                    suggestions = listOf("샤워기 필터", "공기청정기 필터", "정수기 필터"),
                ),
            action =
                SearchScreenAction(
                    onQueryChange = {},
                    onKeywordClick = {},
                    onRemoveKeyword = {},
                    onBackClick = {},
                ),
        )
    }
}
