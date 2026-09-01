package com.obrit.obrit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import com.obrit.feature.detail.screen.DetailEditScreen
import com.obrit.feature.detail.screen.DetailEditSubmitResult
import com.obrit.feature.detail.screen.DetailScreen
import com.obrit.feature.home.screen.HomeScreen
import com.obrit.feature.home.screen.ItemListScreen
import com.obrit.feature.home.screen.SearchScreen
import com.obrit.feature.notification.screen.NotificationScreen
import com.obrit.obrit.navigation.route.HomeRoute

@Composable
@Suppress("LongMethod")
fun HomeNavigation(
    onReceiptRegisterClick: () -> Unit,
    onManualRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val homeBackStack = rememberNavBackStack(HomeRoute.Home)
    var detailEditResult by remember { mutableStateOf<DetailEditSubmitResult?>(null) }

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
