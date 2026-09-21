package com.obrit.android.core.designsystem.component.toggle

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

/**
 * on/off 스위치. Figma `Toggle`(2791:8439).
 *
 * [onCheckedChange]가 null이면 표시 전용이다. 설정 행 전체를 눌러 토글하는 화면처럼
 * 조작을 바깥에서 받는 경우에 쓴다.
 */
@Composable
fun OBRitToggle(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val colors = LocalOBRitColor.current
    val trackColor by animateColorAsState(
        targetValue = if (checked) colors.green300 else colors.gray700,
        label = "OBRitToggleTrackColor",
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) OBRitToggleThumbTravel else 0.dp,
        label = "OBRitToggleThumbOffset",
    )

    Box(
        modifier =
            modifier
                .size(width = OBRitToggleWidth, height = OBRitToggleHeight)
                .clip(RoundedCornerShape(AtomRadius.Exception.dp))
                .background(trackColor)
                .then(toggleableModifier(checked = checked, onCheckedChange = onCheckedChange))
                .padding(OBRitToggleThumbInset),
        contentAlignment = Alignment.CenterStart,
    ) {
        OBRitToggleThumb(modifier = Modifier.offset(x = thumbOffset))
    }
}

@Composable
private fun OBRitToggleThumb(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .size(OBRitToggleThumbSize)
                .clip(CircleShape)
                .background(LocalOBRitColor.current.gray50),
    )
}

private fun toggleableModifier(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
): Modifier =
    if (onCheckedChange != null) {
        Modifier.toggleable(
            value = checked,
            role = Role.Switch,
            onValueChange = onCheckedChange,
        )
    } else {
        Modifier
    }

// Figma 벡터 값: track 44x24, thumb 지름 22, track 가장자리에서 1 inset.
private val OBRitToggleWidth = 44.dp
private val OBRitToggleHeight = 24.dp
private val OBRitToggleThumbSize = 22.dp
private val OBRitToggleThumbInset = 1.dp
private val OBRitToggleThumbTravel = OBRitToggleWidth - OBRitToggleThumbSize - OBRitToggleThumbInset * 2

// 정적 렌더링에서는 꺼짐/켜짐을 나란히 보여 Figma와 비교하고, 인터랙티브 모드에서는 탭해서 동작을 확인한다.
@Preview(name = "OBRitToggle", showBackground = true)
@Composable
private fun OBRitTogglePreview() {
    OBRitTheme(dynamicColor = false) {
        val colors = LocalOBRitColor.current
        var offToggleChecked by remember { mutableStateOf(false) }
        var onToggleChecked by remember { mutableStateOf(true) }

        Row(
            modifier =
                Modifier
                    .background(colors.gray900)
                    .padding(AtomSpacing.S5.dp),
            horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S4.dp),
        ) {
            OBRitToggle(checked = offToggleChecked, onCheckedChange = { offToggleChecked = it })
            OBRitToggle(checked = onToggleChecked, onCheckedChange = { onToggleChecked = it })
        }
    }
}
