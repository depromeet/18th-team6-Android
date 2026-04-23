package com.obrit.android.core.ui.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

fun <T> ViewModel.vmAsync(block: suspend CoroutineScope.() -> T): Deferred<T> {
    return viewModelScope.async(block = block)
}
