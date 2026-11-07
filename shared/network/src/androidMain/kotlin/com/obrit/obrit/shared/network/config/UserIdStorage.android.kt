package com.obrit.obrit.shared.network.config

import android.content.Context
import org.koin.core.module.Module
import org.koin.dsl.module

internal class AndroidUserIdStorage(
    context: Context,
) : UserIdStorage {
    private val preferences =
        context.applicationContext.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE)

    override fun load(): Long? {
        val saved = preferences.getLong(KEY_USER_ID, UNSET)
        return if (saved == UNSET) null else saved
    }

    override fun save(userId: Long) {
        preferences.edit().putLong(KEY_USER_ID, userId).apply()
    }

    private companion object {
        const val USER_PREFERENCES = "orbitUser"
        const val KEY_USER_ID = "userId"
        const val UNSET = -1L
    }
}

internal actual val userIdStorageModule: Module =
    module {
        single<UserIdStorage> { AndroidUserIdStorage(get<Context>()) }
    }
