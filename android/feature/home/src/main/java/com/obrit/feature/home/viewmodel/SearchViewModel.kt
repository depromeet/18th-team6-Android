package com.obrit.feature.home.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.feature.home.data.SearchHistoryDataSource
import com.obrit.feature.home.model.searchKeywords
import org.orbitmvi.orbit.viewmodel.container

class SearchViewModel(
    private val historyDataSource: SearchHistoryDataSource,
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
                val startsWith = searchKeywords.filter { it.startsWith(query, ignoreCase = true) }
                val contains = searchKeywords.filter {
                    !it.startsWith(query, ignoreCase = true) && it.contains(query, ignoreCase = true)
                }
                startsWith + contains
            }
        reduce { state.copy(query = query, suggestions = suggestions) }
    }

    fun onKeywordClick(keyword: String) = intent {
        historyDataSource.addKeyword(keyword)
        reduce {
            state.copy(
                query = keyword,
                recentKeywords = historyDataSource.getHistory(),
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

    fun onBackClick() = intent {
        postSideEffect(SearchSideEffect.NavigateBack)
    }
}

@Immutable
data class SearchUiState(
    val query: String = "",
    val recentKeywords: List<String> = emptyList(),
    val suggestions: List<String> = emptyList(),
)

sealed interface SearchSideEffect {
    data object NavigateBack : SearchSideEffect
}
