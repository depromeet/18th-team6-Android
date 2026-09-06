package com.obrit.feature.register.viewmodel

import android.util.Log
import androidx.compose.runtime.Immutable
import com.obrit.android.core.analytics.AnalyticsLogger
import com.obrit.android.core.analytics.OBRitLoggingEvent
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.obrit.shared.data.repository.ReceiptRepository
import com.obrit.obrit.shared.model.receipts.ReceiptAnalysis
import org.orbitmvi.orbit.viewmodel.container

class ReceiptCameraViewModel(
    private val receiptRepository: ReceiptRepository,
    private val analyticsLogger: AnalyticsLogger,
) : BaseContainerHost<ReceiptCameraUiState, ReceiptCameraSideEffect>() {
    private var analyzeStartedAt = 0L

    override val container =
        container<ReceiptCameraUiState, ReceiptCameraSideEffect>(ReceiptCameraUiState())

    fun analyze(
        image: ByteArray,
        fileName: String,
    ) = intent {
        if (state.isAnalyzing) return@intent
        analyzeStartedAt = System.currentTimeMillis()
        analyticsLogger.log(OBRitLoggingEvent.ReceiptAnalyzeStart)
        reduce { state.copy(isAnalyzing = true, isError = false) }
        receiptRepository
            .analyzeReceipt(image = image, fileName = fileName)
            .onSuccess { analysis ->
                analyticsLogger.log(
                    OBRitLoggingEvent.ReceiptAnalyzeComplete(
                        processingMs = System.currentTimeMillis() - analyzeStartedAt,
                        recognizedCount = analysis.items.size,
                    ),
                )
                reduce { state.copy(isAnalyzing = false) }
                postSideEffect(ReceiptCameraSideEffect.OnAnalyzed(analysis))
            }.onFailure { throwable ->
                analyticsLogger.log(
                    OBRitLoggingEvent.ReceiptAnalyzeFail(
                        processingMs = System.currentTimeMillis() - analyzeStartedAt,
                        failureType = throwable.message ?: "unknown",
                    ),
                )
                Log.e("ReceiptCamera", "analyze failed: ${throwable.message}", throwable)
                reduce { state.copy(isAnalyzing = false, isError = true) }
            }
    }

    fun onErrorDismiss() =
        intent {
            reduce { state.copy(isError = false) }
        }
}

@Immutable
data class ReceiptCameraUiState(
    val isAnalyzing: Boolean = false,
    val isError: Boolean = false,
)

sealed interface ReceiptCameraSideEffect {
    data class OnAnalyzed(
        val analysis: ReceiptAnalysis,
    ) : ReceiptCameraSideEffect
}
