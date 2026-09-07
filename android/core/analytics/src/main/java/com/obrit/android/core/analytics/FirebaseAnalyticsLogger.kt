package com.obrit.android.core.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

internal class FirebaseAnalyticsLogger(
    private val analytics: FirebaseAnalytics,
) : AnalyticsLogger {
    override fun log(event: OBRitLoggingEvent) {
        val bundle = Bundle()
        event.params.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putLong(key, value.toLong())
                is Long -> bundle.putLong(key, value)
                is Double -> bundle.putDouble(key, value)
                is Float -> bundle.putDouble(key, value.toDouble())
                else -> Unit
            }
        }
        analytics.logEvent(event.name, bundle)
    }
}
