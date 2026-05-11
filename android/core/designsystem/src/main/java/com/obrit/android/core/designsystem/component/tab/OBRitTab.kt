package com.obrit.android.core.designsystem.component.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitColor
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

/**
 * 솔리드 배경 위 사용을 가정한다. 사진/지도 같은 동적 배경 위에 띄울 때 디자이너가
 * 의도한 backdrop blur(12px)는 적용되지 않으니 호출부에서 별도 처리한다.
 */
@Composable
@Suppress("LongMethod")
fun OBRitTab(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    number: Int? = null,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val indicatorColor = tabIndicatorColor(colors = colors, selected = selected)
    val elevation = if (selected) OBRitTabSelectedElevation else OBRitTabUnselectedElevation

    Row(
        modifier =
            modifier
                .shadow(
                    elevation = elevation,
                    shape = RectangleShape,
                    ambientColor = OBRitTabShadowColor,
                    spotColor = OBRitTabShadowColor,
                ).clickable(onClick = onClick)
                .drawBehind {
                    if (indicatorColor != Color.Transparent) {
                        val strokeWidthPx = OBRitTabIndicatorStrokeWidth.toPx()
                        val y = size.height - strokeWidthPx / 2f
                        drawLine(
                            color = indicatorColor,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidthPx,
                        )
                    }
                }.padding(horizontal = AtomSpacing.S4.dp, vertical = AtomSpacing.S3.dp),
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = typography.base.copy(fontWeight = FontWeight.Bold),
            color = colors.common00,
        )
        if (number != null) {
            Text(
                text = number.toString(),
                style = typography.base.copy(fontWeight = FontWeight.Bold),
                color = colors.common00,
            )
        }
    }
}

internal fun tabIndicatorColor(
    colors: OBRitColor,
    selected: Boolean,
): Color = if (selected) colors.green300 else Color.Transparent

private val OBRitTabSelectedElevation = 48.dp
private val OBRitTabUnselectedElevation = 24.dp
private val OBRitTabIndicatorStrokeWidth = AtomSpacing.Px.dp

@Suppress("MagicNumber")
private val OBRitTabShadowColor = Color(0x29000000)

@Preview(name = "OBRitTab Selected With Number", showBackground = true)
@Composable
private fun OBRitTabSelectedWithNumberPreview() {
    OBRitTabPreviewContainer {
        OBRitTab(text = "Text", onClick = {}, selected = true, number = 12)
    }
}

@Preview(name = "OBRitTab Unselected With Number", showBackground = true)
@Composable
private fun OBRitTabUnselectedWithNumberPreview() {
    OBRitTabPreviewContainer {
        OBRitTab(text = "Text", onClick = {}, selected = false, number = 12)
    }
}

@Preview(name = "OBRitTab Selected No Number", showBackground = true)
@Composable
private fun OBRitTabSelectedNoNumberPreview() {
    OBRitTabPreviewContainer {
        OBRitTab(text = "Text", onClick = {}, selected = true)
    }
}

@Preview(name = "OBRitTab Unselected No Number", showBackground = true)
@Composable
private fun OBRitTabUnselectedNoNumberPreview() {
    OBRitTabPreviewContainer {
        OBRitTab(text = "Text", onClick = {}, selected = false)
    }
}

@Composable
private fun OBRitTabPreviewContainer(content: @Composable () -> Unit) {
    OBRitTheme(dynamicColor = false) {
        val colors = LocalOBRitColor.current
        Box(
            modifier =
                Modifier
                    .background(colors.gray900)
                    .padding(AtomSpacing.S5.dp),
        ) {
            content()
        }
    }
}
