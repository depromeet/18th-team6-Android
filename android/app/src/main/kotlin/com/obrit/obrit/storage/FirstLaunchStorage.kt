package com.obrit.obrit.storage

import android.content.Context
import java.util.UUID

class FirstLaunchStorage(
    context: Context,
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val devicePrefs = context.getSharedPreferences(DEVICE_PREFS_NAME, Context.MODE_PRIVATE)

    fun isFirstLaunch(): Boolean = prefs.getBoolean(KEY_FIRST_LAUNCH, true)

    fun anonymousUserId(): String {
        val stored = devicePrefs.getString(KEY_DEVICE_UUID, null)
        if (stored != null) return stored
        val generated = UUID.randomUUID().toString()
        devicePrefs.edit().putString(KEY_DEVICE_UUID, generated).apply()
        return generated
    }

    fun markLaunched() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    private companion object {
        const val PREFS_NAME = "first_launch"
        const val KEY_FIRST_LAUNCH = "is_first_launch"
        const val DEVICE_PREFS_NAME = "obrit_device"
        const val KEY_DEVICE_UUID = "device_uuid"
    }
}
