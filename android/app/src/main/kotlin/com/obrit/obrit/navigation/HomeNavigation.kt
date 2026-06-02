package com.obrit.obrit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.obrit.feature.detail.screen.DetailScreen
import com.obrit.feature.home.screen.ItemListScreen
import com.obrit.feature.home.screen.HomeScreen
import com.obrit.obrit.navigation.route.HomeRoute

@Composable
fun HomeNavigation(
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val homeBackStack = rememberNavBackStack(HomeRoute.Home)

    NavDisplay(
        backStack = homeBackStack,
        modifier = modifier,
        onBack = { homeBackStack.removeLastOrNull() },
        entryProvider =
            entryProvider {
                entry<HomeRoute.Home> {
                    HomeScreen(
                        onSearchClick = {},
                        onNotificationClick = {},
                        onProfileClick = {},
                        onRegisterClick = onRegisterClick,
                        onMoreClick = { homeBackStack.add(HomeRoute.ConsumableList) },
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
                        modifier = Modifier,
                    )
                }
            },
    )
}
