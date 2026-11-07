package com.obrit.obrit.shared.network.response.receipt

import com.obrit.obrit.shared.model.receipts.AnalyzedItem
import com.obrit.obrit.shared.model.receipts.ReceiptAnalysis
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnalyzeReceiptResponse(
    @SerialName("receiptImageUrl") val receiptImageUrl: String,
    @SerialName("purchasedDate") val purchasedDate: String? = null,
    @SerialName("items") val items: List<AnalyzedItemResponse> = emptyList(),
)

@Serializable
data class AnalyzedItemResponse(
    @SerialName("suggestedName") val suggestedName: String,
    @SerialName("categoryId") val categoryId: Long? = null,
    @SerialName("suggestedCategoryName") val suggestedCategoryName: String,
    @SerialName("iconUrl") val iconUrl: String,
    @SerialName("quantity") val quantity: Int,
    @SerialName("suggestedReplacementIntervalDays") val suggestedReplacementIntervalDays: Int,
)

fun AnalyzeReceiptResponse.toReceiptAnalysis() =
    ReceiptAnalysis(
        receiptImageUrl = receiptImageUrl,
        purchasedDate = purchasedDate,
        items = items.map { item -> item.toAnalyzedItem() },
    )

private fun AnalyzedItemResponse.toAnalyzedItem() =
    AnalyzedItem(
        suggestedName = suggestedName,
        categoryId = categoryId,
        suggestedCategoryName = suggestedCategoryName,
        iconUrl = iconUrl,
        quantity = quantity,
        suggestedReplacementIntervalDays = suggestedReplacementIntervalDays,
    )
