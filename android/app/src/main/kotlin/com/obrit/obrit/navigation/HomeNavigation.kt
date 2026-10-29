package com.obrit.obrit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import com.obrit.feature.detail.screen.DetailEditScreen
import com.obrit.feature.detail.screen.DetailEditSubmitResult
import com.obrit.feature.detail.screen.DetailScreen
import com.obrit.feature.home.screen.HomeScreen
import com.obrit.feature.home.screen.ItemListScreen
import com.obrit.feature.home.screen.SearchScreen
import com.obrit.feature.notification.screen.NotificationScreen
import com.obrit.feature.notification.screen.NotificationSettingsScreen
import com.obrit.obrit.navigation.deeplink.ObritDeepLink
import com.obrit.obrit.navigation.deeplink.toObritDeepLink
import com.obrit.obrit.navigation.route.HomeRoute

@Composable
@Suppress("LongMethod")
fun HomeNavigation(
    deepLink: ObritDeepLink?,
    onDeepLinkConsume: () -> Unit,
    onReceiptRegisterClick: () -> Unit,
    onManualRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val homeBackStack = rememberNavBackStack(HomeRoute.Home)
    var detailEditResult by remember { mutableStateOf<DetailEditSubmitResult?>(null) }
    val currentOnDeepLinkConsume by rememberUpdatedState(onDeepLinkConsume)

    // 스택을 비우고 다시 쌓는다. 같은 알림을 여러 번 눌러도 같은 화면이 겹치지 않는다(명세서 8.2).
    LaunchedEffect(deepLink) {
        if (deepLink == null) return@LaunchedEffect

        homeBackStack.clear()
        homeBackStack.add(HomeRoute.Home)
        if (deepLink is ObritDeepLink.Item) {
            homeBackStack.add(HomeRoute.Detail(deepLink.itemId))
        }
        currentOnDeepLinkConsume()
    }

    OBRitNavDisplay(
        backStack = homeBackStack,
        modifier = modifier,
        onBack = { homeBackStack.removeLastOrNull() },
        entryProvider =
            entryProvider {
                entry<HomeRoute.Home> {
                    HomeScreen(
                        onSearchClick = { homeBackStack.add(HomeRoute.Search) },
                        onNotificationClick = { homeBackStack.add(HomeRoute.Notification) },
                        onProfileClick = {},
                        onReceiptRegisterClick = onReceiptRegisterClick,
                        onManualRegisterClick = onManualRegisterClick,
                        onMoreClick = { homeBackStack.add(HomeRoute.ConsumableList) },
                        onItemClick = { itemId -> homeBackStack.add(HomeRoute.Detail(itemId.toInt())) },
                        modifier = Modifier,
                    )
                }
                entry<HomeRoute.Notification> {
                    NotificationScreen(
                        onBackClick = { homeBackStack.removeLastOrNull() },
                        onSettingsClick = { homeBackStack.add(HomeRoute.NotificationSettings) },
                        onDeepLinkClick = { link -> homeBackStack.openDeepLink(link) },
                        modifier = Modifier,
                    )
                }
                entry<HomeRoute.NotificationSettings> {
                    NotificationSettingsScreen(
                        onBackClick = { homeBackStack.removeLastOrNull() },
                        modifier = Modifier,
                    )
                }
                entry<HomeRoute.Search> {
                    SearchScreen(
                        onBackClick = { homeBackStack.removeLastOrNull() },
                        onItemClick = { itemId -> homeBackStack.add(HomeRoute.Detail(itemId.toInt())) },
                        modifier = Modifier,
                    )
                }
                entry<HomeRoute.ConsumableList> {
                    ItemListScreen(
                        onBack = { homeBackStack.removeLastOrNull() },
                        onItemClick = { itemId -> homeBackStack.add(HomeRoute.Detail(itemId.toInt())) },
                        modifier = Modifier,
                    )
                }
                entry<HomeRoute.Detail> { entry ->
                    DetailScreen(
                        id = entry.itemId,
                        onBackClick = { homeBackStack.removeLastOrNull() },
                        onEditClick = { itemId ->
                            homeBackStack.add(HomeRoute.DetailEdit(itemId))
                        },
                        editResult = detailEditResult,
                        onEditResultConsume = {
                            detailEditResult = null
                        },
                        modifier = Modifier,
                    )
                }
                entry<HomeRoute.DetailEdit> { entry ->
                    DetailEditScreen(
                        consumableId = entry.itemId,
                        onCloseClick = {
                            homeBackStack.removeLastOrNull()
                        },
                        onCompleteClick = { result ->
                            detailEditResult = result
                            homeBackStack.removeLastOrNull()
                        },
                        modifier = Modifier,
                    )
                }
            },
    )
}

/**
 * 알림함 카드에서 받은 딥링크로 이동한다. 소모품 상세가 아니면 홈으로 보낸다(명세서 8.2).
 *
 * 알림함 위에 상세를 쌓아 올려서 뒤로가기를 누르면 알림함으로 돌아온다.
 */
private fun NavBackStack<NavKey>.openDeepLink(deepLink: String) {
    when (val target = deepLink.toObritDeepLink()) {
        is ObritDeepLink.Item -> add(HomeRoute.Detail(target.itemId))
        else -> {
            clear()
            add(HomeRoute.Home)
        }
    }
}
