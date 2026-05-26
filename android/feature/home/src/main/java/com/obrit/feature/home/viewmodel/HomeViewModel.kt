package com.obrit.feature.home.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.android.core.ui.extensions.vmAsync
import com.obrit.obrit.shared.data.repository.HomeRepository
import com.obrit.obrit.shared.model.home.HomeOverallStatus
import com.obrit.obrit.shared.model.home.MyStatusSummary
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class HomeViewModel internal constructor(
    private val homeRepository: HomeRepository,
) : BaseContainerHost<HomeUiState, HomeSideEffect>() {
    override val container =
        container<HomeUiState, HomeSideEffect>(HomeUiState.Loading) {
            val overallStatusDeferred = vmAsync { homeRepository.getOverallStatus() }
            val myStatusSummaryDeferred = vmAsync { homeRepository.getMyStatusSummary() }

            val overallStatus = overallStatusDeferred.await().getOrNull()
            val myStatusSummary = myStatusSummaryDeferred.await().getOrNull()

            if (overallStatus == null || myStatusSummary == null) {
                reduce { HomeUiState.LoadFailed }
                return@container
            }

            reduce { createSuccessState(overallStatus, myStatusSummary, createMockStatus()) }
        }

    fun onSearchClick() = intent { postSideEffect(HomeSideEffect.OnSearchClick) }

    fun onNotificationClick() = intent { postSideEffect(HomeSideEffect.OnNotificationClick) }

    fun onProfileClick() = intent { postSideEffect(HomeSideEffect.OnProfileClick) }

    fun onListSortOrderChange(sortOrder: ConsumableListSortOrder) =
        intent {
            reduceOn<HomeUiState.Success> { state.copy(listSortOrder = sortOrder) }
        }

    fun onDdayFilterChange(maxDays: Int) =
        intent {
            reduceOn<HomeUiState.Success> { state.copy(ddayFilterMax = maxDays) }
        }

    fun onSpareFilterChange(maxSpare: Int) =
        intent {
            reduceOn<HomeUiState.Success> { state.copy(spareFilterMax = maxSpare) }
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
    val buckets: List<Bucket>,
)

@Immutable
data class HomeRatio(
    val goodPercentage: Float,
    val warningPercentage: Float,
    val illustrationType: IllustrationType,
)

enum class IllustrationType { POSITIVE, NEGATIVE }

@Immutable
data class Bucket(
    val status: BucketStatus,
    val title: String,
    val spare: Int,
    val replacementDate: String,
    val level: BucketLevel,
    val daysInUse: Int,
)

enum class ConsumableListSortOrder(
    val displayName: String,
) {
    REPLACE_IMMINENT("교체 임박 순"),
    LEAST_SPARE("여분 적은 순"),
    OLDEST_REPLACEMENT("교체 오래된 순"),
    ALPHABETICAL("가나다 순"),
}

enum class BucketStatus {
    DANGER,
    WARN,
}

enum class BucketLevel {
    NONE_OVERDUE,
    NONE_WARN,
    HAS_OVERDUE,
    HAS_WARN,
    NONE_SAFE,
    HAS_SAFE,
}

@Suppress("MagicNumber")
private fun createSuccessState(
    overallStatus: HomeOverallStatus,
    myStatusSummary: MyStatusSummary,
    status: HomeStatus,
): HomeUiState.Success {
    val ddayValues = status.buckets.map { daysUntil(it.replacementDate) }
    val spareValues = status.buckets.map { it.spare }
    val ddayMin = ddayValues.minOrNull() ?: DEFAULT_DDAY_MIN
    val ddayMax = ddayValues.maxOrNull() ?: DEFAULT_DDAY_MAX
    val spareMin = spareValues.minOrNull() ?: DEFAULT_SPARE_MIN
    val spareMax = spareValues.maxOrNull() ?: DEFAULT_SPARE_MAX
    return HomeUiState.Success(
        overallStatus = overallStatus,
        myStatusSummary = myStatusSummary,
        status = status,
        ddayRange = ddayMin..ddayMax,
        ddayFilterMax = ddayMax,
        spareRange = spareMin..spareMax,
        spareFilterMax = spareMax,
    )
}

private fun daysUntil(replacementDate: String): Int = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(replacementDate)).toInt()

@Suppress("MagicNumber")
private fun createMockStatus() =
    HomeStatus(
        ratio =
            HomeRatio(
                goodPercentage = 77.0f,
                warningPercentage = 23.0f,
                illustrationType = IllustrationType.NEGATIVE,
            ),
        buckets = createMockBuckets(),
    )

private fun createMockBuckets() = mockReplaceDangerBuckets() + mockReplaceWarnBuckets()

@Suppress("MagicNumber")
private fun mockReplaceDangerBuckets() =
    listOf(
        Bucket(
            status = BucketStatus.DANGER,
            title = "면도기",
            spare = 0,
            replacementDate = "2026-05-23",
            level = BucketLevel.NONE_OVERDUE,
            daysInUse = 30,
        ),
        Bucket(
            status = BucketStatus.DANGER,
            title = "칫솔",
            spare = 1,
            replacementDate = "2026-05-26",
            level = BucketLevel.NONE_WARN,
            daysInUse = 27,
        ),
        Bucket(
            status = BucketStatus.DANGER,
            title = "수건",
            spare = 0,
            replacementDate = "2026-05-22",
            level = BucketLevel.HAS_OVERDUE,
            daysInUse = 45,
        ),
        Bucket(
            status = BucketStatus.DANGER,
            title = "세탁망",
            spare = 2,
            replacementDate = "2026-05-30",
            level = BucketLevel.HAS_WARN,
            daysInUse = 18,
        ),
    )

@Suppress("MagicNumber")
private fun mockReplaceWarnBuckets() =
    listOf(
        Bucket(
            status = BucketStatus.WARN,
            title = "필터",
            spare = 3,
            replacementDate = "2026-05-26",
            level = BucketLevel.NONE_SAFE,
            daysInUse = 10,
        ),
        Bucket(
            status = BucketStatus.WARN,
            title = "화장솜",
            spare = 5,
            replacementDate = "2026-06-06",
            level = BucketLevel.NONE_SAFE,
            daysInUse = 7,
        ),
        Bucket(
            status = BucketStatus.WARN,
            title = "청소포",
            spare = 2,
            replacementDate = "2026-05-21",
            level = BucketLevel.NONE_OVERDUE,
            daysInUse = 35,
        ),
        Bucket(
            status = BucketStatus.WARN,
            title = "욕실매트",
            spare = 4,
            replacementDate = "2026-06-10",
            level = BucketLevel.HAS_OVERDUE,
            daysInUse = 50,
        ),
    )

private const val DEFAULT_DDAY_MIN = 0
private const val DEFAULT_DDAY_MAX = 30
private const val DEFAULT_SPARE_MIN = 0
private const val DEFAULT_SPARE_MAX = 10
