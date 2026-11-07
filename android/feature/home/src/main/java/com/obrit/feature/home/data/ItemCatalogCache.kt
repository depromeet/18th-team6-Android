package com.obrit.feature.home.data

import com.obrit.obrit.shared.model.home.HomeItemCard

class ItemCatalogCache {
    private var items: List<HomeItemCard> = emptyList()

    fun save(items: List<HomeItemCard>) {
        this.items = items
    }

    fun get(): List<HomeItemCard> = items
}
