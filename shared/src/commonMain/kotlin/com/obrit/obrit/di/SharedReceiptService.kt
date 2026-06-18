package com.obrit.obrit.di

import com.obrit.obrit.shared.model.receipts.ReceiptAnalysis

/**
 * Swift-facing receipt facade that unwraps Kotlin Result before crossing the K/N boundary.
 */
class SharedReceiptService(
    private val repositoryProvider: SharedRepositoryProvider = SharedRepositoryProvider(),
) {
    @Throws(Throwable::class)
    suspend fun analyzeReceipt(
        image: ByteArray,
        fileName: String,
    ): ReceiptAnalysis =
        logged(
            event = "SharedReceiptService.analyzeReceipt",
            details = "fileName=$fileName bytes=${image.size}",
        ) {
            repositoryProvider
                .receiptRepository()
                .analyzeReceipt(image = image, fileName = fileName)
                .getOrThrow()
        }

    private suspend fun <T> logged(
        event: String,
        details: String,
        block: suspend () -> T,
    ): T {
        SharedLog.enter(scope = LOG_SCOPE, event = event, details = details)
        val result = runCatching { block() }

        result.onSuccess {
            SharedLog.success(scope = LOG_SCOPE, event = event, details = details)
        }
        result.onFailure { throwable ->
            SharedLog.failure(scope = LOG_SCOPE, event = event, throwable = throwable, details = details)
        }

        return result.getOrThrow()
    }

    private companion object {
        const val LOG_SCOPE = "SharedReceiptService"
    }
}
