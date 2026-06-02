package com.obrit.obrit.navigation.route

import kotlinx.serialization.Serializable

@Serializable
sealed interface OnboardingRoute : Route {
    @Serializable
    data object Start : OnboardingRoute
}
