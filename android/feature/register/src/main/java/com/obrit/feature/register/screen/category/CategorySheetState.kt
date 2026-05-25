package com.obrit.feature.register.screen.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
internal fun rememberCategoryDisplayList(confirmedQuery: String): List<String> =
    remember(confirmedQuery) {
        if (confirmedQuery.isBlank()) {
            MOCK_CATEGORIES
        } else {
            MOCK_CATEGORIES.filter { it.contains(confirmedQuery, ignoreCase = true) }
        }
    }

@Composable
internal fun rememberCategorySuggestions(
    query: String,
    confirmedQuery: String,
): List<String> =
    remember(query, confirmedQuery) {
        if (query.isBlank() || query == confirmedQuery) {
            emptyList()
        } else {
            MOCK_CATEGORIES
                .filter { it.contains(query, ignoreCase = true) }
                .take(MAX_SUGGESTIONS)
        }
    }

private const val MAX_SUGGESTIONS = 3

internal val MOCK_CATEGORIES =
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
