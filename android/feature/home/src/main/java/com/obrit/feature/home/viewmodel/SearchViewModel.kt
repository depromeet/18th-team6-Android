package com.obrit.feature.home.viewmodel

import android.util.Log
import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.feature.home.data.ItemCatalogCache
import com.obrit.feature.home.data.SearchHistoryDataSource
import com.obrit.obrit.shared.model.home.HomeItemCard
import org.orbitmvi.orbit.viewmodel.container

class SearchViewModel(
    private val historyDataSource: SearchHistoryDataSource,
    private val itemCatalogCache: ItemCatalogCache,
) : BaseContainerHost<SearchUiState, SearchSideEffect>() {
    override val container = container<SearchUiState, SearchSideEffect>(SearchUiState())

    fun onScreenOpen() = intent {
        val keywords = historyDataSource.getHistory()
        reduce { SearchUiState(recentKeywords = keywords) }
    }

    fun onQueryChange(query: String) = intent {
        val suggestions =
            if (query.isBlank()) {
                emptyList()
            } else {
                val catalog = historyDataSource.getItemCatalog()
                Log.d(TAG, "onQueryChange: catalog size=${catalog.size}, query='$query'")
                val startsWith = catalog.filter { it.startsWith(query, ignoreCase = true) }
                val contains = catalog.filter {
                    !it.startsWith(query, ignoreCase = true) && it.contains(query, ignoreCase = true)
                }
                startsWith + contains
            }
        Log.d(TAG, "onQueryChange: suggestions=${suggestions.size}")
        reduce { state.copy(query = query, suggestions = suggestions, searchResults = null) }
    }

    fun onKeywordClick(keyword: String) = intent {
        historyDataSource.addKeyword(keyword)
        val results = itemCatalogCache.get().filter { it.name.contains(keyword, ignoreCase = true) }
        Log.d(TAG, "onKeywordClick: keyword='$keyword', results=${results.size}")
        reduce {
            state.copy(
                query = keyword,
                recentKeywords = historyDataSource.getHistory(),
                searchResults = results,
            )
        }
    }

    fun onRemoveKeyword(keyword: String) = intent {
        historyDataSource.removeKeyword(keyword)
        reduce { state.copy(recentKeywords = historyDataSource.getHistory()) }
    }

    fun onClearHistory() = intent {
        historyDataSource.clearHistory()
        reduce { state.copy(recentKeywords = emptyList()) }
    }

    fun onSearch() = intent {
        val query = state.query
        if (query.isBlank()) return@intent
        historyDataSource.addKeyword(query)
        val cache = itemCatalogCache.get()
        Log.d(TAG, "onSearch: query='$query', cache size=${cache.size}")
        val results = cache.filter { it.name.contains(query, ignoreCase = true) }
        Log.d(TAG, "onSearch: results=${results.size}")
        reduce { state.copy(searchResults = results, recentKeywords = historyDataSource.getHistory()) }
    }

    fun onBackClick() = intent {
        postSideEffect(SearchSideEffect.NavigateBack)
    }

    companion object {
        private const val TAG = "SearchViewModel"
    }
}

@Immutable
data class SearchUiState(
    val query: String = "",
    val recentKeywords: List<String> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val searchResults: List<HomeItemCard>? = null,
)

sealed interface SearchSideEffect {
    data object NavigateBack : SearchSideEffect
}
