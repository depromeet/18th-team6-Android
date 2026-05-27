package com.obrit.feature.home.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.android.core.ui.extensions.vmAsync
import com.obrit.obrit.shared.data.repository.HomeRepository
import com.obrit.obrit.shared.model.home.HomeBucketGroup
import com.obrit.obrit.shared.model.home.HomeItemCursorSlice
import com.obrit.obrit.shared.model.home.HomeItemOrder
import com.obrit.obrit.shared.model.home.HomeItemsParams
import com.obrit.obrit.shared.model.home.HomeOverallStatus
import com.obrit.obrit.shared.model.home.MyStatusSummary
import org.orbitmvi.orbit.viewmodel.container

class HomeViewModel internal constructor(
    private val homeRepository: HomeRepository,
) : BaseContainerHost<HomeUiState, HomeSideEffect>() {
    override val container =
        container<HomeUiState, HomeSideEffect>(HomeUiState.Loading) {
            val overallStatusDeferred = vmAsync { homeRepository.getOverallStatus() }
            val myStatusSummaryDeferred = vmAsync { homeRepository.getMyStatusSummary() }
            val bucketsDeferred = vmAsync { homeRepository.getBuckets() }
            val itemsDeferred =
                vmAsync {
                    homeRepository.getItems(HomeItemsParams(order = HomeItemOrder.REPLACEMENT_URGENT))
                }

            val overallStatus = overallStatusDeferred.await().getOrNull()
            val myStatusSummary = myStatusSummaryDeferred.await().getOrNull()
            val buckets = bucketsDeferred.await().getOrNull()
            val items = itemsDeferred.await().getOrNull()
            val allLoaded = overallStatus != null && myStatusSummary != null && buckets != null && items != null
            if (!allLoaded) {
                reduce { HomeUiState.LoadFailed }
                return@container
            }

            reduce { createSuccessState(overallStatus, myStatusSummary, buckets, items, createMockStatus()) }
        }

    fun onSearchClick() = intent { postSideEffect(HomeSideEffect.OnSearchClick) }

    fun onNotificationClick() = intent { postSideEffect(HomeSideEffect.OnNotificationClick) }

    fun onProfileClick() = intent { postSideEffect(HomeSideEffect.OnProfileClick) }

    fun onListSortOrderChange(sortOrder: ConsumableListSortOrder) =
        intent {
            reduceOn<HomeUiState.Success> { state.copy(listSortOrder = sortOrder) }
            val items = homeRepository.getItems(HomeItemsParams(order = sortOrder.toHomeItemOrder())).getOrNull()
            if (items != null) {
                reduceOn<HomeUiState.Success> { state.copy(items = items) }
            }
        }

    fun onDdayFilterChange(maxDays: Int) =
        intent {
            reduceOn<HomeUiState.Success> { state.copy(ddayFilterMax = maxDays) }
        }

    fun onSpareFilterChange(maxSpare: Int) =
        intent {
            reduceOn<HomeUiState.Success> { state.copy(spareFilterMax = maxSpare) }
        }

    fun onLoadMoreItems() =
        intent {
            val current = state as? HomeUiState.Success ?: return@intent
            if (!current.items.hasNext) return@intent
            val result =
                homeRepository
                    .getItems(
                        HomeItemsParams(
                            order = current.listSortOrder.toHomeItemOrder(),
                            cursor = current.items.nextCursor,
                        ),
                    ).getOrNull() ?: return@intent
            reduceOn<HomeUiState.Success> {
                state.copy(
                    items =
                        HomeItemCursorSlice(
                            content = state.items.content + result.content,
                            nextCursor = result.nextCursor,
                            size = result.size,
                            hasNext = result.hasNext,
                        ),
                )
            }
        }

    fun onMoreClick() = intent { postSideEffect(HomeSideEffect.OnMoreClick) }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data object LoadFailed : HomeUiState

    @Immutable
    data class Success(
        val overallStatus: HomeOverallStatus,
        val myStatusSummary: MyStatusSummary,
        val buckets: List<HomeBucketGroup>,
        val items: HomeItemCursorSlice,
        val status: HomeStatus,
        val listSortOrder: ConsumableListSortOrder = ConsumableListSortOrder.REPLACE_IMMINENT,
        val ddayRange: IntRange,
        val ddayFilterMax: Int,
        val spareRange: IntRange,
        val spareFilterMax: Int,
    ) : HomeUiState
}

sealed interface HomeSideEffect {
    data object OnSearchClick : HomeSideEffect

    data object OnNotificationClick : HomeSideEffect

    data object OnProfileClick : HomeSideEffect

    data object OnMoreClick : HomeSideEffect
}

// API 응답 형태를 임시로 ViewModel에 정의한다.
// shared/model에 HomeStatus가 선언되면 해당 타입으로 교체한다.
@Immutable
data class HomeStatus(
    val ratio: HomeRatio,
)

@Immutable
data class HomeRatio(
    val goodPercentage: Float,
    val warningPercentage: Float,
    val illustrationType: IllustrationType,
)

enum class IllustrationType { POSITIVE, NEGATIVE }

enum class ConsumableListSortOrder(
    val displayName: String,
) {
    REPLACE_IMMINENT("교체 임박 순"),
    LEAST_SPARE("여분 적은 순"),
    OLDEST_REPLACEMENT("교체 오래된 순"),
    ALPHABETICAL("가나다 순"),
}

@Suppress("MagicNumber")
private fun createSuccessState(
    overallStatus: HomeOverallStatus,
    myStatusSummary: MyStatusSummary,
    buckets: List<HomeBucketGroup>,
    items: HomeItemCursorSlice,
    status: HomeStatus,
): HomeUiState.Success {
    val ddayValues = items.content.map { parseDday(it.replacementDday) }
    val spareValues = items.content.map { it.spareQuantity }
    val ddayMin = ddayValues.minOrNull() ?: DEFAULT_DDAY_MIN
    val ddayMax = ddayValues.maxOrNull() ?: DEFAULT_DDAY_MAX
    val spareMin = spareValues.minOrNull() ?: DEFAULT_SPARE_MIN
    val spareMax = spareValues.maxOrNull() ?: DEFAULT_SPARE_MAX
    return HomeUiState.Success(
        overallStatus = overallStatus,
        myStatusSummary = myStatusSummary,
        buckets = buckets,
        items = items,
        status = status,
        ddayRange = ddayMin..ddayMax,
        ddayFilterMax = ddayMax,
        spareRange = spareMin..spareMax,
        spareFilterMax = spareMax,
    )
}

@Suppress("MagicNumber")
private fun createMockStatus() =
    HomeStatus(
        ratio =
            HomeRatio(
                goodPercentage = 77.0f,
                warningPercentage = 23.0f,
                illustrationType = IllustrationType.NEGATIVE,
            ),
    )

private const val DEFAULT_DDAY_MIN = 0
private const val DEFAULT_DDAY_MAX = 30
private const val DEFAULT_SPARE_MIN = 0
private const val DEFAULT_SPARE_MAX = 10

private fun parseDday(replacementDday: String): Int =
    when {
        replacementDday.contains("D-day") -> 0
        replacementDday.contains("D+") -> -(replacementDday.substringAfter("D+").toIntOrNull() ?: 0)
        replacementDday.contains("D-") -> replacementDday.substringAfter("D-").toIntOrNull() ?: 0
        else -> 0
    }

private fun ConsumableListSortOrder.toHomeItemOrder(): HomeItemOrder? =
    when (this) {
        ConsumableListSortOrder.REPLACE_IMMINENT -> HomeItemOrder.REPLACEMENT_URGENT
        ConsumableListSortOrder.LEAST_SPARE -> HomeItemOrder.SPARE_LOW
        ConsumableListSortOrder.OLDEST_REPLACEMENT -> HomeItemOrder.USED_OLD
        ConsumableListSortOrder.ALPHABETICAL -> null
    }
