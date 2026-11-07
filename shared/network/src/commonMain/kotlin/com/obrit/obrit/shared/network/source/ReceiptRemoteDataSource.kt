package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.response.receipt.AnalyzeReceiptResponse

interface ReceiptRemoteDataSource {
    suspend fun analyzeReceipt(
        image: ByteArray,
        fileName: String,
    ): AnalyzeReceiptResponse
}
