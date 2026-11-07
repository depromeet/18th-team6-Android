package com.obrit.obrit.shared.data.repository

interface UserRepository {
    suspend fun register(): Result<Long>
}
