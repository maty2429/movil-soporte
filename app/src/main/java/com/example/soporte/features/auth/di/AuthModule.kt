package com.example.soporte.features.auth.di

import com.example.soporte.features.auth.presentation.login.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    viewModel {
        LoginViewModel(
            getTechnicianByRut = get(),
            sessionManager = get(),
        )
    }
}
