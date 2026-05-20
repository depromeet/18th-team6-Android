package com.obrit.android.core.ui.extensions

import com.obrit.android.core.ui.BaseContainerHost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

suspend fun <T> BaseContainerHost<*, *>.vmAsync(block: suspend CoroutineScope.() -> T): Deferred<T> =
    coroutineScope {
        async(block = block)
    }
