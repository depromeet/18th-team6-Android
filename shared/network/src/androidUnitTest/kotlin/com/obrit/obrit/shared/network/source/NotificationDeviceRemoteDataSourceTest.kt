package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.client.configureOBRitHttpClient
import com.obrit.obrit.shared.network.client.createJson
import com.obrit.obrit.shared.network.config.NetworkConfiguration
import com.obrit.obrit.shared.network.config.UserIdProvider
import com.obrit.obrit.shared.network.error.RemoteError
import com.obrit.obrit.shared.network.request.notification.RegisterDeviceRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class NotificationDeviceRemoteDataSourceTest {
    @Test
    fun registersFidWithCurrentUserHeader() =
        runBlocking {
            val engine =
                MockEngine { request ->
                    assertEquals(HttpMethod.Post, request.method)
                    assertEquals("/notifications/devices", request.url.encodedPath)
                    assertEquals("42", request.headers["X-User-Id"])
                    val body = assertIs<TextContent>(request.body).text
                    assertEquals(createJson().parseToJsonElement("""{"fid":"test-installation"}"""), createJson().parseToJsonElement(body))
                    respond(
                        content =
                            """
                            {"success":true,"data":{
                              "id":7,"userId":42,"fid":"test-installation","createdAt":"2026-10-25T12:00:00"
                            }}
                            """.trimIndent(),
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            client(engine).use { httpClient ->
                val source = NotificationDeviceRemoteDataSourceImpl(httpClient, testUserIdProvider)
                val response = source.registerDevice(RegisterDeviceRequest("test-installation"))
                assertEquals(7L, response.id)
                assertEquals(42L, response.userId)
                assertEquals("test-installation", response.fid)
            }
        }

    @Test
    fun exposesServerRejectionAsRemoteError() =
        runBlocking {
            val engine =
                MockEngine {
                    respond(
                        content = """{"message":"Invalid FID"}""",
                        status = HttpStatusCode.BadRequest,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            client(engine).use { httpClient ->
                val source = NotificationDeviceRemoteDataSourceImpl(httpClient, testUserIdProvider)
                val error = assertFailsWith<RemoteError> { source.registerDevice(RegisterDeviceRequest("invalid")) }
                assertEquals(400, error.statusCode)
                assertEquals("Invalid FID", error.message)
            }
        }

    private fun client(engine: MockEngine): HttpClient =
        HttpClient(engine) {
            configureOBRitHttpClient(createJson(), NetworkConfiguration(baseUrl = "https://example.test/", enableLogging = false))
        }

    private val testUserIdProvider =
        object : UserIdProvider {
            override suspend fun get(): Long = 42L
        }
}
