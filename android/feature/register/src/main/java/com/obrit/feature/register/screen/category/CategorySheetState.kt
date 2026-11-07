package com.obrit.feature.register.screen.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.obrit.feature.register.viewmodel.CategorySection
import com.obrit.obrit.shared.model.categories.Category

@Composable
internal fun rememberCategorySearchResults(
    sections: List<CategorySection>,
    query: String,
): List<Category> =
    remember(sections, query) {
        sections
            .flatMap { section -> section.categories }
            .filter { category -> category.name.contains(query.trim(), ignoreCase = true) }
    }

@Composable
internal fun rememberCategorySuggestions(
    sections: List<CategorySection>,
    query: String,
    confirmedQuery: String,
): List<Category> =
    remember(sections, query, confirmedQuery) {
        if (query.isBlank() || query == confirmedQuery) {
            emptyList()
        } else {
            sections
                .flatMap { section -> section.categories }
                .filter { it.name.contains(query, ignoreCase = true) }
                .take(MAX_SUGGESTIONS)
        }
    }

private const val MAX_SUGGESTIONS = 3
