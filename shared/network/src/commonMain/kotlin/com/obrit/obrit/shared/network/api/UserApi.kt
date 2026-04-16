package com.obrit.obrit.shared.network.api

import com.obrit.obrit.shared.network.request.SignUpRequest
import com.obrit.obrit.shared.network.response.SignUpResponse

interface UserApi {
    suspend fun signUp(request: SignUpRequest): SignUpResponse
}
