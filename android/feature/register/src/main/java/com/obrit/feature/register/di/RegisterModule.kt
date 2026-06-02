package com.obrit.feature.register.di

import com.obrit.feature.register.viewmodel.DirectRegisterViewModel
import com.obrit.feature.register.viewmodel.ManualRegisterViewModel
import com.obrit.feature.register.viewmodel.OnboardingDetailViewModel
import com.obrit.feature.register.viewmodel.OnboardingSelectViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val registerFeatureModule =
    module {
        viewModelOf(::ManualRegisterViewModel)
        viewModelOf(::DirectRegisterViewModel)
        viewModelOf(::OnboardingSelectViewModel)
        viewModelOf(::OnboardingDetailViewModel)
    }
