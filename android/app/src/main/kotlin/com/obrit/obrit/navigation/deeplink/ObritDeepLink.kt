package com.obrit.obrit.navigation.deeplink

import android.content.Intent
import androidx.core.net.toUri

/**
 * 알림에서 넘어올 수 있는 화면. 알림 기능 명세서 8장의 딥링크 계약을 따른다.
 *
 * 허용 경로는 `obrit://items/{itemId}`와 `obrit://home` 둘뿐이다.
 */
sealed interface ObritDeepLink {
    data object Home : ObritDeepLink

    data class Item(
        val itemId: Int,
    ) : ObritDeepLink
}

/**
 * 인텐트에서 딥링크를 읽는다. 딥링크가 없으면 null이고 평소대로 앱을 띄운다.
 *
 * 두 경로로 들어온다. 앱이 포그라운드일 때 띄운 알림은 인텐트 data에 URI가 붙고,
 * 백그라운드에서 시스템이 띄운 알림은 payload가 extra로 들어온다(명세서 8.3).
 *
 * OBRit scheme과 허용된 경로만 실행한다. 알 수 없는 경로나 유효하지 않은 ID는 홈으로 보낸다(명세서 8.2).
 */
fun Intent.obritDeepLink(): ObritDeepLink? {
    val uri = data ?: extras?.getString(OBRIT_DEEP_LINK_KEY)?.toUri()
    if (uri == null || !uri.scheme.equals(OBRIT_SCHEME, ignoreCase = true)) return null

    val itemId = uri.pathSegments.firstOrNull()?.toIntOrNull()
    return if (uri.host == OBRIT_ITEMS_HOST && itemId != null) {
        ObritDeepLink.Item(itemId)
    } else {
        // obrit://home은 물론 알 수 없는 경로와 유효하지 않은 ID도 모두 홈으로 보낸다.
        ObritDeepLink.Home
    }
}

/** 서버가 푸시 payload에 딥링크를 담는 키(명세서 8.3). */
const val OBRIT_DEEP_LINK_KEY = "deepLink"

private const val OBRIT_SCHEME = "obrit"
private const val OBRIT_ITEMS_HOST = "items"
