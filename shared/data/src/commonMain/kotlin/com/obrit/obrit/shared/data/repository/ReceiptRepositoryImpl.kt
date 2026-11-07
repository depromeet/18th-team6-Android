package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.receipts.ReceiptAnalysis
import com.obrit.obrit.shared.network.error.runCatchingWith
import com.obrit.obrit.shared.network.response.receipt.toReceiptAnalysis
import com.obrit.obrit.shared.network.source.ReceiptRemoteDataSource

internal class ReceiptRepositoryImpl(
    private val receiptRemoteDataSource: ReceiptRemoteDataSource,
) : ReceiptRepository {
    override suspend fun analyzeReceipt(
        image: ByteArray,
        fileName: String,
    ): Result<ReceiptAnalysis> =
        runCatchingWith {
            receiptRemoteDataSource
                .analyzeReceipt(image = image, fileName = fileName)
                .toReceiptAnalysis()
        }
}
