package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.client.userIdHeader
import com.obrit.obrit.shared.network.config.UserIdProvider
import com.obrit.obrit.shared.network.response.ApiResponse
import com.obrit.obrit.shared.network.response.receipt.AnalyzeReceiptResponse
import com.obrit.obrit.shared.network.response.requireData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

internal class ReceiptRemoteDataSourceImpl(
    private val httpClient: HttpClient,
    private val userIdProvider: UserIdProvider,
) : ReceiptRemoteDataSource {
    override suspend fun analyzeReceipt(
        image: ByteArray,
        fileName: String,
    ): AnalyzeReceiptResponse {
        val userId = userIdProvider.get()

        return httpClient
            .submitFormWithBinaryData(
                url = RECEIPTS_ANALYZE_PATH,
                formData =
                    formData {
                        append(
                            key = "image",
                            value = image,
                            headers =
                                Headers.build {
                                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                                    append(HttpHeaders.ContentType, "image/jpeg")
                                },
                        )
                    },
            ) {
                userIdHeader(userId)
                // Gemini OCR 처리에 수십 초가 걸릴 수 있어 기본(엔진) 타임아웃을 늘린다.
                timeout {
                    requestTimeoutMillis = ANALYZE_TIMEOUT_MILLIS
                    socketTimeoutMillis = ANALYZE_TIMEOUT_MILLIS
                }
            }.body<ApiResponse<AnalyzeReceiptResponse>>()
            .requireData()
    }
}

private const val RECEIPTS_ANALYZE_PATH = "receipts/analyze"
private const val ANALYZE_TIMEOUT_MILLIS = 60_000L
