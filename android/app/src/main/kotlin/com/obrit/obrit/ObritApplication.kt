package com.obrit.obrit

import android.app.Application
import com.obrit.obrit.di.initKoin
import com.obrit.obrit.shared.network.config.NetworkConfiguration

class ObritApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin(
            configuration = NetworkConfiguration(
                baseUrl = "https://example.com/",
                enableLogging = false,
            ),
        )
    }
}
