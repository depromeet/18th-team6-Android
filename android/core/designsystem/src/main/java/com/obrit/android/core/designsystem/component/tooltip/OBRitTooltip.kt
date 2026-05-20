package com.obrit.android.core.designsystem.component.tooltip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.LocalOBRitColor
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.obrit.shared.designsystem.tokens.atom.radius.AtomRadius
import kotlin.math.min

enum class OBRitTooltipDirection {
    Top,
    Bottom,
    Start,
    End,
}

enum class OBRitTooltipAlignment {
    Start,
    Center,
    End,
}

@Composable
fun OBRitTooltip(
    text: String,
    direction: OBRitTooltipDirection,
    alignment: OBRitTooltipAlignment,
    modifier: Modifier = Modifier,
    textStyle: TextStyle =
        LocalOBRitTypography.current.s.copy(
            fontWeight = FontWeight.Medium,
        ),
) {
    val tailPadding = 6.dp

    Text(
        text = text,
        modifier =
            modifier
                .background(
                    color = LocalOBRitColor.current.common00,
                    shape =
                        OBRitTooltipShape(
                            direction = direction,
                            alignment = alignment,
                            tailPadding = tailPadding,
                        ),
                ).padding(
                    start = 16.dp + if (direction == OBRitTooltipDirection.Start) tailPadding else 0.dp,
                    top = 6.dp + if (direction == OBRitTooltipDirection.Top) tailPadding else 0.dp,
                    end = 16.dp + if (direction == OBRitTooltipDirection.End) tailPadding else 0.dp,
                    bottom = 6.dp + if (direction == OBRitTooltipDirection.Bottom) tailPadding else 0.dp,
                ),
        style = textStyle.copy(color = LocalOBRitColor.current.common1000),
    )
}

private data class OBRitTooltipShape(
    private val direction: OBRitTooltipDirection,
    private val alignment: OBRitTooltipAlignment,
    private val tailPadding: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val path =
            with(density) {
                val resolvedDirection = direction.resolve(layoutDirection)
                val tailHeight = tailPadding.toPx()
                val geometry =
                    tooltipGeometry(
                        size = size,
                        direction = resolvedDirection,
                        tailHeight = tailHeight,
                    )

                Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = geometry.bounds.toRect(),
                            cornerRadius = CornerRadius(geometry.radius, geometry.radius),
                        ),
                    )
                    addTooltipTail(
                        tail =
                            TooltipTail(
                                direction = resolvedDirection,
                                alignment = alignment,
                                layoutDirection = layoutDirection,
                            ),
                        geometry = geometry,
                        size = size,
                    )
                }
            }

        return Outline.Generic(path)
    }
}

private fun Density.tooltipGeometry(
    size: Size,
    direction: ResolvedTooltipDirection,
    tailHeight: Float,
): TooltipGeometry {
    val bounds =
        TooltipBounds(
            left = if (direction == ResolvedTooltipDirection.Left) tailHeight else 0f,
            top = if (direction == ResolvedTooltipDirection.Top) tailHeight else 0f,
            right = size.width - if (direction == ResolvedTooltipDirection.Right) tailHeight else 0f,
            bottom = size.height - if (direction == ResolvedTooltipDirection.Bottom) tailHeight else 0f,
        )
    val radius = min(OBRitTooltipRadius.toPx(), min(bounds.width, bounds.height) / 2f)

    return TooltipGeometry(
        bounds = bounds,
        radius = radius,
        tailWidth = tailHeight * OBRIT_TOOLTIP_TAIL_WIDTH_RATIO,
    )
}

private fun Path.addTooltipTail(
    tail: TooltipTail,
    geometry: TooltipGeometry,
    size: Size,
) {
    val bounds = geometry.bounds

    when (tail.direction) {
        ResolvedTooltipDirection.Top -> {
            val center = horizontalTailCenter(tail, geometry)
            moveTo(center - geometry.tailWidth / 2f, bounds.top)
            lineTo(center, 0f)
            lineTo(center + geometry.tailWidth / 2f, bounds.top)
        }
        ResolvedTooltipDirection.Bottom -> {
            val center = horizontalTailCenter(tail, geometry)
            moveTo(center - geometry.tailWidth / 2f, bounds.bottom)
            lineTo(center, size.height)
            lineTo(center + geometry.tailWidth / 2f, bounds.bottom)
        }
        ResolvedTooltipDirection.Left -> {
            val center = verticalTailCenter(tail, geometry)
            moveTo(bounds.left, center - geometry.tailWidth / 2f)
            lineTo(0f, center)
            lineTo(bounds.left, center + geometry.tailWidth / 2f)
        }
        ResolvedTooltipDirection.Right -> {
            val center = verticalTailCenter(tail, geometry)
            moveTo(bounds.right, center - geometry.tailWidth / 2f)
            lineTo(size.width, center)
            lineTo(bounds.right, center + geometry.tailWidth / 2f)
        }
    }
    close()
}

private fun horizontalTailCenter(
    tail: TooltipTail,
    geometry: TooltipGeometry,
): Float {
    val resolvedAlignment =
        if (tail.layoutDirection == LayoutDirection.Rtl) {
            tail.alignment.flip()
        } else {
            tail.alignment
        }
    val bounds = geometry.bounds

    return when (resolvedAlignment) {
        OBRitTooltipAlignment.Start -> bounds.left + geometry.radius + geometry.tailWidth / 2f
        OBRitTooltipAlignment.Center -> bounds.centerX
        OBRitTooltipAlignment.End -> bounds.right - geometry.radius - geometry.tailWidth / 2f
    }
}

private fun verticalTailCenter(
    tail: TooltipTail,
    geometry: TooltipGeometry,
): Float =
    when (tail.alignment) {
        OBRitTooltipAlignment.Start -> geometry.bounds.top + geometry.radius + geometry.tailWidth / 2f
        OBRitTooltipAlignment.Center -> geometry.bounds.centerY
        OBRitTooltipAlignment.End -> geometry.bounds.bottom - geometry.radius - geometry.tailWidth / 2f
    }

private fun OBRitTooltipDirection.resolve(layoutDirection: LayoutDirection): ResolvedTooltipDirection =
    when (this) {
        OBRitTooltipDirection.Top -> ResolvedTooltipDirection.Top
        OBRitTooltipDirection.Bottom -> ResolvedTooltipDirection.Bottom
        OBRitTooltipDirection.Start ->
            if (layoutDirection == LayoutDirection.Ltr) {
                ResolvedTooltipDirection.Left
            } else {
                ResolvedTooltipDirection.Right
            }
        OBRitTooltipDirection.End ->
            if (layoutDirection == LayoutDirection.Ltr) {
                ResolvedTooltipDirection.Right
            } else {
                ResolvedTooltipDirection.Left
            }
    }

private fun OBRitTooltipAlignment.flip(): OBRitTooltipAlignment =
    when (this) {
        OBRitTooltipAlignment.Start -> OBRitTooltipAlignment.End
        OBRitTooltipAlignment.Center -> OBRitTooltipAlignment.Center
        OBRitTooltipAlignment.End -> OBRitTooltipAlignment.Start
    }

private enum class ResolvedTooltipDirection {
    Top,
    Bottom,
    Left,
    Right,
}

private data class TooltipTail(
    val direction: ResolvedTooltipDirection,
    val alignment: OBRitTooltipAlignment,
    val layoutDirection: LayoutDirection,
)

private data class TooltipGeometry(
    val bounds: TooltipBounds,
    val radius: Float,
    val tailWidth: Float,
)

private data class TooltipBounds(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    val width: Float = right - left
    val height: Float = bottom - top
    val centerX: Float = (left + right) / 2f
    val centerY: Float = (top + bottom) / 2f

    fun toRect(): Rect = Rect(left = left, top = top, right = right, bottom = bottom)
}

private const val OBRIT_TOOLTIP_TAIL_WIDTH_RATIO = 5f / 3f

private val OBRitTooltipRadius = AtomRadius.Small.dp

@Preview(
    name = "OBRitTooltip",
    showBackground = true,
    widthDp = 540,
)
@Composable
private fun OBRitTooltipPreview() {
    OBRitTheme(dynamicColor = false) {
        Box(
            modifier =
                Modifier
                    .background(Color.Black)
                    .padding(20.dp),
        ) {
            Column {
                OBRitTooltipPreviewRow(direction = OBRitTooltipDirection.Bottom)
                OBRitTooltipPreviewRow(direction = OBRitTooltipDirection.Top)
                OBRitTooltipPreviewRow(direction = OBRitTooltipDirection.Start)
                OBRitTooltipPreviewRow(direction = OBRitTooltipDirection.End)
            }
        }
    }
}

@Composable
private fun OBRitTooltipPreviewRow(direction: OBRitTooltipDirection) {
    Row(
        modifier = Modifier.padding(bottom = 32.dp),
    ) {
        OBRitTooltip(
            text = "Place your text here",
            direction = direction,
            alignment = OBRitTooltipAlignment.Center,
        )
        OBRitTooltip(
            text = "Place your text here",
            modifier = Modifier.padding(start = 40.dp),
            direction = direction,
            alignment = OBRitTooltipAlignment.Start,
        )
        OBRitTooltip(
            text = "Place your text here",
            modifier = Modifier.padding(start = 40.dp),
            direction = direction,
            alignment = OBRitTooltipAlignment.End,
        )
    }
}
