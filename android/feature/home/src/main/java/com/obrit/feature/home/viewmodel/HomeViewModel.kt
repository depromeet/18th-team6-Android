package com.obrit.feature.home.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import org.orbitmvi.orbit.viewmodel.container

class HomeViewModel internal constructor() : BaseContainerHost<HomeUiState, HomeSideEffect>() {
    override val container =
        container<HomeUiState, HomeSideEffect>(HomeUiState.Loading) {
            intent { reduce { HomeUiState.Success(createMockStatus()) } }
        }

    fun onSearchClick() = intent { postSideEffect(HomeSideEffect.OnSearchClick) }

    fun onNotificationClick() = intent { postSideEffect(HomeSideEffect.OnNotificationClick) }

    fun onProfileClick() = intent { postSideEffect(HomeSideEffect.OnProfileClick) }

    fun onListSortOrderChange(sortOrder: ConsumableListSortOrder) =
        intent {
            reduce {
                (state as? HomeUiState.Success)?.copy(listSortOrder = sortOrder) ?: state
            }
        }

    fun onMoreClick() = intent { postSideEffect(HomeSideEffect.OnMoreClick) }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data object LoadFailed : HomeUiState

    @Immutable
    data class Success(
        val status: HomeStatus,
        val listSortOrder: ConsumableListSortOrder = ConsumableListSortOrder.REPLACE_IMMINENT,
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
    val overallStatus: ConsumableStatusLevel,
    val message: HomeMessage,
    val ratio: HomeRatio,
    val graph: HomeGraph,
    val buckets: List<Bucket>,
)

@Immutable
data class HomeMessage(
    val title: String,
    val highlightWord: String,
    val replacementStatus: ManagementStatusLevel,
    val stockStatus: StockStatusLevel,
)

@Immutable
data class HomeRatio(
    val goodPercentage: Float,
    val warningPercentage: Float,
    val illustrationType: IllustrationType,
)

@Immutable
data class HomeGraph(
    val totalCount: Int,
    val needReplaceCount: Int,
    val score: Float,
    val averageScore: Float,
)

enum class ConsumableStatusLevel { PERFECT, GOOD, WARNING, DANGER }

enum class ManagementStatusLevel(
    val displayName: String,
) {
    GOOD("완벽"),
    WARNING("경고"),
    DANGER("위험"),
}

enum class StockStatusLevel(
    val displayName: String,
) {
    GOOD("완벽"),
    WARNING("경고"),
    LOW_STOCK("위험"),
}

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
private fun createMockStatus() =
    HomeStatus(
        overallStatus = ConsumableStatusLevel.WARNING,
        message =
            HomeMessage(
                title = "오늘의 소모품 관리 상태는 경고예요",
                highlightWord = "경고",
                replacementStatus = ManagementStatusLevel.WARNING,
                stockStatus = StockStatusLevel.WARNING,
            ),
        ratio =
            HomeRatio(
                goodPercentage = 77.0f,
                warningPercentage = 23.0f,
                illustrationType = IllustrationType.NEGATIVE,
            ),
        graph =
            HomeGraph(
                totalCount = 16,
                needReplaceCount = 4,
                score = 0.425f,
                averageScore = 0.65f,
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
