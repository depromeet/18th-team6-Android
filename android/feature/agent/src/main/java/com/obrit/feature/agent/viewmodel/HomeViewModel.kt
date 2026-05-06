@file:Suppress("TooManyFunctions")

package com.obrit.feature.agent.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import org.orbitmvi.orbit.viewmodel.container

private const val MIN_DRAG_DISTANCE = 1f
private const val MIN_RATIO = 0f
private const val MAX_RATIO = 1f
private const val DEFAULT_NORMAL_RATIO = 0.77f
private const val DEFAULT_WARNING_RATIO = 0.23f
private const val PERCENT_MULTIPLIER = 100f
private const val ORB_ROTATION_DRAG_MULTIPLIER = 0.45f
private const val ORB_DRAG_ROLL_WEIGHT = 0.75f
private const val MAX_COMBINED_ROLL = 1.4f
private const val FULL_CIRCLE_DEGREES = 360f
private const val HALF_CIRCLE_DEGREES = 180f

class HomeViewModel : BaseContainerHost<HomeUiState, HomeSideEffect>() {
    override val container = container<HomeUiState, HomeSideEffect>(HomeUiState())

    fun onSearchClick() =
        intent {
            postSideEffect(HomeSideEffect.ShowSnackbar("소모품 검색은 준비 중이에요"))
        }

    fun onNotificationClick() =
        intent {
            postSideEffect(HomeSideEffect.ShowSnackbar("새 알림이 없어요"))
        }

    fun onProfileClick() =
        intent {
            postSideEffect(HomeSideEffect.ShowSnackbar("프로필은 준비 중이에요"))
        }

    fun selectStatusFilter(filter: HomeStatusFilter) =
        intent {
            reduce {
                state.copy(selectedStatusFilter = filter)
            }
        }

    fun cyclePreviewSort() =
        intent {
            reduce {
                state.copy(previewSort = state.previewSort.next())
            }
        }

    fun togglePreviewExpanded() =
        intent {
            reduce {
                state.copy(isPreviewExpanded = !state.isPreviewExpanded)
            }
        }

    fun onOrbDragged(
        deltaX: Float,
        deltaY: Float,
        maxDrag: Float,
    ) = intent {
        val safeMaxDrag = maxDrag.coerceAtLeast(MIN_DRAG_DISTANCE)

        reduce {
            state.copy(
                orb =
                    state.orb.copy(
                        dragX = (state.orb.dragX + deltaX).coerceIn(-safeMaxDrag, safeMaxDrag),
                        dragY = (state.orb.dragY + deltaY).coerceIn(-safeMaxDrag, safeMaxDrag),
                        sphereYaw = (state.orb.sphereYaw + deltaX * ORB_ROTATION_DRAG_MULTIPLIER).normalizeDegrees(),
                        spherePitch = (state.orb.spherePitch + deltaY * ORB_ROTATION_DRAG_MULTIPLIER).normalizeDegrees(),
                    ),
            )
        }
    }

    fun onDeviceTiltChanged(tilt: DeviceTilt) =
        intent {
            reduce {
                state.copy(orb = state.orb.copy(tilt = tilt))
            }
        }

    fun onConsumableClick(consumable: HomeConsumableUiModel) =
        intent {
            postSideEffect(HomeSideEffect.ShowSnackbar("${consumable.title} 상세는 준비 중이에요"))
        }

    fun onUsageClick(usage: HomeUsageUiModel) =
        intent {
            postSideEffect(HomeSideEffect.ShowSnackbar("${usage.title} 사용 현황을 열 수 없어요"))
        }

    fun onHomeTabClick() =
        intent {
            postSideEffect(HomeSideEffect.ShowSnackbar("이미 홈 화면이에요"))
        }

    fun onListTabClick() =
        intent {
            postSideEffect(HomeSideEffect.ShowSnackbar("목록 화면은 준비 중이에요"))
        }

    fun onAddClick() =
        intent {
            postSideEffect(HomeSideEffect.ShowSnackbar("소모품 추가는 준비 중이에요"))
        }
}

@Immutable
data class HomeUiState(
    val selectedStatusFilter: HomeStatusFilter = HomeStatusFilter.ReplacementDanger,
    val previewSort: HomePreviewSort = HomePreviewSort.NearReplacement,
    val isPreviewExpanded: Boolean = false,
    val normalRatio: Float = DEFAULT_NORMAL_RATIO,
    val warningRatio: Float = DEFAULT_WARNING_RATIO,
    val orb: OrbUiState = OrbUiState(),
    val urgentConsumables: List<HomeConsumableUiModel> = ConsumableMemoryStore.urgentConsumables,
    val previewConsumables: List<HomeConsumableUiModel> = ConsumableMemoryStore.previewConsumables,
    val usageItems: List<HomeUsageUiModel> = ConsumableMemoryStore.usageItems,
) {
    val normalPercent: Int
        get() = (normalRatio.coerceIn(MIN_RATIO, MAX_RATIO) * PERCENT_MULTIPLIER).toInt()

    val warningPercent: Int
        get() = (warningRatio.coerceIn(MIN_RATIO, MAX_RATIO) * PERCENT_MULTIPLIER).toInt()
}

@Immutable
data class DeviceTilt(
    val x: Float = 0f,
    val y: Float = 0f,
)

@Immutable
data class OrbUiState(
    val tilt: DeviceTilt = DeviceTilt(),
    val dragX: Float = 0f,
    val dragY: Float = 0f,
    val spherePitch: Float = 0f,
    val sphereYaw: Float = 0f,
) {
    private val rollX: Float
        get() = tilt.x.coerceIn(-MAX_RATIO, MAX_RATIO)

    private val rollY: Float
        get() = tilt.y.coerceIn(-MAX_RATIO, MAX_RATIO)

    fun combinedRoll(maxDrag: Float): OrbRoll {
        val safeMaxDrag = maxDrag.coerceAtLeast(MIN_DRAG_DISTANCE)
        val dragRollX = (dragX / safeMaxDrag).coerceIn(-MAX_RATIO, MAX_RATIO)
        val dragRollY = (dragY / safeMaxDrag).coerceIn(-MAX_RATIO, MAX_RATIO)

        return OrbRoll(
            x = (rollX + dragRollX * ORB_DRAG_ROLL_WEIGHT).coerceIn(-MAX_COMBINED_ROLL, MAX_COMBINED_ROLL),
            y = (rollY + dragRollY * ORB_DRAG_ROLL_WEIGHT).coerceIn(-MAX_COMBINED_ROLL, MAX_COMBINED_ROLL),
        )
    }
}

@Immutable
data class OrbRoll(
    val x: Float = 0f,
    val y: Float = 0f,
)

sealed interface HomeSideEffect {
    data class ShowSnackbar(
        val message: String,
    ) : HomeSideEffect
}

enum class HomeStatusFilter {
    ReplacementDanger,
    SpareShortage,
    ReplacementWarning,
}

enum class HomePreviewSort {
    NearReplacement,
    LowSpare,
    LongUse,
}

internal fun HomePreviewSort.next(): HomePreviewSort =
    when (this) {
        HomePreviewSort.NearReplacement -> HomePreviewSort.LowSpare
        HomePreviewSort.LowSpare -> HomePreviewSort.LongUse
        HomePreviewSort.LongUse -> HomePreviewSort.NearReplacement
    }

@Immutable
data class HomeConsumableUiModel(
    val id: Int,
    val title: String,
    val remainLabel: String,
    val statusFilter: HomeStatusFilter,
    val replacementLabel: String,
    val spareLabel: String,
    val isPrimary: Boolean = false,
)

@Immutable
data class HomeUsageUiModel(
    val id: Int,
    val title: String,
    val daysInUse: Int,
)

private fun Float.normalizeDegrees(): Float {
    val value = this % FULL_CIRCLE_DEGREES
    return when {
        value < -HALF_CIRCLE_DEGREES -> value + FULL_CIRCLE_DEGREES
        value > HALF_CIRCLE_DEGREES -> value - FULL_CIRCLE_DEGREES
        else -> value
    }
}
