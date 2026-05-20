package com.obrit.feature.home.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import org.orbitmvi.orbit.viewmodel.container

class HomeViewModel internal constructor() : BaseContainerHost<HomeUiState, HomeSideEffect>() {
    override val container =
        container<HomeUiState, HomeSideEffect>(HomeUiState.Loading) {
            intent { reduce { HomeUiState.Success(createMockStatus()) } }
        }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data object LoadFailed : HomeUiState

    @Immutable
    data class Success(
        val status: HomeStatus,
    ) : HomeUiState
}

sealed interface HomeSideEffect

// API 응답 형태를 임시로 ViewModel에 정의한다.
// shared/model에 HomeStatus가 선언되면 해당 타입으로 교체한다.
@Immutable
data class HomeStatus(
    val overallStatus: ConsumableStatusLevel,
    val message: HomeMessage,
    val ratio: HomeRatio,
    val graph: HomeGraph,
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
    val totalSupplies: Int,
    val dangerCount: Int,
    val myScore: Float,
    val averageScore: Float,
    val scoreLabel: ScoreLabel,
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

enum class ScoreLabel { ABOVE_AVERAGE, AVERAGE, BELOW_AVERAGE }

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
                totalSupplies = 16,
                dangerCount = 4,
                myScore = 42.5f,
                averageScore = 65.0f,
                scoreLabel = ScoreLabel.BELOW_AVERAGE,
            ),
    )
