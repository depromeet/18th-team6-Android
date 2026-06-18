package com.obrit.obrit.navigation.route

import kotlinx.serialization.Serializable

@Serializable
sealed interface RegisterRoute : Route {
    @Serializable
    data object ManualRegister : RegisterRoute

    @Serializable
    data object DirectRegister : RegisterRoute

    @Serializable
    data object RegisterComplete : RegisterRoute

    @Serializable
    data object ReceiptCamera : RegisterRoute

    @Serializable
    data object ReceiptResult : RegisterRoute

    @Serializable
    data object ReceiptDetail : RegisterRoute
}
