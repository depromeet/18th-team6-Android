package com.obrit.feature.register.screen.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.obrit.obrit.shared.model.categories.Category

@Composable
internal fun rememberCategoryDisplayList(
    categories: List<Category>,
    confirmedQuery: String,
): List<Category> =
    remember(categories, confirmedQuery) {
        if (confirmedQuery.isBlank()) {
            categories
        } else {
            categories.filter { it.name.contains(confirmedQuery, ignoreCase = true) }
        }
    }

@Composable
internal fun rememberCategorySuggestions(
    categories: List<Category>,
    query: String,
    confirmedQuery: String,
): List<Category> =
    remember(categories, query, confirmedQuery) {
        if (query.isBlank() || query == confirmedQuery) {
            emptyList()
        } else {
            categories
                .filter { it.name.contains(query, ignoreCase = true) }
                .take(MAX_SUGGESTIONS)
        }
    }

private const val MAX_SUGGESTIONS = 3
