package com.obrit.feature.register.di

import com.obrit.feature.register.viewmodel.DirectRegisterViewModel
import com.obrit.feature.register.viewmodel.ManualRegisterViewModel
import com.obrit.feature.register.viewmodel.OnboardingDetailViewModel
import com.obrit.feature.register.viewmodel.OnboardingSelectViewModel
import com.obrit.feature.register.viewmodel.ReceiptCameraViewModel
import com.obrit.feature.register.viewmodel.ReceiptDetailViewModel
import com.obrit.feature.register.viewmodel.ReceiptResultViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val registerFeatureModule =
    module {
        viewModelOf(::ManualRegisterViewModel)
        viewModelOf(::DirectRegisterViewModel)
        viewModelOf(::OnboardingSelectViewModel)
        viewModelOf(::OnboardingDetailViewModel)
        viewModelOf(::ReceiptCameraViewModel)
        viewModelOf(::ReceiptResultViewModel)
        viewModelOf(::ReceiptDetailViewModel)
    }
