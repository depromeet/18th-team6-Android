package com.obrit.obrit.storage

import android.content.Context

class OnboardingStorage(
    context: Context,
) {
    private val prefs =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isCompleted(): Boolean = prefs.getBoolean(KEY_COMPLETED, false)

    fun setCompleted() {
        prefs.edit().putBoolean(KEY_COMPLETED, true).apply()
    }

    private companion object {
        const val PREFS_NAME = "onboarding"
        const val KEY_COMPLETED = "completed"
    }
}
