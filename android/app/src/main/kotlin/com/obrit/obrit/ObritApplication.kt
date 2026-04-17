package com.obrit.obrit

import android.app.Application
import com.obrit.feature.login.loginFeatureModule
import com.obrit.obrit.di.initKoin
import org.koin.android.ext.koin.androidContext

class ObritApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@ObritApplication)
            modules(loginFeatureModule)
        }
    }
}
