package com.obrit.obrit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.obrit.feature.register.screen.ManualRegisterScreen
import com.obrit.obrit.navigation.route.RegisterRoute

@Composable
fun RegisterNavigation(
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val registerBackStack = rememberNavBackStack(RegisterRoute.ManualRegister)

    NavDisplay(
        backStack = registerBackStack,
        modifier = modifier,
        onBack = { registerBackStack.removeLastOrNull() },
        entryProvider =
            entryProvider {
                entry<RegisterRoute.ManualRegister> {
                    ManualRegisterScreen(
                        onBack = onExit,
                        onRegistered = onExit,
                        modifier = Modifier,
                    )
                }
            },
    )
}
