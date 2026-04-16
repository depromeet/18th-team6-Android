package com.obrit.obrit.shared.data.repository

import com.obrit.obrit.shared.model.sample.SignUpError
import com.obrit.obrit.shared.model.sample.SignUpParam
import com.obrit.obrit.shared.network.error.runCatchingWith
import com.obrit.obrit.shared.network.request.SignUpRequest
import com.obrit.obrit.shared.network.source.UserRemoteDataSource

internal class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
) : UserRepository {
    override suspend fun signUp(param: SignUpParam): Result<Unit> {
        return runCatchingWith(SignUpError()) {
            userRemoteDataSource.signUp(
                SignUpRequest(
                    email = param.email,
                    password = param.password,
                    nickname = param.nickname,
                ),
            )
        }
    }
}
