package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.request.category.CreateCategoryRequest
import com.obrit.obrit.shared.network.response.category.CategoryApiResponse
import com.obrit.obrit.shared.network.response.category.CategoryIconResponse
import com.obrit.obrit.shared.network.response.category.CategoryResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class CategoryRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : CategoryRemoteDataSource {
    override suspend fun getCategories(): List<CategoryResponse> =
        httpClient
            .get(CATEGORIES_PATH)
            .body<CategoryApiResponse<List<CategoryResponse>>>()
            .requireData()

    override suspend fun createCategory(request: CreateCategoryRequest): CategoryResponse =
        httpClient
            .post(CATEGORIES_PATH) {
                setBody(request)
            }.body()

    override suspend fun deleteCategory(categoryId: Long) {
        httpClient.delete("$CATEGORIES_PATH/$categoryId")
    }

    override suspend fun getCategoryIcons(): List<CategoryIconResponse> =
        httpClient
            .get("$CATEGORIES_PATH/icons")
            .body<CategoryApiResponse<List<CategoryIconResponse>>>()
            .requireData()
}

private const val CATEGORIES_PATH = "categories"

private fun <T> CategoryApiResponse<T>.requireData(): T {
    if (success != true) {
        error(message.orEmpty().ifBlank { "Category API response failed." })
    }

    return data ?: error(message.orEmpty().ifBlank { "Category API response data is missing." })
}
