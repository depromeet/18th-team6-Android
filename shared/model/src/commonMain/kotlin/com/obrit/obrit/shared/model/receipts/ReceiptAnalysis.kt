package com.obrit.obrit.shared.model.receipts

data class ReceiptAnalysis(
    val receiptImageUrl: String,
    val purchasedDate: String?,
    val items: List<AnalyzedItem>,
)

data class AnalyzedItem(
    val suggestedName: String,
    val categoryId: Long?,
    val suggestedCategoryName: String,
    val quantity: Int,
    val suggestedReplacementIntervalDays: Int,
)
