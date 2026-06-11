package com.obrit.obrit.shared.network.client

import com.obrit.obrit.shared.network.config.NetworkConfiguration
import com.obrit.obrit.shared.network.error.NetworkErrorResponse
import com.obrit.obrit.shared.network.error.RemoteError
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal expect fun platformHttpClientEngineFactory(): HttpClientEngineFactory<*>

internal fun createJson(): Json =
    Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

internal fun createHttpClient(
    json: Json,
    configuration: NetworkConfiguration,
): HttpClient =
    HttpClient(platformHttpClientEngineFactory()) {
        configureOBRitHttpClient(json, configuration)
    }

internal fun HttpClientConfig<*>.configureOBRitHttpClient(
    json: Json,
    configuration: NetworkConfiguration,
) {
    expectSuccess = false

    install(DefaultRequest) {
        url(normalizeBaseUrl(configuration.baseUrl))
        accept(ContentType.Application.Json)
        headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    }

    install(ContentNegotiation) {
        json(json)
    }

    install(Logging) {
        logger =
            object : Logger {
                override fun log(message: String) {
                    println("[OBRit][Ktor] $message")
                }
            }
        level =
            if (configuration.enableLogging) {
                LogLevel.HEADERS
            } else {
                LogLevel.NONE
            }
    }

    HttpResponseValidator {
        validateResponse { response ->
            if (!response.status.isSuccess()) {
                throw response.toRemoteError(json)
            }
        }
    }
}

private fun normalizeBaseUrl(baseUrl: String): String = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

private suspend fun HttpResponse.toRemoteError(json: Json): RemoteError {
    val rawErrorBody = bodyAsText()
    val parsedError =
        rawErrorBody
            .takeIf(String::isNotBlank)
            ?.let { body ->
                runCatching {
                    json.decodeFromString<NetworkErrorResponse>(body)
                }.getOrNull()
            }

    val resolvedMessage =
        parsedError
            ?.message
            ?.takeIf(String::isNotBlank)
            ?: status.description.ifBlank { "An unexpected network error occurred." }

    return RemoteError(
        statusCode = status.value,
        errorCode = parsedError?.code ?: 0,
        message = resolvedMessage,
    )
}
