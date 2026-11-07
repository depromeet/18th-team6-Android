package com.obrit.android.core.designsystem.component.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.color.AtomColors
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * 가운데 칸에 멈추는 휠 피커. Figma `교체 사전 알림 바텀시트`(2908:10096).
 *
 * 스크롤이 멈출 때마다 가운데 항목을 [onSelectedIndexChange]로 알린다. 디자인에 확인 버튼이 없어서
 * 시트를 닫는 순간의 값이 그대로 확정된다.
 *
 * 가운데가 아닌 항목을 누르면 그 항목으로 굴러간다. 한 칸만 옮기려고 휠을 미세하게 튕기지 않아도 되고,
 * OS 기본 피커와 iOS 피커가 모두 같게 동작한다.
 *
 * [initialSelectedIndex]는 첫 배치에만 쓴다. 이후 위치는 스크롤이 가지므로 바깥에서 값을 바꿔도
 * 휠은 움직이지 않는다. 시트를 열 때마다 새로 그리는 사용을 전제한다.
 *
 * Figma는 가운데 칸만 높이 36이고 나머지는 24에 간격 12지만, 스냅은 칸 높이가 균일해야 맞아떨어진다.
 * 그래서 모든 칸을 36으로 두고 글자 크기만 거리에 따라 바꿨다. 가운데 칸 위치는 Figma와 같다.
 */
@Composable
fun OBRitWheelPicker(
    items: List<String>,
    initialSelectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemHeightPx = with(LocalDensity.current) { OBRitWheelItemHeight.toPx() }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialSelectedIndex)
    val centeredIndex by remember(itemHeightPx) {
        derivedStateOf {
            val carried = if (listState.firstVisibleItemScrollOffset > itemHeightPx / 2) 1 else 0
            listState.firstVisibleItemIndex + carried
        }
    }
    val currentOnSelectedIndexChange by rememberUpdatedState(onSelectedIndexChange)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { isScrolling -> if (!isScrolling) currentOnSelectedIndexChange(centeredIndex) }
    }

    Box(
        modifier = modifier.height(OBRitWheelHeight),
        contentAlignment = Alignment.Center,
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState),
            contentPadding = PaddingValues(vertical = OBRitWheelItemHeight * OBRIT_WHEEL_SIDE_ITEM_COUNT),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            itemsIndexed(items) { index, text ->
                OBRitWheelItem(
                    text = text,
                    distance = abs(index - centeredIndex),
                    // contentPadding이 위아래로 두 칸이라 항목을 맨 앞에 붙이면 가운데에 놓인다.
                    onClick = { coroutineScope.launch { listState.animateScrollToItem(index) } },
                )
            }
        }
        OBRitWheelEdgeFade()
    }
}

@Composable
private fun OBRitWheelItem(
    text: String,
    distance: Int,
    onClick: () -> Unit,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier =
            Modifier
                .height(OBRitWheelItemHeight)
                .fillMaxWidth()
                // 휠 위에 사각형 리플이 번지면 디자인과 어긋나서 표시 효과는 끈다.
                .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style =
                when (distance) {
                    0 -> typography.xl5.copy(fontWeight = FontWeight.SemiBold)
                    1 -> typography.xl2.copy(fontWeight = FontWeight.Medium)
                    else -> typography.xl.copy(fontWeight = FontWeight.Medium)
                },
            color = if (distance == 0) colors.common00 else colors.gray500,
            maxLines = 1,
        )
    }
}

/** 위아래 끝의 항목을 배경색으로 녹여 가운데만 또렷하게 보이게 한다. */
@Composable
private fun BoxScope.OBRitWheelEdgeFade() {
    val backgroundColor = LocalOBRitColor.current.gray900

    Box(
        modifier =
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(OBRitWheelFadeHeight)
                .background(Brush.verticalGradient(listOf(backgroundColor, Color.Transparent))),
    )
    Box(
        modifier =
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(OBRitWheelFadeHeight)
                .background(Brush.verticalGradient(listOf(Color.Transparent, backgroundColor))),
    )
}

private const val OBRIT_WHEEL_SIDE_ITEM_COUNT = 2
private val OBRitWheelItemHeight = AtomSpacing.S9.dp
private val OBRitWheelHeight = OBRitWheelItemHeight * (OBRIT_WHEEL_SIDE_ITEM_COUNT * 2 + 1)

// Figma 그라데이션 마스크 높이. 대응하는 spacing 토큰이 없어 값을 그대로 쓴다.
private val OBRitWheelFadeHeight = 35.dp

@Preview(name = "OBRitWheelPicker", showBackground = true, backgroundColor = 0xFF1D1B20)
@Composable
private fun OBRitWheelPickerPreview() {
    OBRitTheme(dynamicColor = false) {
        Box(
            modifier =
                Modifier
                    .background(Color(AtomColors.Gray.S900))
                    .padding(AtomSpacing.S5.dp),
        ) {
            OBRitWheelPicker(
                items = listOf("설정 안 함") + (1..7).map { "${it}일 전" },
                initialSelectedIndex = 3,
                onSelectedIndexChange = {},
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
