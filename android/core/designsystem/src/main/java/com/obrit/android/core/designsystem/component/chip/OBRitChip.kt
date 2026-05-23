package com.obrit.android.core.designsystem.component.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitColor
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

/**
 * 솔리드 배경 위 사용을 가정한다. 사진/지도 같은 동적 배경 위에 띄울 때 디자이너가
 * 의도한 backdrop blur(12px)는 적용되지 않으니 호출부에서 별도 처리한다.
 */
@Composable
fun OBRitChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    number: Int? = null,
) {
    val colors = LocalOBRitColor.current
    val typography = LocalOBRitTypography.current
    val contentColor = chipContentColor(colors = colors, selected = selected)

    Row(
        modifier =
            modifier
                .clip(OBRitChipShape)
                .background(chipContainerColor(colors = colors, selected = selected))
                .clickable(onClick = onClick)
                .padding(horizontal = AtomSpacing.S4.dp, vertical = AtomSpacing.S2.dp),
        horizontalArrangement = Arrangement.spacedBy(AtomSpacing.S2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = typography.base.copy(fontWeight = FontWeight.Bold),
            color = contentColor,
        )
        if (number != null) {
            Text(
                text = number.toString(),
                style = typography.base.copy(fontWeight = FontWeight.Bold),
                color = contentColor,
            )
        }
    }
}

internal fun chipContainerColor(
    colors: OBRitColor,
    selected: Boolean,
): Color = if (selected) colors.common00 else colors.gray800

internal fun chipContentColor(
    colors: OBRitColor,
    selected: Boolean,
): Color = if (selected) colors.common1000 else colors.common00

private val OBRitChipShape = RoundedCornerShape(AtomRadius.ExtraLarge.dp)

@Preview(name = "OBRitChip Selected With Number", showBackground = true)
@Composable
private fun OBRitChipSelectedWithNumberPreview() {
    OBRitChipPreviewContainer {
        OBRitChip(text = "Text", onClick = {}, selected = true, number = 12)
    }
}

@Preview(name = "OBRitChip Unselected With Number", showBackground = true)
@Composable
private fun OBRitChipUnselectedWithNumberPreview() {
    OBRitChipPreviewContainer {
        OBRitChip(text = "Text", onClick = {}, selected = false, number = 12)
    }
}

@Preview(name = "OBRitChip Selected No Number", showBackground = true)
@Composable
private fun OBRitChipSelectedNoNumberPreview() {
    OBRitChipPreviewContainer {
        OBRitChip(text = "Text", onClick = {}, selected = true)
    }
}

@Preview(name = "OBRitChip Unselected No Number", showBackground = true)
@Composable
private fun OBRitChipUnselectedNoNumberPreview() {
    OBRitChipPreviewContainer {
        OBRitChip(text = "Text", onClick = {}, selected = false)
    }
}

@Composable
private fun OBRitChipPreviewContainer(content: @Composable () -> Unit) {
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
