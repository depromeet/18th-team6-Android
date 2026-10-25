package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.model.notifications.NotificationPermissionStatus
import com.obrit.obrit.shared.network.client.configureOBRitHttpClient
import com.obrit.obrit.shared.network.client.createJson
import com.obrit.obrit.shared.network.config.NetworkConfiguration
import com.obrit.obrit.shared.network.config.UserIdProvider
import com.obrit.obrit.shared.network.error.RemoteError
import com.obrit.obrit.shared.network.request.notification.UpdateNotificationSettingsRequest
import com.obrit.obrit.shared.network.response.notification.toNotificationPreferences
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
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class NotificationSettingsRemoteDataSourceTest {
    @Test
    fun loadsSavedSettingsWithCurrentUserHeader() =
        runBlocking {
            val engine =
                MockEngine { request ->
                    assertEquals(HttpMethod.Get, request.method)
                    assertEquals("/notifications/settings", request.url.encodedPath)
                    assertEquals("42", request.headers["X-User-Id"])
                    respond(savedSettingsResponse, headers = jsonHeaders)
                }
            client(engine).use { httpClient ->
                val response = NotificationSettingsRemoteDataSourceImpl(httpClient, userIdProvider).getSettings()
                assertTrue(response.preReplacementEnabled)
                assertFalse(response.overdueEnabled)
                assertTrue(response.lowStockEnabled)
                assertEquals("21:30", response.dispatchTime)
                assertEquals(NotificationPermissionStatus.GRANTED, response.toNotificationPreferences().permissionStatus)
            }
        }

    @Test
    fun savesAllSettingsAndDispatchTimeWithPut() =
        runBlocking {
            val engine =
                MockEngine { request ->
                    assertEquals(HttpMethod.Put, request.method)
                    assertEquals("/notifications/settings", request.url.encodedPath)
                    assertEquals("42", request.headers["X-User-Id"])
                    val expected =
                        """
                        {"preReplacementEnabled":true,"overdueEnabled":false,"lowStockEnabled":true,
                         "dispatchTime":"21:30","permissionStatus":"GRANTED"}
                        """.trimIndent()
                    val body = assertIs<TextContent>(request.body).text
                    assertEquals(createJson().parseToJsonElement(expected), createJson().parseToJsonElement(body))
                    respond(savedSettingsResponse, headers = jsonHeaders)
                }
            client(engine).use { httpClient ->
                val source = NotificationSettingsRemoteDataSourceImpl(httpClient, userIdProvider)
                val response = source.updateSettings(UpdateNotificationSettingsRequest(true, false, true, "21:30", "GRANTED"))
                assertEquals("21:30", response.dispatchTime)
            }
        }

    @Test
    fun sendsEachPermissionStatusAndMapsResponse() =
        runBlocking {
            listOf("NOT_REQUESTED", "GRANTED", "DENIED").forEach { status ->
                val engine =
                    MockEngine { request ->
                        val body = assertIs<TextContent>(request.body).text
                        val expected =
                            """
                            {"preReplacementEnabled":false,"overdueEnabled":false,"lowStockEnabled":false,
                             "dispatchTime":"09:00","permissionStatus":"$status"}
                            """.trimIndent()
                        assertEquals(createJson().parseToJsonElement(expected), createJson().parseToJsonElement(body))
                        respond(savedSettingsResponse.replace("GRANTED", status), headers = jsonHeaders)
                    }
                client(engine).use { httpClient ->
                    val source = NotificationSettingsRemoteDataSourceImpl(httpClient, userIdProvider)
                    val response = source.updateSettings(UpdateNotificationSettingsRequest(false, false, false, "09:00", status))
                    assertEquals(NotificationPermissionStatus.valueOf(status), response.toNotificationPreferences().permissionStatus)
                }
            }
        }

    @Test
    fun mapsUnrecognizedPermissionStatusToUnknown() =
        runBlocking {
            val engine =
                MockEngine {
                    respond(savedSettingsResponse.replace("GRANTED", "FUTURE_STATUS"), headers = jsonHeaders)
                }
            client(engine).use { httpClient ->
                val response = NotificationSettingsRemoteDataSourceImpl(httpClient, userIdProvider).getSettings()
                assertEquals(NotificationPermissionStatus.UNKNOWN, response.toNotificationPreferences().permissionStatus)
            }
        }

    @Test
    fun exposesRejectedUpdateAsRemoteError() =
        runBlocking {
            val engine =
                MockEngine {
                    respond(
                        content = """{"message":"Invalid dispatch time"}""",
                        status = HttpStatusCode.BadRequest,
                        headers = jsonHeaders,
                    )
                }
            client(engine).use { httpClient ->
                val source = NotificationSettingsRemoteDataSourceImpl(httpClient, userIdProvider)
                val error =
                    assertFailsWith<RemoteError> {
                        source.updateSettings(UpdateNotificationSettingsRequest(true, false, true, "21:15", "DENIED"))
                    }
                assertEquals(400, error.statusCode)
            }
        }

    private fun client(engine: MockEngine): HttpClient =
        HttpClient(engine) {
            configureOBRitHttpClient(createJson(), NetworkConfiguration(baseUrl = "https://example.test/", enableLogging = false))
        }

    private val userIdProvider =
        object : UserIdProvider {
            override suspend fun get(): Long = 42L
        }

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val savedSettingsResponse =
        """
        {"success":true,"data":{
          "preReplacementEnabled":true,"overdueEnabled":false,"lowStockEnabled":true,
          "dispatchTime":"21:30","permissionStatus":"GRANTED","enabled":true,"leadDays":3
        }}
        """.trimIndent()
}
