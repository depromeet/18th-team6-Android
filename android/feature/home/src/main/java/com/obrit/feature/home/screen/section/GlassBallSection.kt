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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.obrit.android.core.designsystem.theme.OBRitTheme
import com.obrit.android.core.designsystem.R
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
internal fun GlassBallSection(
    icons: List<ConsumableIcon>,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val iconCount = icons.size

    // 볼 이미지의 실제 내부 영역 반경
    val ballRadiusPx = remember(density) {
        with(density) { GlassBallSize.toPx() / 2f * BallIconAreaRatio }
    }
    // 충돌 감지에 사용할 아이콘 반경
    // 실제 이미지보다 작게 설정해 볼 내부 이동 공간 확보
    val iconPhysicsRadiusPx = remember(density) {
        with(density) { IconPhysicsRadiusDp.dp.toPx() }
    }
    // 아이콘 간 반발력이 작동하기 시작하는 최소 거리
    val repulsionMinDistPx = remember(density) {
        with(density) { RepulsionMinDistDp.dp.toPx() }
    }
    // 아이콘들의 초기 위치: 원 위에 균등하게 배치해 시작 시 겹치지 않도록
    val initOffsets = remember(density, iconCount) {
        if (iconCount == 0) return@remember emptyArray()
        val r = with(density) { 22.dp.toPx() }
        Array(iconCount) { i ->
            // 360°를 아이콘 수로 나눠 등간격 배치, 45° 오프셋으로 정렬 편향 제거
            val angle = 2f * PI.toFloat() * i / iconCount + PI.toFloat() / 4f
            Offset(cos(angle) * r, sin(angle) * r)
        }
    }

    // ball[0] = 현재 회전 각도(도), ball[1] = 각속도(도/s)
    val ball = remember { floatArrayOf(0f, 0f) }
    // 다음 프레임에 반영할 각도 충격량. 드래그 스레드 → 물리 루프 전달용 버퍼.
    val pendingAngImpulse = remember { floatArrayOf(0f) }
    // 다음 프레임에 반영할 선형 충격량 (x, y). 드래그 스레드 → 물리 루프 전달용 버퍼.
    val pendingImpulse = remember { floatArrayOf(0f, 0f) }
    // 이전 프레임의 드래그 델타. 가속도(속도 변화량) 계산에 사용한다.
    val prevDrag = remember { floatArrayOf(0f, 0f) }

    // 각 아이콘의 위치 (px, 볼 중심 기준). iconCount가 바뀌면 재초기화된다.
    val posX = remember(iconCount) { FloatArray(iconCount) { i -> initOffsets.getOrNull(i)?.x ?: 0f } }
    val posY = remember(iconCount) { FloatArray(iconCount) { i -> initOffsets.getOrNull(i)?.y ?: 0f } }
    // 각 아이콘의 속도 (px/s)
    val velX = remember(iconCount) { FloatArray(iconCount) { 0f } }
    val velY = remember(iconCount) { FloatArray(iconCount) { 0f } }

    // Compose 재구성을 유발하는 상태. 물리 루프에서 매 프레임 업데이트한다.
    val ballAngleState = remember { mutableFloatStateOf(0f) }
    val iconOffsets = remember(iconCount) {
        Array(iconCount) { i -> mutableStateOf(initOffsets.getOrNull(i) ?: Offset.Zero) }
    }

    // 물리 시뮬레이션 루프: withFrameNanos로 화면 주사율(≈60fps)에 동기화한다.
    LaunchedEffect(iconCount) {
        var lastTime = 0L
        while (true) {
            withFrameNanos { time ->
                // 프레임 간 경과 시간(초). 첫 프레임은 기본값 사용, 이후 실제 경과 시간을 사용한다.
                val dt = if (lastTime == 0L) {
                    DefaultDt
                } else {
                    ((time - lastTime) / NanosPerSecond).coerceIn(0f, MaxDt)
                }
                lastTime = time

                // --- 볼 회전 업데이트 ---
                // 드래그에서 쌓인 각도 충격량을 각속도에 반영하고 버퍼를 초기화한다.
                ball[1] += pendingAngImpulse[0]
                pendingAngImpulse[0] = 0f
                // 각속도로 각도를 적분하고, 감쇠로 서서히 멈춘다.
                ball[0] += ball[1] * dt
                ball[1] *= AngularDamping

                // --- 아이콘 선형 충격량 반영 ---
                // 아이콘마다 다른 질량 계수(MassFactorCycle)를 적용해 같은 힘에도 다르게 반응하게 한다.
                // 이를 통해 아이콘들이 동일한 속도로 뭉쳐 움직이지 않고 자연스럽게 분산된다.
                if (pendingImpulse[0] != 0f || pendingImpulse[1] != 0f) {
                    for (i in 0 until iconCount) {
                        val massFactor = MassFactorCycle[i % MassFactorCycle.size]
                        velX[i] += pendingImpulse[0] * massFactor
                        velY[i] += pendingImpulse[1] * massFactor
                    }
                    pendingImpulse[0] = 0f
                    pendingImpulse[1] = 0f
                }

                // 아이콘이 벗어날 수 있는 최대 반경 = 볼 반경 - 아이콘 충돌 반경
                val maxR = (ballRadiusPx - iconPhysicsRadiusPx).coerceAtLeast(0f)

                for (i in 0 until iconCount) {
                    // 속도로 위치를 적분한다.
                    posX[i] += velX[i] * dt
                    posY[i] += velY[i] * dt

                    // --- 원형 경계 반사 ---
                    // 아이콘이 볼 경계를 넘으면 위치를 경계로 되돌리고 속도를 반사시킨다.
                    val distSq = posX[i] * posX[i] + posY[i] * posY[i]
                    if (distSq > maxR * maxR) {
                        val dist = sqrt(distSq)
                        // 경계 법선 벡터 (중심에서 아이콘 방향으로 향하는 단위벡터)
                        val nx = posX[i] / dist
                        val ny = posY[i] / dist
                        // 위치를 경계 위로 이동
                        posX[i] = nx * maxR
                        posY[i] = ny * maxR
                        // 법선 방향 속도 성분을 반전하고 반발 계수를 곱해 에너지를 감쇠시킨다.
                        val dot = velX[i] * nx + velY[i] * ny
                        velX[i] = (velX[i] - 2f * dot * nx) * Restitution
                        velY[i] = (velY[i] - 2f * dot * ny) * Restitution
                    }

                    // 선형 감쇠: 매 프레임 속도를 곱해 마찰로 서서히 멈추게 한다.
                    velX[i] *= LinearDamping
                    velY[i] *= LinearDamping
                }

                // --- 아이콘 간 반발력 ---
                // 두 아이콘이 RepulsionMinDistDp보다 가까우면 서로 밀어내 겹침을 방지한다.
                for (i in 0 until iconCount) {
                    for (j in i + 1 until iconCount) {
                        val dx = posX[j] - posX[i]
                        val dy = posY[j] - posY[i]
                        val distSq = dx * dx + dy * dy
                        if (distSq < repulsionMinDistPx * repulsionMinDistPx && distSq > 0.01f) {
                            val dist = sqrt(distSq)
                            // 두 아이콘을 잇는 방향의 단위벡터
                            val nx = dx / dist
                            val ny = dy / dist
                            // 겹친 깊이에 비례한 충격량으로 양쪽을 반대 방향으로 밀어낸다.
                            val impulse = (repulsionMinDistPx - dist) * RepulsionStrength
                            velX[i] -= nx * impulse
                            velY[i] -= ny * impulse
                            velX[j] += nx * impulse
                            velY[j] += ny * impulse
                        }
                    }
                }

                // 물리 상태를 Compose 상태로 복사해 UI 재구성을 유발한다.
                for (i in 0 until iconCount) {
                    iconOffsets[i].value = Offset(posX[i], posY[i])
                }
                ballAngleState.floatValue = ball[0]
            }
        }
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(GlassBallSize)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                // 드래그 시작 시 이전 델타를 초기화해 첫 프레임 가속도 계산 오류를 방지한다.
                                prevDrag[0] = 0f
                                prevDrag[1] = 0f
                            },
                            onDragEnd = {
                                // 손을 뗄 때 마지막 드래그 속도 방향으로 충격량을 줘 던지는 효과를 낸다.
                                pendingImpulse[0] += prevDrag[0] * LinearImpulseScale
                                pendingImpulse[1] += prevDrag[1] * LinearImpulseScale
                            },
                        ) { change, dragAmount ->
                            change.consume()
                            // 가속도 = 이번 프레임 델타 - 이전 프레임 델타 (속도 변화량)
                            // 일정 속도로 드래그할 때는 가속도 ≈ 0이므로 추가 힘이 없고,
                            // 드래그를 시작하거나 방향을 바꿀 때만 충격량이 발생한다.
                            val accelX = dragAmount.x - prevDrag[0]
                            val accelY = dragAmount.y - prevDrag[1]
                            prevDrag[0] = dragAmount.x
                            prevDrag[1] = dragAmount.y
                            // 수평 드래그 양에 비례해 볼 회전 각속도를 추가한다.
                            pendingAngImpulse[0] += dragAmount.x * RotationSensitivity
                            // 드래그 가속도 방향으로 아이콘에 충격량을 전달한다.
                            pendingImpulse[0] += accelX * LinearImpulseScale
                            pendingImpulse[1] += accelY * LinearImpulseScale
                        }
                    },
            contentAlignment = Alignment.Center,
        ) {
            // 글래스볼 이미지: 드래그에 따라 회전한다.
            Image(
                painter = painterResource(id = R.drawable.ic_glass_plus_lighter),
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .rotate(ballAngleState.floatValue),
                contentScale = ContentScale.Fit,
            )

            // 아이콘 레이어: 물리 시뮬레이션 결과로 계산된 offset으로 각 아이콘 배치
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
                )
                .size(width = width, height = height)
                .rotate(staticRotation),
    )
}

// 글래스볼 이미지 크기
private val GlassBallSize = 200.dp
// 볼 이미지 중 아이콘이 실제로 존재하는 내부 영역 비율 (이미지 테두리 공간 제외)
private const val BallIconAreaRatio = 0.62f
// 아이콘 충돌 반경(dp). 이미지보다 작게 설정해 볼 안 이동 공간을 확보한다.
private const val IconPhysicsRadiusDp = 15f
// 아이콘 간 반발이 시작되는 최소 거리(dp)
private const val RepulsionMinDistDp = 48f
// 반발력 세기. 클수록 아이콘이 더 강하게 튕겨나간다.
private const val RepulsionStrength = 0.3f
// 수평 드래그 → 볼 회전 변환 비율
private const val RotationSensitivity = 0.3f
// 드래그 가속도 → 아이콘 속도 변환 비율
private const val LinearImpulseScale = 5f
// 볼 회전 감쇠 계수. 매 프레임 각속도에 곱해 마찰로 멈추게 한다. (0~1, 1에 가까울수록 느리게 멈춤)
private const val AngularDamping = 0.90f
// 아이콘 선형 감쇠 계수. 매 프레임 속도에 곱해 마찰로 멈추게 한다. (0~1, 1에 가까울수록 느리게 멈춤)
private const val LinearDamping = 0.97f
// 경계 반사 시 에너지 보존 비율. 낮을수록 벽에 부딪힐 때 많이 감속된다.
private const val Restitution = 0.35f
private const val NanosPerSecond = 1_000_000_000f
// dt 계산 전 첫 프레임에 사용할 기본 시간 간격(초)
private const val DefaultDt = 0.016f
// 프레임 스킵 등으로 dt가 비정상적으로 커지는 경우를 방지하는 최대값(초)
private const val MaxDt = 0.05f

// 아이콘에 순환 적용할 고정 기울기(도). 아이콘 수에 관계없이 인덱스 % 4로 적용된다.
private val StaticRotationCycle = floatArrayOf(25f, -20f, -15f, 15f)
// 아이콘에 순환 적용할 질량 계수. 작을수록 같은 힘에 더 많이 움직인다.
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
private fun GlassBallSectionPreview() {
    OBRitTheme {
        GlassBallSection(
            icons = listOf(
                ConsumableIcon(R.drawable.ic_towel, TowelWidth, TowelHeight),
                ConsumableIcon(R.drawable.ic_toothbrush, ToothbrushWidth, ToothbrushHeight),
                ConsumableIcon(R.drawable.ic_detergent, DetergentWidth, DetergentHeight),
                ConsumableIcon(R.drawable.ic_razor, RazorWidth, RazorHeight),
            ),
        )
    }
}
