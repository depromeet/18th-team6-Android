package com.obrit.obrit.di

import android.content.Context
import com.obrit.obrit.storage.OnboardingStorage
import org.koin.dsl.module

val appModule =
    module {
        single { OnboardingStorage(get<Context>()) }
    }
