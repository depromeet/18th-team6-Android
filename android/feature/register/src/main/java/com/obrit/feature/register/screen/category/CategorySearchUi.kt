package com.obrit.feature.register.screen.category

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

// OBRitTopBar.kt의 TopBarSearchInput과 동일 디자인 (디테일 변경 시 양쪽 함께 수정).
// Figma SSOT에 정식 컴포넌트 등록되면 designsystem으로 추출 예정.
@Composable
internal fun CategorySearchField(
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
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier =
            modifier
                .clip(RoundedCornerShape(AtomRadius.Middle.dp))
                .border(
                    width = CATEGORY_SEARCH_BORDER_WIDTH,
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
        CategorySearchFieldRow(
            isEmpty = query.isEmpty(),
            placeholderStyle = textStyle.copy(color = colors.gray700),
            innerTextField = innerTextField,
            onSearch = onSearch,
            iconTint = colors.common00,
        )
    }
}

@Composable
private fun CategorySearchFieldRow(
    isEmpty: Boolean,
    placeholderStyle: androidx.compose.ui.text.TextStyle,
    innerTextField: @Composable () -> Unit,
    onSearch: () -> Unit,
    iconTint: Color,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
    ) {
        Box(modifier = Modifier.weight(1f)) {
            if (isEmpty) {
                Text(
                    text = CATEGORY_SHEET_SEARCH_PLACEHOLDER,
                    style = placeholderStyle,
                    maxLines = 1,
                )
            }
            innerTextField()
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_topbar_search),
            contentDescription = null,
            tint = iconTint,
            modifier =
                Modifier
                    .size(AtomSpacing.S6.dp)
                    .clickable(onClick = onSearch),
        )
    }
}

@Composable
internal fun BoxScope.CategorySearchSuggestionsOverlay(
    suggestions: List<String>,
    query: String,
    onPick: (String) -> Unit,
    topOffsetPx: Int,
) {
    val density = LocalDensity.current
    val gapPx = with(density) { CATEGORY_SUGGESTIONS_GAP.roundToPx() }
    CategorySearchSuggestions(
        suggestions = suggestions,
        query = query,
        onPick = onPick,
        modifier =
            Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .offset { IntOffset(0, topOffsetPx + gapPx) },
    )
}

@Composable
private fun CategorySearchSuggestions(
    suggestions: List<String>,
    query: String,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(AtomRadius.Small.dp))
                .border(
                    width = CATEGORY_SUGGESTIONS_BORDER_WIDTH,
                    color = colors.gray700,
                    shape = RoundedCornerShape(AtomRadius.Small.dp),
                ).background(colors.gray800),
    ) {
        suggestions.forEach { item ->
            CategorySuggestionItem(
                item = item,
                query = query,
                highlightColor = colors.green300,
                defaultColor = colors.common00,
                onClick = { onPick(item) },
            )
        }
    }
}

@Composable
private fun CategorySuggestionItem(
    item: String,
    query: String,
    highlightColor: Color,
    defaultColor: Color,
    onClick: () -> Unit,
) {
    val typography = LocalOBRitTypography.current
    Text(
        text =
            buildHighlightedSuggestion(
                text = item,
                query = query,
                highlightColor = highlightColor,
                defaultColor = defaultColor,
            ),
        style = typography.xl.copy(fontWeight = FontWeight.Medium, color = defaultColor),
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = AtomSpacing.S5.dp, vertical = AtomSpacing.S4.dp),
        maxLines = 1,
    )
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

private const val CATEGORY_SHEET_SEARCH_PLACEHOLDER = "원하시는 소모품을 검색해보세요"

private val CATEGORY_SEARCH_BORDER_WIDTH = 1.4f.dp
private val CATEGORY_SUGGESTIONS_BORDER_WIDTH = 1.dp
private val CATEGORY_SUGGESTIONS_GAP = AtomSpacing.S1_5.dp
