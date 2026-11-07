package com.obrit.obrit.shared.network.response.home

import com.obrit.obrit.shared.model.home.MyStatusSummary
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyStatusSummaryResponse(
    @SerialName("totalCount") val totalCount: Int,
    @SerialName("needReplaceCount") val needReplaceCount: Int,
    @SerialName("score") val score: Double,
    @SerialName("averageScore") val averageScore: Double,
)

fun MyStatusSummaryResponse.toMyStatusSummary() =
    MyStatusSummary(
        totalCount = totalCount,
        needReplaceCount = needReplaceCount,
        score = score,
        averageScore = averageScore,
    )
