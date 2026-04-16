package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.api.UserApi
import com.obrit.obrit.shared.network.request.SignUpRequest
import com.obrit.obrit.shared.network.response.SignUpResponse

internal class UserRemoteDataSourceImpl(
    private val userApi: UserApi,
) : UserRemoteDataSource {
    override suspend fun signUp(request: SignUpRequest): SignUpResponse {
        return userApi.signUp(request)
    }
}
