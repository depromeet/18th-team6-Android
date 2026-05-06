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
    val urgentConsumables: List<HomeConsumableUiModel> = ConsumableMemoryStore.urgentConsumables,
    val previewConsumables: List<HomeConsumableUiModel> = ConsumableMemoryStore.previewConsumables,
    val usageItems: List<HomeUsageUiModel> = ConsumableMemoryStore.usageItems,
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
