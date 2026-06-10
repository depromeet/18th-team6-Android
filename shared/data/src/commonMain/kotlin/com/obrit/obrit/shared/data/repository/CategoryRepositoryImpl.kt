package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.categories.Category
import com.obrit.obrit.shared.model.categories.CategoryIcon
import com.obrit.obrit.shared.model.categories.error.CreateCategoryError
import com.obrit.obrit.shared.network.error.RemoteError
import com.obrit.obrit.shared.network.error.runCatchingWith
import com.obrit.obrit.shared.network.request.category.CreateCategoryRequest
import com.obrit.obrit.shared.network.response.category.toCategory
import com.obrit.obrit.shared.network.response.category.toCategoryIcon
import com.obrit.obrit.shared.network.source.CategoryRemoteDataSource

internal class CategoryRepositoryImpl(
    private val categoryRemoteDataSource: CategoryRemoteDataSource,
) : CategoryRepository {
    override suspend fun getCategories(): Result<List<Category>> =
        runCatchingWith {
            categoryRemoteDataSource
                .getCategories()
                .map { response -> response.toCategory() }
        }

    override suspend fun createCategory(
        name: String,
        iconId: Long,
    ): Result<Category> =
        runCatchingWith {
            categoryRemoteDataSource
                .createCategory(
                    CreateCategoryRequest(
                        name = name,
                        iconId = iconId,
                    ),
                ).toCategory()
        }.recoverCatching { error ->
            if (error is RemoteError && error.statusCode == HTTP_STATUS_CONFLICT) {
                throw CreateCategoryError.DuplicatedName()
            }
            throw error
        }

    override suspend fun deleteCategory(categoryId: Long): Result<Unit> =
        runCatchingWith {
            categoryRemoteDataSource.deleteCategory(categoryId)
        }

    override suspend fun getCategoryIcons(): Result<List<CategoryIcon>> =
        runCatchingWith {
            categoryRemoteDataSource
                .getCategoryIcons()
                .map { response -> response.toCategoryIcon() }
        }

    private companion object {
        const val HTTP_STATUS_CONFLICT = 409
    }
}
