package com.obrit.feature.register.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.android.core.analytics.AnalyticsLogger
import com.obrit.android.core.analytics.OBRitLoggingEvent
import com.obrit.android.core.ui.BaseContainerHost
import com.obrit.obrit.shared.data.repository.CategoryRepository
import com.obrit.obrit.shared.data.repository.ItemRepository
import com.obrit.obrit.shared.data.repository.NotificationRepository
import com.obrit.obrit.shared.model.categories.Category
import com.obrit.obrit.shared.model.items.CreateItemParams
import org.orbitmvi.orbit.viewmodel.container

class OnboardingSelectViewModel(
    private val categoryRepository: CategoryRepository,
    private val itemRepository: ItemRepository,
    private val notificationRepository: NotificationRepository,
    private val analyticsLogger: AnalyticsLogger,
) : BaseContainerHost<OnboardingSelectUiState, OnboardingSelectSideEffect>() {
    private val startedAt = System.currentTimeMillis()

    override val container =
        container<OnboardingSelectUiState, OnboardingSelectSideEffect>(
            OnboardingSelectUiState(),
        ) {
            analyticsLogger.log(OBRitLoggingEvent.OnboardingStart)
            loadCatalog()
        }

    private fun loadCatalog() =
        intent {
            categoryRepository
                .getCategorySections()
                .onSuccess { sections ->
                    reduce { state.copy(sections = sections.filterNot { it.isUserSection }) }
                }
            // 온보딩 기능명세서 4.3: 새 소모품은 공용 아이콘을 쓴다. 등록 전에도 같은 아이콘으로 보여준다.
            categoryRepository
                .getCategoryIcons()
                .onSuccess { icons ->
                    val url = icons.firstOrNull { it.id == COMMON_ICON_ID }?.url ?: return@onSuccess
                    reduce { state.copy(newItemIconUrl = url) }
                }
        }

    fun onToggle(categoryId: Long) =
        intent {
            if (state.isSubmitting) return@intent
            reduce {
                val newItem = state.newCategories.firstOrNull { it.id == categoryId }
                when {
                    newItem != null -> state.copy(newItemNames = state.newItemNames - newItem.name)
                    categoryId in state.selectedIds -> state.copy(selectedIds = state.selectedIds - categoryId)
                    else -> state.copy(selectedIds = state.selectedIds + categoryId)
                }
            }
        }

    fun onNewItemAdd() =
        intent {
            val name = state.confirmedQuery.trim()
            if (name.isEmpty() || state.newItemError != null) return@intent
            reduce {
                state.copy(
                    newItemNames = state.newItemNames + name,
                    activeSectionId = NEW_ITEM_SECTION_ID,
                    query = "",
                    confirmedQuery = "",
                )
            }
        }

    fun onSectionSelect(sectionId: Long) =
        intent {
            reduce { state.copy(activeSectionId = sectionId, query = "", confirmedQuery = "") }
        }

    fun onQueryChange(value: String) =
        intent {
            reduce {
                if (value.isEmpty()) state.copy(query = value, confirmedQuery = "") else state.copy(query = value)
            }
        }

    fun onSearch(value: String) =
        intent {
            reduce { state.copy(query = value, confirmedQuery = value) }
        }

    fun onSubmit() =
        intent {
            val current = state
            if (!current.isSubmitEnabled) return@intent
            reduce { state.copy(isSubmitting = true) }
            itemRepository
                .createItems(current.toCreateItemParams())
                .onSuccess {
                    logComplete(selectionCount = current.selectedCount, success = true)
                    enableAllNotifications()
                    postSideEffect(OnboardingSelectSideEffect.OnRegistered(current.selectedCount))
                }.onFailure {
                    logComplete(selectionCount = current.selectedCount, success = false)
                    reduce { state.copy(isSubmitting = false) }
                }
        }

    fun onBack() =
        intent {
            postSideEffect(OnboardingSelectSideEffect.OnBack)
        }

    // 온보딩 기능명세서 4.4의 알림 제안을 없앤 대신 등록 후 전체 알림을 켠다. 실패해도 등록 완료는 막지 않는다.
    private suspend fun enableAllNotifications() {
        notificationRepository.getSettings().onSuccess { settings ->
            notificationRepository.updateSettings(
                settings.copy(
                    isReplacementUpcomingEnabled = true,
                    isReplacementOverdueEnabled = true,
                    isSpareShortageEnabled = true,
                ),
            )
        }
    }

    private fun logComplete(
        selectionCount: Int,
        success: Boolean,
    ) {
        analyticsLogger.log(
            OBRitLoggingEvent.OnboardingComplete(
                selectionCount = selectionCount,
                durationMs = System.currentTimeMillis() - startedAt,
                success = success,
            ),
        )
    }
}

@Immutable
data class OnboardingSelectUiState(
    val sections: List<CategorySection> = emptyList(),
    val activeSectionId: Long? = null,
    val query: String = "",
    // 입력 중에는 추천만 보여주고, 검색을 누르거나 추천을 골라야 목록을 거른다. 소모품 추가 바텀시트와 같다.
    val confirmedQuery: String = "",
    val selectedIds: Set<Long> = emptySet(),
    // 카탈로그에 없어 새로 추가한 소모품 이름. 서버에는 등록 요청 때 함께 만든다.
    val newItemNames: List<String> = emptyList(),
    val newItemIconUrl: String = "",
    val isSubmitting: Boolean = false,
) {
    val isSearching: Boolean
        get() = confirmedQuery.isNotBlank()

    // 온보딩 기능명세서 FR-ONB-04대로 처음에는 '직접 등록' 칩을 숨기고, 새 소모품을 추가했을 때만 맨 아래에 보여준다.
    val railSections: List<CategorySection>
        get() = sections + listOfNotNull(newItemSection)

    val activeSection: CategorySection?
        get() = railSections.firstOrNull { it.id == activeSectionId } ?: sections.firstOrNull()

    val newCategories: List<Category>
        get() =
            newItemNames.mapIndexed { index, name ->
                Category(
                    id = -(index + 1L),
                    name = name,
                    iconUrl = newItemIconUrl,
                    itemCount = 0,
                    totalSpareQuantity = 0,
                )
            }

    val newItemError: ItemNameError?
        get() = validateItemName(confirmedQuery.trim(), newItemNames.toSet())

    // 검색은 칩과 관계없이 모든 카테고리를 대상으로 하고 결과를 카테고리별로 묶는다.
    val visibleSections: List<CategorySection>
        get() =
            if (isSearching) {
                sections
                    .map { section ->
                        section.copy(
                            categories =
                                section.categories.filter { it.name.contains(confirmedQuery.trim(), ignoreCase = true) },
                        )
                    }.filter { it.categories.isNotEmpty() }
            } else {
                listOfNotNull(activeSection)
            }

    private val newItemSection: CategorySection?
        get() =
            newCategories.takeIf { it.isNotEmpty() }?.let { categories ->
                CategorySection(
                    id = NEW_ITEM_SECTION_ID,
                    name = NEW_ITEM_SECTION_NAME,
                    categories = categories,
                    isUserSection = true,
                )
            }

    val selectedCount: Int
        get() = selectedIds.size + newItemNames.size

    val isSubmitEnabled: Boolean
        get() = selectedCount > 0 && !isSubmitting

    fun isSelected(category: Category): Boolean = category.id in selectedIds || category in newCategories
}

sealed interface OnboardingSelectSideEffect {
    data object OnBack : OnboardingSelectSideEffect

    data class OnRegistered(
        val registeredCount: Int,
    ) : OnboardingSelectSideEffect
}

// 앱의 소모품 응답이 여분 null을 받지 못해 온보딩 기능명세서 3.2의 미입력(null) 대신 기존처럼 0으로 등록한다.
private fun OnboardingSelectUiState.toCreateItemParams(): List<CreateItemParams> {
    val catalogParams =
        sections
            .flatMap { it.categories }
            .filter { it.id in selectedIds }
            .map { category ->
                CreateItemParams(
                    categoryId = category.id,
                    name = category.name,
                    spareQuantity = ONBOARDING_SPARE_QUANTITY,
                )
            }
    val newItemParams =
        newItemNames.map { name ->
            CreateItemParams(
                name = name,
                spareQuantity = ONBOARDING_SPARE_QUANTITY,
                newCategoryName = name,
            )
        }
    return catalogParams + newItemParams
}

private const val ONBOARDING_SPARE_QUANTITY = 0
private const val NEW_ITEM_SECTION_ID = -1L

// 서버가 사용자 소모품에 넣는 공용 아이콘(none.webp)의 ID.
private const val COMMON_ICON_ID = 1L
private const val NEW_ITEM_SECTION_NAME = "직접 등록"
