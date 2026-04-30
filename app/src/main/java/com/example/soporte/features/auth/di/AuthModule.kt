package com.example.soporte.features.auth.di

import com.example.soporte.features.auth.data.FakeAuthRepository
import com.example.soporte.features.auth.domain.AuthRepository
import com.example.soporte.features.auth.presentation.login.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    single<AuthRepository> { FakeAuthRepository() }
    viewModel { LoginViewModel(repository = get()) }
}
