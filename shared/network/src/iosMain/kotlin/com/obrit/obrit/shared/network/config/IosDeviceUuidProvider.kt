package com.obrit.obrit.shared.network.config

import platform.Foundation.NSUUID
import platform.Foundation.NSUserDefaults

internal class IosDeviceUuidProvider : DeviceUuidProvider {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override fun get(): String {
        val savedDeviceUuid = userDefaults.stringForKey(KEY_DEVICE_UUID)

        if (!savedDeviceUuid.isNullOrBlank()) {
            return savedDeviceUuid
        }

        val newDeviceUuid = NSUUID().UUIDString
        userDefaults.setObject(newDeviceUuid, forKey = KEY_DEVICE_UUID)

        return newDeviceUuid
    }

    private companion object {
        const val KEY_DEVICE_UUID = "device_uuid"
    }
}
