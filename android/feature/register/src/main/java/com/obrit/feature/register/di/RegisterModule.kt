package com.obrit.feature.register.di

import com.obrit.feature.register.viewmodel.ManualRegisterViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val registerFeatureModule =
    module {
        viewModelOf(::ManualRegisterViewModel)
    }
