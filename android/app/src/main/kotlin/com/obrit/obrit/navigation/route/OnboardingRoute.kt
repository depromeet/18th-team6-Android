package com.obrit.obrit.navigation.route

import kotlinx.serialization.Serializable

@Serializable
sealed interface OnboardingRoute : Route {
    @Serializable
    data object Start : OnboardingRoute

    @Serializable
    data object Select : OnboardingRoute

    @Serializable
    data class Detail(
        val selectedIds: List<Long>,
    ) : OnboardingRoute

    @Serializable
    data object Complete : OnboardingRoute
}
