package com.obrit.obrit.shared.network.response.item

import com.obrit.obrit.shared.model.ReplacementDate
import com.obrit.obrit.shared.model.items.ReplacementHistory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReplacementHistoryResponse(
    @SerialName("id") val id: Long,
    @SerialName("replacedDate") val replacedDate: String,
)

fun ReplacementHistoryResponse.toReplacementHistory() =
    ReplacementHistory(
        id = id,
        replacedDate = ReplacementDate(replacedDate),
    )
