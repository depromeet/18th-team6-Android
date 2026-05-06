@file:Suppress("TooManyFunctions")

package com.obrit.feature.agent.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import org.orbitmvi.orbit.viewmodel.container

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
        val safeMaxDrag = maxDrag.coerceAtLeast(1f)

        reduce {
            state.copy(
                orb =
                    state.orb.copy(
                        dragX = (state.orb.dragX + deltaX).coerceIn(-safeMaxDrag, safeMaxDrag),
                        dragY = (state.orb.dragY + deltaY).coerceIn(-safeMaxDrag, safeMaxDrag),
                        sphereYaw = (state.orb.sphereYaw + deltaX * 0.45f).normalizeDegrees(),
                        spherePitch = (state.orb.spherePitch + deltaY * 0.45f).normalizeDegrees(),
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
    val normalRatio: Float = 0.77f,
    val warningRatio: Float = 0.23f,
    val orb: OrbUiState = OrbUiState(),
    val urgentConsumables: List<HomeConsumableUiModel> = ConsumableMemoryStore.urgentConsumables,
    val previewConsumables: List<HomeConsumableUiModel> = ConsumableMemoryStore.previewConsumables,
    val usageItems: List<HomeUsageUiModel> = ConsumableMemoryStore.usageItems,
) {
    val normalPercent: Int
        get() = (normalRatio.coerceIn(0f, 1f) * 100f).toInt()

    val warningPercent: Int
        get() = (warningRatio.coerceIn(0f, 1f) * 100f).toInt()
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
        get() = tilt.x.coerceIn(-1f, 1f)

    private val rollY: Float
        get() = tilt.y.coerceIn(-1f, 1f)

    fun combinedRoll(maxDrag: Float): OrbRoll {
        val safeMaxDrag = maxDrag.coerceAtLeast(1f)
        val dragRollX = (dragX / safeMaxDrag).coerceIn(-1f, 1f)
        val dragRollY = (dragY / safeMaxDrag).coerceIn(-1f, 1f)

        return OrbRoll(
            x = (rollX + dragRollX * 0.75f).coerceIn(-1.4f, 1.4f),
            y = (rollY + dragRollY * 0.75f).coerceIn(-1.4f, 1.4f),
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
    val value = this % 360f
    return when {
        value < -180f -> value + 360f
        value > 180f -> value - 360f
        else -> value
    }
}
