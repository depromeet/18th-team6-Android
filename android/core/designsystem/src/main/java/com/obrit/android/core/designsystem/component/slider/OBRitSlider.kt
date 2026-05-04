package com.obrit.android.core.designsystem.component.slider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.OBRitColor
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongParameterList")
@Composable
fun OBRitSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
) {
    val colors = LocalOBRitColor.current

    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        onValueChangeFinished = onValueChangeFinished,
        thumb = { OBRitSliderThumb(colors = colors) },
        track = { sliderState ->
            OBRitSliderTrack(
                sliderState = sliderState,
                valueRange = valueRange,
                colors = colors,
            )
        },
    )
}

@Composable
private fun OBRitSliderThumb(colors: OBRitColor) {
    Box(
        modifier = Modifier
            .size(OBRitSliderThumbSize)
            .clip(CircleShape)
            .background(colors.common00)
            .padding(OBRitSliderThumbBorderWidth)
            .clip(CircleShape)
            .background(colors.green300),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OBRitSliderTrack(
    sliderState: SliderState,
    valueRange: ClosedFloatingPointRange<Float>,
    colors: OBRitColor,
) {
    val fraction = ((sliderState.value - valueRange.start) / (valueRange.endInclusive - valueRange.start))
        .coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(OBRitSliderTrackHeight),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(OBRitSliderTrackRadius))
                .background(colors.gray800),
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction)
                .clip(RoundedCornerShape(OBRitSliderTrackRadius))
                .background(colors.green300),
        )
    }
}

private const val OBRIT_SLIDER_INTERACTIVE_PREVIEW_INITIAL_VALUE = 0.5f

private val OBRitSliderThumbSize = AtomSpacing.S5.dp
private val OBRitSliderThumbBorderWidth = AtomSpacing.S1.dp
private val OBRitSliderTrackHeight = 4.dp
private val OBRitSliderTrackRadius = AtomRadius.Small.dp

@Preview(name = "OBRitSlider Interactive", showBackground = true)
@Composable
private fun OBRitSliderInteractivePreview() {
    var value by remember { mutableFloatStateOf(OBRIT_SLIDER_INTERACTIVE_PREVIEW_INITIAL_VALUE) }
    OBRitSliderPreviewContainer {
        OBRitSlider(value = value, onValueChange = { value = it })
    }
}

@Composable
private fun OBRitSliderPreviewContainer(content: @Composable () -> Unit) {
    OBRitTheme(dynamicColor = false) {
        val colors = LocalOBRitColor.current
        Box(
            modifier = Modifier
                .background(colors.gray900)
                .padding(AtomSpacing.S5.dp),
        ) {
            content()
        }
    }
}
