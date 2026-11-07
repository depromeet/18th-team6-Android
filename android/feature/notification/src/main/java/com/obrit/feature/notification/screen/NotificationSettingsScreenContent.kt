package com.obrit.feature.notification.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.component.topbar.OBRitDepthTopBar
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.feature.notification.component.NotificationLoadFailed
import com.obrit.feature.notification.component.NotificationLoading
import com.obrit.feature.notification.component.NotificationPermissionBanner
import com.obrit.feature.notification.component.NotificationSettingsCard
import com.obrit.feature.notification.component.NotificationSettingsDivider
import com.obrit.feature.notification.component.NotificationSettingsSection
import com.obrit.feature.notification.component.NotificationSettingsToggleRow
import com.obrit.feature.notification.component.NotificationSettingsValueRow
import com.obrit.feature.notification.viewmodel.NotificationSettingType
import com.obrit.feature.notification.viewmodel.NotificationSettingsUiState
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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

        when (state) {
            is NotificationSettingsUiState.Loading -> NotificationLoading()
            is NotificationSettingsUiState.LoadFailed ->
                NotificationLoadFailed(
                    onRetryClick = action.onRetryClick,
                    message = NOTIFICATION_SETTINGS_LOAD_FAILED_TEXT,
                )

            is NotificationSettingsUiState.Success ->
                NotificationSettingsBody(
                    state = state,
                    isPermissionGranted = isPermissionGranted,
                    onPermissionBannerClick = action.onPermissionBannerClick,
                    onTypeChange = action.onTypeChange,
                    onSendTimeClick = action.onSendTimeClick,
                )
        }
    }
}

@Composable
@Suppress("LongParameterList")
private fun NotificationSettingsBody(
    state: NotificationSettingsUiState.Success,
    isPermissionGranted: Boolean,
    onPermissionBannerClick: () -> Unit,
    onTypeChange: (NotificationSettingType, Boolean) -> Unit,
    onSendTimeClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(bodyPadding(isPermissionGranted)),
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
                enabledTypes = state.enabledTypes,
                onTypeChange = onTypeChange,
            )
            SpareShortageSection(
                enabledTypes = state.enabledTypes,
                onTypeChange = onTypeChange,
            )
            SendTimeSection(
                sendTime = state.sendTime,
                // 세 유형을 모두 끄면 고를 이유가 없어져 비활성화한다(명세서 4.3).
                isEnabled = state.enabledTypes.isNotEmpty(),
                onClick = onSendTimeClick,
            )
        }
    }
}

/** 배너가 보이면 위 여백이 24에서 10으로 줄어든다(Figma 2996:9732). */
private fun bodyPadding(isPermissionGranted: Boolean): PaddingValues =
    PaddingValues(
        start = AtomSpacing.S5.dp,
        end = AtomSpacing.S5.dp,
        top = if (isPermissionGranted) AtomSpacing.S6.dp else AtomSpacing.S2_5.dp,
        bottom = AtomSpacing.S5.dp,
    )

@Composable
private fun ReplacementSection(
    enabledTypes: Set<NotificationSettingType>,
    onTypeChange: (NotificationSettingType, Boolean) -> Unit,
) {
    NotificationSettingsSection(title = "교체 알림") {
        NotificationSettingsCard(verticalPadding = AtomSpacing.S2.dp) {
            NotificationSettingsTypeRow(
                type = NotificationSettingType.ReplacementUpcoming,
                enabledTypes = enabledTypes,
                onTypeChange = onTypeChange,
            )
            NotificationSettingsDivider()
            NotificationSettingsTypeRow(
                type = NotificationSettingType.ReplacementOverdue,
                enabledTypes = enabledTypes,
                onTypeChange = onTypeChange,
            )
        }
    }
}

@Composable
private fun SpareShortageSection(
    enabledTypes: Set<NotificationSettingType>,
    onTypeChange: (NotificationSettingType, Boolean) -> Unit,
) {
    NotificationSettingsSection(title = "여분 알림") {
        NotificationSettingsCard(verticalPadding = AtomSpacing.S2.dp) {
            NotificationSettingsTypeRow(
                type = NotificationSettingType.SpareShortage,
                enabledTypes = enabledTypes,
                onTypeChange = onTypeChange,
            )
        }
    }
}

@Composable
private fun NotificationSettingsTypeRow(
    type: NotificationSettingType,
    enabledTypes: Set<NotificationSettingType>,
    onTypeChange: (NotificationSettingType, Boolean) -> Unit,
) {
    NotificationSettingsToggleRow(
        title = type.title,
        description = type.description,
        checked = type in enabledTypes,
        onCheckedChange = { isEnabled -> onTypeChange(type, isEnabled) },
    )
}

/** 발송 시각은 교체·여분 알림이 함께 쓰는 하나의 값이다(명세서 3장 `T`). */
@Composable
private fun SendTimeSection(
    sendTime: LocalTime,
    isEnabled: Boolean,
    onClick: () -> Unit,
) {
    NotificationSettingsSection(title = "알림 시간") {
        NotificationSettingsCard(verticalPadding = AtomSpacing.S2.dp) {
            NotificationSettingsValueRow(
                title = "알림 시간 설정",
                description = "알림을 받을 시간을 설정해 주세요",
                value = sendTime.format(SendTimeFormatter),
                onClick = onClick,
                enabled = isEnabled,
            )
        }
    }
}

private const val NOTIFICATION_SETTINGS_TITLE = "알림 설정"
private const val NOTIFICATION_SETTINGS_LOAD_FAILED_TEXT = "알림 설정을 불러오지 못했어요"
private const val PREVIEW_SEND_HOUR = 9
private val SendTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("a hh : mm", Locale.KOREAN)

@Preview(name = "NotificationSettings OS 알림 꺼짐", showBackground = true)
@Composable
private fun NotificationSettingsScreenContentPermissionDeniedPreview() {
    OBRitTheme(dynamicColor = false) {
        NotificationSettingsScreenContent(
            state =
                NotificationSettingsUiState.Success(
                    enabledTypes = emptySet(),
                    sendTime = LocalTime.of(PREVIEW_SEND_HOUR, 0),
                ),
            isPermissionGranted = false,
            action = PreviewAction,
        )
    }
}

@Preview(name = "NotificationSettings 전부 켜짐", showBackground = true)
@Composable
private fun NotificationSettingsScreenContentPreview() {
    OBRitTheme(dynamicColor = false) {
        NotificationSettingsScreenContent(
            state =
                NotificationSettingsUiState.Success(
                    enabledTypes = NotificationSettingType.entries.toSet(),
                    sendTime = LocalTime.of(PREVIEW_SEND_HOUR, 0),
                ),
            isPermissionGranted = true,
            action = PreviewAction,
        )
    }
}

private val PreviewAction =
    NotificationSettingsScreenAction(
        onBackClick = {},
        onRetryClick = {},
        onPermissionBannerClick = {},
        onTypeChange = { _, _ -> },
        onSendTimeClick = {},
    )
