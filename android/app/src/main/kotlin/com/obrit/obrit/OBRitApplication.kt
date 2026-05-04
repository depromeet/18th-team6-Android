package com.obrit.obrit

import android.app.Application
import com.obrit.feature.agent.di.agentFeatureModule
import com.obrit.obrit.di.initKoin
import org.koin.android.ext.koin.androidContext

class OBRitApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@OBRitApplication)
            modules(agentFeatureModule)
        }
    }
}
