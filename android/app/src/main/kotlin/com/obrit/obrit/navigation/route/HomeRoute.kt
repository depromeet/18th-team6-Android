package com.obrit.obrit.navigation.route

import kotlinx.serialization.Serializable

@Serializable
sealed interface HomeRoute : Route {
    @Serializable
    data object Home : HomeRoute

    @Serializable
    data object ConsumableList : HomeRoute
}
