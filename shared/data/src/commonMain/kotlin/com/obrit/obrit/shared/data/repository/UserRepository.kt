package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.sample.SignUpParam

interface UserRepository {
    suspend fun signUp(param: SignUpParam): Result<Unit>
}
