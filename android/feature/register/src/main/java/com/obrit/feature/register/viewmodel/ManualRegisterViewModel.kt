package com.obrit.feature.register.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.analytics.AnalyticsLogger
import com.obrit.android.core.analytics.OBRitLoggingEvent
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.obrit.shared.data.repository.CategoryRepository
import com.obrit.obrit.shared.data.repository.ItemRepository
import com.obrit.obrit.shared.model.categories.Category
import com.obrit.obrit.shared.model.items.CreateItemParams
import com.obrit.obrit.shared.model.items.error.CreateItemError
import org.orbitmvi.orbit.viewmodel.container

class ManualRegisterViewModel(
    private val categoryRepository: CategoryRepository,
    private val itemRepository: ItemRepository,
    private val analyticsLogger: AnalyticsLogger,
) : BaseContainerHost<ManualRegisterUiState, ManualRegisterSideEffect>() {
    private val registerStartedAt = System.currentTimeMillis()

    override val container =
        container<ManualRegisterUiState, ManualRegisterSideEffect>(
            ManualRegisterUiState(),
        ) {
            analyticsLogger.log(OBRitLoggingEvent.ManualRegisterStart)
            loadSections()
            loadExistingNames()
        }

    private fun loadSections() =
        intent {
            categoryRepository
                .getCategorySections()
                .onSuccess { sections ->
                    reduce { state.copy(sections = sections) }
                }
        }

    private fun loadExistingNames() =
        intent {
            itemRepository
                .getItems()
                .onSuccess { items ->
                    reduce { state.copy(existingNames = items.map { it.name.trim() }.toSet()) }
                }
        }

    fun onCategorySelect(category: Category) =
        intent {
            reduce {
                val autoName = nextAvailableItemName(category.name, state.existingNames)
                val isNameEdited = state.name.isNotEmpty() && state.name != state.autoName
                state.copy(
                    selectedCategoryId = category.id,
                    categoryName = category.name,
                    name = if (isNameEdited) state.name else autoName,
                    autoName = autoName,
                    isDuplicateName = false,
                )
            }
        }

    fun onDirectRegistered(category: Category) {
        loadSections()
        onCategorySelect(category)
    }

    fun onNameChange(value: String) =
        intent {
            reduce { state.copy(name = value, isDuplicateName = false) }
        }

    fun onQuantityChange(value: Int) =
        intent {
            reduce { state.copy(quantity = value) }
        }

    fun onSubmit() =
        intent {
            val current = state
            val categoryId = current.selectedCategoryId ?: return@intent
            if (!current.isSubmitEnabled) return@intent
            reduce { state.copy(isSubmitting = true) }
            val params =
                CreateItemParams(
                    categoryId = categoryId,
                    name = current.name,
                    spareQuantity = current.quantity,
                )
            itemRepository
                .createItem(params)
                .onSuccess {
                    analyticsLogger.log(
                        OBRitLoggingEvent.ManualRegisterComplete(
                            durationMs = System.currentTimeMillis() - registerStartedAt,
                        ),
                    )
                    reduce { state.clearedAfterRegister(current.name) }
                    postSideEffect(ManualRegisterSideEffect.OnRegistered(current.name))
                    loadSections()
                    loadExistingNames()
                }.onFailure { throwable ->
                    val failureType =
                        if (throwable is CreateItemError.DuplicatedName) "duplicate_name" else "unknown"
                    analyticsLogger.log(OBRitLoggingEvent.ManualRegisterFail(failureType = failureType))
                    reduce {
                        state.copy(
                            isSubmitting = false,
                            isDuplicateName = throwable is CreateItemError.DuplicatedName,
                        )
                    }
                }
        }

    fun onBack() =
        intent {
            postSideEffect(ManualRegisterSideEffect.OnBack)
        }
}

@Immutable
data class ManualRegisterUiState(
    val sections: List<CategorySection> = emptyList(),
    val selectedCategoryId: Long? = null,
    val categoryName: String = "",
    val name: String = "",
    // 자동으로 채운 이름. 사용자가 이름을 고쳤는지 구분해 소모품을 바꿔도 고친 이름을 덮어쓰지 않는다.
    val autoName: String = "",
    val quantity: Int = MANUAL_REGISTER_MIN_QUANTITY,
    val existingNames: Set<String> = emptySet(),
    val isSubmitting: Boolean = false,
    val isDuplicateName: Boolean = false,
) {
    val nameError: ItemNameError?
        get() = if (isDuplicateName) ItemNameError.Duplicated else validateItemName(name, existingNames)

    val isSubmitEnabled: Boolean
        get() =
            selectedCategoryId != null &&
                name.isNotBlank() &&
                nameError == null &&
                !isSubmitting
}

sealed interface ManualRegisterSideEffect {
    data class OnRegistered(
        val itemName: String,
    ) : ManualRegisterSideEffect

    data object OnBack : ManualRegisterSideEffect
}

internal const val MANUAL_REGISTER_MIN_QUANTITY = 1
internal const val MANUAL_REGISTER_MAX_QUANTITY = 99

// ViewModel이 화면을 나가도 남으므로 다음 진입에 이전 입력이 보이지 않게 비운다. 방금 추가한 이름은 중복 검사에 바로 반영한다.
private fun ManualRegisterUiState.clearedAfterRegister(registeredName: String): ManualRegisterUiState =
    ManualRegisterUiState(sections = sections, existingNames = existingNames + registeredName.trim())
