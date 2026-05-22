package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.request.category.CreateCategoryRequest
import com.obrit.obrit.shared.network.response.category.CategoryIconResponse
import com.obrit.obrit.shared.network.response.category.CategoryResponse

interface CategoryRemoteDataSource {
    suspend fun getCategories(): List<CategoryResponse>

    suspend fun createCategory(request: CreateCategoryRequest): CategoryResponse

    suspend fun deleteCategory(categoryId: Long)

    suspend fun getCategoryIcons(): List<CategoryIconResponse>
}
