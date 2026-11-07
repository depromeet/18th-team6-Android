package com.obrit.obrit.shared.network.config

import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSBundle
import platform.Foundation.NSNumber
import platform.Foundation.NSUserDefaults

internal class IosUserIdStorage : UserIdStorage {
    private val userDefaults = NSUserDefaults.standardUserDefaults
    private val sharedDefaults =
        (NSBundle.mainBundle.objectForInfoDictionaryKey("OBRitAppGroupIdentifier") as? String)
            ?.takeIf { it.isNotBlank() && !it.startsWith("$(") }
            ?.let { NSUserDefaults(suiteName = it) }
    private val isWidget =
        (NSBundle.mainBundle.objectForInfoDictionaryKey("OBRitWidgetExtension") as? NSNumber)?.boolValue == true

    override val supportsInMemoryCache: Boolean get() = !isWidget

    override fun load(): Long? {
        if (isWidget) {
            // Extensions must never register a new user when the app has not shared its identity yet.
            return checkNotNull(sharedDefaults?.savedUserId()) { "Open the OBRit app before using the widget." }
        }
        val userId = userDefaults.savedUserId() ?: sharedDefaults?.savedUserId()
        userId?.let(::save)
        return userId
    }

    override fun save(userId: Long) {
        if (!isWidget) userDefaults.setInteger(userId, forKey = KEY_USER_ID)
        sharedDefaults?.setInteger(userId, forKey = KEY_USER_ID)
    }

    private fun NSUserDefaults.savedUserId(): Long? {
        if (objectForKey(KEY_USER_ID) == null) return null
        return integerForKey(KEY_USER_ID).takeIf { it > 0 && it != UNSET }
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
