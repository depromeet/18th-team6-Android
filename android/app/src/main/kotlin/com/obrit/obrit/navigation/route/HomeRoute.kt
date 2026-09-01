package com.obrit.obrit.navigation.route

import kotlinx.serialization.Serializable

@Serializable
sealed interface HomeRoute : Route {
    @Serializable
    data object Home : HomeRoute

    @Serializable
    data object ConsumableList : HomeRoute

    @Serializable
    data object Search : HomeRoute

    @Serializable
    data object Notification : HomeRoute

    @Serializable
    data class Detail(
        val itemId: Int,
    ) : HomeRoute

    @Serializable
    data class DetailEdit(
        val itemId: Long,
    ) : HomeRoute
}
