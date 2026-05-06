package com.obrit.feature.agent.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.ui.BaseContainerHost
import org.orbitmvi.orbit.viewmodel.container

class ConsumableDetailViewModel : BaseContainerHost<ConsumableDetailUiState, ConsumableDetailSideEffect>() {
    override val container =
        container<ConsumableDetailUiState, ConsumableDetailSideEffect>(
            ConsumableDetailUiState(detail = ConsumableMemoryStore.findDetail(1)),
        )

    fun load(consumableId: Int) =
        intent {
            reduce {
                state.copy(detail = ConsumableMemoryStore.findDetail(consumableId))
            }
        }

    fun onMoreClick() =
        intent {
            postSideEffect(ConsumableDetailSideEffect.ShowSnackbar("소모품 설정은 준비 중이에요"))
        }

    fun onSpareManageClick() =
        intent {
            postSideEffect(ConsumableDetailSideEffect.ShowSnackbar("여분 관리는 준비 중이에요"))
        }

    fun onReplacementCompleteClick() =
        intent {
            val updatedDetail = ConsumableMemoryStore.completeReplacement(state.detail.id)

            reduce {
                state.copy(detail = updatedDetail)
            }
            postSideEffect(ConsumableDetailSideEffect.ShowSnackbar("교체 완료로 기록했어요"))
        }
}

@Immutable
data class ConsumableDetailUiState(
    val detail: ConsumableDetailUiModel,
)

sealed interface ConsumableDetailSideEffect {
    data class ShowSnackbar(
        val message: String,
    ) : ConsumableDetailSideEffect
}
