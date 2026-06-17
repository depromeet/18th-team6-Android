package com.obrit.feature.home.data

import android.content.Context
import com.obrit.feature.home.model.MAX_SEARCH_HISTORY

class SearchHistoryDataSource(
    private val context: Context,
) {
    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getHistory(): List<String> {
        val raw = prefs.getString(KEY_KEYWORDS, null) ?: return emptyList()
        return raw.split(SEPARATOR).filter { it.isNotEmpty() }
    }

    fun addKeyword(keyword: String) {
        val current = getHistory().toMutableList()
        current.remove(keyword)
        current.add(0, keyword)
        if (current.size > MAX_SEARCH_HISTORY) current.removeAt(current.size - 1)
        prefs.edit().putString(KEY_KEYWORDS, current.joinToString(SEPARATOR)).apply()
    }

    fun removeKeyword(keyword: String) {
        val updated = getHistory().toMutableList().also { it.remove(keyword) }
        prefs.edit().putString(KEY_KEYWORDS, updated.joinToString(SEPARATOR)).apply()
    }

    fun clearHistory() {
        prefs.edit().remove(KEY_KEYWORDS).apply()
    }

    fun saveItemCatalog(names: List<String>) {
        prefs.edit().putString(KEY_ITEM_CATALOG, names.joinToString(SEPARATOR)).apply()
    }

    fun getItemCatalog(): List<String> {
        val raw = prefs.getString(KEY_ITEM_CATALOG, null) ?: return emptyList()
        return raw.split(SEPARATOR).filter { it.isNotEmpty() }
    }

    private companion object {
        const val PREFS_NAME = "search_history"
        const val KEY_KEYWORDS = "keywords"
        const val KEY_ITEM_CATALOG = "item_catalog"
        const val SEPARATOR = "\n"
    }
}
