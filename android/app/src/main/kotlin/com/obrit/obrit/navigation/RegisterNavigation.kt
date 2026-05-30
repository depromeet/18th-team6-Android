package com.obrit.obrit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.obrit.feature.register.screen.complete.RegisterCompleteScreen
import com.obrit.feature.register.screen.direct.DirectRegisterScreen
import com.obrit.feature.register.screen.manual.ManualRegisterNavigation
import com.obrit.feature.register.screen.manual.ManualRegisterScreen
import com.obrit.feature.register.screen.manual.PendingCategory
import com.obrit.obrit.navigation.route.RegisterRoute
import com.obrit.obrit.shared.model.categories.Category

@Composable
@Suppress("LongMethod")
fun RegisterNavigation(
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val registerBackStack = rememberNavBackStack(RegisterRoute.ManualRegister)
    // DirectRegister → ManualRegister 복귀 시 단발성으로 전달되는 카테고리.
    // 사용 즉시 null로 초기화되며 process death 복구는 의도적으로 다루지 않는다.
    var pendingCategory by remember { mutableStateOf<Category?>(null) }

    NavDisplay(
        backStack = registerBackStack,
        modifier = modifier,
        onBack = { registerBackStack.removeLastOrNull() },
        entryProvider =
            entryProvider {
                entry<RegisterRoute.ManualRegister> {
                    ManualRegisterScreen(
                        navigation =
                            ManualRegisterNavigation(
                                onBack = onExit,
                                onRegistered = {
                                    registerBackStack.add(RegisterRoute.RegisterComplete)
                                },
                                onDirectRegister = {
                                    registerBackStack.add(RegisterRoute.DirectRegister)
                                },
                            ),
                        pendingCategory =
                            PendingCategory(
                                category = pendingCategory,
                                onConsumed = { pendingCategory = null },
                            ),
                        modifier = Modifier,
                    )
                }
                entry<RegisterRoute.DirectRegister> {
                    DirectRegisterScreen(
                        onBack = { registerBackStack.removeLastOrNull() },
                        onRegister = { category ->
                            pendingCategory = category
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
