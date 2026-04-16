package com.obrit.obrit.shared.network.source

import com.obrit.obrit.shared.network.request.SignUpRequest
import com.obrit.obrit.shared.network.response.SignUpResponse

interface UserRemoteDataSource {
    suspend fun signUp(request: SignUpRequest): SignUpResponse
}
