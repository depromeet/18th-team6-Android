package com.obrit.feature.notification.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun NotificationSettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 설정 API가 없고 전체 ↔ 유형 토글 흐름도 정해지지 않아(#162), 화면 안에서만 상태를 들고 각 토글이 독립적으로 동작한다.
    // 화면을 나가면 초기값으로 돌아간다.
    var state by remember { mutableStateOf(NotificationSettingsUiState()) }

    NotificationSettingsScreenContent(
        state = state,
        action =
            NotificationSettingsScreenAction(
                onBackClick = onBackClick,
                onAllNotificationChange = { enabled -> state = state.copy(isAllEnabled = enabled) },
                onTypeChange = { type, enabled ->
                    state =
                        state.copy(
                            enabledTypes = if (enabled) state.enabledTypes + type else state.enabledTypes - type,
                        )
                },
            ),
        modifier = modifier,
    )
}

internal data class NotificationSettingsScreenAction(
    val onBackClick: () -> Unit,
    val onAllNotificationChange: (Boolean) -> Unit,
    val onTypeChange: (NotificationSettingType, Boolean) -> Unit,
)
