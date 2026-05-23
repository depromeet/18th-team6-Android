package com.obrit.obrit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.obrit.feature.register.screen.DirectRegisterScreen
import com.obrit.feature.register.screen.ManualRegisterScreen
import com.obrit.feature.register.screen.RegisterCompleteScreen
import com.obrit.obrit.navigation.route.RegisterRoute

@Composable
fun RegisterNavigation(
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val registerBackStack = rememberNavBackStack(RegisterRoute.ManualRegister)
    var pendingCategoryName by rememberSaveable { mutableStateOf<String?>(null) }

    NavDisplay(
        backStack = registerBackStack,
        modifier = modifier,
        onBack = { registerBackStack.removeLastOrNull() },
        entryProvider =
            entryProvider {
                entry<RegisterRoute.ManualRegister> {
                    ManualRegisterScreen(
                        onBack = onExit,
                        onRegistered = {
                            registerBackStack.add(RegisterRoute.RegisterComplete)
                        },
                        onDirectRegister = { registerBackStack.add(RegisterRoute.DirectRegister) },
                        pendingCategoryName = pendingCategoryName,
                        onPendingCategoryConsumed = { pendingCategoryName = null },
                        modifier = Modifier,
                    )
                }
                entry<RegisterRoute.DirectRegister> {
                    DirectRegisterScreen(
                        onBack = { registerBackStack.removeLastOrNull() },
                        onRegistered = { name, _ ->
                            pendingCategoryName = name
                            registerBackStack.removeLastOrNull()
                        },
                        modifier = Modifier,
                    )
                }
                entry<RegisterRoute.RegisterComplete> {
                    RegisterCompleteScreen(
                        onExit = onExit,
                        modifier = Modifier,
                    )
                }
            },
    )
}
