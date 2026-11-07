package com.obrit.obrit.shared.network.client

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header

internal fun HttpRequestBuilder.userIdHeader(userId: Long) {
    header(USER_ID_HEADER, userId)
}

private const val USER_ID_HEADER = "X-User-Id"
