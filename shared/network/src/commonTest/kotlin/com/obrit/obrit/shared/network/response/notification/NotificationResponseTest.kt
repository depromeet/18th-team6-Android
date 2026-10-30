package com.obrit.obrit.shared.network.response.notification

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NotificationResponseTest {
    @Test
    fun bundledCardsKeepParentReadIdentityAndTheirOwnDestination() {
        val response =
            Json.decodeFromString<NotificationResponse>(
                """
                {
                  "id": 12, "title": "묶음", "isRead": false, "createdAt": "2026-10-31T09:00:00",
                  "deepLink": "obrit://home", "isBundled": true,
                  "cards": [
                    {"entryId": 31, "type": "OVERDUE", "title": "교체 지연", "content": "칫솔을 교체해주세요",
                     "itemName": "칫솔", "hasLowStock": true, "itemId": 10, "deepLink": "obrit://items/10"},
                    {"entryId": 32, "type": "PRE_REPLACEMENT", "title": "교체 예정", "itemId": 20,
                     "deepLink": "obrit://items/20"}
                  ]
                }
                """.trimIndent(),
            )
        val cards = response.toNotifications()
        assertEquals(2, cards.size)
        assertTrue(cards.all { it.notificationId == 12L && !it.isRead })
        assertEquals(listOf(31L, 32L), cards.map { it.entryId })
        assertEquals(listOf("obrit://items/10", "obrit://items/20"), cards.map { it.deepLink })
        assertEquals("칫솔", cards.first().itemName)
        assertEquals("교체 지연", cards.first().heading)
        assertEquals("칫솔을 교체해주세요", cards.first().content)
        assertTrue(cards.first().hasLowStock)
    }

    @Test
    fun legacyNotificationWithoutCardsIsNotDropped() {
        val response =
            NotificationResponse(
                id = 3,
                title = "기존 알림",
                createdAt = "2026-10-30T09:00:00",
                deepLink = "obrit://home",
                isRead = true,
            )
        val card = response.toNotifications().single()
        assertEquals(3L, card.notificationId)
        assertNull(card.entryId)
        assertTrue(card.isRead)
        assertEquals("기존 알림", card.heading)
    }

    @Test
    fun readResponseSupportsBothDocumentedAndLegacyFieldNames() {
        val suffix = ""","readAt":"2026-10-31T09:00:00","deepLink":"obrit://home"}"""
        val documented = Json.decodeFromString<MarkReadNotificationResponse>("""{"id":12,"read":true""" + suffix)
        val legacy = Json.decodeFromString<MarkReadNotificationResponse>("""{"id":12,"isRead":false""" + suffix)
        assertTrue(documented.isRead)
        assertFalse(legacy.isRead)
    }
}
