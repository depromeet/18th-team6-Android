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
}

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data object LoadFailed : HomeUiState

    @Immutable
    data class Success(
        val status: HomeStatus,
    ) : HomeUiState
}

sealed interface HomeSideEffect {
    data object OnSearchClick : HomeSideEffect

    data object OnNotificationClick : HomeSideEffect

    data object OnProfileClick : HomeSideEffect
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
)

enum class BucketStatus {
    REPLACE_DANGER,
    SPARE_SHORTAGE,
    REPLACE_WARN,
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

private fun createMockBuckets() =
    mockReplaceDangerBuckets() + mockSpareShortBuckets() + mockReplaceWarnBuckets()

@Suppress("MagicNumber")
private fun mockReplaceDangerBuckets() =
    listOf(
        Bucket(
            status = BucketStatus.REPLACE_DANGER,
            title = "면도기",
            spare = 0,
            replacementDate = "2026-05-23",
            level = BucketLevel.NONE_OVERDUE,
        ),
        Bucket(
            status = BucketStatus.REPLACE_DANGER,
            title = "칫솔",
            spare = 1,
            replacementDate = "2026-05-26",
            level = BucketLevel.NONE_WARN,
        ),
        Bucket(
            status = BucketStatus.REPLACE_DANGER,
            title = "수건",
            spare = 0,
            replacementDate = "2026-05-22",
            level = BucketLevel.HAS_OVERDUE,
        ),
        Bucket(
            status = BucketStatus.REPLACE_DANGER,
            title = "세탁망",
            spare = 2,
            replacementDate = "2026-05-30",
            level = BucketLevel.HAS_WARN,
        ),
    )

@Suppress("MagicNumber")
private fun mockSpareShortBuckets() =
    listOf(
        Bucket(
            status = BucketStatus.SPARE_SHORTAGE,
            title = "샴푸",
            spare = 0,
            replacementDate = "2026-05-25",
            level = BucketLevel.NONE_SAFE,
        ),
        Bucket(
            status = BucketStatus.SPARE_SHORTAGE,
            title = "치약",
            spare = 1,
            replacementDate = "2026-06-02",
            level = BucketLevel.HAS_SAFE
        ),
        Bucket(
            status = BucketStatus.SPARE_SHORTAGE,
            title = "세제",
            spare = 0,
            replacementDate = "2026-05-20",
            level = BucketLevel.HAS_SAFE
        ),
    )

@Suppress("MagicNumber")
private fun mockReplaceWarnBuckets() =
    listOf(
        Bucket(
            status = BucketStatus.REPLACE_WARN,
            title = "필터",
            spare = 3,
            replacementDate = "2026-05-26",
            level = BucketLevel.NONE_SAFE
        ),
        Bucket(
            status = BucketStatus.REPLACE_WARN,
            title = "화장솜",
            spare = 5,
            replacementDate = "2026-06-06",
            level = BucketLevel.NONE_SAFE
        ),
        Bucket(
            status = BucketStatus.REPLACE_WARN,
            title = "청소포",
            spare = 2,
            replacementDate = "2026-05-21",
            level = BucketLevel.NONE_OVERDUE,
        ),
        Bucket(
            status = BucketStatus.REPLACE_WARN,
            title = "욕실매트",
            spare = 4,
            replacementDate = "2026-06-10",
            level = BucketLevel.HAS_OVERDUE,
        ),
    )
