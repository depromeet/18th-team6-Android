@file:Suppress("TooManyFunctions")

package com.obrit.feature.home.screen.section

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.R
import com.obrit.android.core.designsystem.theme.OBRitTheme
import kotlin.math.PI
import kotlin.math.cos
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
internal fun ConsumableOrbit(
    icons: List<ConsumableIcon>,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val iconCount = icons.size

    // 물리 상태 전체를 density와 iconCount 기준으로 관리한다.
    val physicsState = remember(density, iconCount) { GlassBallPhysicsState(density, iconCount) }

    // Compose 재구성을 유발하는 상태. 물리 루프에서 매 프레임 업데이트한다.
    val ballAngleState = remember { mutableFloatStateOf(0f) }
    val iconOffsets =
        remember(density, iconCount) {
            Array(iconCount) { i -> mutableStateOf(physicsState.initOffsets.getOrNull(i) ?: Offset.Zero) }
        }

    // 물리 시뮬레이션 루프: withFrameNanos로 화면 주사율(≈60fps)에 동기화한다.
    LaunchedEffect(iconCount) {
        var lastTime = 0L
        while (true) {
            withFrameNanos { time ->
                // 프레임 간 경과 시간(초). 첫 프레임은 기본값 사용, 이후 실제 경과 시간을 사용한다.
                val dt = if (lastTime == 0L) DEFAULT_DT else ((time - lastTime) / NANOS_PER_SECOND).coerceIn(0f, MAX_DT)
                lastTime = time
                stepPhysicsFrame(physicsState, dt, iconOffsets, ballAngleState)
            }
        }
    }

    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        GlassBallContent(icons = icons, state = physicsState, ballAngle = ballAngleState.floatValue, iconOffsets = iconOffsets)
    }
}

/** 한 프레임의 물리 시뮬레이션 전체를 단계별로 실행한다. */
private fun stepPhysicsFrame(
    state: GlassBallPhysicsState,
    dt: Float,
    iconOffsets: Array<MutableState<Offset>>,
    ballAngleState: MutableFloatState,
) {
    // --- 볼 회전 업데이트 ---
    stepBallRotation(state, dt)

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
    ballAngleState.floatValue = state.ball[0]
}

/** 드래그에서 쌓인 각도 충격량을 각속도에 반영하고, 각속도로 볼 각도를 적분한다. */
private fun stepBallRotation(
    state: GlassBallPhysicsState,
    dt: Float,
) {
    state.ball[1] += state.pendingAngImpulse[0]
    state.pendingAngImpulse[0] = 0f
    state.ball[0] += state.ball[1] * dt
    // 각속도에 감쇠를 적용해 서서히 멈춘다.
    state.ball[1] *= ANGULAR_DAMPING
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
 */
@Composable
private fun GlassBallContent(
    icons: List<ConsumableIcon>,
    state: GlassBallPhysicsState,
    ballAngle: Float,
    iconOffsets: Array<MutableState<Offset>>,
) {
    Box(
        modifier =
            Modifier
                .size(GlassBallSize)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            // 드래그 시작 시 이전 델타를 초기화해 첫 프레임 가속도 계산 오류를 방지한다.
                            state.prevDrag[0] = 0f
                            state.prevDrag[1] = 0f
                        },
                        onDragEnd = {
                            // 손을 뗄 때 마지막 드래그 속도 방향으로 충격량을 줘 던지는 효과를 낸다.
                            state.pendingImpulse[0] += state.prevDrag[0] * LINEAR_IMPULSE_SCALE
                            state.pendingImpulse[1] += state.prevDrag[1] * LINEAR_IMPULSE_SCALE
                        },
                    ) { change, dragAmount ->
                        change.consume()
                        // 가속도 = 이번 프레임 델타 - 이전 프레임 델타 (속도 변화량)
                        // 일정 속도로 드래그할 때는 가속도 ≈ 0이므로 추가 힘이 없고,
                        // 드래그를 시작하거나 방향을 바꿀 때만 충격량이 발생한다.
                        val accelX = dragAmount.x - state.prevDrag[0]
                        val accelY = dragAmount.y - state.prevDrag[1]
                        state.prevDrag[0] = dragAmount.x
                        state.prevDrag[1] = dragAmount.y
                        // 수평 드래그 양에 비례해 볼 회전 각속도를 추가한다.
                        state.pendingAngImpulse[0] += dragAmount.x * ROTATION_SENSITIVITY
                        // 드래그 가속도 방향으로 아이콘에 충격량을 전달한다.
                        state.pendingImpulse[0] += accelX * LINEAR_IMPULSE_SCALE
                        state.pendingImpulse[1] += accelY * LINEAR_IMPULSE_SCALE
                    }
                },
        contentAlignment = Alignment.Center,
    ) {
        // 글래스볼 이미지: 드래그에 따라 회전한다.
        Image(
            painter = painterResource(id = R.drawable.ic_glass_plus_lighter),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().rotate(ballAngle),
            contentScale = ContentScale.Fit,
        )
        GlassBallIconLayer(icons = icons, iconOffsets = iconOffsets)
    }
}

/** 물리 시뮬레이션 결과로 계산된 offset에 따라 각 아이콘을 볼 내부에 배치한다. */
@Composable
private fun BoxScope.GlassBallIconLayer(
    icons: List<ConsumableIcon>,
    iconOffsets: Array<MutableState<Offset>>,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        icons.forEachIndexed { index, icon ->
            PhysicsIcon(
                drawableRes = icon.drawableRes,
                width = icon.width,
                height = icon.height,
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
    @DrawableRes drawableRes: Int,
    width: Dp,
    height: Dp,
    staticRotation: Float,
    offset: Offset,
) {
    val density = LocalDensity.current
    Image(
        painter = painterResource(id = drawableRes),
        contentDescription = null,
        modifier =
            Modifier
                .align(Alignment.Center)
                .offset(
                    x = with(density) { offset.x.toDp() },
                    y = with(density) { offset.y.toDp() },
                ).size(width = width, height = height)
                .rotate(staticRotation),
    )
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

    // ball[0] = 현재 회전 각도(도), ball[1] = 각속도(도/s)
    val ball = floatArrayOf(0f, 0f)

    // 다음 프레임에 반영할 각도 충격량. 드래그 스레드 → 물리 루프 전달용 버퍼.
    val pendingAngImpulse = floatArrayOf(0f)

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

// 글래스볼 이미지 크기
private val GlassBallSize = 200.dp

// 볼 이미지 중 아이콘이 실제로 존재하는 내부 영역 비율 (이미지 테두리 공간 제외)
private const val BALL_ICON_AREA_RATIO = 0.62f

// 아이콘 충돌 반경(dp). 이미지보다 작게 설정해 볼 안 이동 공간을 확보한다.
private const val ICON_PHYSICS_RADIUS_DP = 15f

// 아이콘 간 반발이 시작되는 최소 거리(dp)
private const val REPULSION_MIN_DIST_DP = 48f

// 반발력 세기. 클수록 아이콘이 더 강하게 튕겨나간다.
private const val REPULSION_STRENGTH = 0.3f

// 수평 드래그 → 볼 회전 변환 비율
private const val ROTATION_SENSITIVITY = 0.3f

// 드래그 가속도 → 아이콘 속도 변환 비율
private const val LINEAR_IMPULSE_SCALE = 5f

// 볼 회전 감쇠 계수. 매 프레임 각속도에 곱해 마찰로 멈추게 한다. (0~1, 1에 가까울수록 느리게 멈춤)
private const val ANGULAR_DAMPING = 0.90f

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

// 아이콘에 순환 적용할 고정 기울기(도). 아이콘 수에 관계없이 인덱스 % 4로 적용된다.
@Suppress("MagicNumber")
private val StaticRotationCycle = floatArrayOf(25f, -20f, -15f, 15f)

// 아이콘에 순환 적용할 질량 계수. 작을수록 같은 힘에 더 많이 움직인다.
@Suppress("MagicNumber")
private val MassFactorCycle = floatArrayOf(0.7f, 1.0f, 1.4f, 0.9f)

private val ToothbrushWidth = 70.dp
private val ToothbrushHeight = 70.dp
private val RazorWidth = 58.dp
private val RazorHeight = 79.dp
private val TowelWidth = 68.dp
private val TowelHeight = 49.dp
private val DetergentWidth = 36.dp
private val DetergentHeight = 54.dp

@Preview(showBackground = true, backgroundColor = 0xFF1D1B20)
@Composable
private fun ConsumableOrbitPreview() {
    OBRitTheme {
        ConsumableOrbit(
            icons =
                listOf(
                    ConsumableIcon(R.drawable.ic_towel, TowelWidth, TowelHeight),
                    ConsumableIcon(R.drawable.ic_toothbrush, ToothbrushWidth, ToothbrushHeight),
                    ConsumableIcon(R.drawable.ic_detergent, DetergentWidth, DetergentHeight),
                    ConsumableIcon(R.drawable.ic_razor, RazorWidth, RazorHeight),
                ),
        )
    }
}
