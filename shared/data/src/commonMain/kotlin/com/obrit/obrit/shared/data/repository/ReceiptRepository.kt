package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.receipts.ReceiptAnalysis

interface ReceiptRepository {
    suspend fun analyzeReceipt(
        image: ByteArray,
        fileName: String,
    ): Result<ReceiptAnalysis>
}
