package com.obrit.obrit

import android.app.Application
import com.obrit.android.core.analytics.AnalyticsLogger
import com.obrit.android.core.analytics.OBRitLoggingEvent
import com.obrit.android.core.analytics.di.analyticsModule
import com.obrit.feature.agent.di.agentFeatureModule
import com.obrit.feature.detail.di.detailFeatureModule
import com.obrit.feature.home.di.homeFeatureModule
import com.obrit.feature.register.di.registerFeatureModule
import com.obrit.obrit.di.appModule
import com.obrit.obrit.di.initKoin
import com.obrit.obrit.notification.createObritNotificationChannel
import com.obrit.obrit.storage.FirstLaunchStorage
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext

class OBRitApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@OBRitApplication)
            modules(appModule, analyticsModule, agentFeatureModule, detailFeatureModule, homeFeatureModule, registerFeatureModule)
        }

        logFirstOpenIfNeeded()
        createObritNotificationChannel(this)
    }

    private fun logFirstOpenIfNeeded() {
        val storage = FirstLaunchStorage(this)
        if (!storage.isFirstLaunch()) return

        val anonymousUserId = storage.anonymousUserId()
        val occurredAt = System.currentTimeMillis()
        storage.markLaunched()

        val analyticsLogger = GlobalContext.get().get<AnalyticsLogger>()
        analyticsLogger.log(
            OBRitLoggingEvent.InitialLaunch(
                anonymousUserId = anonymousUserId,
                occurredAt = occurredAt,
            ),
        )
    }
}
