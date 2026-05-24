package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.categories.Category
import com.obrit.obrit.shared.model.categories.CategoryIcon

interface CategoryRepository {
    suspend fun getCategories(): Result<List<Category>>

    suspend fun createCategory(
        name: String,
        iconId: Long,
    ): Result<Category>

    suspend fun deleteCategory(categoryId: Long): Result<Unit>

    suspend fun getCategoryIcons(): Result<List<CategoryIcon>>
}
