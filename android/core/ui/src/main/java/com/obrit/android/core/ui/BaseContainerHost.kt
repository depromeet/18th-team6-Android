package com.obrit.android.core.ui

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.IntentContext
import org.orbitmvi.orbit.syntax.Syntax

@OptIn(OrbitExperimental::class)
abstract class BaseContainerHost<STATE : Any, SIDE_EFFECT : Any> :
    ViewModel(),
    ContainerHost<STATE, SIDE_EFFECT> {
    suspend inline fun <reified S : STATE> Syntax<STATE, SIDE_EFFECT>.reduceOn(noinline reducer: IntentContext<S>.() -> STATE) {
        runOn<S> {
            reduce(reducer)
        }
    }
}
