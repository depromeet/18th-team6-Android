@file:Suppress("LongMethod", "MagicNumber", "TooManyFunctions")

package com.obrit.feature.agent.screen

import android.content.Context
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.obrit.feature.agent.viewmodel.DeviceTilt
import com.obrit.feature.agent.viewmodel.OrbUiState

@Composable
internal fun DeviceTiltReporter(onTiltChange: (DeviceTilt) -> Unit) {
    if (LocalInspectionMode.current) return

    val context = LocalContext.current
    val currentOnTiltChange by rememberUpdatedState(onTiltChange)

    DisposableEffect(context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor =
            sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
                ?: sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val listener =
            object : SensorEventListener {
                private var smoothedX = 0f
                private var smoothedY = 0f

                override fun onSensorChanged(event: SensorEvent) {
                    val rawX = (-event.values[0] / SensorManager.GRAVITY_EARTH).coerceIn(-1.2f, 1.2f)
                    val rawY = (event.values[1] / SensorManager.GRAVITY_EARTH).coerceIn(-1.2f, 1.2f)

                    smoothedX += (rawX - smoothedX) * 0.16f
                    smoothedY += (rawY - smoothedY) * 0.16f
                    currentOnTiltChange(DeviceTilt(smoothedX, smoothedY))
                }

                override fun onAccuracyChanged(
                    sensor: Sensor?,
                    accuracy: Int,
                ) = Unit
            }

        if (sensor != null) {
            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }
}

internal data class GlassOrbRatios(
    val normal: Float,
    val warning: Float,
)

internal data class GlassOrbColors(
    val positive: Color,
    val warning: Color,
    val shadow: Color,
    val glass: Color,
)

private data class StatusRingSpec(
    val center: Offset,
    val radius: Float,
    val ringWidth: Float,
    val normalRatio: Float,
    val warningRatio: Float,
    val positiveColor: Color,
    val warningColor: Color,
)

private data class TransitionArcSpec(
    val box: Offset,
    val ringSize: Size,
    val ringWidth: Float,
    val startAngle: Float,
    val sweepAngle: Float,
    val from: Color,
    val to: Color,
)

@Composable
internal fun GlassConsumableOrb(
    orb: OrbUiState,
    ratios: GlassOrbRatios,
    colors: GlassOrbColors,
    onOrbDrag: (Float, Float, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val maxDrag = with(density) { 28.dp.toPx() }
    val roll = orb.combinedRoll(maxDrag)
    val warningGlow = ratios.warning.coerceIn(0f, 1f)
    val normalGlow = ratios.normal.coerceIn(0f, 1f)
    val internalRollX by animateFloatAsState(
        targetValue = -roll.x,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        label = "orbInternalRollX",
    )
    val internalRollY by animateFloatAsState(
        targetValue = -roll.y,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        label = "orbInternalRollY",
    )

    Box(
        modifier =
            modifier
                .pointerInput(maxDrag) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        onOrbDrag(dragAmount.x, dragAmount.y, maxDrag)
                    }
                },
        contentAlignment = Alignment.Center,
    ) {
        OrbAmbientBackground(
            positiveColor = colors.positive,
            warningColor = colors.warning,
            shadowColor = colors.shadow,
            warningGlowAlpha = 0.14f + warningGlow * 0.58f,
            modifier = Modifier.matchParentSize(),
        )
        Box(
            modifier = Modifier.matchParentSize(),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(
                modifier =
                    Modifier
                        .matchParentSize()
                        .clip(CircleShape),
            ) {
                drawGlassBody(
                    radius = size.minDimension / 2f,
                    warningGlow = warningGlow,
                    warningColor = colors.warning,
                    shadowColor = colors.shadow,
                )
            }
            OrbBottomBlurLayer(
                modifier =
                    Modifier
                        .matchParentSize()
                        .clip(CircleShape),
            )
            OrbPlanetBackdropLayer(
                positiveColor = colors.positive,
                warningColor = colors.warning,
                modifier =
                    Modifier
                        .matchParentSize()
                        .padding(13.dp)
                        .clip(CircleShape),
            )
            OrbGlassballLayer(
                internalRollX = internalRollX,
                internalRollY = internalRollY,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
            )
            Canvas(
                modifier =
                    Modifier
                        .matchParentSize()
                        .clip(CircleShape),
            ) {
                val radius = size.minDimension / 2f
                val center = Offset(size.width / 2f, size.height / 2f)

                drawStatusRing(
                    spec =
                        StatusRingSpec(
                            center = center,
                            radius = radius - 6.dp.toPx(),
                            ringWidth = 6.dp.toPx(),
                            normalRatio = normalGlow,
                            warningRatio = warningGlow,
                            positiveColor = colors.positive,
                            warningColor = colors.warning,
                        ),
                )
                drawGlassSurfaceArc(
                    center = center,
                    radius = radius - 22.dp.toPx(),
                    glassColor = colors.glass,
                )
            }
        }
    }
}

@Composable
private fun OrbAmbientBackground(
    positiveColor: Color,
    warningColor: Color,
    shadowColor: Color,
    warningGlowAlpha: Float,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val orbRadius = size.minDimension * 0.44f
        val leftRadius = size.minDimension * 0.68f
        val rightRadius = size.minDimension * 0.62f

        drawCircle(
            brush =
                Brush.radialGradient(
                    colors =
                        listOf(
                            positiveColor.copy(alpha = 0.34f),
                            positiveColor.copy(alpha = 0.15f),
                            Color.Transparent,
                        ),
                    center = Offset(leftRadius * 0.7f, size.height * 0.5f),
                    radius = leftRadius,
                ),
            radius = leftRadius,
            center = Offset(leftRadius * 0.7f, size.height * 0.5f),
        )
        drawCircle(
            brush =
                Brush.radialGradient(
                    colors =
                        listOf(
                            warningColor.copy(alpha = warningGlowAlpha),
                            warningColor.copy(alpha = warningGlowAlpha * 0.42f),
                            Color.Transparent,
                        ),
                    center = Offset(size.width - rightRadius * 0.7f, size.height * 0.52f),
                    radius = rightRadius,
                ),
            radius = rightRadius,
            center = Offset(size.width - rightRadius * 0.7f, size.height * 0.52f),
        )
        drawCircle(
            brush =
                Brush.radialGradient(
                    colors =
                        listOf(
                            shadowColor.copy(alpha = 0.28f),
                            shadowColor.copy(alpha = 0.1f),
                            Color.Transparent,
                        ),
                    center = Offset(size.width * 0.5f, size.height * 0.58f),
                    radius = orbRadius,
                ),
            radius = orbRadius,
            center = Offset(size.width * 0.5f, size.height * 0.58f),
        )
    }
}

private fun DrawScope.drawGlassBody(
    radius: Float,
    warningGlow: Float,
    warningColor: Color,
    shadowColor: Color,
) {
    val center = Offset(size.width / 2f, size.height / 2f)

    drawCircle(
        brush =
            Brush.radialGradient(
                colors =
                    listOf(
                        Color.Transparent,
                        Color.Transparent,
                        shadowColor.copy(alpha = 0.45f),
                        shadowColor.copy(alpha = 0.85f),
                    ),
                center = center,
                radius = radius,
            ),
        radius = radius,
        center = center,
        style = Stroke(width = 14.dp.toPx()),
    )

    if (warningGlow > 0.05f) {
        drawCircle(
            brush =
                Brush.radialGradient(
                    colors =
                        listOf(
                            warningColor.copy(alpha = warningGlow * 0.32f),
                            Color.Transparent,
                        ),
                    center = Offset(size.width * 0.82f, size.height * 0.5f),
                    radius = radius * 0.6f,
                ),
            radius = radius * 0.55f,
            center = Offset(size.width * 0.78f, size.height * 0.52f),
        )
    }
}

private fun DrawScope.drawStatusRing(spec: StatusRingSpec) {
    val normal = spec.normalRatio.coerceIn(0f, 1f)
    val warning = spec.warningRatio.coerceIn(0f, 1f)
    val total = (normal + warning).coerceAtLeast(0.0001f)
    val positiveSweep = (normal / total) * 360f
    val warningSweep = (warning / total) * 360f
    val transitionDeg = if (positiveSweep > 0.5f && warningSweep > 0.5f) 22f else 0f
    val positiveStart = 180f - positiveSweep / 2f
    val warningStart = -warningSweep / 2f
    val box = Offset(spec.center.x - spec.radius, spec.center.y - spec.radius)
    val ringSize = Size(spec.radius * 2f, spec.radius * 2f)

    val positiveBodySweep = (positiveSweep - transitionDeg).coerceAtLeast(0f)
    if (positiveBodySweep > 0f) {
        drawArc(
            color = spec.positiveColor,
            startAngle = positiveStart + transitionDeg / 2f,
            sweepAngle = positiveBodySweep,
            useCenter = false,
            topLeft = box,
            size = ringSize,
            style = Stroke(width = spec.ringWidth),
        )
    }

    val warningBodySweep = (warningSweep - transitionDeg).coerceAtLeast(0f)
    if (warningBodySweep > 0f) {
        drawArc(
            color = spec.warningColor,
            startAngle = warningStart + transitionDeg / 2f,
            sweepAngle = warningBodySweep,
            useCenter = false,
            topLeft = box,
            size = ringSize,
            style = Stroke(width = spec.ringWidth),
        )
    }

    if (transitionDeg > 0f) {
        drawTransitionArc(
            spec =
                TransitionArcSpec(
                    box = box,
                    ringSize = ringSize,
                    ringWidth = spec.ringWidth,
                    startAngle = positiveStart + positiveSweep - transitionDeg / 2f,
                    sweepAngle = transitionDeg,
                    from = spec.positiveColor,
                    to = spec.warningColor,
                ),
        )
        drawTransitionArc(
            spec =
                TransitionArcSpec(
                    box = box,
                    ringSize = ringSize,
                    ringWidth = spec.ringWidth,
                    startAngle = warningStart + warningSweep - transitionDeg / 2f,
                    sweepAngle = transitionDeg,
                    from = spec.warningColor,
                    to = spec.positiveColor,
                ),
        )
    }
}

private fun DrawScope.drawGlassSurfaceArc(
    center: Offset,
    radius: Float,
    glassColor: Color,
) {
    val arcInsetX = 3.dp.toPx()
    val arcInsetY = 1.dp.toPx()
    val topLeft =
        Offset(
            x = center.x - radius + arcInsetX,
            y = center.y - radius + arcInsetY,
        )
    val arcSize =
        Size(
            width = (radius - arcInsetX) * 2f,
            height = (radius - arcInsetY) * 2f,
        )
    val arcBrush =
        Brush.linearGradient(
            colorStops =
                arrayOf(
                    0f to Color.Transparent,
                    0.22f to glassColor.copy(alpha = 0.18f),
                    0.48f to Color.White.copy(alpha = 0.58f),
                    0.72f to glassColor.copy(alpha = 0.16f),
                    1f to Color.Transparent,
                ),
            start = Offset(center.x - radius * 0.74f, center.y - radius * 0.18f),
            end = Offset(center.x + radius * 0.28f, center.y - radius * 0.82f),
        )

    drawContext.canvas.saveLayer(
        bounds = Rect(Offset.Zero, size),
        paint =
            Paint().apply {
                alpha = 0.86f
                blendMode = BlendMode.Screen
            },
    )
    drawArc(
        brush = arcBrush,
        startAngle = 205f,
        sweepAngle = 72f,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style =
            Stroke(
                width = 4.2.dp.toPx(),
                cap = StrokeCap.Round,
            ),
    )
    drawContext.canvas.restore()
}

private fun DrawScope.drawTransitionArc(spec: TransitionArcSpec) {
    val steps = 16
    val stepSweep = spec.sweepAngle / steps

    repeat(steps) { index ->
        val t = (index + 0.5f) / steps
        drawArc(
            color = lerp(spec.from, spec.to, t),
            startAngle = spec.startAngle + stepSweep * index,
            sweepAngle = stepSweep + 0.6f,
            useCenter = false,
            topLeft = spec.box,
            size = spec.ringSize,
            style = Stroke(width = spec.ringWidth),
        )
    }
}

private fun Offset.softLimited(maxDistance: Float): Offset {
    val distance = getDistance()
    val threshold = maxDistance * 0.62f
    return when {
        distance <= 0f -> Offset.Zero
        distance <= threshold -> this
        else -> {
            val overflow = distance - threshold
            val remaining = maxDistance - threshold
            val easedDistance = threshold + remaining * (overflow / (overflow + remaining))
            val scale = easedDistance / distance
            Offset(x * scale, y * scale)
        }
    }
}

@Composable
private fun OrbGlassballLayer(
    internalRollX: Float,
    internalRollY: Float,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .graphicsLayer {
                    shape = CircleShape
                    clip = true
                }.drawWithContent {
                    drawContext.canvas.saveLayer(
                        bounds = Rect(Offset.Zero, size),
                        paint =
                            Paint().apply {
                                alpha = 1f
                                blendMode = BlendMode.Hardlight
                            },
                    )
                    drawContent()
                    drawContext.canvas.restore()
                },
    ) {
        OrbAssetConsumablesLayer(
            internalRollX = internalRollX,
            internalRollY = internalRollY,
            modifier = Modifier.matchParentSize(),
        )
        OrbGlassTextureLayer(
            modifier =
                Modifier
                    .matchParentSize()
                    .clip(CircleShape),
        )
    }
}

@Composable
private fun OrbAssetConsumablesLayer(
    internalRollX: Float,
    internalRollY: Float,
    modifier: Modifier = Modifier,
) {
    val motion =
        Offset(
            x = internalRollX * 18f,
            y = internalRollY * 15f,
        )

    BoxWithConstraints(
        modifier = modifier,
    ) {
        val layerMotion = Offset(internalRollX * 16f, internalRollY * 14f).softLimited(maxDistance = 13f)
        val scale = maxWidth.value / 200f
        val enlarge = 1.4f
        val spread = 0.9f
        val maxAssetMotion = 13f

        fun stackOffset(
            originalCenter: Float,
            originalDimension: Float,
        ): Float {
            val newDimension = originalDimension * enlarge
            val newCenter = (originalCenter - 100f) * spread + 100f
            return newCenter - newDimension / 2f
        }

        fun boundedMotion(
            xFactor: Float,
            yFactor: Float,
        ): Offset {
            val raw = Offset(motion.x * xFactor, motion.y * yFactor)
            return raw.softLimited(maxDistance = maxAssetMotion)
        }

        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        translationX = layerMotion.x
                        translationY = layerMotion.y
                        rotationZ = internalRollX * 6f
                    },
        ) {
            OrbAssetImage(
                assetPath = "figma/orb/detergent.png",
                modifier =
                    Modifier
                        .size(width = (78.95f * enlarge * scale).dp, height = (93.15f * enlarge * scale).dp)
                        .offset(x = (stackOffset(55.38f, 78.95f) * scale).dp, y = (stackOffset(70.13f, 93.15f) * scale).dp)
                        .graphicsLayer {
                            val itemMotion = boundedMotion(0.7f, 0.7f)
                            translationX = itemMotion.x
                            translationY = itemMotion.y
                            rotationZ = 22.89f + internalRollX * 8f
                        },
            )
            OrbAssetImage(
                assetPath = "figma/orb/sponge.png",
                modifier =
                    Modifier
                        .size(width = (84.01f * enlarge * scale).dp, height = (72.13f * enlarge * scale).dp)
                        .offset(x = (stackOffset(98.46f, 84.01f) * scale).dp, y = (stackOffset(41.81f, 72.13f) * scale).dp)
                        .graphicsLayer {
                            val itemMotion = boundedMotion(0.52f, 0.52f)
                            translationX = itemMotion.x
                            translationY = itemMotion.y
                            rotationZ = 22.39f + internalRollX * 6f
                        },
            )
            OrbAssetImage(
                assetPath = "figma/orb/toothbrush.png",
                modifier =
                    Modifier
                        .size(width = (130.83f * enlarge * scale).dp, height = (131.46f * enlarge * scale).dp)
                        .offset(x = (stackOffset(94.08f, 130.83f) * scale).dp, y = (stackOffset(107.32f, 131.46f) * scale).dp)
                        .graphicsLayer {
                            val itemMotion = boundedMotion(0.8f, 0.82f)
                            translationX = itemMotion.x
                            translationY = itemMotion.y
                            rotationZ = -19.18f + internalRollX * 9f
                        },
            )
            OrbAssetImage(
                assetPath = "figma/orb/diffuser.png",
                modifier =
                    Modifier
                        .size(width = (100.28f * enlarge * scale).dp, height = (110.63f * enlarge * scale).dp)
                        .offset(x = (stackOffset(138.33f, 100.28f) * scale).dp, y = (stackOffset(61.06f, 110.63f) * scale).dp)
                        .graphicsLayer {
                            val itemMotion = boundedMotion(0.46f, 0.5f)
                            translationX = itemMotion.x
                            translationY = itemMotion.y
                            rotationZ = -30f + internalRollX * 7f
                        },
            )
            OrbAssetImage(
                assetPath = "figma/orb/shower_filter.png",
                modifier =
                    Modifier
                        .size(width = (72.88f * enlarge * scale).dp, height = (83f * enlarge * scale).dp)
                        .offset(x = (stackOffset(157.5f, 72.88f) * scale).dp, y = (stackOffset(113.5f, 83f) * scale).dp)
                        .graphicsLayer {
                            val itemMotion = boundedMotion(0.9f, 0.84f)
                            translationX = itemMotion.x
                            translationY = itemMotion.y
                        },
            )
            OrbAssetImage(
                assetPath = "figma/orb/razor.png",
                modifier =
                    Modifier
                        .size(width = (126.06f * enlarge * scale).dp, height = (136.84f * enlarge * scale).dp)
                        .offset(x = (stackOffset(120.35f, 126.06f) * scale).dp, y = (stackOffset(141.01f, 136.84f) * scale).dp)
                        .graphicsLayer {
                            val itemMotion = boundedMotion(1.06f, 1.02f)
                            translationX = itemMotion.x
                            translationY = itemMotion.y
                            rotationZ = 30f + internalRollY * 8f
                        },
            )
            OrbAssetImage(
                assetPath = "figma/orb/towel.png",
                modifier =
                    Modifier
                        .size(width = (123.13f * enlarge * scale).dp, height = (112.9f * enlarge * scale).dp)
                        .offset(x = (stackOffset(57.53f, 123.13f) * scale).dp, y = (stackOffset(129.9f, 112.9f) * scale).dp)
                        .graphicsLayer {
                            val itemMotion = boundedMotion(1f, 1f)
                            translationX = itemMotion.x
                            translationY = itemMotion.y
                            rotationZ = 30f + internalRollX * 10f
                        },
            )
        }
    }
}

@Composable
private fun OrbBottomBlurLayer(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Box(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 112.dp)
                    .blur(radius = 79.64444732666016.dp)
                    .padding(0.99556.dp)
                    .size(159.28889.dp)
                    .background(color = Color(0xFF1D1B20), shape = CircleShape),
        )
    }
}

@Composable
private fun OrbPlanetBackdropLayer(
    positiveColor: Color,
    warningColor: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val centerY = size.height / 2f
        val radius = size.minDimension / 2f
        val innerShadowBlur = 32.dp.toPx()
        val innerShadowSpread = 32.dp.toPx()

        drawRect(
            brush =
                Brush.linearGradient(
                    colorStops =
                        arrayOf(
                            0f to positiveColor,
                            0.3f to positiveColor,
                            0.7f to warningColor,
                            1f to warningColor,
                        ),
                    start = Offset(0f, centerY),
                    end = Offset(size.width, centerY),
                ),
        )
        drawCircle(
            brush =
                Brush.radialGradient(
                    colorStops =
                        arrayOf(
                            0f to Color.Transparent,
                            ((radius - innerShadowBlur - innerShadowSpread) / radius).coerceIn(0f, 1f) to Color.Transparent,
                            ((radius - innerShadowSpread) / radius).coerceIn(0f, 1f) to Color.White.copy(alpha = 0.08f),
                            1f to Color.White.copy(alpha = 0.24f),
                        ),
                    center = Offset(size.width / 2f, centerY),
                    radius = radius,
                ),
            radius = radius,
            center = Offset(size.width / 2f, centerY),
        )
    }
}

@Composable
private fun OrbGlassTextureLayer(modifier: Modifier = Modifier) {
    val bitmap = rememberOrbAsset("figma/orb/glass_texture_image20.png")

    Canvas(modifier = modifier) {
        drawContext.canvas.saveLayer(
            bounds = Rect(Offset.Zero, size),
            paint =
                Paint().apply {
                    alpha = 0.8f
                    blendMode = BlendMode.Plus
                },
        )
        drawRect(color = Color.LightGray)
        drawImage(
            image = bitmap,
            srcOffset = IntOffset.Zero,
            srcSize = IntSize(bitmap.width, bitmap.height),
            dstOffset = IntOffset.Zero,
            dstSize = IntSize(size.width.toInt().coerceAtLeast(1), size.height.toInt().coerceAtLeast(1)),
            alpha = 1f,
        )
        drawContext.canvas.restore()
    }
}

@Composable
private fun OrbAssetImage(
    assetPath: String,
    modifier: Modifier = Modifier,
) {
    val bitmap = rememberOrbAsset(assetPath)

    Box(modifier = modifier) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.matchParentSize(),
        )
        Image(
            bitmap = bitmap,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier =
                Modifier
                    .matchParentSize()
                    .assetOverlay(alpha = 0.6f, blendMode = BlendMode.SrcAtop),
        )
        Image(
            bitmap = bitmap,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(Color(0xFFDFFFF7), BlendMode.SrcAtop),
            modifier =
                Modifier
                    .matchParentSize()
                    .assetOverlay(alpha = 0.24f, blendMode = BlendMode.Screen),
        )
        Image(
            bitmap = bitmap,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(Color.White, BlendMode.SrcAtop),
            modifier =
                Modifier
                    .matchParentSize()
                    .assetOverlay(alpha = 0.14f, blendMode = BlendMode.Plus),
        )
        Image(
            bitmap = bitmap,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(Color(0xFFFFE2EA), BlendMode.SrcAtop),
            modifier =
                Modifier
                    .matchParentSize()
                    .assetOverlay(alpha = 0.1f, blendMode = BlendMode.Plus),
        )
    }
}

private fun Modifier.assetOverlay(
    alpha: Float,
    blendMode: BlendMode,
): Modifier =
    this
        .alpha(alpha)
        .drawWithContent {
            drawContext.canvas.saveLayer(
                bounds = Rect(Offset.Zero, size),
                paint =
                    Paint().apply {
                        this.alpha = 1f
                        this.blendMode = blendMode
                    },
            )
            drawContent()
            drawContext.canvas.restore()
        }

@Composable
private fun rememberOrbAsset(assetPath: String) =
    LocalContext.current.let { context ->
        remember(context, assetPath) {
            context.assets
                .open(assetPath)
                .use(BitmapFactory::decodeStream)
                .asImageBitmap()
        }
    }
