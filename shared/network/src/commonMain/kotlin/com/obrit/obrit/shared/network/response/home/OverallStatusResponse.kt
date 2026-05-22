package com.obrit.obrit.shared.network.response.home

import com.obrit.obrit.shared.model.home.HomeOverallLevel
import com.obrit.obrit.shared.model.home.HomeOverallStatus
import com.obrit.obrit.shared.model.home.HomeStatusLevel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OverallStatusResponse(
    @SerialName("replacement") val replacement: String,
    @SerialName("spare") val spare: String,
    @SerialName("overall") val overall: String,
)

fun OverallStatusResponse.toHomeOverallStatus() =
    HomeOverallStatus(
        replacement = replacement.toHomeStatusLevel(),
        spare = spare.toHomeStatusLevel(),
        overall =
            when (overall) {
                "PERFECT" -> HomeOverallLevel.PERFECT
                "GOOD" -> HomeOverallLevel.GOOD
                "WARNING" -> HomeOverallLevel.WARNING
                "DANGER" -> HomeOverallLevel.DANGER
                else -> HomeOverallLevel.UNKNOWN
            },
    )

private fun String.toHomeStatusLevel(): HomeStatusLevel =
    when (this) {
        "GOOD" -> HomeStatusLevel.GOOD
        "WARNING" -> HomeStatusLevel.WARNING
        "DANGER" -> HomeStatusLevel.DANGER
        else -> HomeStatusLevel.UNKNOWN
    }
