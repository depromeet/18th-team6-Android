package com.obrit.obrit.shared.data.di

import com.obrit.obrit.shared.data.repository.UserRepository
import com.obrit.obrit.shared.data.repository.UserRepositoryImpl
import org.koin.dsl.module

val dataModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
}
