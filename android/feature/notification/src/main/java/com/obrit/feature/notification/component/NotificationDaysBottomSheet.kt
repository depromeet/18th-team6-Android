package com.obrit.feature.notification.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.component.bottomsheet.OBRitBottomSheet
import com.obrit.android.core.designsystem.component.picker.OBRitWheelPicker
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

/**
 * 교체 사전·지연 알림 일수를 고르는 바텀시트. Figma `교체 사전 알림 바텀시트`(2908:10096).
 *
 * 두 화면이 접미사만 다르고 나머지가 같아서 [daySuffix]로 구분한다. 사전은 `전`, 지연은 `후`다.
 * [selectedDays]가 null이면 `설정 안 함`이고, 그 단계의 알림을 보내지 않는다.
 */
@Composable
internal fun NotificationDaysBottomSheet(
    selectedDays: Int?,
    daySuffix: String,
    onDaysChange: (Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = remember(daySuffix) { notificationDayOptions(daySuffix) }

    OBRitBottomSheet(modifier = modifier) {
        Spacer(modifier = Modifier.height(DaysSheetExtraTopGap))
        OBRitWheelPicker(
            items = options.map { it.label },
            initialSelectedIndex = options.indexOfFirst { it.days == selectedDays }.coerceAtLeast(0),
            onSelectedIndexChange = { index -> onDaysChange(options[index].days) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(DaysSheetExtraBottomGap))
    }
}

private data class NotificationDayOption(
    val days: Int?,
    val label: String,
)

private fun notificationDayOptions(daySuffix: String): List<NotificationDayOption> =
    buildList {
        add(NotificationDayOption(days = null, label = NOTIFICATION_DAYS_UNSET_LABEL))
        for (day in MIN_NOTIFICATION_DAYS..MAX_NOTIFICATION_DAYS) {
            add(NotificationDayOption(days = day, label = "${day}일 $daySuffix"))
        }
    }

internal const val NOTIFICATION_DAYS_UNSET_LABEL = "설정 안 함"
private const val MIN_NOTIFICATION_DAYS = 1
private const val MAX_NOTIFICATION_DAYS = 7

// OBRitBottomSheet가 핸들 아래에 26dp를 두는데 Figma는 44dp이고, 아래 여백은 20dp인데 Figma는 28dp다.
private val DaysSheetExtraTopGap = 18.dp
private val DaysSheetExtraBottomGap = AtomSpacing.S2.dp

@Preview(name = "NotificationDaysBottomSheet", showBackground = true, backgroundColor = 0xFF101213)
@Composable
private fun NotificationDaysBottomSheetPreview() {
    OBRitTheme(dynamicColor = false) {
        NotificationDaysBottomSheet(
            selectedDays = 3,
            daySuffix = "전",
            onDaysChange = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
