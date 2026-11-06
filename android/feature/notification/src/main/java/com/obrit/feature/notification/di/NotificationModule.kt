package com.obrit.feature.notification.di

import com.obrit.feature.notification.NotificationReceivedSignal
import com.obrit.feature.notification.viewmodel.NotificationSettingsViewModel
import com.obrit.feature.notification.viewmodel.NotificationViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val notificationFeatureModule =
    module {
        single { NotificationReceivedSignal() }
        viewModelOf(::NotificationViewModel)
        viewModelOf(::NotificationSettingsViewModel)
    }
