package com.obrit.feature.home.di

import com.obrit.feature.home.viewmodel.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeFeatureModule =
    module {
        viewModelOf(::HomeViewModel)
    }
