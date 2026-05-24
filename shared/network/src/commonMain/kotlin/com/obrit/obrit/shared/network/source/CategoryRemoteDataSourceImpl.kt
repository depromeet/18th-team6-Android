package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.client.userIdHeader
import com.obrit.obrit.shared.network.config.UserIdProvider
import com.obrit.obrit.shared.network.request.category.CreateCategoryRequest
import com.obrit.obrit.shared.network.response.ApiResponse
import com.obrit.obrit.shared.network.response.category.CategoryIconResponse
import com.obrit.obrit.shared.network.response.category.CategoryResponse
import com.obrit.obrit.shared.network.response.requireData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class CategoryRemoteDataSourceImpl(
    private val httpClient: HttpClient,
    private val userIdProvider: UserIdProvider,
) : CategoryRemoteDataSource {
    override suspend fun getCategories(): List<CategoryResponse> {
        val userId = userIdProvider.get()

        return httpClient
            .get(CATEGORIES_PATH) {
                userIdHeader(userId)
            }.body<ApiResponse<List<CategoryResponse>>>()
            .requireData()
    }

    override suspend fun createCategory(request: CreateCategoryRequest): CategoryResponse {
        val userId = userIdProvider.get()

        return httpClient
            .post(CATEGORIES_PATH) {
                userIdHeader(userId)
                setBody(request)
            }.body<ApiResponse<CategoryResponse>>()
            .requireData()
    }

    override suspend fun deleteCategory(categoryId: Long) {
        val userId = userIdProvider.get()

        httpClient.delete("$CATEGORIES_PATH/$categoryId") {
            userIdHeader(userId)
        }
    }

    override suspend fun getCategoryIcons(): List<CategoryIconResponse> =
        httpClient
            .get("$CATEGORIES_PATH/icons")
            .body<ApiResponse<List<CategoryIconResponse>>>()
            .requireData()
}

private const val CATEGORIES_PATH = "categories"
