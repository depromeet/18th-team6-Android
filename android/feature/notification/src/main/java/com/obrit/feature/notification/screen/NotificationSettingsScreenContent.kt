package com.obrit.feature.notification.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.component.topbar.OBRitDepthTopBar
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.notification.component.NotificationSettingsCard
import com.obrit.feature.notification.component.NotificationSettingsDivider
import com.obrit.feature.notification.component.NotificationSettingsSection
import com.obrit.feature.notification.component.NotificationSettingsToggleRow
import com.obrit.feature.notification.component.NotificationSettingsValueRow
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** PRD 7.1의 세 알림 유형. */
internal enum class NotificationSettingType(
    val title: String,
    val description: String,
) {
    ReplacementUpcoming("교체 예정 알림", "교체일이 다가오면 미리 알려드려요"),
    ReplacementOverdue("교체 지연 알림", "교체일이 지났지만 완료되지 않은 항목을 알려드려요"),
    SpareShortage("여분 부족 알림", "여분이 없고 교체일이 가까운 항목을 알려드려요"),
}

/** 기본값은 PRD 7.2 발송 설정을 따른다. */
@Immutable
internal data class NotificationSettingsUiState(
    val isAllEnabled: Boolean = true,
    val enabledTypes: Set<NotificationSettingType> = NotificationSettingType.entries.toSet(),
    val leadDays: Int = DEFAULT_LEAD_DAYS,
    val sendTime: LocalTime = DEFAULT_SEND_TIME,
)

@Composable
internal fun NotificationSettingsScreenContent(
    state: NotificationSettingsUiState,
    action: NotificationSettingsScreenAction,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colors.gray900)
                .statusBarsPadding(),
    ) {
        OBRitDepthTopBar(
            title = NOTIFICATION_SETTINGS_TITLE,
            onBackClick = action.onBackClick,
        )
        NotificationSettingsBody(
            state = state,
            onAllNotificationChange = action.onAllNotificationChange,
            onTypeChange = action.onTypeChange,
        )
    }
}

@Composable
private fun NotificationSettingsBody(
    state: NotificationSettingsUiState,
    onAllNotificationChange: (Boolean) -> Unit,
    onTypeChange: (NotificationSettingType, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(
                    start = AtomSpacing.S5.dp,
                    end = AtomSpacing.S5.dp,
                    top = AtomSpacing.S6.dp,
                    bottom = AtomSpacing.S5.dp,
                ),
        verticalArrangement = Arrangement.spacedBy(AtomSpacing.S7.dp),
    ) {
        AllNotificationSection(
            checked = state.isAllEnabled,
            onCheckedChange = onAllNotificationChange,
        )
        NotificationTypeSection(
            enabledTypes = state.enabledTypes,
            onTypeChange = onTypeChange,
        )
        AdvanceNotificationSection(
            leadDays = state.leadDays,
            sendTime = state.sendTime,
        )
    }
}

@Composable
private fun AllNotificationSection(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    NotificationSettingsSection(title = "전체 알림", modifier = modifier) {
        NotificationSettingsCard(verticalPadding = AtomSpacing.S0.dp) {
            NotificationSettingsToggleRow(
                title = "전체 소모품 알림",
                description = "모든 소모품 관련 알림을 한 번에 관리해요",
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
    }
}

@Composable
private fun NotificationTypeSection(
    enabledTypes: Set<NotificationSettingType>,
    onTypeChange: (NotificationSettingType, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val types = NotificationSettingType.entries

    NotificationSettingsSection(title = "알림 유형", modifier = modifier) {
        NotificationSettingsCard(verticalPadding = AtomSpacing.S2.dp) {
            types.forEachIndexed { index, type ->
                NotificationSettingsToggleRow(
                    title = type.title,
                    description = type.description,
                    checked = type in enabledTypes,
                    onCheckedChange = { enabled -> onTypeChange(type, enabled) },
                )
                if (index != types.lastIndex) {
                    NotificationSettingsDivider()
                }
            }
        }
    }
}

@Composable
private fun AdvanceNotificationSection(
    leadDays: Int,
    sendTime: LocalTime,
    modifier: Modifier = Modifier,
) {
    // 시점·시간을 고르는 바텀시트는 디자인이 나오지 않아 아직 탭 동작이 없다(#162).
    NotificationSettingsSection(title = "사전 알림 설정", modifier = modifier) {
        NotificationSettingsCard(verticalPadding = AtomSpacing.S2.dp) {
            NotificationSettingsValueRow(title = "알림 시점", value = "${leadDays}일 전")
            NotificationSettingsDivider()
            NotificationSettingsValueRow(title = "알림 시간", value = sendTime.format(SendTimeFormatter))
        }
    }
}

private const val NOTIFICATION_SETTINGS_TITLE = "알림 설정"
private const val DEFAULT_LEAD_DAYS = 3
private const val DEFAULT_SEND_HOUR = 9
private val DEFAULT_SEND_TIME = LocalTime.of(DEFAULT_SEND_HOUR, 0)
private val SendTimeFormatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN)

@Preview(name = "NotificationSettingsScreenContent", showBackground = true)
@Composable
private fun NotificationSettingsScreenContentPreview() {
    OBRitTheme(dynamicColor = false) {
        NotificationSettingsScreenContent(
            state = NotificationSettingsUiState(),
            action =
                NotificationSettingsScreenAction(
                    onBackClick = {},
                    onAllNotificationChange = {},
                    onTypeChange = { _, _ -> },
                ),
        )
    }
}
