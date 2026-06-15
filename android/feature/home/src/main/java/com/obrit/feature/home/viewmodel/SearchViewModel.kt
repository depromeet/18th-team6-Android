package com.obrit.feature.home.viewmodel

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

    fun onScreenOpen() =
        intent {
            val keywords = historyDataSource.getHistory()
            reduce { SearchUiState(recentKeywords = keywords) }
        }

    fun onQueryChange(query: String) =
        intent {
            val suggestions =
                if (query.isBlank()) {
                    emptyList()
                } else {
                    val catalog = historyDataSource.getItemCatalog()
                    val startsWith = catalog.filter { it.startsWith(query, ignoreCase = true) }
                    val contains =
                        catalog.filter {
                            !it.startsWith(query, ignoreCase = true) && it.contains(query, ignoreCase = true)
                        }
                    startsWith + contains
                }
            reduce { state.copy(query = query, suggestions = suggestions, searchResults = null) }
        }

    fun onKeywordClick(keyword: String) =
        intent {
            historyDataSource.addKeyword(keyword)
            val results = itemCatalogCache.get().filter { it.name.contains(keyword, ignoreCase = true) }
            reduce {
                state.copy(
                    query = keyword,
                    recentKeywords = historyDataSource.getHistory(),
                    searchResults = results,
                )
            }
        }

    fun onRemoveKeyword(keyword: String) =
        intent {
            historyDataSource.removeKeyword(keyword)
            reduce { state.copy(recentKeywords = historyDataSource.getHistory()) }
        }

    fun onClearHistory() =
        intent {
            historyDataSource.clearHistory()
            reduce { state.copy(recentKeywords = emptyList()) }
        }

    fun onSearch() =
        intent {
            val query = state.query
            if (query.isBlank()) return@intent
            historyDataSource.addKeyword(query)
            val results = itemCatalogCache.get().filter { it.name.contains(query, ignoreCase = true) }
            reduce { state.copy(searchResults = results, recentKeywords = historyDataSource.getHistory()) }
        }

    fun onBackClick() =
        intent {
            postSideEffect(SearchSideEffect.NavigateBack)
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
