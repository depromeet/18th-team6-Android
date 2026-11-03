package com.obrit.feature.register.viewmodel

import androidx.compose.runtime.Immutable
import com.obrit.obrit.shared.data.repository.CategoryRepository
import com.obrit.obrit.shared.model.categories.Category
import com.obrit.obrit.shared.model.categories.CategoryGroup
import com.obrit.obrit.shared.model.categories.CategoryGroupType

@Immutable
data class CategorySection(
    val id: Long,
    val name: String,
    val categories: List<Category>,
    val isUserSection: Boolean,
)

internal suspend fun CategoryRepository.getCategorySections(): Result<List<CategorySection>> =
    getCategoryGroups().mapCatching { groups ->
        buildCategorySections(groups, getCategories().getOrThrow())
    }

// 소모품이 없는 기본 그룹은 숨긴다. 직접 등록은 비어 있어도 추가 진입점이라 남긴다.
private fun buildCategorySections(
    groups: List<CategoryGroup>,
    categories: List<Category>,
): List<CategorySection> =
    groups
        .filter { group -> group.type != CategoryGroupType.UNKNOWN }
        .sortedBy { group -> group.displayOrder }
        .map { group ->
            val isUserSection = group.type == CategoryGroupType.USER
            CategorySection(
                id = group.id,
                name = if (isUserSection) USER_SECTION_NAME else group.name,
                categories = categories.filter { category -> category.categoryGroupId == group.id },
                isUserSection = isUserSection,
            )
        }.filter { section -> section.isUserSection || section.categories.isNotEmpty() }

private const val USER_SECTION_NAME = "직접 등록"
