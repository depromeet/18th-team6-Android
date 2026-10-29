package com.obrit.feature.notification.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.component.bottomsheet.OBRitBottomSheet
import com.obrit.android.core.designsystem.component.picker.OBRitWheelPicker
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import java.time.LocalTime

/**
 * 공통 발송 시각을 고르는 바텀시트. Figma `알림시간 바텀시트`(2908:10109).
 *
 * 오전·오후, 1~12시, 00·30분 휠 세 개를 쓴다. 조합이 48가지라 명세서 3장의
 * "00:00부터 23:30까지 30분 단위"와 같은 범위다.
 */
@Composable
internal fun NotificationTimeBottomSheet(
    selectedTime: LocalTime,
    onTimeChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    var meridiemIndex by remember { mutableIntStateOf(meridiemIndexOf(selectedTime)) }
    var hourIndex by remember { mutableIntStateOf(hourIndexOf(selectedTime)) }
    var minuteIndex by remember { mutableIntStateOf(minuteIndexOf(selectedTime)) }
    val currentOnTimeChange by rememberUpdatedState(onTimeChange)

    LaunchedEffect(meridiemIndex, hourIndex, minuteIndex) {
        currentOnTimeChange(localTimeOf(meridiemIndex, hourIndex, minuteIndex))
    }

    OBRitBottomSheet(modifier = modifier) {
        Spacer(modifier = Modifier.height(TimeSheetExtraTopGap))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TimeWheelGap, Alignment.CenterHorizontally),
        ) {
            OBRitWheelPicker(
                items = MeridiemLabels,
                initialSelectedIndex = meridiemIndex,
                onSelectedIndexChange = { meridiemIndex = it },
                modifier = Modifier.width(TimeWheelWidth),
            )
            OBRitWheelPicker(
                items = HourLabels,
                initialSelectedIndex = hourIndex,
                onSelectedIndexChange = { hourIndex = it },
                modifier = Modifier.width(TimeWheelWidth),
            )
            OBRitWheelPicker(
                items = MinuteLabels,
                initialSelectedIndex = minuteIndex,
                onSelectedIndexChange = { minuteIndex = it },
                modifier = Modifier.width(TimeWheelWidth),
            )
        }
        Spacer(modifier = Modifier.height(TimeSheetExtraBottomGap))
    }
}

private fun meridiemIndexOf(time: LocalTime): Int = if (time.hour >= HOURS_PER_MERIDIEM) 1 else 0

/** 12시간제에서 0시와 12시는 모두 `12`로 보여준다. */
private fun hourIndexOf(time: LocalTime): Int = (time.hour + HOURS_PER_MERIDIEM - 1) % HOURS_PER_MERIDIEM

private fun minuteIndexOf(time: LocalTime): Int = if (time.minute >= MINUTE_STEP) 1 else 0

private fun localTimeOf(
    meridiemIndex: Int,
    hourIndex: Int,
    minuteIndex: Int,
): LocalTime {
    val hour = (hourIndex + 1) % HOURS_PER_MERIDIEM + meridiemIndex * HOURS_PER_MERIDIEM
    return LocalTime.of(hour, minuteIndex * MINUTE_STEP)
}

private const val HOURS_PER_MERIDIEM = 12
private const val MINUTE_STEP = 30

private val MeridiemLabels = listOf("오전", "오후")
private val HourLabels = (1..HOURS_PER_MERIDIEM).map { "%02d".format(it) }
private val MinuteLabels = listOf(0, MINUTE_STEP).map { "%02d".format(it) }

// Figma 기준 휠 폭 73, 휠 사이 간격 36.
private val TimeWheelWidth = 73.dp
private val TimeWheelGap = AtomSpacing.S9.dp

// OBRitBottomSheet가 핸들 아래에 26dp를 두는데 Figma는 46dp이고, 아래 여백은 20dp인데 Figma는 28dp다.
private val TimeSheetExtraTopGap = AtomSpacing.S5.dp
private val TimeSheetExtraBottomGap = AtomSpacing.S2.dp

private const val PREVIEW_SEND_HOUR = 9

@Preview(name = "NotificationTimeBottomSheet", showBackground = true, backgroundColor = 0xFF101213)
@Composable
private fun NotificationTimeBottomSheetPreview() {
    OBRitTheme(dynamicColor = false) {
        NotificationTimeBottomSheet(
            selectedTime = LocalTime.of(PREVIEW_SEND_HOUR, 0),
            onTimeChange = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
