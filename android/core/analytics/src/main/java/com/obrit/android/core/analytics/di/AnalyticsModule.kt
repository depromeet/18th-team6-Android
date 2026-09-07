package com.obrit.android.core.analytics.di

import com.google.firebase.analytics.FirebaseAnalytics
import com.obrit.android.core.analytics.AnalyticsLogger
import com.obrit.android.core.analytics.FirebaseAnalyticsLogger
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val analyticsModule =
    module {
        single<FirebaseAnalytics> { FirebaseAnalytics.getInstance(androidContext()) }
        single<AnalyticsLogger> { FirebaseAnalyticsLogger(get()) }
    }
