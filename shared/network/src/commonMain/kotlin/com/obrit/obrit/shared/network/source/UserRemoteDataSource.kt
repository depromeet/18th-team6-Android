package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.request.user.RegisterUserRequest
import com.obrit.obrit.shared.network.response.user.RegisterUserResponse

interface UserRemoteDataSource {
    suspend fun register(request: RegisterUserRequest): RegisterUserResponse
}
