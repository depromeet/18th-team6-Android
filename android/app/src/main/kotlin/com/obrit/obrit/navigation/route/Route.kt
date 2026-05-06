package com.obrit.obrit.navigation.route

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey

@Serializable
data object HomeRoute : Route

@Serializable
data class ConsumableDetailRoute(
    val consumableId: Int,
) : Route
