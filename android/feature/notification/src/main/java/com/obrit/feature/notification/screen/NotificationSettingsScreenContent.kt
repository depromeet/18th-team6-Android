package com.obrit.feature.notification.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.obrit.feature.notification.component.NotificationPermissionBanner
import com.obrit.feature.notification.component.NotificationSettingsCard
import com.obrit.feature.notification.component.NotificationSettingsDivider
import com.obrit.feature.notification.component.NotificationSettingsSection
import com.obrit.feature.notification.component.NotificationSettingsToggleRow
import com.obrit.feature.notification.component.NotificationSettingsValueRow
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * 알림 설정 화면 상태. 기본값은 알림 기능 명세서 3장 기준값을 따른다.
 *
 * [leadDays]와 [overdueDays]는 null이면 해당 단계를 보내지 않는다(Figma `설정 안함`).
 * 둘 다 null이어도 [isReplacementEnabled]가 켜져 있으면 교체일 당일 알림은 온다.
 */
@Immutable
internal data class NotificationSettingsUiState(
    val isReplacementEnabled: Boolean = true,
    val isSpareShortageEnabled: Boolean = true,
    val leadDays: Int? = DEFAULT_LEAD_DAYS,
    val overdueDays: Int? = DEFAULT_OVERDUE_DAYS,
    val sendTime: LocalTime = DEFAULT_SEND_TIME,
)

@Composable
internal fun NotificationSettingsScreenContent(
    state: NotificationSettingsUiState,
    isPermissionGranted: Boolean,
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
            isPermissionGranted = isPermissionGranted,
            onPermissionBannerClick = action.onPermissionBannerClick,
            onReplacementChange = action.onReplacementChange,
            onSpareShortageChange = action.onSpareShortageChange,
        )
    }
}

@Composable
private fun NotificationSettingsBody(
    state: NotificationSettingsUiState,
    isPermissionGranted: Boolean,
    onPermissionBannerClick: () -> Unit,
    onReplacementChange: (Boolean) -> Unit,
    onSpareShortageChange: (Boolean) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(
                    start = AtomSpacing.S5.dp,
                    end = AtomSpacing.S5.dp,
                    top = if (isPermissionGranted) AtomSpacing.S6.dp else AtomSpacing.S2_5.dp,
                    bottom = AtomSpacing.S5.dp,
                ),
    ) {
        if (!isPermissionGranted) {
            NotificationPermissionBanner(
                onClick = onPermissionBannerClick,
                showChevron = true,
            )
            Spacer(modifier = Modifier.height(AtomSpacing.S6.dp))
        }

        Column(verticalArrangement = Arrangement.spacedBy(AtomSpacing.S7.dp)) {
            ReplacementSection(
                isEnabled = state.isReplacementEnabled,
                leadDays = state.leadDays,
                overdueDays = state.overdueDays,
                onEnabledChange = onReplacementChange,
            )
            SpareShortageSection(
                isEnabled = state.isSpareShortageEnabled,
                onEnabledChange = onSpareShortageChange,
            )
            SendTimeSection(
                sendTime = state.sendTime,
                // T는 교체·여분 알림이 함께 쓰므로 둘 다 꺼지면 고를 이유가 없어진다(명세서 4.3).
                isEnabled = state.isReplacementEnabled || state.isSpareShortageEnabled,
            )
        }
    }
}

/** 교체 예정 알림을 끄면 사전·지연 알림을 보내지 않으므로 두 일수 행을 함께 흐리게 한다(명세서 4.3). */
@Composable
private fun ReplacementSection(
    isEnabled: Boolean,
    leadDays: Int?,
    overdueDays: Int?,
    onEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    NotificationSettingsSection(title = "교체 알림", modifier = modifier) {
        NotificationSettingsCard(verticalPadding = AtomSpacing.S2.dp) {
            NotificationSettingsToggleRow(
                title = "교체 예정 알림",
                description = "교체일이 다가오면 미리 알려드려요",
                checked = isEnabled,
                onCheckedChange = onEnabledChange,
            )
            NotificationSettingsDivider()
            NotificationSettingsValueRow(
                title = "교체 사전 알림",
                value = leadDays?.let { "${it}일 전" } ?: UNSET_VALUE_TEXT,
                enabled = isEnabled,
            )
            NotificationSettingsDivider()
            NotificationSettingsValueRow(
                title = "교체 지연 알림",
                value = overdueDays?.let { "${it}일 후" } ?: UNSET_VALUE_TEXT,
                enabled = isEnabled,
            )
        }
    }
}

@Composable
private fun SpareShortageSection(
    isEnabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    NotificationSettingsSection(title = "여분 알림", modifier = modifier) {
        NotificationSettingsCard(verticalPadding = AtomSpacing.S2.dp) {
            NotificationSettingsToggleRow(
                title = "여분 부족 알림",
                description = "여분이 없고 교체일이 가까운 항목을 알려드려요",
                checked = isEnabled,
                onCheckedChange = onEnabledChange,
            )
        }
    }
}

@Composable
private fun SendTimeSection(
    sendTime: LocalTime,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    NotificationSettingsSection(title = "알림 시간", modifier = modifier) {
        NotificationSettingsCard(verticalPadding = AtomSpacing.S2.dp) {
            NotificationSettingsValueRow(
                title = "알림 시간 설정",
                value = sendTime.format(SendTimeFormatter),
                enabled = isEnabled,
            )
        }
    }
}

private const val NOTIFICATION_SETTINGS_TITLE = "알림 설정"
private const val UNSET_VALUE_TEXT = "설정 안함"
private const val DEFAULT_LEAD_DAYS = 3
private const val DEFAULT_OVERDUE_DAYS = 3
private const val DEFAULT_SEND_HOUR = 9
private val DEFAULT_SEND_TIME = LocalTime.of(DEFAULT_SEND_HOUR, 0)
private val SendTimeFormatter = DateTimeFormatter.ofPattern("a hh : mm", Locale.KOREAN)

@Preview(name = "NotificationSettings 전부 꺼짐", showBackground = true)
@Composable
private fun NotificationSettingsScreenContentAllDisabledPreview() {
    OBRitTheme(dynamicColor = false) {
        NotificationSettingsScreenContent(
            state =
                NotificationSettingsUiState(
                    isReplacementEnabled = false,
                    isSpareShortageEnabled = false,
                    leadDays = null,
                    overdueDays = null,
                ),
            isPermissionGranted = false,
            action = PreviewAction,
        )
    }
}

@Preview(name = "NotificationSettings 기본값", showBackground = true)
@Composable
private fun NotificationSettingsScreenContentPreview() {
    OBRitTheme(dynamicColor = false) {
        NotificationSettingsScreenContent(
            state = NotificationSettingsUiState(),
            isPermissionGranted = true,
            action = PreviewAction,
        )
    }
}

private val PreviewAction =
    NotificationSettingsScreenAction(
        onBackClick = {},
        onPermissionBannerClick = {},
        onReplacementChange = {},
        onSpareShortageChange = {},
    )
