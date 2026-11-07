package com.obrit.obrit.shared.network.config

import android.content.Context
import java.util.UUID

internal class AndroidDeviceUuidProvider(
    context: Context,
) : DeviceUuidProvider {
    private val preferences =
        context.applicationContext.getSharedPreferences(DEVICE_ID_PREFERENCES, Context.MODE_PRIVATE)

    override fun get(): String {
        val savedDeviceUuid = preferences.getString(KEY_DEVICE_UUID, null)

        if (!savedDeviceUuid.isNullOrBlank()) {
            return savedDeviceUuid
        }

        val newDeviceUuid = UUID.randomUUID().toString()
        preferences
            .edit()
            .putString(KEY_DEVICE_UUID, newDeviceUuid)
            .apply()

        return newDeviceUuid
    }

    private companion object {
        const val DEVICE_ID_PREFERENCES = "obrit_device"
        const val KEY_DEVICE_UUID = "device_uuid"
    }
}
