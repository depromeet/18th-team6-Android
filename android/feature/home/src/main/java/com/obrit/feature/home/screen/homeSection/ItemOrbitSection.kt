@file:Suppress("TooManyFunctions")

package com.obrit.feature.home.screen.homeSection

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.obrit.android.core.designsystem.theme.LocalOBRitTypography
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.android.feature.home.R
import com.obrit.obrit.shared.designsystem.tokens.atom.spacing.AtomSpacing
import com.obrit.obrit.shared.designsystem.tokens.semantic.SemanticColors
import com.obrit.obrit.shared.model.home.HomeItemCard
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 스노우볼 형태의 글래스볼 섹션.
 * 글래스볼 이미지 위에 소모품 아이콘들이 물리 시뮬레이션으로 떠다닌다.
 * 볼을 드래그하면 아이콘이 드래그 방향으로 반응하고, 손을 떼면 마지막 속도로 던져진다.
 *
 * @param icons 볼 안에 표시할 소모품 아이콘 목록. 변경 시 물리 상태가 초기화된다.
 */
@Composable
internal fun ItemOrbitSection(
    items: List<HomeItemCard>,
    normalRatio: Float,
    negativeRatio: Float,
    modifier: Modifier = Modifier,
) {
    val (physicsState, iconOffsets) = rememberGlassBallPhysics(items.size)
    val tilt = rememberGlassBallTilt()

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val ballDiameter = minOf(GlassBallOuterDiameter, maxWidth * 0.8f)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            GlassBallRatioLabel(
                ratio = normalRatio,
                label = "양호",
                color = Color(SemanticColors.Text.Positive.Default),
                modifier = Modifier.weight(1f).padding(top = 68.dp),
            )
            Box(
                modifier = Modifier.size(ballDiameter),
                contentAlignment = Alignment.Center,
            ) {
                GlassBallStatusRing(normalRatio = normalRatio, warningRatio = negativeRatio)
                GlassBallGroundShadow()
                GlassBallContent(
                    items = items,
                    state = physicsState,
                    visualState =
                        GlassBallVisualState(
                            normalRatio = normalRatio,
                            warningRatio = negativeRatio,
                            tilt = tilt,
                        ),
                    iconOffsets = iconOffsets,
                )
            }
            GlassBallRatioLabel(
                ratio = negativeRatio,
                label = "경고",
                color = Color(SemanticColors.Text.Warning.Default),
                modifier = Modifier.weight(1f).padding(top = 68.dp),
            )
        }
    }
}

@Composable
private fun rememberGlassBallPhysics(itemCount: Int): Pair<GlassBallPhysicsState, Array<MutableState<Offset>>> {
    val density = LocalDensity.current
    val physicsState = remember(density, itemCount) { GlassBallPhysicsState(density, itemCount) }
    val iconOffsets =
        remember(density, itemCount) {
            Array(itemCount) { i -> mutableStateOf(physicsState.initOffsets.getOrNull(i) ?: Offset.Zero) }
        }
    LaunchedEffect(itemCount) {
        var lastTime = 0L
        while (true) {
            withFrameNanos { time ->
                val dt = if (lastTime == 0L) DEFAULT_DT else ((time - lastTime) / NANOS_PER_SECOND).coerceIn(0f, MAX_DT)
                lastTime = time
                stepPhysicsFrame(physicsState, dt, iconOffsets)
            }
        }
    }
    return physicsState to iconOffsets
}

/** 중력 센서를 읽어 [-1, 1]로 정규화된 기기 기울기를 반환한다. 센서 미지원 시 Offset.Zero를 반환한다. */
@Composable
private fun rememberGlassBallTilt(): Offset {
    val context = LocalContext.current
    val tilt = remember { mutableStateOf(Offset.Zero) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(SensorManager::class.java)
        val gravitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GRAVITY)

        if (gravitySensor == null) return@DisposableEffect onDispose {}

        val listener =
            object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val normX =
                        (event.values[0] / SensorManager.GRAVITY_EARTH)
                            .coerceIn(-TILT_LIMIT, TILT_LIMIT) / TILT_LIMIT
                    val normY =
                        (event.values[1] / SensorManager.GRAVITY_EARTH)
                            .coerceIn(-TILT_LIMIT, TILT_LIMIT) / TILT_LIMIT
                    val current = tilt.value
                    tilt.value =
                        Offset(
                            x = current.x * TILT_SMOOTH_FACTOR + normX * (1f - TILT_SMOOTH_FACTOR),
                            y = current.y * TILT_SMOOTH_FACTOR + normY * (1f - TILT_SMOOTH_FACTOR),
                        )
                }

                override fun onAccuracyChanged(
                    sensor: Sensor?,
                    accuracy: Int,
                ) = Unit
            }

        sensorManager.registerListener(listener, gravitySensor, SensorManager.SENSOR_DELAY_GAME)
        onDispose { sensorManager.unregisterListener(listener) }
    }

    return tilt.value
}

/** 한 프레임의 물리 시뮬레이션 전체를 단계별로 실행한다. */
private fun stepPhysicsFrame(
    state: GlassBallPhysicsState,
    dt: Float,
    iconOffsets: Array<MutableState<Offset>>,
) {
    // --- 아이콘 선형 충격량 반영 ---
    // 아이콘마다 다른 질량 계수(MassFactorCycle)를 적용해 같은 힘에도 다르게 반응하게 한다.
    // 이를 통해 아이콘들이 동일한 속도로 뭉쳐 움직이지 않고 자연스럽게 분산된다.
    applyPendingImpulse(state)

    // 아이콘이 벗어날 수 있는 최대 반경 = 볼 반경 - 아이콘 충돌 반경
    val maxR = (state.ballRadiusPx - state.iconPhysicsRadiusPx).coerceAtLeast(0f)

    // --- 위치 적분 + 원형 경계 반사 + 선형 감쇠 ---
    stepIconPositions(state, dt, maxR)

    // --- 아이콘 간 반발력 ---
    // 두 아이콘이 REPULSION_MIN_DIST_DP보다 가까우면 서로 밀어내 겹침을 방지한다.
    applyIconRepulsion(state)

    // 물리 상태를 Compose 상태로 복사해 UI 재구성을 유발한다.
    for (i in 0 until state.iconCount) {
        iconOffsets[i].value = Offset(state.posX[i], state.posY[i])
    }
}

/** 버퍼에 쌓인 선형 충격량을 각 아이콘의 속도에 반영한다. */
private fun applyPendingImpulse(state: GlassBallPhysicsState) {
    if (state.pendingImpulse[0] == 0f && state.pendingImpulse[1] == 0f) return
    for (i in 0 until state.iconCount) {
        val massFactor = MassFactorCycle[i % MassFactorCycle.size]
        state.velX[i] += state.pendingImpulse[0] * massFactor
        state.velY[i] += state.pendingImpulse[1] * massFactor
    }
    state.pendingImpulse[0] = 0f
    state.pendingImpulse[1] = 0f
}

/** 속도로 위치를 적분하고, 원형 경계를 벗어난 아이콘을 반사시킨 뒤 선형 감쇠를 적용한다. */
private fun stepIconPositions(
    state: GlassBallPhysicsState,
    dt: Float,
    maxR: Float,
) {
    for (i in 0 until state.iconCount) {
        state.posX[i] += state.velX[i] * dt
        state.posY[i] += state.velY[i] * dt

        // --- 원형 경계 반사 ---
        // 아이콘이 볼 경계를 넘으면 위치를 경계로 되돌리고 속도를 반사시킨다.
        val distSq = state.posX[i] * state.posX[i] + state.posY[i] * state.posY[i]
        if (distSq > maxR * maxR) {
            val dist = sqrt(distSq)
            // 경계 법선 벡터 (중심에서 아이콘 방향으로 향하는 단위벡터)
            val nx = state.posX[i] / dist
            val ny = state.posY[i] / dist
            state.posX[i] = nx * maxR
            state.posY[i] = ny * maxR
            // 법선 방향 속도 성분을 반전하고 반발 계수를 곱해 에너지를 감쇠시킨다.
            val dot = state.velX[i] * nx + state.velY[i] * ny
            state.velX[i] = (state.velX[i] - 2f * dot * nx) * RESTITUTION
            state.velY[i] = (state.velY[i] - 2f * dot * ny) * RESTITUTION
        }

        // 선형 감쇠: 매 프레임 속도를 곱해 마찰로 서서히 멈추게 한다.
        state.velX[i] *= LINEAR_DAMPING
        state.velY[i] *= LINEAR_DAMPING
    }
}

/** 두 아이콘이 최소 거리보다 가까우면 겹친 깊이에 비례한 충격량으로 밀어낸다. */
private fun applyIconRepulsion(state: GlassBallPhysicsState) {
    for (i in 0 until state.iconCount) {
        for (j in i + 1 until state.iconCount) {
            val dx = state.posX[j] - state.posX[i]
            val dy = state.posY[j] - state.posY[i]
            val distSq = dx * dx + dy * dy
            if (distSq < state.repulsionMinDistPx * state.repulsionMinDistPx && distSq > MIN_DIST_SQ_THRESHOLD) {
                val dist = sqrt(distSq)
                // 두 아이콘을 잇는 방향의 단위벡터
                val nx = dx / dist
                val ny = dy / dist
                val impulse = (state.repulsionMinDistPx - dist) * REPULSION_STRENGTH
                state.velX[i] -= nx * impulse
                state.velY[i] -= ny * impulse
                state.velX[j] += nx * impulse
                state.velY[j] += ny * impulse
            }
        }
    }
}

/**
 * 글래스볼 이미지와 내부 아이콘 레이어를 렌더링한다.
 * 드래그 제스처를 감지해 물리 상태(state)에 충격량을 누적한다.
 * 드래그 + 기기 기울기로 3D 원근 회전을 적용한다 (iOS: rotation3DEffect).
 */
@Composable
private fun GlassBallContent(
    items: List<HomeItemCard>,
    state: GlassBallPhysicsState,
    visualState: GlassBallVisualState,
    iconOffsets: Array<MutableState<Offset>>,
) {
    val density = LocalDensity.current
    // 드래그 시작점 기준 누적 이동거리 (iOS: DragGesture.Value.translation)
    val dragTrans = remember { mutableStateOf(Offset.Zero) }
    val maxDragPx = with(density) { GlassBallSize.toPx() * MAX_DRAG_RATIO }

    // iOS: pitchDegrees = -translation.height * 0.032 - screenTilt.height * 2.6
    val pitchDeg = -dragTrans.value.y * DRAG_ROT_SCALE - visualState.tilt.y * TILT_ROT_SCALE

    // iOS: yawDegrees = translation.width * 0.032 + screenTilt.width * 2.6
    val yawDeg = dragTrans.value.x * DRAG_ROT_SCALE + visualState.tilt.x * TILT_ROT_SCALE

    // 외부 Box: 터치 영역 (클립 없이 전체 크기)
    Box(
        modifier =
            Modifier
                .size(GlassBallSize)
                .glassBallDrag(state = state, dragTrans = dragTrans, maxDragPx = maxDragPx),
        contentAlignment = Alignment.Center,
    ) {
        GlassBallLayer(
            visualState = visualState,
            rotationState = GlassBallRotationState(pitchDeg = pitchDeg, yawDeg = yawDeg),
            items = items,
            iconOffsets = iconOffsets,
        )
    }
}

@Composable
private fun GlassBallLayer(
    visualState: GlassBallVisualState,
    rotationState: GlassBallRotationState,
    items: List<HomeItemCard>,
    iconOffsets: Array<MutableState<Offset>>,
) {
    Box(
        modifier =
            Modifier
                .size(GlassBallSize)
                .graphicsLayer {
                    rotationX = rotationState.pitchDeg
                    rotationY = rotationState.yawDeg
                    cameraDistance = CAMERA_DISTANCE_SCALE * this.density
                    compositingStrategy = CompositingStrategy.Offscreen
                    clip = true
                    shape = CircleShape
                },
        contentAlignment = Alignment.Center,
    ) {
        // clip은 graphicsLayer 안에서 지정해야 한다. Modifier.clip(CircleShape)은 내부적으로 별도의
        // graphicsLayer를 생성하기 때문에, 그 레이어에 CompositingStrategy.Offscreen이 없으면
        // sphere가 아닌 화면 배경을 기준으로 합성될 수 있다.
        GlassBallInternalShadow(
            normalRatio = visualState.normalRatio,
            warningRatio = visualState.warningRatio,
        )
        GlassBallTextureOverlay()
        GlassBallIconLayer(items = items, iconOffsets = iconOffsets)
    }
}

private data class GlassBallVisualState(
    val normalRatio: Float,
    val warningRatio: Float,
    val tilt: Offset,
)

private data class GlassBallRotationState(
    val pitchDeg: Float,
    val yawDeg: Float,
)

private fun Modifier.glassBallDrag(
    state: GlassBallPhysicsState,
    dragTrans: MutableState<Offset>,
    maxDragPx: Float,
): Modifier =
    pointerInput(state, maxDragPx) {
        detectDragGestures(
            onDragStart = {
                state.prevDrag[0] = 0f
                state.prevDrag[1] = 0f
                dragTrans.value = Offset.Zero
            },
            onDragEnd = {
                state.pendingImpulse[0] += state.prevDrag[0] * LINEAR_IMPULSE_SCALE
                state.pendingImpulse[1] += state.prevDrag[1] * LINEAR_IMPULSE_SCALE
                dragTrans.value = Offset.Zero
            },
        ) { change, dragAmount ->
            change.consume()
            val accelX = dragAmount.x - state.prevDrag[0]
            val accelY = dragAmount.y - state.prevDrag[1]
            state.prevDrag[0] = dragAmount.x
            state.prevDrag[1] = dragAmount.y
            state.pendingImpulse[0] += accelX * LINEAR_IMPULSE_SCALE
            state.pendingImpulse[1] += accelY * LINEAR_IMPULSE_SCALE

            val rawX = dragTrans.value.x + dragAmount.x
            val rawY = dragTrans.value.y + dragAmount.y
            val dist = hypot(rawX, rawY)
            val scale = if (dist > maxDragPx) maxDragPx / dist else 1f
            dragTrans.value = Offset(rawX * scale, rawY * scale)
        }
    }

/** 물리 시뮬레이션 결과로 계산된 offset에 따라 각 아이콘을 볼 내부에 배치한다. */
@Composable
private fun GlassBallIconLayer(
    items: List<HomeItemCard>,
    iconOffsets: Array<MutableState<Offset>>,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        items.forEachIndexed { index, item ->
            PhysicsIcon(
                iconUrl = item.iconUrl,
                // 아이콘별 고정 기울기: 인덱스에 따라 순환 적용해 자연스럽게 배치
                staticRotation = StaticRotationCycle[index % StaticRotationCycle.size],
                offset = iconOffsets[index].value,
            )
        }
    }
}

/**
 * 물리 시뮬레이션 위치(offset)에 따라 볼 중심 기준으로 아이콘을 배치하는 컴포저블.
 * staticRotation으로 고정 기울기를 적용해 정렬된 느낌을 줄인다.
 */
@Composable
private fun BoxScope.PhysicsIcon(
    iconUrl: String,
    staticRotation: Float,
    offset: Offset,
) {
    val density = LocalDensity.current
    AsyncImage(
        model = iconUrl,
        contentDescription = null,
        modifier =
            Modifier
                .align(Alignment.Center)
                .offset(
                    x = with(density) { offset.x.toDp() },
                    y = with(density) { offset.y.toDp() },
                ).size(ORBIT_ICON_SIZE)
                .rotate(staticRotation),
    )
}

/**
 * 유리 질감 텍스처 레이어 (iOS: HomeOrbGlassTextureOverlay).
 * hardLight 합성으로 구체 배경 위에 프로스트 유리 효과를 만든다.
 * 위→아래 그래디언트 마스크로 하단으로 갈수록 텍스처가 희미해진다.
 */
@Composable
private fun GlassBallTextureOverlay() {
    Image(
        painter = painterResource(R.drawable.ic_glass_plus_lighter),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier =
            Modifier
                .size(GlassBallInternalShadowSize)
                .graphicsLayer {
                    blendMode = BlendMode.Hardlight
                    compositingStrategy = CompositingStrategy.Offscreen
                    clip = true
                    shape = CircleShape
                }.drawWithContent {
                    drawContent()
                    drawTextureMask()
                },
    )
}

private fun DrawScope.drawTextureMask() {
    drawRect(
        brush =
            Brush.verticalGradient(
                colorStops =
                    arrayOf(
                        0f to Color.Transparent,
                        TEXTURE_MASK_STOP_1 to Color.Black.copy(alpha = TEXTURE_MASK_ALPHA_038),
                        TEXTURE_MASK_STOP_2 to Color.Black.copy(alpha = TEXTURE_MASK_ALPHA_068),
                        1f to Color.Black.copy(alpha = TEXTURE_MASK_ALPHA_100),
                    ),
            ),
        blendMode = BlendMode.DstOut,
    )
}

/**
 * 상태 비율에 따라 색이 결정되는 구체 배경 레이어 (iOS: HomeOrbInternalShadow).
 * 왼쪽 = 양호(초록), 오른쪽 = 경고(빨강)의 수평 그래디언트로 채워지며,
 * 중심부 어두운 그림자와 우상단 하이라이트로 3D 구체 느낌을 준다.
 */
@Composable
private fun GlassBallInternalShadow(
    normalRatio: Float,
    warningRatio: Float,
) {
    val sphereBrush = remember(normalRatio, warningRatio) { buildSphereBrush(normalRatio, warningRatio) }
    Canvas(
        modifier =
            Modifier
                .size(GlassBallInternalShadowSize)
                .alpha(INTERNAL_SHADOW_OPACITY),
    ) {
        val diam = size.minDimension

        // 1. 수평 그래디언트 배경 구체 (양호=초록/왼쪽, 경고=빨강/오른쪽)
        drawCircle(brush = sphereBrush)
        drawInnerDarkShadow(diam)
        drawInnerLightHighlight(diam)
        drawTrailingHighlight(diam)
        drawTopHighlight(diam)
        drawBottomShadow(diam)
    }
}

private fun DrawScope.drawInnerDarkShadow(diameter: Float) {
    val darkCircleRadius = diameter * INNER_DARK_DIAMETER_RATIO / 2f
    val blurRadius = diameter * INNER_DARK_BLUR_RATIO
    drawIntoCanvas { canvas ->
        val paint =
            Paint().apply {
                asFrameworkPaint().apply {
                    isAntiAlias = true
                    color = InnerShadowCoreColor.toArgb()
                    maskFilter =
                        android.graphics.BlurMaskFilter(
                            blurRadius,
                            android.graphics.BlurMaskFilter.Blur.NORMAL,
                        )
                }
            }
        canvas.drawCircle(Offset(size.width / 2f, size.height / 2f), darkCircleRadius, paint)
    }
}

private fun DrawScope.drawInnerLightHighlight(diameter: Float) {
    val lightRadius = diameter * INNER_LIGHT_DIAMETER_RATIO / 2f + diameter * INNER_LIGHT_BLUR_RATIO
    drawCircle(
        brush =
            Brush.linearGradient(
                colors = listOf(Color.White, Color.Transparent),
                start = Offset(size.width * INNER_LIGHT_START_X, size.height * INNER_LIGHT_START_Y),
                end = Offset(size.width * INNER_LIGHT_END_X, size.height * INNER_LIGHT_END_Y),
            ),
        radius = lightRadius,
    )
}

private fun DrawScope.drawTrailingHighlight(diameter: Float) {
    val highlightWidth = diameter * TRAILING_HIGHLIGHT_WIDTH_RATIO
    val highlightHeight = diameter * TRAILING_HIGHLIGHT_HEIGHT_RATIO
    val centerX = diameter * TRAILING_HIGHLIGHT_CENTER_X_RATIO
    val centerY = diameter * TRAILING_HIGHLIGHT_CENTER_Y_RATIO
    drawOval(
        brush =
            Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = TRAILING_HIGHLIGHT_OPACITY), Color.Transparent),
                center = Offset(centerX, centerY),
                radius = maxOf(highlightWidth, highlightHeight) / 2f + diameter * TRAILING_HIGHLIGHT_BLUR_RATIO,
            ),
        topLeft = Offset(centerX - highlightWidth / 2f, centerY - highlightHeight / 2f),
        size = Size(highlightWidth, highlightHeight),
    )
}

private fun DrawScope.drawTopHighlight(diameter: Float) {
    val highlightWidth = diameter * TOP_HIGHLIGHT_WIDTH_RATIO
    val highlightHeight = diameter * TOP_HIGHLIGHT_HEIGHT_RATIO
    val centerX = diameter * TOP_HIGHLIGHT_CENTER_X_RATIO
    val centerY = diameter * TOP_HIGHLIGHT_CENTER_Y_RATIO
    drawOval(
        brush =
            Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = TOP_HIGHLIGHT_OPACITY), Color.Transparent),
                center = Offset(centerX, centerY),
                radius = maxOf(highlightWidth, highlightHeight) / 2f + diameter * TOP_HIGHLIGHT_BLUR_RATIO,
            ),
        topLeft = Offset(centerX - highlightWidth / 2f, centerY - highlightHeight / 2f),
        size = Size(highlightWidth, highlightHeight),
    )
}

private fun DrawScope.drawBottomShadow(diameter: Float) {
    val shadowDiameter = diameter * BOTTOM_SHADOW_WIDTH_RATIO
    val shadowHeight = diameter * BOTTOM_SHADOW_HEIGHT_RATIO
    val centerX = size.width * BOTTOM_SHADOW_CENTER_X
    val centerY = size.height + shadowHeight * BOTTOM_SHADOW_OFFSET_Y_RATIO
    drawOval(
        brush =
            Brush.radialGradient(
                colors = listOf(Color.Black.copy(alpha = BOTTOM_SHADOW_PEAK_ALPHA), Color.Transparent),
                center = Offset(centerX, centerY),
                radius = shadowDiameter * BOTTOM_SHADOW_BLUR_RATIO + shadowDiameter / 2f,
            ),
        topLeft = Offset(centerX - shadowDiameter / 2f, centerY - shadowHeight / 2f),
        size = Size(shadowDiameter, shadowHeight),
    )
}

/** normalRatio/warningRatio 비율에 따라 좌(양호=초록) → 우(경고=빨강) 수평 그래디언트를 만든다. */
private fun buildSphereBrush(
    normalRatio: Float,
    warningRatio: Float,
): Brush {
    val total = maxOf(normalRatio + warningRatio, MIN_RATIO_TOTAL)
    val positiveShare = (normalRatio / total).coerceIn(0f, 1f)
    val warningShare = 1f - positiveShare
    return when {
        warningShare <= 0f -> SolidColor(GlassBallPositiveColor)
        positiveShare <= 0f -> SolidColor(GlassBallWarningColor)
        else -> {
            val transition = minOf(GRADIENT_TRANSITION_WIDTH, positiveShare / 2f, warningShare / 2f)
            Brush.horizontalGradient(
                0f to GlassBallPositiveColor,
                maxOf(0f, positiveShare - transition) to GlassBallPositiveColor,
                minOf(1f, positiveShare + transition) to GlassBallWarningColor,
                1f to GlassBallWarningColor,
            )
        }
    }
}

/** 상태 비율에 따라 경고/양호 색상의 스윕 그래디언트로 링을 렌더링한다. */
@Composable
private fun GlassBallStatusRing(
    normalRatio: Float,
    warningRatio: Float,
) {
    val ringBrush = remember(normalRatio, warningRatio) { buildRingBrush(normalRatio, warningRatio) }
    Canvas(modifier = Modifier.size(GlassBallOuterDiameter)) {
        val strokeWidth = RingLineWidth.toPx()
        drawCircle(
            brush = ringBrush,
            radius = size.minDimension / 2f - strokeWidth / 2f,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
        )
    }
}

/** 링 아래쪽에 방사형 그래디언트 섀도를 그려 글래스볼이 링 위에 떠 있는 느낌을 낸다. */
@Composable
private fun GlassBallGroundShadow() {
    Canvas(modifier = Modifier.size(GlassBallOuterDiameter)) {
        val diam = size.minDimension
        val shadowRadius = diam * GROUND_SHADOW_DIAMETER_RATIO / 2f + diam * GROUND_SHADOW_BLUR_RATIO
        val shadowCenter = Offset(diam * GROUND_SHADOW_CENTER_X_RATIO, diam * GROUND_SHADOW_CENTER_Y_RATIO)
        drawCircle(
            brush =
                Brush.radialGradient(
                    colors = listOf(GroundShadowColor, Color.Transparent),
                    center = shadowCenter,
                    radius = shadowRadius,
                ),
            center = shadowCenter,
            radius = shadowRadius,
        )
    }
}

/** normalRatio/warningRatio 비율에 맞춰 경고(빨강)와 양호(초록) 영역이 전환되는 스윕 그래디언트를 만든다. */
private fun buildRingBrush(
    normalRatio: Float,
    warningRatio: Float,
): Brush {
    val total = maxOf(normalRatio + warningRatio, MIN_RATIO_TOTAL)
    val positiveShare = (normalRatio / total).coerceIn(0f, 1f)
    val warningShare = 1f - positiveShare
    return when {
        warningShare <= 0f -> SolidColor(GlassBallPositiveColor)
        positiveShare <= 0f -> SolidColor(GlassBallWarningColor)
        else -> {
            val transitionArc = minOf(STATUS_RING_TRANSITION_ARC, warningShare / 2f, positiveShare / 2f)
            val warningArcHalf = warningShare / 2f
            Brush.sweepGradient(
                0f to GlassBallWarningColor,
                (warningArcHalf - transitionArc) to GlassBallWarningColor,
                (warningArcHalf + transitionArc) to GlassBallPositiveColor,
                (1f - warningArcHalf - transitionArc) to GlassBallPositiveColor,
                (1f - warningArcHalf + transitionArc) to GlassBallWarningColor,
                1f to GlassBallWarningColor,
            )
        }
    }
}

/** 아이콘들의 초기 위치를 원 위에 균등하게 배치한다. */
private fun buildInitialOffsets(
    iconCount: Int,
    radiusPx: Float,
): Array<Offset> {
    if (iconCount == 0) return emptyArray()
    return Array(iconCount) { i ->
        // 360°를 아이콘 수로 나눠 등간격 배치, 45° 오프셋으로 정렬 편향 제거
        val angle = 2f * PI.toFloat() * i / iconCount + PI.toFloat() / INITIAL_ANGLE_QUADRANT
        Offset(cos(angle) * radiusPx, sin(angle) * radiusPx)
    }
}

/**
 * 물리 시뮬레이션에 필요한 모든 가변 배열 상태와 사전 계산된 반경 값을 하나의 객체로 묶어 관리한다.
 *
 * @param density 반경 값(px 변환)에 사용할 화면 밀도
 * @param iconCount 시뮬레이션 대상 아이콘 수
 */
private class GlassBallPhysicsState(
    density: Density,
    val iconCount: Int,
) {
    // 볼 이미지의 실제 내부 영역 반경 (px)
    val ballRadiusPx = with(density) { GlassBallSize.toPx() / 2f * BALL_ICON_AREA_RATIO }

    // 충돌 감지에 사용할 아이콘 반경. 실제 이미지보다 작게 설정해 볼 내부 이동 공간 확보.
    val iconPhysicsRadiusPx = with(density) { ICON_PHYSICS_RADIUS_DP.dp.toPx() }

    // 아이콘 간 반발력이 작동하기 시작하는 최소 거리 (px)
    val repulsionMinDistPx = with(density) { REPULSION_MIN_DIST_DP.dp.toPx() }

    // 아이콘들의 초기 위치 (원 위에 균등 배치). Compose state 초기화에도 사용된다.
    val initOffsets = buildInitialOffsets(iconCount, with(density) { 22.dp.toPx() })

    // 다음 프레임에 반영할 선형 충격량 (x, y). 드래그 스레드 → 물리 루프 전달용 버퍼.
    val pendingImpulse = floatArrayOf(0f, 0f)

    // 이전 프레임의 드래그 델타. 가속도(속도 변화량) 계산에 사용한다.
    val prevDrag = floatArrayOf(0f, 0f)

    // 각 아이콘의 위치 (px, 볼 중심 기준)
    val posX = FloatArray(iconCount) { i -> initOffsets.getOrNull(i)?.x ?: 0f }
    val posY = FloatArray(iconCount) { i -> initOffsets.getOrNull(i)?.y ?: 0f }

    // 각 아이콘의 속도 (px/s)
    val velX = FloatArray(iconCount) { 0f }
    val velY = FloatArray(iconCount) { 0f }
}

// 글래스볼 이미지 크기 (iOS: HomeOrbMetrics.glassBallDiameter = 200)
private val GlassBallSize = 200.dp

// 상태 링과 그라운드 섀도를 포함하는 외곽 크기 (iOS: HomeOrbMetrics.outerDiameter = S40 + S16 = 224)
private val GlassBallOuterDiameter = 224.dp

// 상태 링 선 굵기 (iOS: HomeOrbMetrics.ringLineWidth = S1_5 = 6)
private val RingLineWidth = 6.dp

// 상태 링 색상 전환 구간 크기 (iOS: HomeOrbVisualConfig.statusRingTransitionArc)
private const val STATUS_RING_TRANSITION_ARC = 0.05f

// normalRatio + warningRatio 합산 최솟값 (0 나누기 방지, iOS: HomeOrbVisualConfig.minimumRatioTotal)
private const val MIN_RATIO_TOTAL = 0.0001f

private val GlassBallPositiveColor = Color(SemanticColors.Background.Positive.Default)
private val GlassBallWarningColor = Color(SemanticColors.Background.Warning.Default)

// 그라운드 섀도 중심/크기 비율 (iOS: HomeOrbGlassMetrics)
private const val GROUND_SHADOW_COLOR_HEX = 0x661C1C21
private val GroundShadowColor = Color(GROUND_SHADOW_COLOR_HEX)
private const val GROUND_SHADOW_DIAMETER_RATIO = 0.7112f
private const val GROUND_SHADOW_CENTER_X_RATIO = 0.4978f
private const val GROUND_SHADOW_CENTER_Y_RATIO = 0.8756f
private const val GROUND_SHADOW_BLUR_RATIO = 0.1778f

// 내부 구체 크기 (iOS: HomeOrbMetrics.internalShadowDiameter = 198)
private val GlassBallInternalShadowSize = 198.dp

// 내부 구체 전체 불투명도 (iOS: HomeOrbGlassMetrics.internalShadowOpacity = 0.62)
private const val INTERNAL_SHADOW_OPACITY = 0.62f

// 구체 수평 그래디언트 전환 폭 (iOS: HomeOrbVisualConfig.gradientTransitionWidth = 0.12)
private const val GRADIENT_TRANSITION_WIDTH = 0.12f

// 내부 어두운 그림자 비율 (iOS: HomeOrbGlassMetrics inner dark circle)
private const val INNER_SHADOW_CORE_COLOR_HEX = 0xFF1C1C21
private val InnerShadowCoreColor = Color(INNER_SHADOW_CORE_COLOR_HEX)
private const val INNER_DARK_DIAMETER_RATIO = 0.804f // 159.289 / 198
private const val INNER_DARK_BLUR_RATIO = 0.201f // 39.822 / 198

// 내부 밝은 하이라이트 비율 (iOS: HomeOrbGlassMetrics inner light circle)
private const val INNER_LIGHT_DIAMETER_RATIO = 0.854f // 169.244 / 198
private const val INNER_LIGHT_BLUR_RATIO = 0.060f // 11.947 / 198
private const val INNER_LIGHT_START_X = 1f // UnitPoint(x:1, y:0.13)
private const val INNER_LIGHT_START_Y = 0.13f
private const val INNER_LIGHT_END_X = 0.22f // UnitPoint(x:0.22, y:1)
private const val INNER_LIGHT_END_Y = 1f

// 우측 세로 하이라이트 비율 (iOS: HomeOrbGlassMetrics trailingHighlight)
private const val TRAILING_HIGHLIGHT_OPACITY = 0.30f
private const val TRAILING_HIGHLIGHT_WIDTH_RATIO = 70f / 198f // 0.354
private const val TRAILING_HIGHLIGHT_HEIGHT_RATIO = 162f / 198f // 0.818
private const val TRAILING_HIGHLIGHT_CENTER_X_RATIO = 181f / 198f // 198-35+18=181
private const val TRAILING_HIGHLIGHT_CENTER_Y_RATIO = 107f / 198f // 99+8=107
private const val TRAILING_HIGHLIGHT_BLUR_RATIO = 18f / 198f // 0.091

// 상단 가로 하이라이트 비율 (iOS: HomeOrbGlassMetrics topHighlight)
private const val TOP_HIGHLIGHT_OPACITY = 0.18f
private const val TOP_HIGHLIGHT_WIDTH_RATIO = 104f / 198f // 0.525
private const val TOP_HIGHLIGHT_HEIGHT_RATIO = 54f / 198f // 0.273
private const val TOP_HIGHLIGHT_CENTER_X_RATIO = 0.5f
private const val TOP_HIGHLIGHT_CENTER_Y_RATIO = 39f / 198f // 27+12=39
private const val TOP_HIGHLIGHT_BLUR_RATIO = 16f / 198f // 0.081

// 하단 타원형 그림자 비율 (iOS: HomeOrbGlassMetrics bottomShadow*)
private const val BOTTOM_SHADOW_WIDTH_RATIO = 0.758f // 150 / 198
private const val BOTTOM_SHADOW_HEIGHT_RATIO = 0.434f // 86 / 198
private const val BOTTOM_SHADOW_CENTER_X = 0.46f
private const val BOTTOM_SHADOW_OFFSET_Y_RATIO = -0.091f // -18 / 198
private const val BOTTOM_SHADOW_BLUR_RATIO = 0.071f // 14 / 198
private const val BOTTOM_SHADOW_PEAK_ALPHA = 0.58f

// 볼 이미지 중 아이콘이 실제로 존재하는 내부 영역 비율 (이미지 테두리 공간 제외)
private const val BALL_ICON_AREA_RATIO = 0.62f

// 아이콘 충돌 반경(dp). 이미지보다 작게 설정해 볼 안 이동 공간을 확보한다.
private const val ICON_PHYSICS_RADIUS_DP = 15f

// 아이콘 간 반발이 시작되는 최소 거리(dp)
private const val REPULSION_MIN_DIST_DP = 48f

// 반발력 세기. 클수록 아이콘이 더 강하게 튕겨나간다.
private const val REPULSION_STRENGTH = 0.3f

// 드래그 가속도 → 아이콘 속도 변환 비율
private const val LINEAR_IMPULSE_SCALE = 5f

// 아이콘 선형 감쇠 계수. 매 프레임 속도에 곱해 마찰로 멈추게 한다. (0~1, 1에 가까울수록 느리게 멈춤)
private const val LINEAR_DAMPING = 0.97f

// 경계 반사 시 에너지 보존 비율. 낮을수록 벽에 부딪힐 때 많이 감속된다.
private const val RESTITUTION = 0.35f
private const val NANOS_PER_SECOND = 1_000_000_000f

// dt 계산 전 첫 프레임에 사용할 기본 시간 간격(초)
private const val DEFAULT_DT = 0.016f

// 프레임 스킵 등으로 dt가 비정상적으로 커지는 경우를 방지하는 최대값(초)
private const val MAX_DT = 0.05f

// 초기 배치 각도를 45° 오프셋하기 위한 분모 (π/4 = 45°)
private const val INITIAL_ANGLE_QUADRANT = 4f

// 반발력 계산 시 분모 0 방지를 위한 최소 거리 제곱 임계값
private const val MIN_DIST_SQ_THRESHOLD = 0.01f

// 기기 기울기 정규화 한계값 (iOS: HomeOrbTiltController.tiltLimit = 0.42)
private const val TILT_LIMIT = 0.42f

// 기울기 평활화 계수 (iOS: tilt * 0.84 + next * 0.16)
private const val TILT_SMOOTH_FACTOR = 0.84f

// 드래그 이동거리 → 3D 회전 각도 변환 비율 (iOS: translation * 0.032)
private const val DRAG_ROT_SCALE = 0.032f

// 기기 기울기 → 3D 회전 각도 변환 비율 (iOS: screenTilt * 2.6)
private const val TILT_ROT_SCALE = 2.6f

// 드래그 최대 반경 비율 (iOS: HomeOrbInteractionConfig.maxDragDistanceRatio = 92 / 200 = 0.46)
private const val MAX_DRAG_RATIO = 0.46f

// 3D 원근 카메라 거리 배율. density를 곱해 px 단위로 사용 (iOS: perspective = 0.55 근사)
private const val CAMERA_DISTANCE_SCALE = 12f

// 아이콘에 순환 적용할 고정 기울기(도). 아이콘 수에 관계없이 인덱스 % 4로 적용된다.
@Suppress("MagicNumber")
private val StaticRotationCycle = floatArrayOf(25f, -20f, -15f, 15f)

// 아이콘에 순환 적용할 질량 계수. 작을수록 같은 힘에 더 많이 움직인다.
@Suppress("MagicNumber")
private val MassFactorCycle = floatArrayOf(0.7f, 1.0f, 1.4f, 0.9f)

private val ORBIT_ICON_SIZE = AtomSpacing.S24.dp

// 텍스처 그래디언트 마스크 stop 위치 (iOS: HomeOrbGlassMetrics.textureMaskStops)
private const val TEXTURE_MASK_STOP_1 = 0.38f
private const val TEXTURE_MASK_STOP_2 = 0.68f

// 텍스처 그래디언트 마스크 알파 (DstOut 기준: 1 - iOS 가시성 비율)
private const val TEXTURE_MASK_ALPHA_038 = 0.44f // 1 - 0.56
private const val TEXTURE_MASK_ALPHA_068 = 0.82f // 1 - 0.18
private const val TEXTURE_MASK_ALPHA_100 = 0.94f // 1 - 0.06

@Composable
private fun GlassBallRatioLabel(
    ratio: Float,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val typography = LocalOBRitTypography.current
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "${(ratio * 100).roundToInt()}%",
            style = typography.xl3.copy(fontWeight = FontWeight.Bold),
            color = color,
        )
        Text(
            text = label,
            style = typography.xs,
            color = color,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1D1B20)
@Composable
private fun ItemOrbitSectionPreview() {
    OBRitTheme {
        ItemOrbitSection(
            items = emptyList(),
            normalRatio = 0.62f,
            negativeRatio = 0.38f,
        )
    }
}
