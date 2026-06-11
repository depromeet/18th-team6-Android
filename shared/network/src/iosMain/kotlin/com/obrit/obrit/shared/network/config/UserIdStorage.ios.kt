package com.obrit.obrit.shared.network.config

import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

internal class IosUserIdStorage : UserIdStorage {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override fun load(): Long? {
        if (userDefaults.objectForKey(KEY_USER_ID) == null) return null
        return userDefaults.integerForKey(KEY_USER_ID).takeIf { it != UNSET }
    }

    override fun save(userId: Long) {
        userDefaults.setInteger(userId, forKey = KEY_USER_ID)
    }

    private companion object {
        const val KEY_USER_ID = "userId"
        const val UNSET = -1L
    }
}

internal actual val userIdStorageModule: Module =
    module {
        single<UserIdStorage> { IosUserIdStorage() }
    }
