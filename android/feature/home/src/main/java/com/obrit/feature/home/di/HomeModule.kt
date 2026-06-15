package com.obrit.feature.home.di

import com.obrit.feature.home.data.ItemCatalogCache
import com.obrit.feature.home.data.SearchHistoryDataSource
import com.obrit.feature.home.viewmodel.HomeViewModel
import com.obrit.feature.home.viewmodel.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeFeatureModule =
    module {
        single { SearchHistoryDataSource(androidContext()) }
        single { ItemCatalogCache() }
        viewModelOf(::HomeViewModel)
        viewModelOf(::SearchViewModel)
    }
