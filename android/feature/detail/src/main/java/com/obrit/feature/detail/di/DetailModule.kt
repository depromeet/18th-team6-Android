package com.obrit.feature.detail.di

import com.obrit.feature.detail.viewmodel.DetailViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val detailFeatureModule =
    module {
        viewModelOf(::DetailViewModel)
    }
